package com.example.djung.locally.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Presenter.MarketPresenter;
import com.example.djung.locally.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Anna on 04.11.2016.
 */

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";
        @Override
        public void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_calendar);
            MarketPresenter helper= new MarketPresenter(this);
            List<Market> markets;
            try {
                markets = helper.fetchMarkets();
                GridView gridview = (GridView) findViewById(R.id.gridview);
                CalendarAdapter marketAdapter = new CalendarAdapter(this,new ArrayList<Market>(markets));
                gridview.setAdapter(marketAdapter);
            } catch (ExecutionException e) {
                Log.e(TAG,e.getMessage());
            } catch (InterruptedException e) {
                Log.e(TAG,e.getMessage());
            }

            ArrayList<Market> sampleMarkets=new ArrayList<Market>();
            Market entry1= new Market();

            entry1.setId(1);
            entry1.setName("Market_1 Name");
            entry1.setLatitude(1);
            entry1.setLatitude(1);
            entry1.setAddress("Location 1");
            entry1.setDescription("Description 1");
            entry1.setDailyHours("10.00-12.00");
            entry1.setYearOpen("April-October");

            sampleMarkets.add(entry1);
        }

        public void displayMarket(View view) {
            Intent intent = new Intent(this, MarketActivity.class);
            Market marketItem=new Market();
            //Bundle extras = new Bundle();
            //TextView marketName = (TextView)findViewById(R.id.market_name);
            //String name = marketName.getText().toString();

            intent.putExtra("marketItem",marketItem);
            startActivity(intent);

        }

    }

