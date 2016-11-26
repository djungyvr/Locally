package com.example.djung.locally.View;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Presenter.VendorListPresenter;
import com.example.djung.locally.R;

/**
 * Created by Andy Lin on 2016-11-01.
 */

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.ViewHolder>{
    private VendorListPresenter vendorListPresenter;

    public VendorListAdapter(VendorListPresenter vendorListPresenter){
        this.vendorListPresenter = vendorListPresenter;
    }

    @Override
    public VendorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(VendorListAdapter.ViewHolder holder, final int position) {
        Vendor item = vendorListPresenter.getVendorList().get(position);
        holder.vendorListItemVendorName.setText(item.getName());
        holder.vendorListItemDescription.setText(item.getDescription());
        holder.vendorListItemCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vendorListPresenter.onCallButtonClick(position);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vendorListPresenter.onVendorListItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (vendorListPresenter.getVendorList() != null){
            return vendorListPresenter.getVendorList().size();
        }
        else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView vendorListItemVendorName;
        public TextView vendorListItemDescription;
        public Button vendorListItemCallButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.vendorListItemDescription = (TextView) itemView.findViewById(R.id.vendor_list_item_description);
            this.vendorListItemCallButton = (Button) itemView.findViewById(R.id.vendor_list_item_call_button);
            this.vendorListItemVendorName = (TextView) itemView.findViewById(R.id.vendor_list_item_name);
        }
    }
}