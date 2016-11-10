package com.example.djung.locally.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public interface OnVendorListItemClickListener{
        public void onVendorListItemClick(String vendorName, String marketName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.marketName = getArguments().getString("marketName");
        this.marketAddress = getArguments().getString("marketAddress");
        this.marketHours = DateUtils.parseHours(getArguments().getString("marketHours"));
        View view = inflater.inflate(R.layout.vendor_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        populateVendorList();
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

        VendorListAdapter adapter = new VendorListAdapter(vendorListItems, getActivity(), mCallback, marketAddress, marketHours);
        recyclerView.setAdapter(adapter);
    }
}
