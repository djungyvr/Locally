package com.example.djung.locally.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.example.djung.locally.Utils.DateUtils.parseHours;
import static com.example.djung.locally.Utils.DateUtils.parseYear;

/**
 * Created by Anna on 08.11.2016.
 */

public class MarketPageAdapter extends ArrayAdapter<Market> {
   public MarketPageAdapter(Context context,  ArrayList<Market> item) {
        super(context,0, item);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Market marketItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_market, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.market_name);
        TextView location = (TextView) convertView.findViewById(R.id.market_location);
        TextView weekDays = (TextView) convertView.findViewById(R.id.market_hours);
        TextView yearDates = (TextView) convertView.findViewById(R.id.market_year_open);
        WebView description = (WebView ) convertView.findViewById(R.id.market_description);
        //set the link for directions to open in Google maps
        TextView directions = (TextView) convertView.findViewById(R.id.market_directions);
        directions.setLinkTextColor(Color.parseColor("#4C516D"));
        String strUri = "http://maps.google.com/maps?q=loc:" + marketItem.getLatitude() + "," + marketItem.getLongitude();
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Linkify.addLinks(directions, pattern, strUri);

        //Justify the market description text
        String htmlText = "<html><body style=\"text-align:justify\"> %s </body></Html>";
        String descriptionText=marketItem.getDescription();

        //Fill in page elements with data to display
        name.setText(marketItem.getName());
        location.setText("Location: "+marketItem.getAddress());
        description.loadData(String.format(htmlText, descriptionText),"text/html; charset=UTF-8", null);
        weekDays.setText(parseHours(marketItem.getDailyHours()));
        yearDates.setText(parseYear(marketItem.getYearOpen()));

        return convertView;
    }



}
