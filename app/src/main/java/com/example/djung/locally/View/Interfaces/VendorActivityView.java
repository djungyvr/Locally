package com.example.djung.locally.View.Interfaces;

import android.support.v4.app.Fragment;

/**
 * Created by Angy Chung on 2016-12-04.
 */

public interface VendorActivityView {
    boolean isNavigationDrawerOpen();
    void closeNavigationDrawer();
    Fragment getCurrentContentFragment();
    void signOut();
    void showSignOutDialog();
    void showDiscardChangesDialog();
    void launchVendorDashboardFragment();
    void launchEditGoodsFragment();
    void launchEditDetailsFragment();
    void clearFragmentBackStack();
    void setNavigationDrawerCheckedItem(int resId);
}
