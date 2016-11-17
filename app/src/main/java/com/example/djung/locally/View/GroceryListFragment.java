package com.example.djung.locally.View;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.djung.locally.DB.GroceryListDatabase;
import com.example.djung.locally.DB.VendorItemDatabase;
import com.example.djung.locally.DB.VendorItemsProvider;
import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by David Jung on 15/11/16.
 */

public class GroceryListFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
    private final String TAG = "GroceryListFragment";

    // Should not be used in productions, simply for beta, reflects the market names in the db
    private final String[] marketNames = {
            "Trout Lake Farmers Market",
            "West End Farmers Market",
            "Hastings Park Winter Farmers Market",
            "Downtown Farmers Market",
            "Nat Bailey Stadium Winter Market",
            "Mount Pleasant Farmers Market",
            "UBC Farmers Market",
            "Kitsilano Farmers Market",
            "Main St Station Farmers Market"
    };

    private RecyclerView mRecyclerViewGroceryList;
    private SearchView mSearchView;
    private FloatingActionButton mSearchGroceryList;
    private Spinner mSpinnerMarketNames;

    private SuggestionAdapter mGroceryItemsSuggestionAdapter;
    private GroceryListAdapter mGroceryListAdapter;

    private GroceryListDatabase mGroceryListDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grocery_list_fragment, container, false);

        mGroceryListDatabase = new GroceryListDatabase(getContext());

        initializeViews(view);
        initializeSearch(view);
        initializeAdapter(view);

        return view;
    }

    @Override
    public void onPause() {
        // Each time we pause we update the database
        mGroceryListDatabase.clear();
        if (mGroceryListDatabase != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String item : mGroceryListAdapter.getItemNames()) {
                        mGroceryListDatabase.addGroceryItem(item);
                    }
                }
            }).start();
        }
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Search the vendors in the market that have the items
            case R.id.fab_search_grocery_list:
                Log.d(TAG, "Start searching");
                VendorPresenter vendorPresenter = new VendorPresenter(getContext());
                try {
                    List<String> items = vendorPresenter.lookupGroceryList(mSpinnerMarketNames.getSelectedItem().toString(), mGroceryListAdapter.getItemNames());
                    if(items.size() > 1) {
                        Toast.makeText(getContext(),"Found " + items.size() + " items",Toast.LENGTH_SHORT).show();
                    } else if(items.size() == 1) {
                        Toast.makeText(getContext(),"Found " + items.size() + " item",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(),"No items found",Toast.LENGTH_SHORT).show();
                    }
                    mGroceryListAdapter.setFound(items);
                    for(String item : items) {
                        Log.d(TAG,item);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG,e.getMessage());
                }
                break;
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
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        String vendorItem = mGroceryItemsSuggestionAdapter.getSuggestion(position);
        Log.e(TAG, "Selected suggestion: " + vendorItem);
        mGroceryListAdapter.addItem(vendorItem);
        return true;
    }

    private void initializeViews(View view) {
        mSearchGroceryList = (FloatingActionButton) view.findViewById(R.id.fab_search_grocery_list);
        mSearchGroceryList.setOnClickListener(this);
        mSpinnerMarketNames = (Spinner) view.findViewById(R.id.spinner_market_name);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                marketNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMarketNames.setAdapter(adapter);
    }

    private void initializeSearch(View view) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) view.findViewById(R.id.search_view_grocery_list_items);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
    }

    private void initializeAdapter(View view) {
        mRecyclerViewGroceryList = (RecyclerView) view.findViewById(R.id.recycler_view_grocery_list);

        mRecyclerViewGroceryList.setHasFixedSize(true);

        ArrayList<String> groceryList = new ArrayList<>();

        Cursor cursor = mGroceryListDatabase.getGroceryItems();

        // Iterate through the cursor and add the items
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor
                        .getColumnIndex(GroceryListDatabase.KEY_GROCERY_ITEM_NAME));

                groceryList.add(name);
                cursor.moveToNext();
            }
        }

        mGroceryListAdapter = new GroceryListAdapter(groceryList, getContext());

        mRecyclerViewGroceryList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        mRecyclerViewGroceryList.setAdapter(mGroceryListAdapter);
    }

    private void handleIntent(Intent intent) {
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
     * @param query The search query
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
            mGroceryItemsSuggestionAdapter = new SuggestionAdapter(getActivity(), cursor);

            mSearchView.setSuggestionsAdapter(mGroceryItemsSuggestionAdapter);
        }
    }
}
