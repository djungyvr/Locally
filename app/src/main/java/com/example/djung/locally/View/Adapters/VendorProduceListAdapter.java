package com.example.djung.locally.View.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.djung.locally.R;

import java.util.List;

/**
 * Created by Andy Lin on 2016-11-11.
 */

public class VendorProduceListAdapter extends RecyclerView.Adapter<VendorProduceListAdapter.ViewHolder>{
    private List<String> produceList;


    public VendorProduceListAdapter (List<String> produceList){
        this.produceList = produceList;
    }

    @Override
    public VendorProduceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_details_produce_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(VendorProduceListAdapter.ViewHolder holder, int position) {
        String product = produceList.get(position);
        holder.produceListItemName.setText(product);
    }

    @Override
    public int getItemCount() {
        if (produceList != null){
            return produceList.size();
        }
        else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView produceListItemName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.produceListItemName = (TextView) itemView.findViewById(R.id.produce_list_item_name);
        }
    }
}
