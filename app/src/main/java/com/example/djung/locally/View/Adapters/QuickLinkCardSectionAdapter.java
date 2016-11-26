package com.example.djung.locally.View.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.djung.locally.R;
import com.example.djung.locally.View.MainActivity;
import com.example.djung.locally.View.QuickLinkCard;
import com.example.djung.locally.View.SquareImageView;

import java.util.ArrayList;

/**
 * Created by Angy Chung on 2016-11-12.
 */

public class QuickLinkCardSectionAdapter extends
        RecyclerView.Adapter<QuickLinkCardSectionAdapter.ViewHolder>  {

    private ArrayList<QuickLinkCard> mDataset;
    private Context mContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public QuickLinkCardSectionAdapter(Context c, ArrayList<QuickLinkCard> myDataset) {
        mContext = c;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public QuickLinkCardSectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_quick_link, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final QuickLinkCard name = mDataset.get(position);
        holder.txtHeader.setText(mDataset.get(position).getHeading());
        holder.txtFooter.setText(mDataset.get(position).getSubheading());
        holder.imageView.setImageResource(mDataset.get(position).getImageId());
        holder.imageView.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;
        public SquareImageView imageView;

        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.thumbnail_heading);
            txtFooter = (TextView) v.findViewById(R.id.thumbnail_subheading);
            imageView = (SquareImageView) v.findViewById(R.id.thumbnail_image);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mContext instanceof MainActivity){
                int position = getAdapterPosition();
                switch(position) {
                    case 0: ((MainActivity) mContext).selectNavigationDrawer(R.id.market_list);
                        break;
                    case 1: ((MainActivity) mContext).selectNavigationDrawer(R.id.nav_calendar);
                        break;
                    case 2: break;
                    case 3: ((MainActivity) mContext).selectNavigationDrawer(R.id.nav_grocery_list);
                    default:
                        break;
                }
            }
        }
    }

}


