package com.example.djung.locally.View.Adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Presenter.MarketPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.LocationUtils;
import com.example.djung.locally.Utils.MarketUtils;
import com.example.djung.locally.View.VendorSearchItemFragment;

/**
 * Created by Angy Chung on 2016-11-18.
 */
public class VendorSearchItemAdapter extends RecyclerView.Adapter<VendorSearchItemAdapter.ViewHolder> {
    private ArrayList<Vendor> mVendorsList;
    private Location mCurrentLocation;
    private Context mContext;
    private VendorSearchItemFragment.OnVendorListItemClickListener mListener;
    private static HashMap<String, Integer> marketIds;
    private static String TAG = "VendorSearchAdapter";

    // Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView vendorListItemVendorName;
        private TextView vendorListItemVendorLocation;
        private TextView vendorListItemVendorStatus;
        private TextView vendorListItemVendorDistance;
        private TextView vendorListItemVendorMarketName;
        private Market vendorMarket;

        public ViewHolder(View itemView) {
            super(itemView);
            this.vendorListItemVendorDistance = (TextView) itemView.findViewById(R.id.vendor_list_item_distance);
            this.vendorListItemVendorLocation = (TextView) itemView.findViewById(R.id.vendor_list_item_location);
            this.vendorListItemVendorName = (TextView) itemView.findViewById(R.id.vendor_list_item_name);
            this.vendorListItemVendorStatus = (TextView) itemView.findViewById(R.id.vendor_list_item_status);
            this.vendorListItemVendorMarketName = (TextView) itemView.findViewById(R.id.vendor_list_item_market);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Vendor vendor = mVendorsList.get(position);
            if(vendorMarket != null && mListener!=null)
                mListener.onVendorListItemClick(vendor.getName(), vendorMarket);
        }
    }

    // constructor
    public VendorSearchItemAdapter(ArrayList<Vendor> vendors, Location location, Context context,
                                   VendorSearchItemFragment.OnVendorListItemClickListener listener) {
        mVendorsList = vendors;
        mCurrentLocation = location;
        mContext = context;
        mListener = listener;
        if(marketIds == null) {
            marketIds = new HashMap<>();
            marketIds.put("West End Farmers Market", 8);
            marketIds.put("Trout Lake Farmers Market", 7);
            marketIds.put("Mount Pleasant Farmers Market", 6);
            marketIds.put("Main St Station Farmers Market", 5);
            marketIds.put("Nat Bailey Stadium Winter Market", 4);
            marketIds.put("Hastings Park Winter Farmers Market", 3);
            marketIds.put("Downtown Farmers Market", 2);
            marketIds.put("UBC Farmers Market", 1);
            marketIds.put("Kitsilano Farmers Market", 0);
        }
    }


    @Override
    public VendorSearchItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_search_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Vendor item = mVendorsList.get(position);
        holder.vendorListItemVendorName.setText(item.getName());
        holder.vendorListItemVendorMarketName.setText(item.getMarketName());

        // get market of the vendor
        MarketPresenter presenter = new MarketPresenter(mContext);
        Market currentMarket = null;
        try{
            currentMarket = presenter.fetchMarket(item.getMarketName());
        } catch (final ExecutionException | InterruptedException e) {
            Log.e("VendorSearchAdapter", e.getMessage());
        }

        if (currentMarket != null) {
            holder.vendorMarket = currentMarket;
            holder.vendorListItemVendorLocation.setText(currentMarket.getAddress());
            if (MarketUtils.isMarketCurrentlyOpen(currentMarket)){
                holder.vendorListItemVendorStatus.setText("Open Now!");
            }
            else {
                holder.vendorListItemVendorStatus.setText("Closed Now!");
            }
            if (mCurrentLocation != null){
                float distance = MarketUtils.getDistanceFromMarket(currentMarket, mCurrentLocation);
                holder.vendorListItemVendorDistance.setText(LocationUtils.formatDistanceInKm(distance));
            }
            else {
                holder.vendorListItemVendorDistance.setText("");
            }
        } else {
            holder.vendorListItemVendorStatus.setText("");
            holder.vendorListItemVendorLocation.setText("");
            holder.vendorListItemVendorDistance.setText("");
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mVendorsList.size();
    }

}