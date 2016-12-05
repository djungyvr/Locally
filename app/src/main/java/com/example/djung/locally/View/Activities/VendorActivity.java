package com.example.djung.locally.View.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.example.djung.locally.AWS.AppHelper;
import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Presenter.VendorActivityPresenter;
import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.View.Fragments.VendorEditDetailsFragment;
import com.example.djung.locally.View.Fragments.VendorDashboardFragment;
import com.example.djung.locally.View.Fragments.VendorEditStockFragment;
import com.example.djung.locally.View.Interfaces.VendorActivityView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Activity for vendors
 * <p>
 * To see how search for items was implemented see here:
 * https://github.com/android/platform_development/tree/master/samples/SearchableDictionary
 * <p>
 * https://www.youtube.com/watch?v=9OWmnYPX1uc
 */
public class VendorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VendorActivityView {

    private final String TAG = "VendorActivity";

    private AlertDialog userDialog;
    private Dialog waitDialog;

    // View objects
    private TextView mTextViewVendorName;

    // Cognito user objects
    private CognitoUser user;
    private CognitoUserSession session;
    private CognitoUserDetails details;

    // User details
    private String mMarketName;
    private String mVendorName;
    private String mVendorEmail;
    private String mVendorPhoneNumber;
    private Vendor mCurrentVendor;
    private String mUsername;

    // Fragment for editing vendor details
    private Fragment mContentVendor;
    private FragmentManager mFragmentManager;

    private AppBarLayout mAppBarLayout;
    private DrawerLayout mDrawerLayout;
    private VendorActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);

        if(mPresenter == null)
            mPresenter = new VendorActivityPresenter(this);
        initializeBaseViews();

        initialize();
        Log.e(TAG, "OnCreate");
    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroyView();
        super.onDestroy();
    }

    public void initializeBaseViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_vendor);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.vendor_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.vendor_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        mTextViewVendorName = (TextView) headerView.findViewById(R.id.text_view_nav_vendor_name);

    }

    public void setAppBarElevation(float elevation){
        if(mAppBarLayout == null) {
            mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout_vendor);
        }
        if(android.os.Build.VERSION.SDK_INT >= 21) {
//            Log.e(TAG, "Setting app bar elevation=" + elevation);
            mAppBarLayout.setStateListAnimator(null);
            mAppBarLayout.setElevation(elevation);
        } else {
            mAppBarLayout.setTargetElevation(0);
        }
    }

    /**
     * Sets the action bar title as the given string
     * @param title
     */
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * Checks if the navigation drawer is currently open
     * @return
     */
    @Override
    public boolean isNavigationDrawerOpen() {
        return (mDrawerLayout.isDrawerOpen(GravityCompat.START));
    }

    /**
     * Closes the navigation drawer
     */
    @Override
    public void closeNavigationDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        if (mContentVendor instanceof VendorEditStockFragment) {
            VendorEditStockFragment fragment = (VendorEditStockFragment) mContentVendor;
            fragment.handleIntent(intent);
        }
        else{
            super.onNewIntent(intent);
        }
    }


    @Override
    public void onBackPressed() {
        mPresenter.onBackPressed();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        mPresenter.onNavigationItemSelected(id);
        return true;
    }

    @Override
    public void launchVendorDashboardFragment() {
        mContentVendor = new VendorDashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putString("vendor_username", mUsername);
        bundle.putString("vendor_name", mCurrentVendor.getName());
        bundle.putString("market_name", mCurrentVendor.getMarketName());
        bundle.putString("vendor_photo_url", mCurrentVendor.getPhotoUrl());
        mContentVendor.setArguments(bundle);

        if(mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.content_vendor, mContentVendor).commit();
    }

    @Override
    public void launchEditGoodsFragment() {
        mContentVendor = new VendorEditStockFragment();
        Bundle bundle = new Bundle();
        bundle.putString("vendor_name", mCurrentVendor.getName());
        bundle.putString("market_name", mCurrentVendor.getMarketName());
        bundle.putStringArrayList("vendor_items", new ArrayList<>(mCurrentVendor.getItemSet()));
        mContentVendor.setArguments(bundle);

        if (mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();
        // Replace the container with the fragment
        mFragmentManager.beginTransaction().replace(R.id.content_vendor, mContentVendor).commit();
    }

    @Override
    public void launchEditDetailsFragment() {
        mContentVendor = new VendorEditDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("vendor_name", mCurrentVendor.getName());
        bundle.putString("market_name", mCurrentVendor.getMarketName());
        bundle.putString("vendor_description", mCurrentVendor.getDescription());
        bundle.putString("vendor_phone_number", mCurrentVendor.getPhoneNumber());
        bundle.putString("vendor_email", mCurrentVendor.getEmail());
        bundle.putString("vendor_photo_url", mCurrentVendor.getPhotoUrl());
        mContentVendor.setArguments(bundle);

        if (mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();
        // Replace the container with the fragment
        mFragmentManager.beginTransaction().replace(R.id.content_vendor, mContentVendor).commit();
    }

    @Override
    public Fragment getCurrentContentFragment() {
        return mContentVendor;
    }

    @Override
    public void signOut() {
        user.signOut();
        exit();
    }

    @Override
    public void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mPresenter.onSignOutDialogClick(true);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mPresenter.onSignOutDialogClick(false);
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.dialog_message_sign_out);
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDiscardChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mPresenter.onDiscardChangesDialogClick(true);
            }
        });
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mPresenter.onDiscardChangesDialogClick(false);
            }
        });
        // Set other dialog properties
        builder.setMessage(R.string.dialog_message_discard_changes);
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Initialize this activity
     */
    private void initialize() {
        // Get the user name
        mUsername = AppHelper.getUser();
        user = AppHelper.getCognitoUserPool().getUser(mUsername);
        getDetails();
    }

    /**
     * Get vendor details from CIP service
     */
    private void getDetails() {
        AppHelper.getCognitoUserPool().getUser(mUsername).getDetailsInBackground(detailsHandler);
    }

    /**
     * Populate content_main with products the vendor carries
     */
    private void populateContentMain() {
        VendorPresenter vendorPresenter = new VendorPresenter(this);
        try {
            // Fetch the items
            Vendor vendor = vendorPresenter.fetchVendor(mMarketName, mVendorName);
            // Check if we found a matching vendor in the database
            if (vendor != null) {
                mCurrentVendor = vendor;
            } else {
                // Add the vendor to the database since we can't find it
                mCurrentVendor = vendorPresenter.addVendor(mMarketName, mVendorName, mVendorEmail, mVendorPhoneNumber);
            }
        }
        // IllegalArgumentException means we didn't find a vendor so we add it
        catch (final IllegalArgumentException | InterruptedException | ExecutionException findException) {
            try {
                // Add the vendor to the database since we can't find it
                mCurrentVendor = vendorPresenter.addVendor(mMarketName, mVendorName, mVendorEmail, mVendorPhoneNumber);
            } catch (final InterruptedException | ExecutionException exception) {
                showDialogMessage("Error Adding Vendor", exception.getMessage(), false);
            }
        }
    }

    // Handler callbacks
    GetDetailsHandler detailsHandler = new GetDetailsHandler() {
        @Override
        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
            closeWaitDialog();
            // Store details in the AppHandler
            AppHelper.setUserDetails(cognitoUserDetails);
            // Change the nav header to vendor name
            mVendorName = AppHelper.getUserDetails().getAttributes().getAttributes().get("custom:vendor_name");
            mMarketName = AppHelper.getUserDetails().getAttributes().getAttributes().get("custom:market_name");
            mVendorEmail = AppHelper.getUserDetails().getAttributes().getAttributes().get("email");
            mVendorPhoneNumber = AppHelper.getUserDetails().getAttributes().getAttributes().get("custom:phone_number");
            Log.e(TAG, "Details of : " + mVendorName);
            if (mVendorName != null && mTextViewVendorName != null) {
                mTextViewVendorName.setText(mVendorName);
                populateContentMain();
                launchVendorDashboardFragment();
               // launchEditGoodsFragment();
            }
        }

        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();
            showDialogMessage("Could not fetch user details!", AppHelper.formatException(exception), true);
        }
    };

    // Dialog stuff
    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if (exit) {
                        exit();
                    }
                } catch (Exception e) {
                    // Log failure
                    Log.e(TAG, "Dialog dismiss failed");
                    if (exit) {
                        exit();
                    }
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        } catch (Exception e) {
            //
        }
    }

    private void exit() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
}