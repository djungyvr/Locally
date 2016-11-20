package com.example.djung.locally.View;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Presenter.MarketPresenter;
import com.example.djung.locally.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;

import static android.R.id.list;

/**
 * Created by Anna on 15.11.2016.
 */

public class CalendarFragment extends android.support.v4.app.Fragment{
    private onCalendarItemClick mCallback;
    private static final String TAG = "CalendarActivity";

    public interface onCalendarItemClick {
        public void onCalendarItemClick(Market market);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_calendar, container, false);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_fragment_calendar));
        ((MainActivity) getActivity()).setAppBarElevation(4);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        populateCalendarView();
    }

    public void populateCalendarView() {
        MarketPresenter helper= new MarketPresenter(this.getContext());
        final List<Market> markets;
        try {
            markets = helper.fetchMarkets();
            GridView gridview = (GridView) getView().findViewById(R.id.gridview);
            CalendarAdapter marketAdapter = new CalendarAdapter(this.getContext(),new ArrayList<Market>(markets));
            gridview.setAdapter(marketAdapter);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), MarketActivity.class);

                    intent.putExtra("M_ID",String.valueOf(position));
                    startActivity(intent);
                }
            });
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    public void onAttach (Context context) {
        super.onAttach(context);
        try {
            Activity activity = (Activity) context;
            mCallback = (CalendarFragment.onCalendarItemClick) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
