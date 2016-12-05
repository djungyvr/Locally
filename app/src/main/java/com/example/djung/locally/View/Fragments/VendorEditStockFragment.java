package com.example.djung.locally.View.Fragments;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.djung.locally.DB.VendorItemDatabase;
import com.example.djung.locally.DB.VendorItemsProvider;
import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.VendorUtils;
import com.example.djung.locally.View.Activities.VendorActivity;
import com.example.djung.locally.View.Adapters.SuggestionAdapter;
import com.example.djung.locally.View.Adapters.VendorItemAdapter;
import com.example.djung.locally.View.VendorItemRecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Contains the default fragment for vendor side of the app
 *
 * Created by David Jung on 29/11/16.
 */

public class VendorEditStockFragment extends Fragment implements View.OnClickListener,
        SearchView.OnQueryTextListener, SearchView.OnSuggestionListener{
    private static final String TAG = "VendorEditStockFragment";
    //TODO COMPLETE CLASS
    // Dialogs
    private Dialog mVendorSaveDialog;
    private Dialog mWaitDialog;

    // Views inside the fragment
    private RecyclerView mRecyclerViewVendorItems;
    private FloatingActionButton mFabSaveList;
    private VendorItemAdapter mVendorItemAdapter;
    private SearchView mSearchView;
    private SuggestionAdapter mVendorItemsSuggestionAdapter;

    // Vendor attributes that are displayed or needed
    private List<String> mVendorItemSet;
    private String mMarketName;
    private String mVendorName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vendor_edit_stock_fragment, container, false);
        Bundle bundle = this.getArguments();

        if(bundle != null) {
            mVendorItemSet = bundle.getStringArrayList("vendor_items");
            mVendorName = bundle.getString("vendor_name");
            mMarketName = bundle.getString("market_name");
            initializeFab(view);
            initializeSearch(view);
            initializeAdapter(view);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        ((VendorActivity) getActivity()).setActionBarTitle("Items You're Selling");
        ((VendorActivity) getActivity()).setAppBarElevation(0);
    }

    private void initializeFab(View view) {
        mFabSaveList = (FloatingActionButton) view.findViewById(R.id.fab_save_vendor_edit_stock);
        mFabSaveList.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_save_vendor_edit_stock:
                VendorPresenter vendorPresenter = new VendorPresenter(getContext());
                try {
                    vendorPresenter.updateVendorProducts(mMarketName, mVendorName,new HashSet<String>(mVendorItemAdapter.getItemNames()));
                    Toast.makeText(getContext(),"Updating list",Toast.LENGTH_SHORT).show();
                } catch (ExecutionException | InterruptedException e) {
                    showDialogMessage("Save Error", "Failed to save item list");
                    Log.e(TAG,e.getMessage());
                }
                break;
        }
    }

    private void initializeSearch(View view) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) view.findViewById(R.id.search_view);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
        mSearchView.setQueryHint("Search for items");
    }

    public void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Log.e(TAG, intent.getAction());
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

    /**
     * Searches the vendor items and displays results for the given query.
     *
     * @param query The search queryx
     */
    private void showResults(String query) {

        Cursor cursor = getActivity().managedQuery(VendorItemsProvider.CONTENT_URI, null, null,
                new String[]{query}, null);

        if (cursor == null) {
        } else {
            // Specify the columns we want to display in the result
            String[] from = new String[]{VendorItemDatabase.KEY_VENDOR_ITEM_NAME,
                    VendorItemDatabase.KEY_VENDOR_ITEM_INFO};

            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[]{R.id.vendor_item_name_search_result,
                    R.id.vendor_item_info_search_result};

            // Create a simple cursor adapter for vendor
            mVendorItemsSuggestionAdapter = new SuggestionAdapter(getActivity(), cursor);

            mSearchView.setSuggestionsAdapter(mVendorItemsSuggestionAdapter);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        showResults(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        showResults(newText);
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return true;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        String vendorItem = mVendorItemsSuggestionAdapter.getSuggestion(position);
        Log.e(TAG, "Selected suggestion: " + vendorItem);
        mVendorItemAdapter.addItem(vendorItem);
        mSearchView.setQuery("", false);
        mSearchView.clearFocus();
        return true;
    }

    /**
     * Initialize the adapter/recyclerview
     */
    private void initializeAdapter(View view) {
        if(mVendorItemSet != null) {
            mRecyclerViewVendorItems = (RecyclerView) view.findViewById(R.id.recycler_view_vendor_edit_stock);
            mRecyclerViewVendorItems.setHasFixedSize(true);
            ArrayList<String> filteredList = VendorUtils.filterPlaceholderText(mVendorItemSet);
            mVendorItemAdapter = new VendorItemAdapter(filteredList, getContext());
            mRecyclerViewVendorItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            mRecyclerViewVendorItems.setAdapter(mVendorItemAdapter);
        }
    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    mVendorSaveDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        mVendorSaveDialog = builder.create();
        mVendorSaveDialog.show();
    }
}
