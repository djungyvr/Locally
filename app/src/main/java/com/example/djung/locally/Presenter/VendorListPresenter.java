package com.example.djung.locally.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.ThreadUtils;
import com.example.djung.locally.View.Adapters.VendorListAdapter;
import com.example.djung.locally.View.Interfaces.VendorListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Andy Lin on 2016-11-25.
 */

public class VendorListPresenter {
    private Activity activity;
    private VendorListView vendorListView;
    private Market currentMarket;
    private List<Vendor> vendorList;
    private OnVendorListItemClickListener mCallback;
    private final String TAG = "VendorListPresenter";

    public VendorListPresenter(Activity activity, VendorListView vendorListView, Bundle bundle){
        this.activity = activity;
        this.vendorListView = vendorListView;
        this.currentMarket = (Market) bundle.getSerializable("currentMarket");
        vendorList = new ArrayList<Vendor>();
        mCallback = (OnVendorListItemClickListener) activity;
    }

    public interface OnVendorListItemClickListener{
        void onVendorListItemClick(String vendorName, Market market);
    }

    public void setActionBar(){
        vendorListView.setActionBarTitle(currentMarket.getName());
    }

    public void setNavDrawerSelectedItem(){
        vendorListView.setNavDrawerSelectedItem(R.id.nav_market_list);
    }

    public void populateVendorList(){
        Log.e(TAG, "Populating the vendor list");
        VendorPresenter presenter = new VendorPresenter(activity);

        try {
            List<Vendor> fetchedList = presenter.fetchVendors(currentMarket.getName());

            for(Vendor v : fetchedList) {
                vendorList.add(v);
            }

        } catch (final ExecutionException ee) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(activity)
                            .setTitle("Execute Exception")
                            .setMessage(ee.getMessage())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            });
        } catch (final InterruptedException ie) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(activity)
                            .setTitle("Interrupted Exception")
                            .setMessage(ie.getMessage())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            });
        }

        VendorListAdapter adapter = new VendorListAdapter(this);
        vendorListView.showVendorList(adapter);
    }

    public List<Vendor> getVendorList(){
        return vendorList;
    }

    public void onCallButtonClick(int position){
        Vendor vendor = vendorList.get(position);
        String number = vendor.getPhoneNumber();
        if (number == null || number.equals("")){
            Log.e(TAG, "Not a valid phone number");
            Toast.makeText(activity, "The vendor does not have a valid phone number", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.e(TAG, "Calling phone number " + number);
            Uri call = Uri.parse("tel:" + number);
            Intent intent = new Intent(Intent.ACTION_DIAL, call);
            activity.startActivity(intent);
        }
    }

    public void onVendorListItemClick(int position){
        Vendor vendor = vendorList.get(position);
        Log.e(TAG, "Callback on Vendor List Item Click of vendor: " + vendor.getName());
        mCallback.onVendorListItemClick(vendor.getName(), currentMarket);
    }
}
