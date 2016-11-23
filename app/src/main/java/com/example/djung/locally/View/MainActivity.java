package com.example.djung.locally.View;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.djung.locally.AWS.AWSMobileClient;
import com.example.djung.locally.AWS.IdentityManager;
import com.example.djung.locally.DB.VendorItemDatabase;
import com.example.djung.locally.DB.VendorItemsProvider;
import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Presenter.MarketPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.DateUtils;
import com.example.djung.locally.Utils.MarketUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VendorListFragment.OnVendorListItemClickListener,
        MarketListFragment.onMarketListItemClick, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener,
        VendorSearchItemFragment.OnVendorListItemClickListener{

    private final String TAG = "MainActivity";

    private ArrayList<Object> mCardSectionsData;

    private ArrayList<Market> mAllMarketsList;

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

    // Fragment for displaying calendar
    private Fragment mCalendarFragment;

    // Fragment for displaying settings
    private Fragment mSettingsFragment;

    // Fragment for displaying the grocery list
    private Fragment mGroceryFragment;

    // Fragment for display vendor item search result
    private Fragment mVendorSearchItemFragment;

    private FragmentManager mFragmentManager;

    private IdentityManager identityManager;

    private NavigationView mNavigationView;

    private SearchView mSearchView;

    private SuggestionAdapter mVendorItemsSuggestionAdapter;

    private AppBarLayout mAppBarLayout;

    MarketCardSectionAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAWS();

        initializeBaseViews();

        mCardSectionsData = new ArrayList<>();

        fetchAllMarketsData();

        initializeContentMain();

        getUserLocation();

        setAppBarElevation(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAppBarElevation(0);
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();
    }

    @Override
    protected void onPause(){
        super.onPause();
        setAppBarElevation(4);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mFragmentManager != null) {
            int stackCount = mFragmentManager.getBackStackEntryCount();
            if(stackCount > 1) {
                String fragmentName = mFragmentManager.getBackStackEntryAt(stackCount - 2).getName();
                setActionBarTitle(fragmentName);
                if (fragmentName.equals(getString(R.string.title_fragment_grocery_list))) {
                    mNavigationView.setCheckedItem(R.id.nav_grocery_list);
                } else if (fragmentName.equals(getString(R.string.title_fragment_calendar))) {
                    mNavigationView.setCheckedItem(R.id.nav_calendar);
                } else if (fragmentName.equals(getString(R.string.title_activity_maps))) {
                    mNavigationView.setCheckedItem(R.id.nav_map);
                } else if (fragmentName.equals(getString(R.string.title_fragment_settings))) {
                    mNavigationView.setCheckedItem(R.id.nav_manage);
                } else {
                    mNavigationView.setCheckedItem(R.id.market_list);
                }
            }
            else {
                mNavigationView.setCheckedItem(R.id.nav_home);
                setAppBarElevation(0);
                setActionBarTitle("Locally");
            }
            super.onBackPressed();
        }
        else {
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
                    if (mSettingsFragment != null) {
                        mFragmentManager.beginTransaction().remove(mSettingsFragment).commit();
                    }
                    if (mCalendarFragment != null) {
                        mFragmentManager.beginTransaction().remove(mCalendarFragment).commit();
                    }
                    if (mGroceryFragment != null) {
                        mFragmentManager.beginTransaction().remove(mGroceryFragment).commit();
                    }
                    for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); i++) {
                            mFragmentManager.popBackStack();
                    }
                    setAppBarElevation(0);
                    setActionBarTitle("Locally");
                }
                break;
            case R.id.nav_grocery_list:
                mSearchView.clearFocus();
                launchGroceryFragment();
                break;
            case R.id.nav_map:
                mSearchView.clearFocus();
                launchMapFragment();
                break;
            case R.id.market_list:
                mSearchView.clearFocus();
                launchMarketFragment();
                break;
            case R.id.nav_manage:
                mSearchView.clearFocus();
                launchSettingsFragment();
                break;
            case R.id.nav_use_as_vendor:
                mSearchView.clearFocus();
                Intent loginActivity = new Intent(this, LoginActivity.class);
                startActivity(loginActivity);
                break;
            case R.id.nav_calendar:
                mSearchView.clearFocus();
                launchCalendarFragment();
                //Intent calendarActivity = new Intent(this, CalendarActivity.class);
                //startActivity(calendarActivity);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Initializes content_main which has card sections for quick links and nearby
     * and recently viewed markets
     */
    public void initializeContentMain() {
        initializeSearchView();
        initializeQuickLinksCardSection();
        initializeMarketsCardSection();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        mRecyclerViewAdapter = new MarketCardSectionAdapter(this, mCardSectionsData, currentLocation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mRecyclerViewAdapter);
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

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Launches the Google maps fragment
     */
    void launchMapFragment() {
        if (mGoogleMapsFragment == null)
            mGoogleMapsFragment = new MapFragment();
        if (mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, mGoogleMapsFragment);
        ft.addToBackStack(getString(R.string.title_fragment_maps));
        ft.commit();
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
        ft.replace(R.id.main_layout, mMarketListFragment);
        ft.addToBackStack(getString(R.string.title_fragment_market_list));
        ft.commit();
    }


    /**
     * Launches the Settings fragment
     */
    private void launchSettingsFragment() {
        if (mSettingsFragment == null)
            mSettingsFragment = new SyncCalendarFragment();
        if (mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();

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
        mSettingsFragment.setArguments(bundle);

        // Replace the container with the fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, mSettingsFragment);
        ft.addToBackStack(getString(R.string.title_fragment_settings));
        ft.commit();
    }

    /**
     * Launches the Grocery fragment
     */
    private void launchGroceryFragment() {
        if (mGroceryFragment == null)
            mGroceryFragment = new GroceryListFragment();
        if (mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();

        // Replace the container with the fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, mGroceryFragment);
        ft.addToBackStack(getString(R.string.title_fragment_grocery_list));
        ft.commit();
    }

    /**
     * Launches the Calendar fragment
     */
    void launchCalendarFragment() {
        if (mCalendarFragment == null)
            mCalendarFragment = new CalendarFragment();
        if (mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();

        // Replace the container with the fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, mCalendarFragment);
        ft.addToBackStack(getString(R.string.title_fragment_calendar));
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
        ft.replace(R.id.main_layout, mVendorListFragment);
        ft.addToBackStack(market.getName());
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

        if (mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, mVendorDetailsFragment);
        ft.addToBackStack(market.getName());
        ft.commit();
    }

    /**
     * Initializes the quick links section which includes All Markets, Calendar,
     * Fruits & Vegetables, Your Lists
     */
    public void initializeQuickLinksCardSection() {
        ArrayList<QuickLinkCard> q = new ArrayList<>();

        String allMarkets = "";
        String marketsOpen = "";

        if(mAllMarketsList != null) {
            int numMarkets = mAllMarketsList.size();
            if(numMarkets >= 1) {
                allMarkets = numMarkets + " market";
                if(numMarkets != 1)
                    allMarkets = allMarkets + "s";
            }
            int numOpen = MarketUtils.getNumberOfCurrentlyOpenMarkets(mAllMarketsList);
            if(numOpen != 1) {
                marketsOpen = numOpen + " markets open now";
            } else {
                marketsOpen = numOpen + " market open now";
            }
        }

//       TODO: get number of items on the user's grocery list

        q.add(new QuickLinkCard(R.drawable.ubc, "All Markets", allMarkets));
        q.add(new QuickLinkCard(R.drawable.thumbnail2, "Calendar", marketsOpen));
        q.add(new QuickLinkCard(R.drawable.thumbnail3, "In Season Produce", "16 items"));
        q.add(new QuickLinkCard(R.drawable.thumbnail4, "Your Grocery List", "3 saved items"));

        QuickLinkCardSection qs = new QuickLinkCardSection(q);
        mCardSectionsData.add(qs);
    }

    /**
     * Decide which markets to display in the sections
     * TODO: for now we'll just add all the markets
     */
    public void initializeMarketsCardSection() {
        MarketCardSection nearbySection = new MarketCardSection();
        nearbySection.setSectionTitle("Markets Nearby");

        MarketCardSection recentlyViewedSection = new MarketCardSection();
        recentlyViewedSection.setSectionTitle("Recently Viewed");

        if (mAllMarketsList != null && !mAllMarketsList.isEmpty()) {
            if(currentLocation != null){
                nearbySection.setMarketList(mAllMarketsList);
                mCardSectionsData.add(nearbySection);

            }
            recentlyViewedSection.setMarketList(mAllMarketsList);
            mCardSectionsData.add(recentlyViewedSection);
        }

        // if location permissions denied, show the rationale
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mCardSectionsData.add(new EnablePermissionsCard(this));

        }
    }

    /**
     * Gets a list of all the markets
     */
    private void fetchAllMarketsData() {
        MarketPresenter presenter = new MarketPresenter(this);
        mAllMarketsList = new ArrayList<>();
        try {
            mAllMarketsList = new ArrayList<>(presenter.fetchMarkets());
        } catch (final ExecutionException | InterruptedException e) {
            Log.e(TAG, e.getMessage());
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
     * Simulates selecting an item from the navigation bar
     * @param resId menu item id for the navigation bar
     */
    public void selectNavigationDrawer(int resId) {
        mNavigationView.setCheckedItem(resId);
        onNavigationItemSelected(mNavigationView.getMenu().findItem(resId));
    }

    private void initializeSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) findViewById(R.id.search_view);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
        mSearchView.setQueryHint("Search locally for:");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

    /**
     * Searches the vendor items and displays results for the given query.
     * @param query The search query
     */
    private void showResults(String query) {
        Cursor cursor = managedQuery(VendorItemsProvider.CONTENT_URI, null, null,
                new String[] {query}, null);

        if (cursor == null) {
        } else {
            // Specify the columns we want to display in the result
            String[] from = new String[] {VendorItemDatabase.KEY_VENDOR_ITEM_NAME,
                    VendorItemDatabase.KEY_VENDOR_ITEM_INFO};

            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] { R.id.vendor_item_name_search_result,
                    R.id.vendor_item_info_search_result };

            // Create a simple cursor adapter for vendor
            mVendorItemsSuggestionAdapter = new SuggestionAdapter(this,cursor);

            mSearchView.setSuggestionsAdapter(mVendorItemsSuggestionAdapter);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        showResults(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        showResults(newText);
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return true;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        String vendorItem = mVendorItemsSuggestionAdapter.getSuggestion(position);
        mSearchView.clearFocus();
        launchVendorSearchItemFragment(vendorItem);
        return true;
    }


    /**
     * Launches fragment for showing Vendors search result
     */
    void launchVendorSearchItemFragment(String item) {
        if (mVendorSearchItemFragment == null) {
            mVendorSearchItemFragment = new VendorSearchItemFragment();
            Bundle bundle = new Bundle();
            bundle.putString("searchItem", item);
            mVendorSearchItemFragment.setArguments(bundle);
        } else {
            Bundle b = mVendorSearchItemFragment.getArguments();
            b.putString("searchItem", item);
        }

        if (mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_layout, mVendorSearchItemFragment);
        ft.addToBackStack("Search Results");
        ft.commit();
    }

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
     * Creates a new adapter for the MarketCardSection and replaces the old adapter with the new one
     */
    public void updateContentMain(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mCardSectionsData = new ArrayList<>();
        initializeQuickLinksCardSection();
        initializeMarketsCardSection();
        MarketCardSectionAdapter adapter = new MarketCardSectionAdapter(this, mCardSectionsData, currentLocation);
        recyclerView.swapAdapter(adapter, true);
        Log.e(TAG, "Replaced old adapter with new adapter for recycler view due to updated location permissions");
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
                    Toast.makeText(this, "Please turn on location permissions in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Permissions.REQUEST_COURSE_PERMISSION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSearchView.clearFocus();
        if(requestCode == Permissions.REQUEST_LOCATION_SETTINGS) {
            if(mGoogleMapsFragment != null) {
                mGoogleMapsFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
