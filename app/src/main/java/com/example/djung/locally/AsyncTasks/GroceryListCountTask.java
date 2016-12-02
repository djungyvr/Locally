package com.example.djung.locally.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.djung.locally.DB.GroceryListDatabase;

/**
 * Created by David Jung on 01/12/16.
 */

public class GroceryListCountTask extends AsyncTask<Void, Void, Long>{
    private Context mContext;
    public GroceryListCountTask(Context context) {
        mContext = context;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        return new GroceryListDatabase(mContext).getNumItems();
    }
}
