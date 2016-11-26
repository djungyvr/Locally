package com.example.djung.locally.View;

import com.example.djung.locally.View.Adapters.MarketListAdapter;

/**
 * Created by Andy Lin on 2016-11-26.
 */

public interface MarketListView {
    void setActionBarTitle(String title);
    void showMarketList(MarketListAdapter marketListAdapter);
}
