package com.example.djung.locally.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Presenter.MarketPresenter;
import com.example.djung.locally.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Anna on 04.11.2016.
 */

public class MarketActivity extends AppCompatActivity{

    private static final String TAG = "CalendarActivity";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.content_market);
            List<Market> markets=new ArrayList<Market>();

            Intent intent = getIntent();
            int marketName=Integer.valueOf(intent.getExtras().getString("M_ID"));

            MarketPresenter helper= new MarketPresenter(this);
            try {
                Market market = helper.fetchMarket(marketName);
                markets.add(market);
                ListView listview = (ListView) findViewById(R.id.marketPage);
                MarketPageAdapter marketAdapter = new MarketPageAdapter(this,new ArrayList<Market>(markets));
                listview.setAdapter(marketAdapter);
            } catch (ExecutionException e) {
                Log.e(TAG,e.getMessage());
            } catch (InterruptedException e) {
                Log.e(TAG,e.getMessage());
            }

        }
    }

