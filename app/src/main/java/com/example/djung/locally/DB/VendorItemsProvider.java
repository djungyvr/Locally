package com.example.djung.locally.DB;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import static android.app.SearchManager.*;
import static android.content.ContentResolver.*;

/**
 * Allows access to the VendorItemDatabase
 *
 * Created by David Jung on 08/11/16.
 */
public class VendorItemsProvider extends ContentProvider{
    String TAG = "VendorItemProvider";
    public static final String AUTHORITY = "com.example.djung.locally.DB.VendorItemsProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/vendor_items");

    // Uses MIME (Multipurpose Internet Mail Extensions) to search vendor item
    public static final String WORDS_MIME_TYPE = CURSOR_DIR_BASE_TYPE +
            "/vnd.example.djung.locally";
    public static final String DEFINITION_MIME_TYPE = CURSOR_ITEM_BASE_TYPE +
            "/vnd.example.djung.locally";

    private VendorItemDatabase mVendorItemDatabase;

    // Uri matcher
    private static final int SEARCH_ITEM = 0;
    private static final int GET_ITEM = 1;
    private static final int SEARCH_SUGGEST = 2;
    private static final int REFRESH_SHORTCUT = 3;
    private static final UriMatcher sURIMatcher = buildUriMatcher();

    /**
     * Builds up a UriMatcher for search suggestion and shortcut refresh queries.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...
        matcher.addURI(AUTHORITY, "vendor_items", SEARCH_ITEM);
        matcher.addURI(AUTHORITY, "vendor_items/#", GET_ITEM);
        // to get suggestions...
        matcher.addURI(AUTHORITY, SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

        /* The following are unused in this implementation, but if we include
         * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
         * could expect to receive refresh queries when a shortcutted suggestion is displayed in
         * Quick Search Box, in which case, the following Uris would be provided and we
         * would return a cursor with a single item representing the refreshed suggestion data.
         */
        matcher.addURI(AUTHORITY, SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
        matcher.addURI(AUTHORITY, SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mVendorItemDatabase = new VendorItemDatabase(getContext());
        return true;
    }

    /**
     * Handles all the vendor items searches and suggestion queries from the Search Manager.
     * When requesting a specific item, the uri alone is required.
     * When searching all of the dictionary for matches, the selectionArgs argument must carry
     * the search query as the first element.
     * All other arguments are ignored.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Use the UriMatcher to see what kind of query we have and format the db query accordingly
        switch (sURIMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                if (selectionArgs == null) {
                    throw new IllegalArgumentException(
                            "selectionArgs must be provided for the Uri: " + uri);
                }
                return getSuggestions(selectionArgs[0]);
            case SEARCH_ITEM:
                if (selectionArgs == null) {
                    throw new IllegalArgumentException(
                            "selectionArgs must be provided for the Uri: " + uri);
                }
                return search(selectionArgs[0]);
            case GET_ITEM:
                return getItem(uri);
            case REFRESH_SHORTCUT:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    private Cursor getSuggestions(String query) {
        query = query.toLowerCase();
        String[] columns = new String[] {
                BaseColumns._ID,
                VendorItemDatabase.KEY_VENDOR_ITEM_NAME,
                VendorItemDatabase.KEY_VENDOR_ITEM_INFO,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

        return mVendorItemDatabase.getWordMatches(query, columns);
    }

    private Cursor search(String query) {
        query = query.toLowerCase();
        String[] columns = new String[] {
                BaseColumns._ID,
                mVendorItemDatabase.KEY_VENDOR_ITEM_NAME,
                mVendorItemDatabase.KEY_VENDOR_ITEM_INFO
        };
        return mVendorItemDatabase.getWordMatches(query, columns);
    }

    private Cursor getItem(Uri uri) {
        String rowId = uri.getLastPathSegment();
        String[] columns = new String[] {
                mVendorItemDatabase.KEY_VENDOR_ITEM_NAME,
                mVendorItemDatabase.KEY_VENDOR_ITEM_INFO};

        return mVendorItemDatabase.getVendorItem(rowId, columns);
    }

    /**
     * Required in order to query the supported types.
     * It's also useful in our own query() method to determine the type of Uri received.
     */
    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case SEARCH_ITEM:
                return WORDS_MIME_TYPE;
            case GET_ITEM:
                return DEFINITION_MIME_TYPE;
            case SEARCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
            case REFRESH_SHORTCUT:
                return SearchManager.SHORTCUT_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }
}
