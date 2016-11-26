package com.example.djung.locally.View.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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
    private ArrayList<Market> markets;
    private Context context;
    private GoogleMap googleMap;
    private LatLng latLng;

    public MarketPageAdapter(ArrayList<Market> markets, Context context) {
        this.markets = markets;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_market, parent, false);
        return new ViewHolder(v, context, markets);
    }

    //Fill in page elements with data to display
    @Override
    public void onBindViewHolder(MarketPageAdapter.ViewHolder holder, int position) {
        Market marketItem = markets.get(position);
        holder.marketName.setText(marketItem.getName());
        holder.marketLocation.setText("Location: " + marketItem.getAddress());
        holder.marketWeekDays.setText(parseHours(marketItem.getDailyHours()));
        holder.marketYearOpen.setText(parseYear(marketItem.getYearOpen()));

        if (holder.mapPreview != null) {
            holder.mapPreview.onCreate(null);
            holder.mapPreview.onResume();
            holder.mapPreview.getMapAsync(this);
        }

        latLng = new LatLng(marketItem.getLatitude(), marketItem.getLongitude());

        //Justify the market description text
        String htmlText = "<html><body style=\"text-align:justify\"> %s </body></Html>";
        String descriptionText = marketItem.getDescription();
        holder.marketDescription.loadData(String.format(htmlText, descriptionText), "text/html; charset=UTF-8", null);

        //set the link for directions to open in Google maps
        String strUri = "http://maps.google.com/maps?q=loc:" + marketItem.getLatitude() + "," + marketItem.getLongitude();
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Linkify.addLinks(holder.marketDirections, pattern, strUri);

        if (MarketUtils.isMarketCurrentlyOpen(marketItem)) {
            holder.marketStatus.setText("Open Now!");
        } else {
            holder.marketStatus.setText("Closed Now");
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Add a marker for market
        googleMap.addMarker(new MarkerOptions().position(latLng));
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 13);
        googleMap.animateCamera(yourLocation);
    }


    @Override
    public int getItemCount() {
        if (markets != null) {
            return markets.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView marketName;
        public TextView marketStatus;
        public TextView marketLocation;
        public TextView marketWeekDays;
        public TextView marketYearOpen;
        public TextView marketDirections;
        public WebView marketDescription;
        public MapView mapPreview;
        public Context context;
        public ArrayList<Market> markets;

        public ViewHolder(View v, Context context, ArrayList<Market> markets) {
            super(v);
            this.context = context;
            this.markets = markets;
            this.marketName = (TextView) v.findViewById(R.id.market_name);
            this.marketStatus = (TextView) v.findViewById(R.id.market_status);
            this.marketLocation = (TextView) v.findViewById(R.id.market_location);
            this.marketWeekDays = (TextView) v.findViewById(R.id.market_hours);
            this.marketYearOpen = (TextView) v.findViewById(R.id.market_year_open);
            this.mapPreview = (MapView) v.findViewById(R.id.market_map_preview);
            this.marketDescription = (WebView) v.findViewById(R.id.market_description);

            this.marketDirections = (TextView) v.findViewById(R.id.market_directions);
            marketDirections.setLinkTextColor(Color.parseColor("#4C516D"));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
        }

    }
}

