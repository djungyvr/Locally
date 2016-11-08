package com.example.djung.locally.View;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.example.djung.locally.AWS.AppHelper;
import com.example.djung.locally.DB.VendorItemDatabase;
import com.example.djung.locally.DB.VendorItemsProvider;
import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Activity for vendors
 *
 * To see how search for items was implemented see here:
 * https://github.com/android/platform_development/tree/master/samples/SearchableDictionary
 *
 * https://www.youtube.com/watch?v=9OWmnYPX1uc
 */
public class VendorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    private final String TAG = "VendorActivity";

    private AlertDialog userDialog;

    // View objects
    private TextView mTextViewVendorName;
    private VendorItemRecyclerView mRecyclerViewVendorItems;
    private SearchView mSearchView;
    private ListView mListView;
    private TextView mTextView;

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

    // Adapters
    private SuggestionAdapter mVendorItemsSuggestionAdapter;
    private VendorItemAdapter mVendorItemAdapter;

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

        mListView = (ListView) findViewById(R.id.list);
        mTextView = (TextView) findViewById(R.id.text);

        NavigationView navigationView = (NavigationView) findViewById(R.id.vendor_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView =  navigationView.getHeaderView(0);
        mTextViewVendorName = (TextView)headerView.findViewById(R.id.text_view_nav_vendor_name);
        initialize();

        initializeSearch();

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }

    private void initializeSearch() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) findViewById(R.id.search_view_vendor_items);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Log.e(TAG, intent.getAction());
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
            // There are no results
            mTextView.setText("No results");
        } else {
            // Display the number of results
            int count = cursor.getCount();
            mTextView.setText("Returned " + count + " results");

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

            mVendorItemAdapter = new VendorItemAdapter(new ArrayList<>(currentVendor.getItemSet()), this);

            mRecyclerViewVendorItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            mRecyclerViewVendorItems.setAdapter(mVendorItemAdapter);

            mRecyclerViewVendorItems.setEmptyView(findViewById(R.id.recycler_view_vendor_items_empty));
        }
    }

    private void addToVendorList() {
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
        String vendorItem = mVendorItemsSuggestionAdapter.getVendorItemSuggestion(position);
        Log.e(TAG,"Selected suggestion: " + vendorItem);
        return true;
    }
}