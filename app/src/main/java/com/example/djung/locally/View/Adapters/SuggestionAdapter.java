package com.example.djung.locally.View.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.djung.locally.DB.VendorItemDatabase;
import com.example.djung.locally.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom adapter for search suggestions
 *
 * Created by David Jung on 08/11/16.
 */
public class SuggestionAdapter extends CursorAdapter {
    private TextView text;
    private List<String> items;

    public SuggestionAdapter(Context context, Cursor cursor)
    {
        super(context, cursor, false);
        items = new ArrayList<>();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        String vendorItem = cursor.getString(cursor.getColumnIndex(VendorItemDatabase.KEY_VENDOR_ITEM_NAME));
        text.setText(vendorItem);
        items.add(vendorItem);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.result, parent, false);

        text = (TextView)view.findViewById(R.id.vendor_item_name_search_result);

        return view;
    }

    public String getSuggestion(int position) {
        return items.get(position);
    }
}