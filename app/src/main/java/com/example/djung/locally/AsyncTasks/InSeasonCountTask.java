package com.example.djung.locally.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.djung.locally.DB.GroceryListDatabase;
import com.example.djung.locally.DB.VendorItemDatabase;
import com.example.djung.locally.Utils.DateUtils;

/**
 * Task to count the number of items in season
 *
 * Created by David Jung on 04/12/16.
 */

public class InSeasonCountTask extends AsyncTask<Void, Void, Integer> {
    private Context mContext;
    public InSeasonCountTask(Context context) {
        mContext = context;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        return new VendorItemDatabase(mContext).getInSeasonCount();
    }
}
