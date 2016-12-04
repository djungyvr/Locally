package com.example.djung.locally.View.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.djung.locally.Presenter.ContentMainPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.View.Activities.MainActivity;
import com.example.djung.locally.View.Adapters.ContentMainAdapter;
import com.example.djung.locally.View.Adapters.SuggestionAdapter;
import com.example.djung.locally.View.Interfaces.ContentMainView;

/**
 * Created by Angy Chung on 2016-11-27.
 */

public class ContentMainFragment extends android.support.v4.app.Fragment implements ContentMainView,
        SearchView.OnQueryTextListener, SearchView.OnSuggestionListener{
    private ContentMainPresenter mPresenter;
    private MainActivity mMainActivity;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;

    @Override
    public void setActionBarTitle(String title) {
        mMainActivity.setActionBarTitle(title);
        mMainActivity.setAppBarElevation(0);
    }

    @Override
    public void setNavigationDrawer(int id) {
        mMainActivity.setNavigationDrawerCheckedItem(id);
    }

    @Override
    public void showContentMain(ContentMainAdapter adapter) {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivity, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showSearchSuggestions(SuggestionAdapter adapter) {
        mSearchView.setSuggestionsAdapter(adapter);
    }

    @Override
    public void clearSearchFocus() {
        mSearchView.clearFocus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main_fragment, container, false);
        mPresenter = new ContentMainPresenter(getActivity(), this);
        this.mMainActivity = (MainActivity) getActivity();
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.content_main_recycler_view);
        this.mSearchView = (SearchView) view.findViewById(R.id.search_view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        initializeSearchView();
        mPresenter.setActionBar();
        mPresenter.getUserLocation();
        mPresenter.populateContentMain();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onDestroyView();
        mMainActivity = null;
        mRecyclerView = null;
        mSearchView = null;
    }

    private void initializeSearchView() {
        SearchManager searchManager = (SearchManager) mMainActivity.getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(mMainActivity.getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
        mSearchView.setQueryHint("Search locally for:");
        mSearchView.clearFocus();
    }

    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            mPresenter.showResults(query);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        mPresenter.showResults(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mPresenter.showResults(newText);
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return true;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        mPresenter.onSuggestionClick(position);
        return true;
    }
}
