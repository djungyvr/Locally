package com.example.djung.locally.Presenter;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.example.djung.locally.R;
import com.example.djung.locally.Utils.EnumTypes;
import com.example.djung.locally.View.Activities.VendorActivity;
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
                if(mView.getCurrentContentFragment() instanceof VendorDashboardFragment)
                    break;
                if(((VendorSaveView)mView.getCurrentContentFragment()).needSave()) {
                    mView.showSaveChangesDialog();
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
            mView.showSaveChangesDialog();
        } else {
            // don't need save so go to dashboard screen
            mView.launchVendorDashboardFragment();
        }
    }

    /**
     * Responds to user input from the save changes dialog
     * @param input - EnumType.SaveRequest
     */
    public void onSaveChangesDialogClick(EnumTypes.SaveRequest input) {
        switch(input) {
            case SAVE: // have to save our changes first before loading dashboard
                ((VendorSaveView) mView.getCurrentContentFragment()).saveChanges();
            case DONT_SAVE:
                mView.launchVendorDashboardFragment();
                break;
            case CANCEL:    // don't do anything in this case
                break;
        }
    }

    /**
     * Responds to user input frm sign out dialog
     * @param input - EnumType.DialogInput
     */
    public void onSignOutDialogClick(EnumTypes.DialogInput input) {
        if(input == EnumTypes.DialogInput.OK) {
            mView.signOut();
        }
        // else do nothing since they clicked cancelled
    }

}
