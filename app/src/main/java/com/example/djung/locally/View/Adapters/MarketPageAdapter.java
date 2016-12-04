package com.example.djung.locally.View.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.MarketUtils;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.example.djung.locally.Utils.DateUtils.parseHours;
import static com.example.djung.locally.Utils.DateUtils.parseYear;

/**
 * Created by Anna on 19.11.2016.
 */

public class MarketPageAdapter extends RecyclerView.Adapter<MarketPageAdapter.ViewHolder> implements OnMapReadyCallback {
    private final String TAG = "MarketPageAdapter";
    private ArrayList<Market> mMarkets;
    private Context mContext;
    private GoogleMap mGoogleMap;
    private LatLng mLatLng;

    public MarketPageAdapter(ArrayList<Market> markets, Context context) {
        mMarkets = markets;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.market_fragment, parent, false);
        return new ViewHolder(v, mContext, mMarkets);
    }

    //Fill in page elements with data to display
    @Override
    public void onBindViewHolder(MarketPageAdapter.ViewHolder holder, int position) {
        Market marketItem = mMarkets.get(position);
        holder.mMarketName.setText(marketItem.getName());
        holder.mMarketDescriptions.setText(marketItem.getDescription());
        Log.d(TAG,marketItem.getDescription());
        holder.mMarketLocation.setText("Location: " + marketItem.getAddress());
        holder.mMarketWeekDays.setText(parseHours(marketItem.getDailyHours()));
        holder.mMarketYearOpen.setText(parseYear(marketItem.getYearOpen()));

        if (holder.mapPreview != null) {
            holder.mapPreview.onCreate(null);
            holder.mapPreview.onResume();
            holder.mapPreview.getMapAsync(this);
        }

        mLatLng = new LatLng(marketItem.getLatitude(), marketItem.getLongitude());

        //set the link for directions to open in Google maps
        String strUri = "http://maps.google.com/maps?q=loc:" + marketItem.getLatitude() + "," + marketItem.getLongitude();
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Linkify.addLinks(holder.mMarketDirections, pattern, strUri);

        if (MarketUtils.isMarketCurrentlyOpen(marketItem)) {
            holder.mMarketStatus.setText("Open Now!");
        } else {
            holder.mMarketStatus.setText("Closed Now");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        // Add a marker for market
        mGoogleMap.addMarker(new MarkerOptions().position(mLatLng));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(mLatLng, 13);
        mGoogleMap.animateCamera(yourLocation);
    }


    @Override
    public int getItemCount() {
        if (mMarkets != null) {
            return mMarkets.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mMarketName;
        public TextView mMarketStatus;
        public TextView mMarketLocation;
        public TextView mMarketWeekDays;
        public TextView mMarketYearOpen;
        public TextView mMarketDirections;
        public TextView mMarketDescriptions;
        public MapView mapPreview;
        public Context mContext;
        public ArrayList<Market> mMarkets;

        public ViewHolder(View v, Context context, ArrayList<Market> markets) {
            super(v);
            mContext = context;
            mMarkets = markets;
            mMarketName = (TextView) v.findViewById(R.id.market_name);
            mMarketStatus = (TextView) v.findViewById(R.id.market_status);
            mMarketLocation = (TextView) v.findViewById(R.id.market_location);
            mMarketWeekDays = (TextView) v.findViewById(R.id.market_hours);
            mMarketYearOpen = (TextView) v.findViewById(R.id.market_year_open);
            mapPreview = (MapView) v.findViewById(R.id.market_map_preview);
            mMarketDescriptions = (TextView) v.findViewById(R.id.market_description);

            mMarketDirections = (TextView) v.findViewById(R.id.market_directions);
            mMarketDirections.setLinkTextColor(Color.parseColor("#4C516D"));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
        }

    }
}

