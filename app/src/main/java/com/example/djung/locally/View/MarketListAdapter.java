package com.example.djung.locally.View;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.DateUtils;
import com.example.djung.locally.Utils.LocationUtils;
import com.example.djung.locally.Utils.MarketUtils;
import com.squareup.picasso.Picasso;

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
            holder.marketListItemMarketStatus.setText("Closed Now");
        }

        if (currentLocation != null){
            float distance = MarketUtils.getDistanceFromMarket(item, currentLocation);
            holder.marketListItemMarketDistance.setText(LocationUtils.formatDistanceInKm(distance));
        }
        else {
            holder.marketListItemMarketDistance.setText("");
        }

        String imageResource = MarketUtils.getMarketUrl(item.getName());
        if(imageResource.isEmpty()) {
            holder.marketListItemImage.setImageResource(R.drawable.ubc);
        } else {
            Picasso.with(context).setIndicatorsEnabled(true);
            Picasso.with(context).load(imageResource).into(holder.marketListItemImage);
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
        public ImageView marketListItemImage;
        public TextView marketListItemMarketName;
        public TextView marketListItemMarketLocation;
        public TextView marketListItemMarketHours;
        public TextView marketListItemMarketDistance;
        public TextView marketListItemMarketDates;
        public TextView marketListItemMarketStatus;
        public Button marketListItemSupportTextButton;
        public Button marketListItemNotificationButton;
        public LinearLayout marketListItemSupportText;
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
            this.marketListItemImage = (ImageView) itemView.findViewById(R.id.market_list_item_image);
            this.marketListItemSupportTextButton = (Button) itemView.findViewById(R.id.market_list_item_details_button);
            this.marketListItemSupportText = (LinearLayout) itemView.findViewById(R.id.market_list_item_support_text);
            this.marketListItemNotificationButton = (Button) itemView.findViewById(R.id.market_list_item_notifications_button);
            this.marketListItemSupportTextButton.setOnClickListener(this);
            this.marketListItemNotificationButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.market_list_item_details_button:
                    toggleSupportText();
                    break;
                case R.id.market_list_item_notifications_button:
                    buildNotification();
                    break;
                case R.id.market_list_item:
                    int position = getAdapterPosition();
                    Market market = marketListItems.get(position);
                    mCallBack.onMarketListItemClick(market);
                    break;
            }
        }

        /**
         * Toggle the visibility of the support text
         */
        public void toggleSupportText(){
            if (marketListItemSupportText.getVisibility() == View.VISIBLE){
                marketListItemSupportText.setVisibility(View.GONE);
                marketListItemSupportTextButton.setText("More Details");
            }
            else {
                marketListItemSupportText.setVisibility(View.VISIBLE);
                marketListItemSupportTextButton.setText("Less Details");
            }
        }

        public void buildNotification(){
            int position = getAdapterPosition();
            Market market = marketListItems.get(position);
            Log.e("Market List Item", "Building market list item notification now");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.locally_launcher);

            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(market.getName() + " is about to open! Do not miss it!");
            bigText.setBigContentTitle("Market Notification");
            builder.setStyle(bigText);

            Notification notification = builder.build();
            NotificationManagerCompat.from(context).notify(0,notification);

        }
    }
}
