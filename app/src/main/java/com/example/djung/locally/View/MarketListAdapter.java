package com.example.djung.locally.View;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.DateUtils;
import com.example.djung.locally.Utils.LocationUtils;
import com.example.djung.locally.Utils.MarketUtils;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Andy Lin on 2016-11-08.
 */

public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.ViewHolder>{
    private List<Market> marketListItems;
    private Context context;
    private MarketListFragment.onMarketListItemClick mCallBack;
    private Location currentLocation;

    public MarketListAdapter(List<Market> marketListItems, Context context, MarketListFragment.onMarketListItemClick mCallBack, Location currentLocation){
        this.marketListItems = marketListItems;
        this.context = context;
        this.mCallBack = mCallBack;
        this.currentLocation = currentLocation;
    }

    @Override
    public MarketListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_list_item, parent, false);
        MarketListAdapter.ViewHolder vh = new MarketListAdapter.ViewHolder(v, context, marketListItems);
        return vh;
    }

    @Override
    public void onBindViewHolder(MarketListAdapter.ViewHolder holder, int position) {
        Market item = marketListItems.get(position);
        holder.marketListItemMarketName.setText(item.getName());
        holder.marketListItemMarketHours.setText(DateUtils.parseHours(item.getDailyHours()));
        holder.marketListItemMarketLocation.setText(item.getAddress());
        holder.marketListItemMarketDates.setText(DateUtils.parseYear(item.getYearOpen()));

        if (MarketUtils.isMarketCurrentlyOpen(item)){
            holder.marketListItemMarketStatus.setText("Open Now!");
        }
        else {
            holder.marketListItemMarketStatus.setText("Closed Now!");
        }

        if (currentLocation != null){
            float distance = MarketUtils.getDistanceFromMarket(item, currentLocation);
            holder.marketListItemMarketDistance.setText(LocationUtils.formatDistanceInKm(distance));
        }
        else {
            holder.marketListItemMarketDistance.setText("");
        }
    }

    @Override
    public int getItemCount() {
        if (marketListItems != null){
            return marketListItems.size();
        }
        else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView marketListItemMarketName;
        public TextView marketListItemMarketLocation;
        public TextView marketListItemMarketHours;
        public TextView marketListItemMarketDistance;
        public TextView marketListItemMarketDates;
        public TextView marketListItemMarketStatus;
        public Context context;
        public List<Market> items;

        public ViewHolder(View itemView, Context context, List<Market> items) {
            super(itemView);
            this.context = context;
            this.items = items;
            this.marketListItemMarketName = (TextView) itemView.findViewById(R.id.market_list_item_name);
            this.marketListItemMarketLocation = (TextView) itemView.findViewById(R.id.market_list_item_location);
            this.marketListItemMarketHours = (TextView) itemView.findViewById(R.id.market_list_item_hours);
            this.marketListItemMarketDistance = (TextView) itemView.findViewById(R.id.market_list_item_distance);
            this.marketListItemMarketDates = (TextView) itemView.findViewById(R.id.market_list_item_open_dates);
            this.marketListItemMarketStatus = (TextView) itemView.findViewById(R.id.market_list_item_open_status);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Market market = marketListItems.get(position);
            mCallBack.onMarketListItemClick(market.getName(), market.getAddress(), market.getDailyHours());
        }
    }
}
