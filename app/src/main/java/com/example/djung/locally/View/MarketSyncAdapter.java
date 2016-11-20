package com.example.djung.locally.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adapter for markets and syncing them to the user's calendar
 *
 * Created by David Jung on 17/11/16.
 */

public class MarketSyncAdapter extends RecyclerView.Adapter<MarketSyncAdapter.ViewHolder>{
    private final String SYNCHED_MARKETS_KEY = "synced_markets";
    private List<Market> mMarkets;
    private Set<String> mSynchedMarketSet;
    private Context mContext;
    private SharedPreferences mSettings;
    private Callback mCallback;

    public interface Callback {
        void callback(Market market, boolean shouldAdd);
    }


    public MarketSyncAdapter(List<Market> markets, Context context, String syncPreferencesFile, Callback callback){
        mMarkets = markets;
        mContext = context;
        mSettings = context.getSharedPreferences(syncPreferencesFile,0);
        mSynchedMarketSet = new HashSet<>();
        mSynchedMarketSet = new HashSet<>(mSettings.getStringSet(SYNCHED_MARKETS_KEY,mSynchedMarketSet));
        mCallback = callback;
    }

    @Override
    public MarketSyncAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_sync_market_item, parent, false);
        MarketSyncAdapter.ViewHolder vh = new MarketSyncAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Market market = mMarkets.get(position);
        holder.mMarket = market;
        holder.mTextViewMarketName.setText(market.getName());
        holder.mSwitchMarketSync.setChecked(mSynchedMarketSet.contains(market.getName()));
    }

    @Override
    public int getItemCount() {
        return mMarkets != null ? mMarkets.size() : 0;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private final String TAG = "ViewHolder";
        protected TextView mTextViewMarketName;
        protected Switch mSwitchMarketSync;
        protected Market mMarket;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextViewMarketName = (TextView) itemView.findViewById(R.id.text_view_sync_market_item);
            mSwitchMarketSync = (Switch) itemView.findViewById(R.id.switch_sync_market_item);
            mSwitchMarketSync.setOnCheckedChangeListener(this);
        }

        /**
         * Triggered when the user switches the market sync switch
         */
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            Log.e(TAG, "Set to " + b + " at position " + getAdapterPosition());
            // If b is true sync the market with the calendar otherwise delete the events from the calendar
            if(compoundButton.isChecked()) {
                // Add the synced market to the set
                mSynchedMarketSet.add(mMarket.getName());
                mCallback.callback(mMarket,true);
            } else {
                // Remove the market from the set
                mSynchedMarketSet.remove(mMarket.getName());
                mCallback.callback(mMarket,false);
            }
            // Save the updated synched market sets
            mSettings.edit().putStringSet(SYNCHED_MARKETS_KEY,mSynchedMarketSet).commit();
        }
    }
}
