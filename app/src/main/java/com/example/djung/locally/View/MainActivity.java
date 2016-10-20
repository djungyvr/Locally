package com.example.djung.locally.View;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ViewFlipper;

import com.example.djung.locally.Manifest;
import com.example.djung.locally.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private int MY_PERMISSIONS_REQUEST_COURSE_LOCATION = 0;

    private ArrayList<MarketCardSection> sampleData;
    private ViewFlipper mViewFlipper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeBaseViews();

        populateSampleData();

        initializeContentMain();

        initializeMapMain();

        mViewFlipper = (ViewFlipper)findViewById(R.id.view_flipper);
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
                mViewFlipper.setDisplayedChild(0);
                break;
            case R.id.nav_map:
                mViewFlipper.setDisplayedChild(1);
                break;
            case R.id.nav_manage:
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

    /**
     * Initialize the toolbar, floating action button, and the drawer
     */
    public void initializeBaseViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
     * Initialize the map
     */
    private void initializeMapMain() {
        // Gets the MapView from the XML layout and creates it
        MapView mapView = (MapView) findViewById(R.id.map_view);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);
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

    @Override
    public void onMapReady(GoogleMap map) {
        // Request for in app permission
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION )
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  }, MY_PERMISSIONS_REQUEST_COURSE_LOCATION);
        }

        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(true);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }
}
