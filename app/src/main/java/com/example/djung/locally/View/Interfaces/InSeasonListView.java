package com.example.djung.locally.View.Interfaces;

import com.example.djung.locally.View.Adapters.InSeasonListAdapter;

/**
 * Created by AleSa on 30-Nov-16.
 */

public interface InSeasonListView {
        void setActionBarTitle(String title);
        void showInSeasonList(InSeasonListAdapter vendorListAdapter);
}
