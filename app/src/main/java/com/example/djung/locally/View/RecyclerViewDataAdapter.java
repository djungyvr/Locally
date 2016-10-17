package com.example.djung.locally.View;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.djung.locally.R;

import java.util.ArrayList;

/**
 * Created by David Jung on 16/10/16.
 */
public class RecyclerViewDataAdapter extends RecyclerView.Adapter<RecyclerViewDataAdapter.ItemRowHolder> {

    private ArrayList<MarketCardViewModelSection> dataList;
    private Context mContext;

    public RecyclerViewDataAdapter() {
    }

    public RecyclerViewDataAdapter(Context context, ArrayList<MarketCardViewModelSection> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }

    @Override
    public RecyclerViewDataAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
        ItemRowHolder rowHolder = new ItemRowHolder(view);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewDataAdapter.ItemRowHolder holder, int position) {
        final String sectionName = dataList.get(position).getSectionTitle();

        ArrayList singleSectionItems = dataList.get(position).getMarketCardViewModelArrayList();

        holder.mItemTitle.setText(sectionName);

        SectionListAdapter itemListDataAdapter = new SectionListAdapter(mContext,singleSectionItems);

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
