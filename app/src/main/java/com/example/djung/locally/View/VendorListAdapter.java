package com.example.djung.locally.View;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy Lin on 2016-11-01.
 */

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.ViewHolder>{
    private List<Vendor> vendorListItems;
    private Context context;
    private VendorListFragment.OnVendorListItemClickListener listener;
    private String vendorLocation;
    private String vendorHours;

    public VendorListAdapter(List<Vendor> list, Context context, VendorListFragment.OnVendorListItemClickListener listener,
                             String vendorLocation, String vendorHours){
        this.vendorListItems = list;
        this.context = context;
        this.listener = listener;
        this.vendorLocation = vendorLocation;
        this.vendorHours = vendorHours;
    }

    @Override
    public VendorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v, context, vendorListItems);
        return vh;
    }

    @Override
    public void onBindViewHolder(VendorListAdapter.ViewHolder holder, int position) {
        Vendor item = vendorListItems.get(position);
        holder.vendorListItemVendorName.setText(item.getName());
        holder.vendorListItemVendorLocation.setText(vendorLocation);
        
//        holder.vendorListItemVendorStatus.setText(item.get());
//        holder.vendorListItemVendorLocation.setText(item.getVendorLocation());
//        holder.vendorListItemVendorDistance.setText(item.getVendorDistance());
    }

    @Override
    public int getItemCount() {
        if (vendorListItems != null){
            return vendorListItems.size();
        }
        else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView vendorListItemVendorName;
        public TextView vendorListItemVendorLocation;
        public TextView vendorListItemVendorStatus;
        public TextView vendorListItemVendorDistance;
        public Context context;
        public List<Vendor> items;

        public ViewHolder(View itemView, Context context, List<Vendor> items) {
            super(itemView);
            this.context = context;
            this.items = items;
            this.vendorListItemVendorDistance = (TextView) itemView.findViewById(R.id.vendor_list_item_distance);
            this.vendorListItemVendorLocation = (TextView) itemView.findViewById(R.id.vendor_list_item_location);
            this.vendorListItemVendorName = (TextView) itemView.findViewById(R.id.vendor_list_item_name);
            this.vendorListItemVendorStatus = (TextView) itemView.findViewById(R.id.vendor_list_item_status);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Vendor vendor = vendorListItems.get(position);
            listener.onVendorListItemClick(vendor.getName(), vendor.getMarketName());
        }
    }
}