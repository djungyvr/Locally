package com.example.djung.locally.Presenter;


import com.example.djung.locally.R;
import com.example.djung.locally.View.MainActivityView;

import java.lang.ref.WeakReference;

/**
 * Created by Angy Chung on 2016-11-28.
 */

public class MainActivityPresenter {
    private final static String TAG = "MainActivityPresenter";
    private MainActivityView mView;

    public MainActivityPresenter(MainActivityView view) {
        mView = view;
    }

    /**
     * Called when view is destroyed
     */
    public void onDestroy() {
        mView = null;
    }


    /**
     * Process user input i.e. back press
     * @return boolean if super.onBackPressed should be called
     */
    public boolean onBackPressed() {
        // close the navigation drawer if it's open
        if(mView.isNavigationDrawerOpen()) {
            mView.closeNavigationDrawer();
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Processes user input i.e. selecting an item from the navigation drawer
     * @param id
     */
    public void onNavigationItemSelected(int id) {
        switch (id) {
            case R.id.nav_home:
                mView.clearFragmentBackStack();
                mView.launchContentMainFragment();
                break;
            case R.id.nav_grocery_list:
                mView.launchGroceryFragment();
                break;
            case R.id.nav_map:
                mView.launchMapFragment();
                break;
            case R.id.market_list:
                mView.launchMarketFragment();
                break;
            case R.id.nav_manage:
                mView.launchSettingsFragment();
                break;
            case R.id.nav_use_as_vendor:
                mView.startLoginActivityIntent();
                break;
            case R.id.nav_calendar:
                mView.launchCalendarFragment();
                break;
        }
        mView.closeNavigationDrawer();
    }

}
