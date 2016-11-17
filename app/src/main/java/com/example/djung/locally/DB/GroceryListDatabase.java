package com.example.djung.locally.DB;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.HashMap;

/**
 *
 *
 * Created by David Jung on 15/11/16.
 */

public class GroceryListDatabase {
    private static final String TAG = "GroceryListDB";

    // Database Info
    private static final String DATABASE_NAME = "grocery_list";
    private static final String FTS_VIRTUAL_TABLE = "FTS_grocery_list";
    private static final int DATABASE_VERSION = 1;

    // Columns
    public static final String KEY_GROCERY_ITEM_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String KEY_GROCERY_ITEM_INFO = SearchManager.SUGGEST_COLUMN_TEXT_2;

    private final GroceryListDatabase.GroceryItemOpenHelper mGroceryItemOpenHelper;

    private static final HashMap<String,String> mColumnMap = buildColumnMap();

    public GroceryListDatabase(Context context) {
        this.mGroceryItemOpenHelper = new GroceryListDatabase.GroceryItemOpenHelper(context);
    }

    /**
     * Builds a map for all columns that may be requested, which will be given to the
     * SQLiteQueryBuilder.
     */
    private static HashMap<String,String> buildColumnMap() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put(KEY_GROCERY_ITEM_NAME, KEY_GROCERY_ITEM_NAME);
        map.put(KEY_GROCERY_ITEM_INFO, KEY_GROCERY_ITEM_INFO);
        map.put(BaseColumns._ID, "rowid AS " +
                BaseColumns._ID);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        return map;
    }

    /**
     * Returns a Cursor of the grocery items in the database
     *
     * @return all the grocery list items
     */
    public Cursor getGroceryItems() {
        Cursor  cursor = mGroceryItemOpenHelper.getReadableDatabase().rawQuery("SELECT * FROM " + FTS_VIRTUAL_TABLE,null);
        return cursor;

        /* This builds a query that looks like:
         *     SELECT * FROM <table>
         */
    }

    /**
     * Clears the database so new items can be added
     */
    public void clear() {
        mGroceryItemOpenHelper.getWritableDatabase().delete(FTS_VIRTUAL_TABLE, null, null);
        
        /* This builds a query that looks like:
         *     DELETE FROM <table>
         */
    }

    /**
     * Adds an item to the grocery list
     *
     * @param item to add to the database
     * @return id of row added, -1 if unsuccessful
     */
    public long addGroceryItem(String item) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_GROCERY_ITEM_NAME,item);
        initialValues.put(KEY_GROCERY_ITEM_INFO,"INFO");
        return mGroceryItemOpenHelper.getWritableDatabase().insert(FTS_VIRTUAL_TABLE, null,initialValues);
    }

    /**
     * deletes an item from the grocery list
     *
     * @param item to delete from the database
     * @return true if deleted, false otherwise
     */
    public boolean deleteGroceryItem(String item) {
        String whereClause = KEY_GROCERY_ITEM_NAME + "=" + item;
        return mGroceryItemOpenHelper.getWritableDatabase().delete(FTS_VIRTUAL_TABLE,whereClause,null) > 0;
    }

    private static class GroceryItemOpenHelper extends SQLiteOpenHelper {
        private final Context mContext;
        private SQLiteDatabase mDatabase;

        private static final String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                        " USING fts3 (" +
                        KEY_GROCERY_ITEM_NAME + "," +
                        KEY_GROCERY_ITEM_INFO + ");";

        GroceryItemOpenHelper(Context context) {
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
