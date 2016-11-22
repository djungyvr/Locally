package com.example.djung.locally.DB;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

import com.amazonaws.auth.policy.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.HashMap;

/**
 * /**
 * Contains logic to return specific words from the vendor item list, and
 * loads the vendor item table when it needs to be created.
 *
 * Created by David Jung on 07/11/16.
 */
public class VendorItemDatabase {
    private static final String TAG = "VendorItemDatabase";

    // Database Info
    private static final String DATABASE_NAME = "vendor_items";
    private static final String FTS_VIRTUAL_TABLE = "FTS_vendor_items";
    private static final int DATABASE_VERSION = 4;

    // Columns
    public static final String KEY_VENDOR_ITEM_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String KEY_VENDOR_ITEM_INFO = SearchManager.SUGGEST_COLUMN_TEXT_2;

    private final VendorItemOpenHelper mVendorItemOpenHelper;

    private static final HashMap<String,String> mColumnMap = buildColumnMap();

    public VendorItemDatabase(Context context) {
        this.mVendorItemOpenHelper = new VendorItemOpenHelper(context);
    }

    /**
     * Builds a map for all columns that may be requested, which will be given to the
     * SQLiteQueryBuilder.
     */
    private static HashMap<String,String> buildColumnMap() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put(KEY_VENDOR_ITEM_NAME, KEY_VENDOR_ITEM_NAME);
        map.put(KEY_VENDOR_ITEM_INFO, KEY_VENDOR_ITEM_INFO);
        map.put(BaseColumns._ID, "rowid AS " +
                BaseColumns._ID);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        return map;
    }

    /**
     * Returns a Cursor positioned at the vendor item specified by rowId
     *
     * @param rowId id of vendor item to retrieve
     * @param columns The columns to include, if null then all are included
     * @return Cursor positioned to matching word, or null if not found.
     */
    public Cursor getVendorItem(String rowId, String[] columns) {
        String selection = "rowid = ?";
        String[] selectionArgs = new String[] {rowId};

        return query(selection, selectionArgs, columns);

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE rowid = <rowId>
         */
    }

    /**
     * Returns a Cursor over all vendor items that match the given query
     *
     * @param query The string to search for
     * @param columns The columns to include, if null then all are included
     * @return Cursor over all words that match, or null if none found.
     */
    public Cursor getWordMatches(String query, String[] columns) {
        String selection = KEY_VENDOR_ITEM_NAME + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};

        return query(selection, selectionArgs, columns);

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE <KEY_WORD> MATCH 'query*'
         */
    }

    /**
     * Performs a database query.
     * @param selection The selection clause
     * @param selectionArgs Selection arguments for "?" components in the selection
     * @param columns The columns to return
     * @return A Cursor over all rows matching the query
     */
    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        /* The SQLiteBuilder provides a map for all possible columns requested to
         * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);
        builder.setProjectionMap(mColumnMap);

        Cursor cursor = builder.query(mVendorItemOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    private static class VendorItemOpenHelper extends SQLiteOpenHelper {
        private final Context mContext;
        private SQLiteDatabase mDatabase;

        private static final String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                        " USING fts3 (" +
                        KEY_VENDOR_ITEM_NAME + "," +
                        KEY_VENDOR_ITEM_INFO + ");";

        VendorItemOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }

        /**
         * Called when the database connection is being configured.
         * Configure database settings for things like foreign key support, write-ahead logging, etc.
         */
        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
            db.setForeignKeyConstraintsEnabled(true);
        }

        /**
         * Called when the created for the first time.
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            mDatabase = sqLiteDatabase;
            mDatabase.execSQL(FTS_TABLE_CREATE);
            try {
                loadDatabase();
            } catch (RuntimeException e) {
                Log.e(TAG,e.getMessage());
            }
        }

        /**
         * Starts a thread to load the database with entries
         */
        private void loadDatabase() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        loadVendorItems();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        private void loadVendorItems() throws IOException{
            Log.e(TAG, "Loading items");

            AssetManager am = mContext.getAssets();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(am.open("vendor_items.txt")));

            try {
                String line;
                while((line=buffer.readLine()) != null) {
                    String[] vendorItem = line.trim().split(",");
                    long id = addVendorItem(vendorItem[0],vendorItem[1]);
                    if(id < 0) {
                        Log.e(TAG,"Error adding item: " + line.trim());
                    }
                }
            } finally {
                buffer.close();
            }
            Log.e(TAG,"Done adding items");
        }

        /**
         * Adds vendor item to the database
         *
         * @param vendorItemName name of the item in the file
         * @return the id of the item added
         */
        private long addVendorItem(String vendorItemName, String itemSeason) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_VENDOR_ITEM_NAME,vendorItemName);
            initialValues.put(KEY_VENDOR_ITEM_INFO,itemSeason);
            return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
        }

        /**
         * Called when the database needs to be upgraded.
         * This method will only be called if a database already exists on disk with the same DATABASE_NAME,
         * but the DATABASE_VERSION is different than the version of the database that exists on disk.
         */
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(sqLiteDatabase);
        }
    }
}