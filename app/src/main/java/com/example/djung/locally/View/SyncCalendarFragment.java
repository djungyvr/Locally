package com.example.djung.locally.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment that handles syncing market schedules with your Google Calendar
 *
 * Created by David Jung on 17/11/16.
 */

public class SyncCalendarFragment extends Fragment{
    private final String PREFS_NAME = "SyncCalenderPrefs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_sync_fragment, container, false);

        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_fragment_settings));
        ((MainActivity) getActivity()).setAppBarElevation(4);

        Object[] serializedMarkets = (Object[]) getArguments().getSerializable("list_markets");

        List<Market> markets = new ArrayList<>();

        for(Object serializedMarket : serializedMarkets) {
            markets.add((Market)serializedMarket);
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_sync_markets);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        MarketSyncAdapter adapter = new MarketSyncAdapter(markets,getContext(),PREFS_NAME);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
