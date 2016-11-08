package com.example.djung.locally.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Anna on 04.11.2016.
 */

public class MarketActivity extends AppCompatActivity{

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_market);

            Intent intent = getIntent();

            ArrayList<Market> marketItem= new ArrayList<Market>();
            //this should be he market element we passed to this activity
            Market market = (Market)intent.getSerializableExtra("marketItem");
            marketItem.add(market);

            ListView listview = (ListView) findViewById(R.id.marketPage);
            MarketPageAdapter marketAdapter = new MarketPageAdapter(this, marketItem);
            listview.setAdapter(marketAdapter);

        }
    }

