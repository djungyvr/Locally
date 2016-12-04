package com.example.djung.locally.View.Interfaces;

import com.example.djung.locally.View.Adapters.MarketListAdapter;

/**
 * Created by Andy Lin on 2016-11-26.
 */

public interface MarketListView {
    void setActionBarTitle(String title);
    void setNavDrawerSelectedItem(int resID);
    void showMarketList(MarketListAdapter marketListAdapter);
}
