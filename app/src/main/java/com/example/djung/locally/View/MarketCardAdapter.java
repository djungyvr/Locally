package com.example.djung.locally.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by David Jung on 16/10/16.
 */
public class MarketCardAdapter extends RecyclerView.Adapter<MarketCardAdapter.SingleItemRowHolder> {
    private ArrayList<Market> marketsList;
    private Context mContext;

    MarketCardAdapter(Context context, ArrayList<Market> marketsList) {
        this.marketsList = marketsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_card,null);
        SingleItemRowHolder rowHolder = new SingleItemRowHolder(view, marketsList);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int position) {
        Market market = marketsList.get(position);
        holder.mTitle.setText(market.getName());
        holder.mImage.setImageResource(R.drawable.ubc);
        holder.mDistance.setText("100");
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
                ((MainActivity)mContext).launchVendorListFragment(m.getName(), m.getAddress(), m.getDailyHours());
            }
        }
    }
}
