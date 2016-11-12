package com.example.djung.locally.View;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.djung.locally.AWS.AWSMobileClient;
import com.example.djung.locally.AWS.AppHelper;
import com.example.djung.locally.AWS.IdentityManager;
import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Presenter.MarketPresenter;
import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VendorListFragment.OnVendorListItemClickListener,
        MarketListFragment.onMarketListItemClick {

    private final String TAG = "MainActivity";

    private ArrayList<MarketCardSection> marketData;
    private Location currentLocation;
    private LocationManager locationManager;

    // Fragment for displaying maps
    private Fragment mGoogleMapsFragment;

    // Fragment for displaying market list
    private Fragment mMarketListFragment;

    // Fragment for displaying vendor list
    private Fragment mVendorListFragment;

    // Fragment for displaying vendor detail
    private Fragment mVendorDetailsFragment;

    // Fragment for displaying settings
    private Fragment mSettingsFragment;

    private FragmentManager mFragmentManager;

    private IdentityManager identityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAWS();

        initializeBaseViews();

        fetchMarketData();

        getUserLocation();

        initializeContentMain();

        // Initialize application
        AppHelper.initialize(getApplicationContext());

        //fetchMarket();
    }

    @Override
    protected void onResume() {
        super.onResume();

        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                if (mFragmentManager != null) {
                    if (mGoogleMapsFragment != null) {
                        mFragmentManager.beginTransaction().remove(mGoogleMapsFragment).commit();
                    }
                    if (mMarketListFragment != null) {
                        mFragmentManager.beginTransaction().remove(mMarketListFragment).commit();
                    }
                    if (mVendorListFragment != null) {
                        mFragmentManager.beginTransaction().remove(mVendorListFragment).commit();
                    }
                    if (mVendorDetailsFragment != null) {
                        mFragmentManager.beginTransaction().remove(mVendorDetailsFragment).commit();
                    }
                    for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); i++) {
                        mFragmentManager.popBackStack();
                    }
                }
                break;
            case R.id.nav_map:
                launchMapFragment();
                break;

            case R.id.market_list:
                launchMarketFragment();
                break;

            case R.id.nav_manage:
                if (mSettingsFragment == null)
                    break;
            case R.id.nav_use_as_vendor:
                Intent loginActivity = new Intent(this, LoginActivity.class);
                startActivity(loginActivity);
                break;
            case R.id.nav_calendar:
                Intent calendarActivity = new Intent(this, CalendarActivity.class);
                startActivity(calendarActivity);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Initializes content_main which has the cards of nearby and recently viewed markets
     */
    public void initializeContentMain() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        MarketCardSectionAdapter adapter = new MarketCardSectionAdapter(this, marketData, currentLocation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Creates a new adapter for the MarketCardSection and replaces the old adapter with the new one
     */
    public void updateContentMain(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        MarketCardSectionAdapter adapter = new MarketCardSectionAdapter(this, marketData, currentLocation);
        recyclerView.swapAdapter(adapter,false);
        Log.e(TAG, "Replaced old adapter with new adapter for recycler view due to updated location permissions");
    }

    public void initializeAWS() {
        // Obtain a reference to the mobile client. It is created in the Application class,
        // but in case a custom Application class is not used, we initialize it here if necessary.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        // Obtain a reference to the mobile client. It is created in the Application class.
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        // Obtain a reference to the identity manager.
        identityManager = awsMobileClient.getIdentityManager();
    }

    /**
     * Initialize the toolbar, floating action button, and the drawer
     */
    public void initializeBaseViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Launches the Google maps fragment
     */
    void launchMapFragment() {
        if (mGoogleMapsFragment == null)
            mGoogleMapsFragment = new MapFragment();
        if (mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();

        // Replace the container with the fragment
        mFragmentManager.beginTransaction().replace(R.id.main_activity_container, mGoogleMapsFragment).commit();
    }

    /**
     * Launches the MarketList fragment
     */
    void launchMarketFragment() {
        if (mMarketListFragment == null)
            mMarketListFragment = new MarketListFragment();
        if (mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_activity_container, mMarketListFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onMarketListItemClick(Market market) {
        launchVendorListFragment(market);
    }

    /**
     * Launches the VendorList fragment
     */
    void launchVendorListFragment(Market market) {
        if (mVendorListFragment == null) {
            mVendorListFragment = new VendorListFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("currentMarket", market);
            bundle.putString("marketName", market.getName());
            bundle.putString("marketAddress", market.getAddress());
            bundle.putString("marketHours", market.getDailyHours());
            bundle.putString("marketDatesOpen", market.getYearOpen());
            mVendorListFragment.setArguments(bundle);
        } else {
            Bundle b = mVendorListFragment.getArguments();
            b.putSerializable("currentMarket", market);
            b.putString("marketName", market.getName());
            b.putString("marketAddress", market.getAddress());
            b.putString("marketHours", market.getDailyHours());
            b.putString("marketDatesOpen", market.getYearOpen());
        }

        if (mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_activity_container, mVendorListFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Launches the vendor details fragment on click from the vendor list fragment
     *
     * @param vendorName Name of the vendor that was selected
     * @param market The market that the vendor belongs to
     */
    @Override
    public void onVendorListItemClick(String vendorName, Market market) {
        if (mVendorDetailsFragment == null) {
            mVendorDetailsFragment = new VendorDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("marketName", market.getName());
            bundle.putString("vendorName", vendorName);
            bundle.putString("marketHours", DateUtils.parseHours(market.getDailyHours()));
            bundle.putString("marketAddress", market.getAddress());
            bundle.putString("marketDatesOpen", market.getYearOpen());
            mVendorDetailsFragment.setArguments(bundle);
        } else {
            Bundle bundle = mVendorDetailsFragment.getArguments();
            bundle.putString("marketName", market.getName());
            bundle.putString("vendorName", vendorName);
            bundle.putString("marketHours", DateUtils.parseHours(market.getDailyHours()));
            bundle.putString("marketAddress", market.getAddress());
            bundle.putString("marketDatesOpen", market.getYearOpen());
        }

        if (mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_activity_container, mVendorDetailsFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    void fetchMarketData() {
        marketData = new ArrayList<>();

        MarketCardSection openNowSection = new MarketCardSection();
        openNowSection.setSectionTitle("Markets Open Now");

        MarketCardSection recentlyViewedSection = new MarketCardSection();
        recentlyViewedSection.setSectionTitle("Recently Viewed");

        MarketPresenter presenter = new MarketPresenter(this);
        try {
            ArrayList<Market> marketList = new ArrayList<>(presenter.fetchMarkets());

            if (marketList != null || !marketList.isEmpty()) {
                openNowSection.setMarketList(marketList);
                recentlyViewedSection.setMarketList(marketList);
            }

        } catch (final ExecutionException e) {
            Log.e(TAG, e.getMessage());
        } catch (final InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }

        marketData.add(openNowSection);
        marketData.add(recentlyViewedSection);
    }

    /**
     * Get the current user location. Need to check runtime permissions to access location data.
     */
    void getUserLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //We should show an explanation to the user about why we need location data
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "Location permission is needed to show distances to markets.", Toast.LENGTH_SHORT).show();
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
            updateContentMain();
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
                    updateContentMain();
                }

                //Permission is denied to use location data and we explain to the user that distance to markets will not be shown
                else {
                    currentLocation = null;
                    Toast.makeText(this, "Location permission denied, distances to markets will not be shown.", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    void fetchMarket() {
        MarketPresenter marketPresenter = new MarketPresenter(this);
        try {
            Log.e(TAG, "Market Fetched: " + marketPresenter.fetchMarket(0).getName());
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage());
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Used for testing, tries to fetch from Vendor table using Vendor presenter
     */
    void fetchVendorData() {
        VendorPresenter vendorPresenter = new VendorPresenter(this);

        try {
            List<Vendor> vendorList = vendorPresenter.fetchVendors("TestMarket");

            for (Vendor v : vendorList) {
                Log.e(TAG, "Fetch Vendors Result: " + v.getName());
            }
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage());
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            Vendor vendor = vendorPresenter.fetchVendor("TestMarket", "Vendor1");
            Log.e(TAG, "Fetch Single Vendor Result: " + vendor.getName());
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage());
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            Vendor vendor = vendorPresenter.fetchVendor("TestMarket", "Vendor3");
            Log.e(TAG, "Fetch Single Vendor Result: " + vendor.getName());
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage());
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
