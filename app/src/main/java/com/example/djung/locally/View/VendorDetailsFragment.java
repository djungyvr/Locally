package com.example.djung.locally.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Utils.MarketUtils;
import com.example.djung.locally.Utils.ThreadUtils;
import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by Andy Lin on 2016-11-07.
 */

public class VendorDetailsFragment extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String marketName = getArguments().getString("marketName");
        String vendorName = getArguments().getString("vendorName");
        String vendorAddress = getArguments().getString("marketAddress");
        String vendorHours = getArguments().getString("marketHours");
        String vendorDatesOpen = getArguments().getString("marketDatesOpen");

        View view = inflater.inflate(R.layout.vendor_details, container, false);

        VendorPresenter presenter = new VendorPresenter(this.getContext());
        Vendor currentVendor = new Vendor();

        try {
            currentVendor = presenter.fetchVendor(marketName,vendorName);

        } catch (final ExecutionException ee) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Execute Exception")
                            .setMessage(ee.getMessage())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            });
        } catch (final InterruptedException ie) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Interrupted Exception")
                            .setMessage(ie.getMessage())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            });
        }

        TextView vendorNameView = (TextView) view.findViewById(R.id.vendor_detail_banner_name);
        vendorNameView.setText(currentVendor.getName());

        TextView vendorStatusView = (TextView) view.findViewById(R.id.vendor_detail_banner_status);
        if (MarketUtils.isMarketCurrentlyOpen(vendorDatesOpen, vendorHours)){
            vendorStatusView.setText("Open Now!");
        }
        else {
            vendorStatusView.setText("Closed Now!");
        }

        Button vendorLocationButton = (Button) view.findViewById(R.id.vendor_detail_location_button);
        vendorLocationButton.setText(vendorAddress);

        TextView vendorHoursView = (TextView) view.findViewById(R.id.vendor_detail_hours);
        vendorHoursView.setText(vendorHours);

        TextView vendorDescriptionView = (TextView) view.findViewById(R.id.vendor_detail_description);
        vendorDescriptionView.setText(currentVendor.getDescription());

        Button hoursButton = (Button) view.findViewById(R.id.vendor_detail_hours_button);
        hoursButton.setOnClickListener(this);

        Button descriptionButton = (Button) view.findViewById(R.id.vendor_detail_description_button);
        descriptionButton.setOnClickListener(this);

        Button produceListButton = (Button) view.findViewById(R.id.vendor_detail_produce_list_button);
        produceListButton.setOnClickListener(this);


        //Populate produce list using data from Database
        ListView produceListView = (ListView) view.findViewById(R.id.produce_list);
        ArrayList<String> produceListAdapter = new ArrayList<String>();

        Set<String> produceItemsSet = currentVendor.getItemSet();

        for (String item: produceItemsSet){
            produceListAdapter.add(item);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, produceListAdapter );
        produceListView.setAdapter(arrayAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vendor_detail_hours_button:
                toggleHours();
                break;
            case R.id.vendor_detail_description_button:
                toggleDescription();
                break;
            case R.id.vendor_detail_produce_list_button:
                toggleProduceList();
                break;
        }
    }

    public void toggleDescription(){
        TextView description = (TextView) getView().findViewById(R.id.vendor_detail_description);
        if (description.getVisibility() == View.VISIBLE){
            description.setVisibility(View.GONE);
        }
        else {
            description.setVisibility(View.VISIBLE);
        }
    }

    public void toggleHours(){
        TextView description = (TextView)  getView().findViewById(R.id.vendor_detail_hours);
        if (description.getVisibility() == View.VISIBLE){
            description.setVisibility(View.GONE);
        }
        else {
            description.setVisibility(View.VISIBLE);
        }
    }

    public void toggleProduceList(){
        ListView description = (ListView)  getView().findViewById(R.id.produce_list);
        if (description.getVisibility() == View.VISIBLE){
            description.setVisibility(View.GONE);
        }
        else {
            description.setVisibility(View.VISIBLE);
        }
    }
}
