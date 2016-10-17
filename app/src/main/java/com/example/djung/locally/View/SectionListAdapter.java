package com.example.djung.locally.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.djung.locally.R;

import java.util.ArrayList;

/**
 * Created by David Jung on 16/10/16.
 */
public class SectionListAdapter extends RecyclerView.Adapter<SectionListAdapter.SingleItemRowHolder> {
    private ArrayList<MarketCardViewModel> marketsList;
    private Context mContext;

    SectionListAdapter(Context context, ArrayList<MarketCardViewModel> marketsList) {
        this.marketsList = marketsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_market_card_item,null);
        SingleItemRowHolder rowHolder = new SingleItemRowHolder(view);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int position) {
        MarketCardViewModel marketCardViewModel = marketsList.get(position);
        holder.mTitle.setText(marketCardViewModel.getMarketName());
    }

    @Override
    public int getItemCount() {
        return (marketsList != null ? marketsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {
        protected TextView mTitle;
        protected ImageView mImage;

        public SingleItemRowHolder(View view) {
            super(view);

            this.mTitle = (TextView) view.findViewById(R.id.text_view_market_name);
            this.mImage = (ImageView) view.findViewById(R.id.image_view_market_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), mTitle.getText(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
