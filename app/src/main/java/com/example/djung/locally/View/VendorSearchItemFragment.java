package com.example.djung.locally.View;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Angy Chung on 2016-11-18.
 */

public class VendorSearchItemFragment extends Fragment {
    private OnVendorListItemClickListener mCallback;
    private String mSearchItem;
    private ArrayList<Vendor> mVendorsList;
    private static final String TAG = "VendorSearchResults";
    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private Location mCurrentLocation;

    public interface OnVendorListItemClickListener{
        public void onVendorListItemClick(String vendorName, Market market);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mSearchItem = getArguments().getString("searchItem");
        View view = inflater.inflate(R.layout.vendor_search_item_list, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(
                R.string.title_fragment_vendors_search_results_list));
        ((MainActivity) getActivity()).setAppBarElevation(4);
        searchVendorsForItem();
        initializeAdapter(view);

        return view;
    }

    private void searchVendorsForItem() {
        VendorPresenter presenter = new VendorPresenter(getContext());
        mVendorsList = new ArrayList<>();
        try {
            mVendorsList = new ArrayList<>(presenter.lookForVendorsItem(mSearchItem));
        } catch (final ExecutionException | InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void initializeAdapter(View view) {
        mTextView = (TextView) view.findViewById(R.id.vendors_search_text);
        if(mVendorsList == null || mVendorsList.isEmpty()) {
            String noResults = "No results for " + mSearchItem + " found";
            mTextView.setText(noResults);
        } else {
            String resultsFound = "Vendors selling " + mSearchItem + ":";
            mTextView.setText(resultsFound);

            VendorSearchItemAdapter adapter = new VendorSearchItemAdapter(mVendorsList, mCurrentLocation,
                    getContext(), mCallback);

            mRecyclerView = (RecyclerView) view.findViewById(R.id.vendors_search_recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            mCurrentLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onAttach (Context context) {
        super.onAttach(context);
        try {
            Activity activity = (Activity) context;
            mCallback = (OnVendorListItemClickListener) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, "Class cast exception");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

}
