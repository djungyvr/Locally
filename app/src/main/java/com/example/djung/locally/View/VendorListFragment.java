package com.example.djung.locally.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.djung.locally.Presenter.VendorListPresenter;
import com.example.djung.locally.R;


/**
 * Created by Andy Lin on 2016-11-04.
 */

public class VendorListFragment extends Fragment implements VendorListView{
    private VendorListPresenter vendorListPresenter;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;

    @Override
    public void setActionBarTitle(String title) {
        mainActivity.setActionBarTitle(title);
        mainActivity.setAppBarElevation(4);
    }

    @Override
    public void showVendorList(VendorListAdapter vendorListAdapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(vendorListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vendor_list, container, false);
        vendorListPresenter = new VendorListPresenter(getActivity(), this, getArguments());
        this.mainActivity = ((MainActivity) getActivity());
        this.recyclerView = (RecyclerView) view.findViewById(R.id.vendor_list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        vendorListPresenter.populateVendorList();
    }
}
