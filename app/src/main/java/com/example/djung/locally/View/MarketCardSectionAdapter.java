package com.example.djung.locally.View;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.djung.locally.R;
import com.example.djung.locally.Utils.MarketUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Jung on 16/10/16.
 */
public class MarketCardSectionAdapter extends RecyclerView.Adapter<MarketCardSectionAdapter.ItemRowHolder> {

    private ArrayList<MarketCardSection> dataList;
    private Context mContext;
    private Location currentLocation;

    public MarketCardSectionAdapter() {
    }

    public MarketCardSectionAdapter(Context context, ArrayList<MarketCardSection> dataList, Location currentLocation) {
        this.mContext = context;
        this.dataList = dataList;
        this.currentLocation = currentLocation;
    }

    @Override
    public MarketCardSectionAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_market_cards, null);
        ItemRowHolder rowHolder = new ItemRowHolder(view);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(MarketCardSectionAdapter.ItemRowHolder holder, int position) {
        final String sectionName = dataList.get(position).getSectionTitle();

        List singleSectionItems = dataList.get(position).getMarketList();

        singleSectionItems = MarketUtils.getClosestMarkets(singleSectionItems, currentLocation);

        ArrayList marketList = new ArrayList(singleSectionItems);

        holder.mItemTitle.setText(sectionName);

        MarketCardAdapter itemListDataAdapter = new MarketCardAdapter(mContext, marketList, currentLocation);

        holder.mRecyclerView.setHasFixedSize(true);
        holder.mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        holder.mRecyclerView.setAdapter(itemListDataAdapter);
    }

    @Override
    public int getItemCount() {
        return (dataList != null ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        protected TextView mItemTitle;
        protected RecyclerView mRecyclerView;

        public ItemRowHolder(View view) {
            super(view);

            this.mItemTitle = (TextView) view.findViewById(R.id.text_view_item_title);
            this.mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_list);
        }
    }
}
