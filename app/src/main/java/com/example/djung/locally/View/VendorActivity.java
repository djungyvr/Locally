package com.example.djung.locally.View;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class VendorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "VendorActivity";

    private AlertDialog userDialog;

    // View objects
    private TextView mTextViewVendorName;
    private VendorItemRecyclerView mRecyclerViewVendorItems;

    // Cognito user objects
    private CognitoUser user;
    private CognitoUserSession session;
    private CognitoUserDetails details;

    // User details
    private String username;
    private ProgressDialog waitDialog;
    private String marketName;
    private String vendorName;
    private Set<Integer> vendorItems;
    private Vendor currentVendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.vendor_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.vendor_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView =  navigationView.getHeaderView(0);
        mTextViewVendorName = (TextView)headerView.findViewById(R.id.text_view_nav_vendor_name);
        initialize();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.vendor_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_edit_goods_list) {

        } else if (id == R.id.nav_signout) {
            user.signOut();
            exit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.vendor_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *  Initialize this activity
      */
    private void initialize() {
        // Get the user name
        Bundle extras = getIntent().getExtras();
        username = AppHelper.getUser();
        user = AppHelper.getCognitoUserPool().getUser(username);
        getDetails();
    }

    /**
     * Get vendor details from CIP service
     */
    private void getDetails() {
        AppHelper.getCognitoUserPool().getUser(username).getDetailsInBackground(detailsHandler);
    }

    /**
     * Populate content_main with products the vendor carries
     */
    private void populateContentMain() {
        VendorPresenter vendorPresenter = new VendorPresenter(this);
        try {
            // Fetch the items
            Vendor vendor = vendorPresenter.fetchVendor(marketName,vendorName);
            // Check if we found a matching vendor in the database
            if(vendor != null) {
                currentVendor = vendor;
            } else {
                // Add the vendor to the database since we can't find it
                currentVendor = vendorPresenter.addVendor(marketName, vendorName);
            }
        }
        // IllegalArgumentException means we didn't find a vendor so we add it
        catch (final IllegalArgumentException | InterruptedException | ExecutionException findException) {
            try {
                // Add the vendor to the database since we can't find it
                currentVendor = vendorPresenter.addVendor(marketName, vendorName);
            } catch (final InterruptedException | ExecutionException exception) {
                showDialogMessage("Error Adding Vendor",exception.getMessage(),false);
            }
        }

        initializeAdapter();
    }

    private void initializeAdapter() {
        if(currentVendor != null) {
            mRecyclerViewVendorItems = (VendorItemRecyclerView) findViewById(R.id.recycler_view_vendor_items);

            mRecyclerViewVendorItems.setHasFixedSize(true);

            VendorItemAdapter adapter = new VendorItemAdapter(new ArrayList<>(currentVendor.getItemSet()), this);

            mRecyclerViewVendorItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            mRecyclerViewVendorItems.setAdapter(adapter);

            mRecyclerViewVendorItems.setEmptyView(findViewById(R.id.recycler_view_vendor_items_empty));
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
            vendorName = AppHelper.getUserDetails().getAttributes().getAttributes().get("custom:vendor_name");
            marketName = AppHelper.getUserDetails().getAttributes().getAttributes().get("custom:market_name");
            Log.e(TAG,"Details of : " + vendorName);
            if(vendorName != null && mTextViewVendorName != null) {
                mTextViewVendorName.setText(vendorName);
                populateContentMain();
            }
        }

        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();
            showDialogMessage("Could not fetch user details!", AppHelper.formatException(exception), true);
        }
    };

    /**
     * Populates the adapter with the vendor items
     */
    private void populateRecycler() {
        Set<String> itemIds = currentVendor.getItemSet();
    }

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