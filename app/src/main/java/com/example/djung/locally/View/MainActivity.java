package com.example.djung.locally.View;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.djung.locally.AWS.AWSMobileClient;
import com.example.djung.locally.AWS.IdentityManager;
import com.example.djung.locally.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<MarketCardSection> sampleData;
    // Fragment for displaying maps
    private Fragment mGoogleMapsFragment;
    // Fragment for displaying settings
    private Fragment mSettingsFragment;

    private FragmentManager mFragmentManager;

    private IdentityManager identityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAWS();

        initializeBaseViews();

        populateSampleData();

        initializeContentMain();
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

        switch(id) {
            case R.id.nav_home:
                if(mFragmentManager != null && mGoogleMapsFragment != null)
                    mFragmentManager.beginTransaction().remove(mGoogleMapsFragment).commit();
                break;
            case R.id.nav_map:
                launchMapFragment();
                break;
            case R.id.nav_manage:
                if(mSettingsFragment == null)

                break;
            case R.id.nav_use_as_vendor:
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

        MarketCardSectionAdapter adapter = new MarketCardSectionAdapter(this,sampleData);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.search_floating_action);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        if(mGoogleMapsFragment == null)
            mGoogleMapsFragment = new MapFragment();
        if(mFragmentManager == null)
            mFragmentManager = getSupportFragmentManager();

        // Replace the container with the fragment
        mFragmentManager.beginTransaction().replace(R.id.main_activity_container, mGoogleMapsFragment).commit();
    }

    /**
     * Used to populate layout_main's recycler view with dummy data
     */
    void populateSampleData() {
        sampleData = new ArrayList<>();

        // Add sample data for markets open now
        MarketCardSection openNowSection = new MarketCardSection();
        openNowSection.setSectionTitle("Markets Open Now");

        ArrayList<MarketCard> marketsOpenNow = new ArrayList<>();
         marketsOpenNow.add(new MarketCard("UBC","100 m",R.drawable.ubc));
         marketsOpenNow.add(new MarketCard("Kitsilano","1.1 km",R.drawable.kitsilano));

        openNowSection.setMarketCardArrayList(marketsOpenNow);

        sampleData.add(openNowSection);

        // Add sample data for recently viewed markets
        MarketCardSection recentlyViewedSection = new MarketCardSection();
        recentlyViewedSection.setSectionTitle("Recently Viewed");

        ArrayList<MarketCard> marketsRecentlyViewed = new ArrayList<>();
        marketsRecentlyViewed.add(new MarketCard("UBC","100 m",R.drawable.ubc));
        marketsRecentlyViewed.add(new MarketCard("Kitsilano","1.1 km",R.drawable.kitsilano));

        recentlyViewedSection.setMarketCardArrayList(marketsRecentlyViewed);

        sampleData.add(recentlyViewedSection);
    }

}
