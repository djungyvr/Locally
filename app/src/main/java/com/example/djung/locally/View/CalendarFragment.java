package com.example.djung.locally.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Presenter.MarketPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.View.Adapters.CalendarAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Anna on 15.11.2016.
 */

public class CalendarFragment extends android.support.v4.app.Fragment{
    private onCalendarItemClick mCallback;
    private static final String TAG = "CalendarFragment";
    // Fragment for displaying calendar
    private Fragment mMarketPageFragment;
    private FragmentManager mFragmentManager;

    public interface onCalendarItemClick {
        public void onCalendarItemClick(Market market);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_fragment_calendar));
        ((MainActivity) getActivity()).setAppBarElevation(4);
        ((MainActivity) getActivity()).setNavigationDrawerCheckedItem(R.id.nav_calendar);
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
                    if (mMarketPageFragment == null)
                        mMarketPageFragment = new MarketPageFragment();
                    if (mFragmentManager == null)
                        mFragmentManager = getFragmentManager ();
                    //save market position index for sending to next fragment
                    Bundle marketIndex = new Bundle();
                    marketIndex.putInt("M_Index", position);
                    mMarketPageFragment.setArguments(marketIndex);

                    // Replace the container with the fragment
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.main_layout, mMarketPageFragment);
                    ft.addToBackStack(getString(R.string.title_fragment_market_description));
                    ft.commit();
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
