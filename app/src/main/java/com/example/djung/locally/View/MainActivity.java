package com.example.djung.locally.View;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Presenter.MainActivityPresenter;
import com.example.djung.locally.Presenter.MarketListPresenter;
import com.example.djung.locally.Presenter.MarketPresenter;
import com.example.djung.locally.Presenter.VendorListPresenter;
import com.example.djung.locally.R;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VendorListPresenter.OnVendorListItemClickListener,
        MarketListPresenter.onMarketListItemClick, VendorSearchItemFragment.OnVendorListItemClickListener,
        MainActivityView {

    private final String TAG = "MainActivity";
    private MainActivityPresenter mPresenter;
    private Location currentLocation;
    private LocationManager locationManager;

    // Fragment for displaying maps
    private Fragment mGoogleMapsFragment;

    // Fragment for displaying vendor list
    private Fragment mVendorListFragment;

    // Fragment for displaying vendor detail
    private Fragment mVendorDetailsFragment;

    // Fragment for display vendor item search result
    private Fragment mVendorSearchItemFragment;

    private NavigationView mNavigationView;
    private AppBarLayout mAppBarLayout;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mPresenter == null)
            mPresenter = new MainActivityPresenter(this);

        initializeBaseViews();

        // getUserLocation();

        if(savedInstanceState != null) {
            return;
        }

        launchContentMainFragment();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }


    /**
     * Sets the action bar title as the given string
     * @param title
     */
    @Override
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

    /**
     * Highlights the menu item with given id in the navigation drawer
     * @param resId
     */
    public void setNavigationDrawerCheckedItem(int resId){
        mNavigationView.setCheckedItem(resId);
    }

    /**
     * Sets the app bar elevation to the given value
     * Only takes effect for devices with API >= 21
     * @param elevation
     */
    @Override
    public void setAppBarElevation(float elevation){
        if(mAppBarLayout == null) {
            mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
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
     * Handles back press user input
     */
    @Override
    public void onBackPressed() {
        if(mPresenter.onBackPressed())
            super.onBackPressed();
    }

    /**
     * Handles navigation drawer item selection user input
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        mPresenter.onNavigationItemSelected(id);
        return true;
    }

    /**
     * Initialize the toolbar, floating action button, and the drawer
     */
    public void initializeBaseViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Launches the content main fragment
     */
    public void launchContentMainFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, new ContentMainFragment(),
                String.valueOf(R.id.nav_home)).commit();
    }

    /**
     * Launches the Google maps fragment
     */
    public void launchMapFragment() {
        if(mGoogleMapsFragment == null)
            mGoogleMapsFragment = new MapFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, mGoogleMapsFragment, String.valueOf(R.id.nav_map));
        ft.addToBackStack(String.valueOf(R.id.nav_map));
        ft.commit();
    }

    /**
     * Launches the MarketList fragment
     */
    public void launchMarketFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, new MarketListFragment(), String.valueOf(R.id.market_list));
        ft.addToBackStack(String.valueOf(R.id.market_list));
        ft.commit();
    }


    /**
     * Launches the Settings fragment
     */
    public void launchSettingsFragment() {
        List<Market> markets;

        try {
            markets = new MarketPresenter(this).fetchMarkets();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        if (markets == null || markets.isEmpty())
            return;

        Bundle bundle = new Bundle();
        bundle.putSerializable("list_markets", markets.toArray());
        SyncCalendarFragment settingsFragment = new SyncCalendarFragment();
        settingsFragment.setArguments(bundle);

        // Replace the container with the fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, settingsFragment, String.valueOf(R.id.nav_manage));
        ft.addToBackStack(String.valueOf(R.id.nav_manage));
        ft.commit();
    }

    /**
     * Launches the Grocery fragment
     */
    public void launchGroceryFragment() {
        // Replace the container with the fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, new GroceryListFragment(), String.valueOf(R.id.nav_grocery_list));
        ft.addToBackStack(String.valueOf(R.id.nav_grocery_list));
        ft.commit();
    }

    /**
     * Launches the Calendar fragment
     */
    public void launchCalendarFragment() {
        // Replace the container with the fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, new CalendarFragment(), String.valueOf(R.id.nav_calendar));
        ft.addToBackStack(String.valueOf(R.id.nav_calendar));
        ft.commit();
    }

    @Override
    public void onMarketListItemClick(Market market) {
        launchVendorListFragment(market);
    }

    /**
     * Launches the VendorList fragment
     */
    public void launchVendorListFragment(Market market) {
        if (mVendorListFragment == null) {
            mVendorListFragment = new VendorListFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("currentMarket", market);
            mVendorListFragment.setArguments(bundle);
        } else {
            Bundle b = mVendorListFragment.getArguments();
            b.putSerializable("currentMarket", market);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, mVendorListFragment, "");
        ft.addToBackStack(String.valueOf(R.id.market_list));
        ft.commit();
    }

    /**
     * Launches the vendor details fragment on click from the vendor list fragment
     *
     * @param vendorName Name of the vendor that was selected
     * @param market     The market that the vendor belongs to
     */
    @Override
    public void onVendorListItemClick(String vendorName, Market market) {
        if (mVendorDetailsFragment == null) {
            mVendorDetailsFragment = new VendorDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("marketName", market.getName());
            bundle.putString("vendorName", vendorName);
            bundle.putString("marketHours", market.getDailyHours());
            bundle.putString("marketAddress", market.getAddress());
            bundle.putString("marketDatesOpen", market.getYearOpen());
            mVendorDetailsFragment.setArguments(bundle);
        } else {
            Bundle bundle = mVendorDetailsFragment.getArguments();
            bundle.putString("marketName", market.getName());
            bundle.putString("vendorName", vendorName);
            bundle.putString("marketHours", market.getDailyHours());
            bundle.putString("marketAddress", market.getAddress());
            bundle.putString("marketDatesOpen", market.getYearOpen());
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, mVendorDetailsFragment, "");
        ft.addToBackStack(String.valueOf(R.id.market_list));
        ft.commit();
    }

    /**
     * Launches fragment for showing Vendors search result
     */
    public void launchVendorSearchItemFragment(String item) {
        if (mVendorSearchItemFragment == null) {
            mVendorSearchItemFragment = new VendorSearchItemFragment();
            Bundle bundle = new Bundle();
            bundle.putString("searchItem", item);
            mVendorSearchItemFragment.setArguments(bundle);
        } else {
            Bundle b = mVendorSearchItemFragment.getArguments();
            b.putString("searchItem", item);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, mVendorSearchItemFragment);
        ft.addToBackStack("Search Results");
        ft.commit();
    }

    /**
     * Starts the intent for the login activity
     */
    @Override
    public void startLoginActivityIntent() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
    }

    /**
     * Clears the fragment back stack
     */
    @Override
    public void clearFragmentBackStack() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Permissions.REQUEST_LOCATION_SETTINGS) {
            if(mGoogleMapsFragment != null) {
                mGoogleMapsFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Get the current user location. Need to check runtime permissions to access location data.
     */
    void getUserLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.e(TAG, "Location services disabled");
            Toast.makeText(this, "Enable location services for accurate data", Toast.LENGTH_LONG).show();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //We should show an explanation to the user about why we need location data
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "Location permission is needed to show distances to markets.", Toast.LENGTH_LONG).show();
            }

            // No explanation needed, we can request the permission.
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Permissions.REQUEST_COURSE_PERMISSION);
            }
        }

        //Permission is granted and we go ahead to access the location data
        else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.e(TAG, "Location permissions passed, successfully got user location");
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Permissions.REQUEST_COURSE_PERMISSION:

                //Permission is granted to use location data
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.e(TAG, "Location permissions passed, successfully got user location");

                }

                //Permission is denied to use location data and we explain to the user that distance to markets will not be shown
                else {
                    currentLocation = null;
                    Toast.makeText(this, "Please turn on location permissions in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }


}
