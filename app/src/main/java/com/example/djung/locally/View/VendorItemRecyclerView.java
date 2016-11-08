package com.example.djung.locally.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.example.djung.locally.Model.Vendor;

import org.w3c.dom.Attr;

/**
 * RecyclerView subclass that supports providing an empty view, displayed when the adapter has no
 * data
 *
 * Created by David Jung on 07/11/16.
 */
public class VendorItemRecyclerView extends android.support.v7.widget.RecyclerView {
    private View mEmptyView;

    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
        }
    };

    public VendorItemRecyclerView(Context context) {
        super(context);
    }

    public VendorItemRecyclerView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
    }

    public VendorItemRecyclerView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context,attributeSet,defStyle);
    }

    /**
     * Designate a view as the empty view
     * @param emptyView the empty view to be shown when there is no data
     */
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(mDataObserver);
        }
        if(adapter != null) {
            adapter.registerAdapterDataObserver(mDataObserver);
        }
        super.setAdapter(adapter);
        updateEmptyView();
    }

    private void updateEmptyView() {
        if(mEmptyView != null && getAdapter() != null) {
            boolean showEmptyView = getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(showEmptyView ? VISIBLE : GONE);
            setVisibility(showEmptyView ? GONE : VISIBLE);
        }
    }
}
