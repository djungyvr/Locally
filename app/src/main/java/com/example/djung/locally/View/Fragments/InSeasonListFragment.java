package com.example.djung.locally.View.Fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.djung.locally.Presenter.InSeasonListPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.View.Activities.MainActivity;
import com.example.djung.locally.View.Adapters.InSeasonListAdapter;
import com.example.djung.locally.View.Interfaces.InSeasonListView;


/**
 * Created by AleSa on 26-Nov-16.
 */

public class InSeasonListFragment extends Fragment implements InSeasonListView {
    private final String TAG = "InSeasonListFragment";

    private InSeasonListPresenter mInSeasonListPresenter;
    private MainActivity mMainActivity;
    private RecyclerView mRecyclerView;

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
        this.mMainActivity = ((MainActivity) getActivity());
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.in_season);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        mInSeasonListPresenter.setActionBar();
        mInSeasonListPresenter.populateInSeasonList();
    }
}
