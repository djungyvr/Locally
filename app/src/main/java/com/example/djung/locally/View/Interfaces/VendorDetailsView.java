package com.example.djung.locally.View.Interfaces;

import android.widget.ImageView;

import java.util.List;

/**
 * Created by Andy Lin on 2016-11-27.
 */

public interface VendorDetailsView {
    void setActionBarTitle(String title);
    void setNavDrawerSelectedItem(int resID);
    void showProduceList(List<String> produceList);
    void showVendorName(String vendorName);
    void showVendorDescription(String vendorDescription);
    void showVendorLocation(String vendorLocation);
    void showVendorStatus(String vendorStatus);
    void showVendorHours(String vendorHours);
    void showVendorPhoneNumber(String vendorPhoneNumber);
    void showVendorEmail(String vendorEmail);
    ImageView getVendorImageView();
}
