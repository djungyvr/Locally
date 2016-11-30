package com.example.djung.locally.View;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.djung.locally.Presenter.MarketListPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.View.Adapters.MarketListAdapter;

/**
 * Created by Andy Lin on 2016-11-08.
 */

public class MarketListFragment extends android.support.v4.app.Fragment implements MarketListView{
    private MarketListPresenter marketListPresenter;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;

    @Override
    public void setActionBarTitle(String title) {
        mainActivity.setActionBarTitle(title);
        mainActivity.setAppBarElevation(4);
    }

    @Override
    public void setNavDrawerSelectedItem(int resID) {
        mainActivity.setNavigationDrawerCheckedItem(resID);
    }

    @Override
    public void showMarketList(MarketListAdapter marketListAdapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(marketListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.market_list, container, false);
        marketListPresenter = new MarketListPresenter(getActivity(), this);
        this.mainActivity = (MainActivity) getActivity();
        this.recyclerView = (RecyclerView) view.findViewById(R.id.market_list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        marketListPresenter.setActionBar();
        marketListPresenter.setNavDrawerSelectedItem();
        marketListPresenter.getUserLocation();
        marketListPresenter.populateMarketList();
    }


}
