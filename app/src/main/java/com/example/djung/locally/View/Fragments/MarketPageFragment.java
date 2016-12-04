package com.example.djung.locally.View.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Presenter.MarketPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.View.Activities.MainActivity;
import com.example.djung.locally.View.Adapters.MarketPageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Anna on 19.11.2016.
 */

public class MarketPageFragment extends android.support.v4.app.Fragment{
    private MarketPageFragment.onMarketPageItemClick mCallback;
    private static final String TAG = "onMarketPageActivity";

    public interface onMarketPageItemClick {
        public void onMarketPageItemClick(Market market);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_market_fragment, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_fragment_market_description));
        ((MainActivity) getActivity()).setAppBarElevation(4);

        Market market = (Market) getArguments().getSerializable("currentMarket");
        ArrayList<Market> displayMarket = new ArrayList<>();
        displayMarket.add(market);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.market_page);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        MarketPageAdapter marketAdapter = new MarketPageAdapter(displayMarket,getContext());
        recyclerView.setAdapter(marketAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //populateMarketPageView();
    }

    public void populateMarketPageView() {
        MarketPresenter helper= new MarketPresenter(this.getContext());
        final List<Market> markets;
        ArrayList<Market> displayMarket=new ArrayList<>();

        Bundle marketData = getArguments();
        int marketIndex = marketData.getInt("M_Index");

        try {
            markets = helper.fetchMarkets();
            displayMarket.add(markets.get(marketIndex));
            RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.market_page);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            MarketPageAdapter marketAdapter = new MarketPageAdapter(new ArrayList<Market>(displayMarket),this.getContext());
            recyclerView.setAdapter(marketAdapter);
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    public void onAttach (Context context) {
        super.onAttach(context);
        try {
            Activity activity = (Activity) context;
            mCallback = (MarketPageFragment.onMarketPageItemClick) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
