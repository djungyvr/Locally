package com.example.djung.locally.View.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.djung.locally.Presenter.InSeasonListPresenter;
import com.example.djung.locally.R;

/**
 * Created by AleSa on 26-Nov-16.
 */

public class InSeasonListAdapter extends RecyclerView.Adapter<InSeasonListAdapter.ViewHolder>{
    private InSeasonListPresenter mInSeasonListPresenter;

    public InSeasonListAdapter(InSeasonListPresenter mInSeasonListPresenter){
        this.mInSeasonListPresenter = mInSeasonListPresenter;
    }

    @Override
    public InSeasonListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_season_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(InSeasonListAdapter.ViewHolder holder, final int position) {
        String item = mInSeasonListPresenter.getProduceList().get(position);
        holder.inSeasonListItemName.setText(item);
    }

    @Override
    public int getItemCount() {
        if (mInSeasonListPresenter.getProduceList() != null){
            return mInSeasonListPresenter.getProduceList().size();
        }
        else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView inSeasonListItemName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.inSeasonListItemName = (TextView) itemView.findViewById(R.id.in_season_item);
        }
    }
}
