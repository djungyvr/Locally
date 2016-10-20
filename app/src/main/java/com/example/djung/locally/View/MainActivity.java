package com.example.djung.locally.View;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.example.djung.locally.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<MarketCardSection> sampleData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sampleData = new ArrayList<>();

        populateSampleData();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        MarketCardSectionAdapter adapter = new MarketCardSectionAdapter(this,sampleData);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);

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
                break;
            case R.id.nav_map:
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
     * Used to populate layout_main's recycler view
     */
    void populateSampleData() {
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
