package com.example.djung.locally.View;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Utils.ThreadUtils;
import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Andy Lin on 2016-11-04.
 */

public class VendorListFragment extends Fragment {
    private OnVendorListItemClickListener mCallback;
    private String marketName;
    private String marketAddress;
    private String marketHours;
    private String marketDatesOpen;
    private Location currentLocation;
    private Market currentMarket;

    public interface OnVendorListItemClickListener{
        public void onVendorListItemClick(String vendorName, Market market);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.marketName = getArguments().getString("marketName");
        this.marketAddress = getArguments().getString("marketAddress");
        this.marketDatesOpen = getArguments().getString("marketDatesOpen");
        this.marketHours = DateUtils.parseHours(getArguments().getString("marketHours"));
        this.currentMarket = (Market)  getArguments().getSerializable("currentMarket");
        View view = inflater.inflate(R.layout.vendor_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        Permissions.REQUEST_COURSE_PERMISSION);
            }
        }
        currentLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        populateVendorList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Permissions.REQUEST_COURSE_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission is granted to use location data

                } else {
                    //Permission is denied to use location data
                    currentLocation = null;
                }
                return;
        }
    }

    public void onAttach (Context context) {
        super.onAttach(context);
        try {
            Activity activity = (Activity) context;
            mCallback = (OnVendorListItemClickListener) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public void populateVendorList(){
        List<Vendor> vendorListItems = new ArrayList<Vendor>();
        VendorPresenter presenter = new VendorPresenter(this.getContext());

        try {
            List<Vendor> fetchedList = presenter.fetchVendors(marketName);

            for(Vendor v : fetchedList) {
                vendorListItems.add(v);
            }

        } catch (final ExecutionException ee) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(getActivity())
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
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Interrupted Exception")
                            .setMessage(ie.getMessage())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            });
        }

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.vendor_list);

        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        VendorListAdapter adapter = new VendorListAdapter(vendorListItems, getActivity(), mCallback, currentLocation, currentMarket);
        recyclerView.setAdapter(adapter);
    }
}
