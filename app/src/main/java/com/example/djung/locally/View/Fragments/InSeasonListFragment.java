package com.example.djung.locally.View.Fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.djung.locally.Presenter.InSeasonListPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.DateUtils;
import com.example.djung.locally.View.Activities.MainActivity;
import com.example.djung.locally.View.Adapters.InSeasonListAdapter;
import com.example.djung.locally.View.Interfaces.InSeasonListView;


/**
 * Created by AleSa on 26-Nov-16.
 */

public class InSeasonListFragment extends Fragment implements InSeasonListView , AdapterView.OnItemSelectedListener{
    private final String TAG = "InSeasonListFragment";

    private InSeasonListPresenter mInSeasonListPresenter;
    private MainActivity mMainActivity;
    private RecyclerView mRecyclerView;
    private Spinner mSpinnerSeasons;

    @Override
    public void setActionBarTitle(String title) {
        mMainActivity.setActionBarTitle(title);
        mMainActivity.setAppBarElevation(4);
    }

    @Override
    public void showInSeasonList(InSeasonListAdapter inSeasonListAdapter) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(inSeasonListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.in_season_list, container, false);
        mInSeasonListPresenter = new InSeasonListPresenter(getActivity(), this);
        mMainActivity = ((MainActivity) getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.in_season);

        mSpinnerSeasons = (Spinner) view.findViewById(R.id.spinner_season);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                DateUtils.seasons
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSeasons.setAdapter(adapter);
        mSpinnerSeasons.setOnItemSelectedListener(this);
        mSpinnerSeasons.setSelection(DateUtils.getCurrentSeason());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        mInSeasonListPresenter.setActionBar();
    }

    /**
     * Called whenever a season is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String query = DateUtils.seasons[position].toLowerCase();
        if(position == 0) {
            query = "allseason";
        }
        mInSeasonListPresenter.populateInSeasonList(query);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Nothing happens
    }
}
