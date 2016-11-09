package com.example.djung.locally.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.djung.locally.R;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Adds an item to the adapter if it is not already in there
     * @param itemName name of the item to add
     */
    public void addItem(String itemName) {
        // Only add the item if it unique
        if(!itemNames.contains(itemName))
            itemNames.add(itemName);
        notifyDataSetChanged();
    }

    public List<String> getItemNames() {
        return itemNames;
    }

    public class VendorItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView mVendorItemName;
        protected Button mButtonDelete;

        public VendorItemViewHolder(View view) {
            super(view);
            this.mButtonDelete = (Button) view.findViewById(R.id.button_vendor_item_delete);
            this.mButtonDelete.setOnClickListener(this);
            this.mVendorItemName = (TextView) view.findViewById(R.id.vendor_item_name);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_vendor_item_delete:
                    removeAt(getAdapterPosition());
                    break;
            }
        }

        public void removeAt(int position) {
            itemNames.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemNames.size());
        }
    }
}
