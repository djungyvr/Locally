package com.example.djung.locally.View;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.VendorUtils;
import com.example.djung.locally.View.Adapters.VendorItemAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Contains the default fragment for vendor side of the app
 *
 * Created by David Jung on 29/11/16.
 */

public class VendorFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "VendorFragment";
    //TODO COMPLETE CLASS
    // Dialogs
    private Dialog mVendorSaveDialog;
    private Dialog mWaitDialog;

    // Views inside the fragment
    private VendorItemRecyclerView mRecyclerViewVendorItems;
    private FloatingActionButton mFabSaveList;
    private VendorItemAdapter mVendorItemAdapter;

    // Vendor attributes that are displayed or needed
    private List<String> mVendorItemSet;
    private String mMarketName;
    private String mVendorName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_vendor, container, false);
        Bundle bundle = this.getArguments();

        if(bundle != null) {
            mVendorItemSet = bundle.getStringArrayList("vendor_items");
            mVendorName = bundle.getString("vendor_name");
            mMarketName = bundle.getString("market_name");
            initializeViews(view);
            initializeAdapter(view);
        }
        return view;
    }

    private void initializeViews(View view) {
        mFabSaveList = (FloatingActionButton) view.findViewById(R.id.fab_save_vendor_list);
        mFabSaveList.setOnClickListener(this);
    }

    /**
     * Initialize the adapter/recyclerview
     */
    private void initializeAdapter(View view) {
        if(mVendorItemSet != null) {
            mRecyclerViewVendorItems = (VendorItemRecyclerView) view.findViewById(R.id.recycler_view_vendor_items);
            mRecyclerViewVendorItems.setHasFixedSize(true);
            ArrayList<String> filteredList = VendorUtils.filterPlaceholderText(mVendorItemSet);
            mVendorItemAdapter = new VendorItemAdapter(filteredList, getContext());
            mRecyclerViewVendorItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            mRecyclerViewVendorItems.setAdapter(mVendorItemAdapter);
            mRecyclerViewVendorItems.setEmptyView(view.findViewById(R.id.recycler_view_vendor_items_empty));
        }
    }

    public void addItem(String item) {
        mVendorItemAdapter.addItem(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_save_vendor_list:
                VendorPresenter vendorPresenter = new VendorPresenter(getContext());
                try {
                    vendorPresenter.updateVendorProducts(mMarketName, mVendorName,new HashSet<String>(mVendorItemAdapter.getItemNames()));
                    Toast.makeText(getContext(),"Updating list",Toast.LENGTH_SHORT).show();
                } catch (ExecutionException | InterruptedException e) {
                    showDialogMessage("Save Error", "Failed to save item list");
                    Log.e(TAG,e.getMessage());
                }
                break;
        }
    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    mVendorSaveDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        mVendorSaveDialog = builder.create();
        mVendorSaveDialog.show();
    }
}
