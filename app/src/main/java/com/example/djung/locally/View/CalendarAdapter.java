package com.example.djung.locally.View;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.DateUtils;

import java.util.ArrayList;

import static com.example.djung.locally.Utils.DateUtils.parseYear;

/**
 * Created by Anna on 04.11.2016.
 */

public class CalendarAdapter extends BaseAdapter {
    ArrayList<Market> entry;
    Context myContext;

    public CalendarAdapter(Context context, ArrayList<Market> entry) {
        this.myContext = context;
        this.entry=entry;
    }
    public int getCount() {
        return entry.size();
    }

    public Object getItem(int position) {
        return entry.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Market marketItem = entry.get(position);

        if (convertView == null) {
            // get reference to the activity
            LayoutInflater inflater = ((Activity) myContext).getLayoutInflater();
            // Inflate the custom text which will replace the default text view
            convertView = inflater.inflate(R.layout.market_item,null);
        }

        TextView name = (TextView) convertView.findViewById(R.id.market_name);
        TextView location = (TextView) convertView.findViewById(R.id.market_location);
        TextView dates = (TextView) convertView.findViewById(R.id.market_dates);
        TextView days = (TextView) convertView.findViewById(R.id.market_days);
        TextView id = (TextView) convertView.findViewById(R.id.market_id);

        id.setText(String.valueOf(marketItem.getId()));
        name.setText(marketItem.getName());
        location.setText(marketItem.getAddress());
        dates.setText(parseYear(marketItem.getYearOpen()));
        days.setText(DateUtils.parseHours(marketItem.getDailyHours()));

        return convertView;
    }

}