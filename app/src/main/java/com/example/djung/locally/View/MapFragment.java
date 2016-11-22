package com.example.djung.locally.View;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Utils.LocationUtils;
import com.example.djung.locally.Utils.ThreadUtils;
import com.example.djung.locally.Presenter.MarketPresenter;
import com.example.djung.locally.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.location.LocationSettingsStatusCodes.*;

//TODO: FIX SO THAT LOCATION IS ADDED UPON PRESSING YES

public class MapFragment extends Fragment
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Marker mLastPositionMarker;

    // Returns the view of the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_layout, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);

        // Needed to get the map to display immediately
        mMapView.onResume();

        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.title_fragment_maps));
        ((MainActivity) getActivity()).setAppBarElevation(4);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        // Create instance of google services api
        initializeApiClient();

        return v;
    }

    @Override
    public void onStart() {
        if(mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Initialize the Google Service Api Client
     */
    private void initializeApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * Manipulates the map once available.
     * This Callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Vancouver, Canada.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Latitude and longitude of Vancouver
        double latitude = 49.2827;
        double longitude = -123.1207;

        mGoogleMap = googleMap;

        dropPins(mGoogleMap);

        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude) , 12.0f) );
    }

    /**
     * Displays the location settings request dialog and allows users to choose yes or no to improve location
     */
    public void requestLocation() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        // Removes the never option
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        setLastLocation();
                        setLastPosition();
                        moveCameraFocus(mLastLocation);
                        break;
                    case RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(), Permissions.REQUEST_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case Permissions.REQUEST_LOCATION_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        setLastLocation();
                        setLastPosition();
                        moveCameraFocus(mLastLocation);
                        break;
                    case Activity.RESULT_CANCELED:
                        requestLocation();
                        break;
                }
                break;
        }
        ((MainActivity)getActivity()).initializeBaseViews();
    }

    /**
     * Called once the Google Play Service is connected
     *
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocation();
    }

    private void setLastLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        Permissions.REQUEST_COURSE_PERMISSION);
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    /**
     * Set the last known position to the map
     */
    public void setLastPosition() {
        if (mLastLocation != null) {
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Your Location");

            // Changing marker icon
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mLastPositionMarker = mGoogleMap.addMarker(marker);

            mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()) , 12.0f) );

            Log.d("MapFragment", "Added marker");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Connection Failed")
                        .setMessage("Connection failed.")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });
    }

    /**
     * Drops market pins onto the map
     *
     * @param googleMap the map to drop the pins onto
     */
    public void dropPins(GoogleMap googleMap) {
        MarketPresenter marketPresenter = new MarketPresenter(this.getContext());
        try {
            List<Market> marketList = marketPresenter.fetchMarkets();

            for(Market market : marketList) {
                // Create marker
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(market.getLatitude(), market.getLongitude())).title(market.getName());

                // Changing marker icon
                marker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                googleMap.addMarker(marker);
            }
        } catch (final ExecutionException ee) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Execute Exception")
                            .setMessage(ee.getMessage())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            });
        } catch (final InterruptedException ie) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Interrupted Exception")
                            .setMessage(ie.getMessage())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            });
        }
    }

    /**
     * Hangles in app permission of location results
     *
     * @param requestCode request code of in app permission
     * @param permissions permissions requested
     * @param grantResults permissions that have been granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Permissions.REQUEST_COURSE_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Course Location")
                                    .setMessage("Permission Granted")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        }
                    });
                    Log.d("PERMISSION RESULT","Course Location Permission is Granted");
                } else {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Course Location")
                                    .setMessage("Permission Denied")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        }
                    });
                    Log.d("PERMISSION RESULT","Course Location Permission is Granted");
                }
                return;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mLastLocation == null) {
            mLastLocation = location;
            setLastPosition();
            moveCameraFocus(mLastLocation);
        }
        // check if we should redraw the current position marker
        // if new location is 50m away from last one
        else if (LocationUtils.getDistanceBetween(mLastLocation, location) > 50) {
            mLastLocation = location;
            mLastPositionMarker.remove();
            setLastPosition();
        } else {
            mLastLocation = location;
        }
    }

    /**
     * Moves camera focus to given location
     */
    public void moveCameraFocus(Location location) {
        if(location != null)
            mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(),location.getLongitude()) , 14.0f) );
    }
}


