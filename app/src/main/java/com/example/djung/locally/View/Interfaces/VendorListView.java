package com.example.djung.locally.View.Interfaces;

import com.example.djung.locally.View.Adapters.VendorListAdapter;

/**
 * Created by Andy Lin on 2016-11-25.
 */

public interface VendorListView {
    void setActionBarTitle(String title);
    void setNavDrawerSelectedItem(int resID);
    void showVendorList(VendorListAdapter vendorListAdapter);
}
