package com.example.djung.locally.View;

import android.content.Context;

/**
 * Created by Angy Chung on 2016-11-28.
 */

public interface MainActivityView {
    Context getActivityContext();
    void setActionBarTitle(String title);
    boolean isNavigationDrawerOpen();
    void closeNavigationDrawer();
    void setAppBarElevation(float elevation);
    void launchContentMainFragment();
    void launchMapFragment();
    void launchMarketFragment();
    void launchSettingsFragment();
    void launchAboutUsFragment();
    void launchGroceryFragment();
    void launchCalendarFragment();
    void startLoginActivityIntent();
    void clearFragmentBackStack();
}
