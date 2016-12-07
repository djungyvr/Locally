package com.example.djung.locally.Presenter;

import com.example.djung.locally.R;
import com.example.djung.locally.View.Fragments.VendorDashboardFragment;
import com.example.djung.locally.View.Fragments.VendorEditDetailsFragment;
import com.example.djung.locally.View.Fragments.VendorEditStockFragment;
import com.example.djung.locally.View.Interfaces.VendorActivityView;
import com.example.djung.locally.View.Interfaces.VendorSaveView;

/**
 * Created by Angy Chung on 2016-12-04.
 */

public class VendorActivityPresenter {

    private final static String TAG = "MainActivityPresenter";
    private VendorActivityView mView;

    /**
     * Constructor
     */
    public VendorActivityPresenter(VendorActivityView view) {
        mView = view;
    }

    /**
     * Called when view is destroyed
     */
    public void onDestroyView() {
        mView = null;
    }

    /**
     * Responds when user selects something in the navigation drawer
     * @param id
     */
    public void onNavigationItemSelected(int id) {
        switch (id) {
            case R.id.nav_vendor_home:
                mView.clearFragmentBackStack();
                mView.launchVendorDashboardFragment();
                if(mView.getCurrentContentFragment() instanceof VendorDashboardFragment)
                    break;
                if(((VendorSaveView)mView.getCurrentContentFragment()).needSave()) {
                    mView.showDiscardChangesDialog();
                    break;
                }
            case R.id.nav_edit_details:
                if(mView.getCurrentContentFragment() instanceof VendorEditDetailsFragment)
                    break;
                mView.launchEditDetailsFragment();
                break;
            case R.id.nav_edit_goods_list:
                if(mView.getCurrentContentFragment() instanceof VendorEditStockFragment)
                    break;
                mView.launchEditGoodsFragment();
                break;
            case R.id.nav_sign_out:
                mView.showSignOutDialog();
                break;
        }
        mView.closeNavigationDrawer();
    }

    /**
     * Process user input i.e. back press
     */
    public void onBackPressed() {
        if(mView.isNavigationDrawerOpen()) {
            // close the navigation drawer if it's open
            mView.closeNavigationDrawer();
        }
        else if((mView.getCurrentContentFragment() instanceof VendorDashboardFragment)) {
            // if it's dashboard screen then we try to sign out
            mView.showSignOutDialog();
        }
        else if (((VendorSaveView)mView.getCurrentContentFragment()).needSave()) {
            // otherwise it's the edit screen so we check if it needs to be saved
            mView.showDiscardChangesDialog();
        } else {
            // don't need save so go to dashboard screen
            mView.launchVendorDashboardFragment();
        }
    }

    /**
     * Responds to user input from the save changes dialog
     * @param discard - true if changes should be discarded, false if keep editing
     */
    public void onDiscardChangesDialogClick(boolean discard) {
        if(discard)
            mView.launchVendorDashboardFragment();
        // else do nothing
    }

    /**
     * Responds to user input frm sign out dialog
     * @param signout - true if should sign out, false if not
     */
    public void onSignOutDialogClick(boolean signout) {
        if(signout)
            mView.signOut();
        // else do nothing since they clicked cancelled
    }

}
