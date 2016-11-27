package com.example.djung.locally.View;

import com.example.djung.locally.View.Adapters.VendorListAdapter;

/**
 * Created by Andy Lin on 2016-11-25.
 */

public interface VendorListView {
    void setActionBarTitle(String title);
    void showVendorList(VendorListAdapter vendorListAdapter);
}
