package com.example.djung.locally.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.djung.locally.Model.Product;
import com.example.djung.locally.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Andy Lin on 2016-11-11.
 */

public class VendorProduceListAdapter extends RecyclerView.Adapter<VendorProduceListAdapter.ViewHolder>{
    private List<String> produceList;
    private Context context;


    public VendorProduceListAdapter (List<String> produceList, Context context){
        this.produceList = produceList;
        this.context = context;
    }

    @Override
    public VendorProduceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_details_produce_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v, context, produceList);
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
        private Context context;
        private List<String> productList;
        private TextView produceListItemName;

        public ViewHolder(View itemView, Context  context, List<String> produceList) {
            super(itemView);
            this.context = context;
            this.productList = produceList;
            this.produceListItemName = (TextView) itemView.findViewById(R.id.produce_list_item_name);
        }
    }
}
