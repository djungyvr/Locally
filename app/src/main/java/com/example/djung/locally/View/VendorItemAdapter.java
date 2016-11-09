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
 * Adapter for displaying vendor items in a recycler view
 *
 * Created by David Jung on 07/11/16.
 */
public class VendorItemAdapter extends RecyclerView.Adapter<VendorItemAdapter.VendorItemViewHolder>{

    private ArrayList<String> itemNames;
    private Context context;

    VendorItemAdapter(ArrayList<String> itemNames, Context context) {
        this.itemNames = itemNames;
        this.context = context;
    }

    @Override
    public VendorItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_item,null);
        VendorItemViewHolder rowHolder = new VendorItemViewHolder(view);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(VendorItemViewHolder holder, int position) {
        // Set the text view
        holder.mVendorItemName.setText(itemNames.get(position));
    }

    @Override
    public int getItemCount() {
        return (itemNames != null ? itemNames.size() : 0);
    }

    public void addItem(String itemName) {
        itemNames.add(itemName);
        notifyItemChanged(itemNames.size() - 1);
    }

    public class VendorItemViewHolder extends RecyclerView.ViewHolder {
        protected TextView mVendorItemName;

        public VendorItemViewHolder(View view) {
            super(view);

            this.mVendorItemName = (TextView) view.findViewById(R.id.vendor_item_name);
        }
    }
}
