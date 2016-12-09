package com.example.djung.locally.Presenter;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.LocationUtils;
import com.example.djung.locally.Utils.MarketUtils;
import com.example.djung.locally.Utils.ThreadUtils;
import com.example.djung.locally.View.Adapters.MarketListAdapter;
import com.example.djung.locally.View.Interfaces.MarketListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Andy Lin on 2016-11-26.
 */

public class MarketListPresenter {
    private Activity activity;
    private MarketListView marketListView;
    private List<Market> marketList;
    private onMarketListItemClick mCallback;
    private Location currentLocation;
    private final String TAG = "MarketListPresenter";

    public MarketListPresenter(Activity activity, MarketListView marketListView){
        this.activity = activity;
        this.marketListView = marketListView;
        marketList = new ArrayList<Market>();
        this.mCallback = (onMarketListItemClick) activity;
    }

    public interface onMarketListItemClick {
        void onMarketListItemClick(Market market);
        void onMarketListItemDetailsClick(Market market);
    }

    public void setActionBar(){
        marketListView.setActionBarTitle("Market List");
    }

    public void setNavDrawerSelectedItem(){
        marketListView.setNavDrawerSelectedItem(R.id.nav_market_list);
    }

    public void getUserLocation(){
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            currentLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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

    public void populateMarketList(){
        Log.e(TAG, "Populating the market list");
        MarketPresenter presenter = new MarketPresenter(activity);

        try {
            List<Market> fetchedList = presenter.fetchMarkets();

            for(Market m : fetchedList) {
                marketList.add(m);
            }

        } catch (final ExecutionException ee) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(activity)
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
                    new AlertDialog.Builder(activity)
                            .setTitle("Interrupted Exception")
                            .setMessage(ie.getMessage())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            });
        }

        if (currentLocation != null){
            marketList = MarketUtils.getClosestMarkets(marketList, currentLocation);
        }

        MarketListAdapter adapter = new MarketListAdapter(this);
        marketListView.showMarketList(adapter);
    }

    public List<Market> getMarketList(){
        return marketList;
    }

    public String getDistance(int position){
        Market market = marketList.get(position);
        if (currentLocation != null){
            float distance = MarketUtils.getDistanceFromMarket(market, currentLocation);
            return LocationUtils.formatDistanceInKm(distance);
        }
        else {
            return "";
        }
    }

    public String getStatus(int position){
        Market market = marketList.get(position);
        if (MarketUtils.isMarketCurrentlyOpen(market)){
            return "Open Now!";
        }
        else {
            return "Closed Now";
        }
    }

    public void setImage(int position, ImageView imageView){
        Market market = marketList.get(position);
        String imageResource = MarketUtils.getMarketUrl(market.getName());
        if(imageResource.isEmpty()) {
            imageView.setImageResource(R.drawable.ubc);
        } else {
            Picasso.with(activity).setIndicatorsEnabled(false);
            Picasso.with(activity).load(imageResource).into(imageView);
        }
    }

    public void addMarketToCalendar(int position){
        Market market = marketList.get(position);
        Log.e(TAG, "Adding market " + market.getName() + " to calendar");

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        String hoursOpen = market.getDailyHours();
        int dayOfWeek = 0;
        String timeRange = "";
        String[] dailyHours = hoursOpen.split(",");
        for (int i = 0; i < dailyHours.length; i++){
            if (dailyHours[i].equals("00:00-00:00")){
                continue;
            }
            else {
                dayOfWeek = i + 1;
                timeRange = dailyHours[i];
            }
        }

        String[] times = timeRange.split("-");
        String[] startHours = times[0].split(":");
        String[] endHours = times[1].split(":");

        switch(dayOfWeek){
            case 1: startTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    endTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    break;
            case 2: startTime.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                    endTime.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                    break;
            case 3: startTime.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                    endTime.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                    break;
            case 4: startTime.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                    endTime.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                    break;
            case 5: startTime.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                    endTime.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                    break;
            case 6: startTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                    endTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                    break;
            case 7: startTime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    endTime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    break;
        }

        startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startHours[0]));
        startTime.set(Calendar.MINUTE, Integer.parseInt(startHours[1]));

        endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endHours[0]));
        endTime.set(Calendar.MINUTE, Integer.parseInt(endHours[1]));

        Intent calendarIntent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, market.getName() + " Opening")
                .putExtra(CalendarContract.Events.DESCRIPTION, market.getName() + " is opening now! Do not miss it! -Locally")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, market.getAddress())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        try {
            activity.startActivity(calendarIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "There is no calendar application installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onVisitMarketPageClick(int position){
        Market market = marketList.get(position);
        mCallback.onMarketListItemDetailsClick(market);
    }

    public void onNotificationsButtonClick(int position){
        Market market = marketList.get(position);
        Log.e(TAG, "Building market list item notification now");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
        builder.setSmallIcon(R.mipmap.ic_app_launcher);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(market.getName() + " is about to open! Do not miss it!");
        bigText.setBigContentTitle("Market Notification");
        builder.setStyle(bigText);

        Notification notification = builder.build();
        NotificationManagerCompat.from(activity).notify(0,notification);
    }

    public void onMarketListItemClick(int position){
        Market market = marketList.get(position);
        Log.e(TAG, "Callback on Market List Item Click of market: " + market.getName());
        mCallback.onMarketListItemClick(market);
    }
}
