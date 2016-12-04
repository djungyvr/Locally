package com.example.djung.locally.View.Adapters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.djung.locally.Presenter.ContentMainPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.View.EnablePermissionsCard;
import com.example.djung.locally.View.MarketCardSection;
import com.example.djung.locally.View.QuickLinkCardSection;

import java.util.ArrayList;

/**
 * Created by David Jung on 16/10/16.
 */
public class ContentMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ContentMainPresenter mPresenter;
    private final int THUMBNAILS = 0, MARKETCARDSECTION = 1, REQUESTPERMISSIONS = 3;

    public ContentMainAdapter() {
    }

    /**
     * Constructor
     * @param presenter
     */
    public ContentMainAdapter(ContentMainPresenter presenter) {
        this.mPresenter = presenter;
    }

    /**
     * Returns the view type of the item at position for the purposes of view recycling.
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (mPresenter.getContentMainData(position) instanceof QuickLinkCardSection) {
            return THUMBNAILS;
        } else if (mPresenter.getContentMainData(position) instanceof MarketCardSection) {
            return MARKETCARDSECTION;
        }
        else if (mPresenter.getContentMainData(position) instanceof EnablePermissionsCard){
            return REQUESTPERMISSIONS;
        } else {
            return -1;
        }
    }

    /**
     * This method creates different RecyclerView.ViewHolder objects based on the item view type.\
     *
     * @param parent ViewGroup container for the item
     * @param viewType type of view to be inflated
     * @return viewHolder to be inflated
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch(viewType) {
            case THUMBNAILS:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_quick_link_cards, null);
                viewHolder = new ViewHolder1(v1);
                break;
            case MARKETCARDSECTION:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_market_cards, null);
                viewHolder = new ViewHolder2(v2);
                break;
            case REQUESTPERMISSIONS:
                View v3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_enable_permissions, null);
                viewHolder = new ViewHolder3(v3);
                break;
            default:
                View v = inflater.inflate(R.layout.simple_list_item_1, parent, false);
                viewHolder = new RecyclerViewSimpleTextViewHolder(v);
                break;
        }
        return viewHolder;
    }

    /**
     * This method internally calls onBindViewHolder(ViewHolder, int) to update the
     * RecyclerView.ViewHolder contents with the item at the given position
     * and also sets up some private fields to be used by RecyclerView.
     *
     * @param viewHolder The type of RecyclerView.ViewHolder to populate
     * @param position Item position in the viewgroup.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case THUMBNAILS:
                ViewHolder1 vh1 = (ViewHolder1) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case MARKETCARDSECTION:
                ViewHolder2 vh2 = (ViewHolder2) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            case REQUESTPERMISSIONS:
                ViewHolder3 vh3 = (ViewHolder3) viewHolder;
                configureViewHolder3(vh3, position);
                break;
            default:
                RecyclerViewSimpleTextViewHolder vh = (RecyclerViewSimpleTextViewHolder) viewHolder;
                configureDefaultViewHolder(vh, position);
                break;
        }
    }

    private void configureDefaultViewHolder(RecyclerViewSimpleTextViewHolder vh, int position) {
        vh.getLabel().setText((CharSequence) mPresenter.getContentMainData(position));
    }

    /**
     * Configure ViewHolder1 for the thumbnail images
     * @param holder
     * @param position
     */
    private void configureViewHolder1(ViewHolder1 holder, int position) {
        QuickLinkCardSection ts = (QuickLinkCardSection) mPresenter.getContentMainData(position);
        ArrayList sectionItems = ts.getThumbnailList();
        QuickLinkCardSectionAdapter adapter = new QuickLinkCardSectionAdapter(mPresenter.getActivityContext(), sectionItems);
        holder.mRecyclerView.setHasFixedSize(true);
        holder.mRecyclerView.setLayoutManager(new GridLayoutManager(mPresenter.getActivityContext(), 2));
        holder.mRecyclerView.setAdapter(adapter);
    }

    /**
     * Configure ViewHolder2 for the MarketCardSections
     * @param holder
     * @param position
     */
    private void configureViewHolder2(ViewHolder2 holder, int position) {
        MarketCardSection m = (MarketCardSection) mPresenter.getContentMainData(position);
        final String sectionName = m.getSectionTitle();

        ArrayList singleSectionItems = m.getMarketList();

        MarketCardAdapter itemListDataAdapter = new MarketCardAdapter(
                mPresenter.getActivityContext(), singleSectionItems, mPresenter.getCurrentLocation());
        holder.mItemTitle.setText(sectionName);
        holder.mRecyclerView.setHasFixedSize(true);
        holder.mRecyclerView.setLayoutManager(new GridLayoutManager(mPresenter.getActivityContext(), 2));
        holder.mRecyclerView.setAdapter(itemListDataAdapter);
    }

    /**
     * Configure ViewHolder3 for the thumbnail images
     * @param holder
     * @param position
     */
    private void configureViewHolder3(ViewHolder3 holder, int position) {
        EnablePermissionsCard card = (EnablePermissionsCard) mPresenter.getContentMainData(position);
        holder.mTextView.setText(card.getRationale());
    }


    @Override
    public int getItemCount() {
        return (mPresenter != null ? mPresenter.getContentMainDataSize() : 0);
    }

    // 2 Viewholder classes:
    /**
     * ViewHolder class for Thumbnails
     */
    public class ViewHolder1 extends RecyclerView.ViewHolder {
        protected RecyclerView mRecyclerView;

        public ViewHolder1(View view) {
            super(view);
            this.mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_list);
        }
    }

    /**
     * ViewHolder class for MarketCardSections
     */
    public class ViewHolder2 extends RecyclerView.ViewHolder {
        protected TextView mItemTitle;
        protected RecyclerView mRecyclerView;

        public ViewHolder2(View view) {
            super(view);

            this.mItemTitle = (TextView) view.findViewById(R.id.text_view_item_title);
            this.mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_list);
        }
    }

    /**
     * ViewHolder class for Enable Permissions rationale
     */
    public class ViewHolder3 extends RecyclerView.ViewHolder{
        protected TextView mTextView;

        public ViewHolder3(View view) {
            super(view);
            this.mTextView = (TextView) view.findViewById(R.id.rationale_text);
        }
    }

    /**
     * dummy backup viewholder class
     */
    public class RecyclerViewSimpleTextViewHolder extends RecyclerView.ViewHolder {

        private TextView label1;

        public RecyclerViewSimpleTextViewHolder(View v) {
            super(v);
            label1 = (TextView) v.findViewById(R.id.text1);
        }

        public TextView getLabel() {
            return label1;
        }

        public void setLabel1(TextView label1) {
            this.label1 = label1;
        }
    }

}
