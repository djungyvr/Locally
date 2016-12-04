package com.example.djung.locally.Presenter;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.djung.locally.View.Adapters.InSeasonListAdapter;
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

    public void populateInSeasonList() {
        Log.e(TAG, "Populating the in season list");

        //TODO: Change this to query the database instead

        AssetManager am = mActivity.getAssets();
        BufferedReader buffer = null;

        try {
            buffer = new BufferedReader(new InputStreamReader(am.open("vendor_items.txt")));
            String line;
            while((line=buffer.readLine()) != null) {
                String[] vendorItem = line.trim().split(",");
                mProduceList.add(vendorItem[0]);
                mSeasonList.add(vendorItem[1]);
            }
            buffer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //END of TODO


        InSeasonListAdapter adapter = new InSeasonListAdapter(this);
        mInSeasonListView.showInSeasonList(adapter);
    }

    public List<String> getProduceList() {
        return mProduceList;
    }
}
