package com.example.djung.locally.View.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Presenter.MarketListPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.DateUtils;

/**
 * Created by Andy Lin on 2016-11-08.
 */

public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.ViewHolder>{
    private MarketListPresenter marketListPresenter;

    public MarketListAdapter(MarketListPresenter marketListPresenter){
        this.marketListPresenter = marketListPresenter;
    }

    @Override
    public MarketListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_list_item, parent, false);
        MarketListAdapter.ViewHolder vh = new MarketListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MarketListAdapter.ViewHolder holder, final int position) {
        Market item = marketListPresenter.getMarketList().get(position);
        holder.marketListItemMarketName.setText(item.getName());
        holder.marketListItemMarketLocation.setText(item.getAddress());
        holder.marketListItemMarketHours.setText(DateUtils.parseHours(item.getDailyHours()));
        holder.marketListItemMarketDates.setText(DateUtils.parseYear(item.getYearOpen()));

        holder.marketListItemMarketStatus.setText(marketListPresenter.getStatus(position));
        holder.marketListItemMarketDistance.setText(marketListPresenter.getDistance(position));
        marketListPresenter.setImage(position, holder.marketListItemImage);

        holder.marketListItemNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marketListPresenter.onNotificationsButtonClick(position);
            }
        });

        holder.marketListItemSupportTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSupportText(holder);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marketListPresenter.onMarketListItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (marketListPresenter.getMarketList() != null){
            return marketListPresenter.getMarketList().size();
        }
        else {
            return 0;
        }
    }

    /**
     * Toggle the visibility of the support text
     */
    public void toggleSupportText(MarketListAdapter.ViewHolder view){
        if (view.marketListItemSupportText.getVisibility() == View.VISIBLE){
            view.marketListItemSupportText.setVisibility(View.GONE);
            view.marketListItemSupportTextButton.setText("More Details");
        }
        else {
            view. marketListItemSupportText.setVisibility(View.VISIBLE);
            view.marketListItemSupportTextButton.setText("Less Details");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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

        public ViewHolder(View itemView) {
            super(itemView);
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
        }
    }
}
