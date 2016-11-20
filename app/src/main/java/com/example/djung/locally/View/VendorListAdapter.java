package com.example.djung.locally.View;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.LocationUtils;
import com.example.djung.locally.Utils.MarketUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy Lin on 2016-11-01.
 */

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.ViewHolder>{
    private List<Vendor> vendorListItems;
    private Context context;
    private VendorListFragment.OnVendorListItemClickListener listener;
//    private String vendorLocation;
//    private String vendorHours;
//    private String vendorDatesOpen;
    private Location currentLocation;
    private Market currentMarket;

    public VendorListAdapter(List<Vendor> list, Context context, VendorListFragment.OnVendorListItemClickListener listener,
                             Location currentLocation, Market currentMarket){
        this.vendorListItems = list;
        this.context = context;
        this.listener = listener;
        this.currentLocation = currentLocation;
//        this.vendorLocation = currentMarket;
//        this.vendorHours = vendorHours;
//        this.vendorDatesOpen = vendorDatesOpen;
//        this.currentLocation = currentLocation;
        this.currentMarket = currentMarket;
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
        holder.vendorListItemDescription.setText(item.getDescription());
        //holder.vendorListItemVendorLocation.setText(currentMarket.getAddress());

//        if (MarketUtils.isMarketCurrentlyOpen(currentMarket)){
//            holder.vendorListItemVendorStatus.setText("Open Now!");
//        }
//        else {
//            holder.vendorListItemVendorStatus.setText("Closed Now");
//        }
//
//        if (currentLocation != null){
//            float distance = MarketUtils.getDistanceFromMarket(currentMarket, currentLocation);
//            holder.vendorListItemVendorDistance.setText(LocationUtils.formatDistanceInKm(distance));
//        }
//        else {
//            holder.vendorListItemVendorDistance.setText("");
//        }
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
        public TextView vendorListItemDescription;
        public Button vendorListItemCallButton;
        //public TextView vendorListItemVendorLocation;
        //public TextView vendorListItemVendorStatus;
        //public TextView vendorListItemVendorDistance;
        public Context context;
        public List<Vendor> items;

        public ViewHolder(View itemView, Context context, List<Vendor> items) {
            super(itemView);
            this.context = context;
            this.items = items;
            this.vendorListItemDescription = (TextView) itemView.findViewById(R.id.vendor_list_item_description);
            this.vendorListItemCallButton = (Button) itemView.findViewById(R.id.vendor_list_item_call_button);
            //this.vendorListItemVendorDistance = (TextView) itemView.findViewById(R.id.vendor_list_item_distance);
            //this.vendorListItemVendorLocation = (TextView) itemView.findViewById(R.id.vendor_list_item_location);
            this.vendorListItemVendorName = (TextView) itemView.findViewById(R.id.vendor_list_item_name);
            //this.vendorListItemVendorStatus = (TextView) itemView.findViewById(R.id.vendor_list_item_status);
            vendorListItemCallButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.vendor_list_item_call_button:
                    String number = "7782978789";
                    Uri call = Uri.parse("tel:" + number);
                    Intent intent = new Intent(Intent.ACTION_DIAL, call);
                    context.startActivity(intent);
                    break;
                case R.id.vendor_list_item:
                    int position = getAdapterPosition();
                    Vendor vendor = vendorListItems.get(position);
                    listener.onVendorListItemClick(vendor.getName(), currentMarket);
                    break;
            }

        }
    }
}