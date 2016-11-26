package com.example.djung.locally.View.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.djung.locally.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by djung on 15/11/16.
 */

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.GroceryListItemViewHolder>{

    private ArrayList<String> itemNames;
    private Context context;
    private List<String> foundItems;

    public GroceryListAdapter(ArrayList<String> itemNames, Context context) {
        this.itemNames = itemNames;
        this.context = context;
    }

    @Override
    public GroceryListAdapter.GroceryListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_list_item,parent,false);
        GroceryListAdapter.GroceryListItemViewHolder rowHolder = new GroceryListAdapter.GroceryListItemViewHolder(view);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(GroceryListAdapter.GroceryListItemViewHolder holder, int position) {
        // Set the text view
        holder.mGroceryListItemName.setText(itemNames.get(position));

        // Handle setting the text colour
        if(foundItems != null) {
            if(foundItems.contains(itemNames.get(position))) {
                holder.mGroceryListItemName.setTextColor(context.getResources().getColor(R.color.success));
            } else {
                holder.mGroceryListItemName.setTextColor(context.getResources().getColor(R.color.black));
            }
        }
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
        // Only add the item if its unique
        if(!itemNames.contains(itemName))
            itemNames.add(itemName);
        notifyDataSetChanged();
    }

    public List<String> getItemNames() {
        return itemNames;
    }

    public void setFound(List<String> foundItems) {
        this.foundItems = foundItems;
        notifyDataSetChanged();
    }

    public class GroceryListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView mGroceryListItemName;
        protected Button mButtonDelete;

        public GroceryListItemViewHolder(View view) {
            super(view);
            this.mButtonDelete = (Button) view.findViewById(R.id.button_grocery_list_item_delete);
            this.mButtonDelete.setOnClickListener(this);
            this.mGroceryListItemName = (TextView) view.findViewById(R.id.grocery_list_item_name);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_grocery_list_item_delete:
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
