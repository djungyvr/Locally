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

        }

        public void displayMarket(View view) {
            Intent intent = new Intent(this, MarketActivity.class);
            TextView marketName = (TextView)findViewById(R.id.market_id);
            String name = marketName.getText().toString();

            intent.putExtra("M_ID",name);
            startActivity(intent);

        }

    }

