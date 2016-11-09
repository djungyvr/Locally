package com.example.djung.locally.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;

import java.util.ArrayList;

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
        TextView description = (TextView) convertView.findViewById(R.id.market_description);

        name.setText(marketItem.getName());
        location.setText("Location: "+marketItem.getAddress());
        description.setText(marketItem.getDescription());

        return convertView;
    }



}
