package com.example.djung.locally.Presenter;

import android.app.Activity;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.djung.locally.DB.VendorItemDatabase;
import com.example.djung.locally.DB.VendorItemsProvider;
import com.example.djung.locally.R;
import com.example.djung.locally.View.Adapters.InSeasonListAdapter;
import com.example.djung.locally.View.Adapters.SuggestionAdapter;
import com.example.djung.locally.View.Interfaces.InSeasonListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AleSa on 30-Nov-16.
 */

public class InSeasonListPresenter {
    private Activity mActivity;
    private InSeasonListView mInSeasonListView;
    private List<String> mProduceList;
    private List<String> mSeasonList;
    private final String TAG = "InSeasonListPresenter";


    public InSeasonListPresenter(Activity mActivity, InSeasonListView mInSeasonListView) {
        this.mActivity = mActivity;
        this.mInSeasonListView = mInSeasonListView;
        mProduceList = new ArrayList<String>();
        mSeasonList = new ArrayList<String>();
    }

    public void setActionBar(){
        mInSeasonListView.setActionBarTitle("In Season List");
    }

    public void populateInSeasonList(String season) {
        Log.e(TAG, "Populating the in season list");
        mProduceList.clear();

        VendorItemDatabase db = new VendorItemDatabase(mActivity);
        String[] columns = new String[] {
                BaseColumns._ID,
                db.KEY_VENDOR_ITEM_NAME,
                db.KEY_VENDOR_ITEM_INFO
        };
        Cursor cursor = db.getSeasonMatches(season,columns);

        if (cursor == null) {
        } else {
            cursor.moveToFirst();
            // Specify the columns we want to display in the result
            while(cursor.moveToNext()) {
                mProduceList.add(cursor.getString(cursor.getColumnIndex(VendorItemDatabase.KEY_VENDOR_ITEM_NAME)));
            }
        }

        InSeasonListAdapter adapter = new InSeasonListAdapter(this);
        mInSeasonListView.showInSeasonList(adapter);
    }

    public List<String> getProduceList() {
        return mProduceList;
    }
}
