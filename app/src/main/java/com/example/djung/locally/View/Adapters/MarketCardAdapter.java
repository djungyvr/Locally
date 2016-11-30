package com.example.djung.locally.View.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.LocationUtils;
import com.example.djung.locally.Utils.MarketUtils;
import com.example.djung.locally.View.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by David Jung on 16/10/16.
 */
public class MarketCardAdapter extends RecyclerView.Adapter<MarketCardAdapter.SingleItemRowHolder> {
    private ArrayList<Market> marketsList;
    private Context mContext;
    private Location currentLocation;

    MarketCardAdapter(Context context, ArrayList<Market> marketsList, Location currentLocation) {
        this.marketsList = marketsList;
        this.mContext = context;
        this.currentLocation = currentLocation;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_market,null);
        SingleItemRowHolder rowHolder = new SingleItemRowHolder(view, marketsList);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int position) {
        // view all button
        if(position == 5) {
            String title = "View All";
            holder.mTitle.setText(title);
            holder.mTitle.setTextSize(18);
            holder.mDistance.setText("");
            holder.mImage.setImageResource(R.drawable.view_all_markets);
            holder.mImage.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        else {
            Market market = marketsList.get(position);
            holder.mTitle.setText(market.getName());
            String imageResource = MarketUtils.getMarketUrl(market.getName());
            if (imageResource.isEmpty()) {
                holder.mImage.setImageResource(R.drawable.ubc);
            } else {
                Picasso.with(mContext).setIndicatorsEnabled(true);
                Picasso.with(mContext).load(imageResource)
                        .error(R.drawable.default_market_image)
                        //.resize()
                        //.centerCrop()
                        .into(holder.mImage);
            }
            holder.mImage.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
            if (currentLocation != null) {
                float distance = MarketUtils.getDistanceFromMarket(market, currentLocation);
                holder.mDistance.setText(LocationUtils.formatDistanceInKm(distance));
            } else {
                holder.mDistance.setText("");
            }
        }
    }

    @Override
    public int getItemCount() {
        return (marketsList != null ? marketsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        protected TextView mTitle;
        protected TextView mDistance;
        protected ImageView mImage;
        protected ArrayList<Market> markets;

        public SingleItemRowHolder(View view, ArrayList<Market> markets) {
            super(view);
            this.markets = markets;
            this.mTitle = (TextView) view.findViewById(R.id.text_view_market_name);
            this.mDistance = (TextView) view.findViewById(R.id.text_view_market_distance);
            this.mImage = (ImageView) view.findViewById(R.id.image_view_market_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mContext instanceof MainActivity){
                Market m = markets.get(getAdapterPosition());
                if(m != null) {
                    ((MainActivity) mContext).launchVendorListFragment(m);
                } else {
                    ((MainActivity) mContext).launchMarketFragment();
                }
            }
        }
    }
}
