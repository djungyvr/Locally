package com.example.djung.locally.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Utils.DateUtils;
import com.example.djung.locally.Utils.MarketUtils;
import com.example.djung.locally.Utils.ThreadUtils;
import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.VendorUtils;
import com.example.djung.locally.View.Adapters.VendorProduceListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Andy Lin on 2016-11-07.
 */

public class VendorDetailsFragment extends Fragment implements View.OnClickListener{
    private List<String> produceList;
    private Vendor currentVendor;
    private String vendorHours;
    private String vendorAddress;
    private String vendorDatesOpen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String marketName = getArguments().getString("marketName");
        String vendorName = getArguments().getString("vendorName");
        vendorAddress = getArguments().getString("marketAddress");
        vendorHours = getArguments().getString("marketHours");
        vendorDatesOpen = getArguments().getString("marketDatesOpen");

        View view = inflater.inflate(R.layout.vendor_details, container, false);

        getVendor(marketName, vendorName);
        populateViews(view);
        addButtonListeners(view);
        ((MainActivity) getActivity()).setActionBarTitle(marketName);
        ((MainActivity) getActivity()).setAppBarElevation(4);
        return view;
    }

    /**
     * Fetch the vendor details of the vendor with the given market name and vendor name from the database
     * @param marketName
     * @param vendorName
     */
    public void getVendor(String marketName, String vendorName){
        VendorPresenter presenter = new VendorPresenter(this.getContext());
        currentVendor = new Vendor();

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

        //Get the list of products that the vendor sells
        produceList = new ArrayList<String>();
        List<String> produceItemsSet = VendorUtils.filterPlaceholderText(new ArrayList<String>(currentVendor.getItemSet()));
        for (String item: produceItemsSet){
            produceList.add(item);
        }
    }

    /**
     * Populate the text inside the buttons and views based on the details of the vendor
     * @param view
     */
    public void populateViews(View view){
        TextView vendorNameView = (TextView) view.findViewById(R.id.vendor_detail_banner_name);
        vendorNameView.setText(currentVendor.getName());

        TextView vendorDescriptionView = (TextView) view.findViewById(R.id.vendor_detail_description);
        vendorDescriptionView.setText(currentVendor.getDescription());

        Button vendorLocationButton = (Button) view.findViewById(R.id.vendor_detail_location_button);
        vendorLocationButton.setText(vendorAddress);

        TextView vendorStatusView = (TextView) view.findViewById(R.id.vendor_detail_banner_status);
        if (MarketUtils.isMarketCurrentlyOpen(vendorDatesOpen, vendorHours)){
            vendorStatusView.setText("Open Now!");
        }
        else {
            vendorStatusView.setText("Closed Now!");
        }

        TextView vendorHoursView = (TextView) view.findViewById(R.id.vendor_detail_hours);
        vendorHoursView.setText(DateUtils.parseHours(vendorHours));
    }

    /**
     * Add button listeners to each of the buttons in the view
     * @param view
     */
    public void addButtonListeners(View view){
        Button hoursButton = (Button) view.findViewById(R.id.vendor_detail_hours_button);
        hoursButton.setOnClickListener(this);

        Button descriptionButton = (Button) view.findViewById(R.id.vendor_detail_description_button);
        descriptionButton.setOnClickListener(this);

        Button produceListButton = (Button) view.findViewById(R.id.vendor_detail_produce_list_button);
        produceListButton.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        populateProduceList(produceList);
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

    /**
     * Populate the RecyclerView with the produce names in the given produceList
     * @param produceList
     */
    public void populateProduceList(List<String> produceList){
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.produce_list);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        VendorProduceListAdapter adapter = new VendorProduceListAdapter(produceList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Toggle the visibility of the description
     */
    public void toggleDescription(){
        TextView description = (TextView) getView().findViewById(R.id.vendor_detail_description);
        if (description.getVisibility() == View.VISIBLE){
            description.setVisibility(View.GONE);
        }
        else {
            description.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Toggle the visibility of the hours
     */
    public void toggleHours(){
        TextView description = (TextView)  getView().findViewById(R.id.vendor_detail_hours);
        if (description.getVisibility() == View.VISIBLE){
            description.setVisibility(View.GONE);
        }
        else {
            description.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Toggle the visibility of the produce list
     */
    public void toggleProduceList(){
        RecyclerView description = (RecyclerView)  getView().findViewById(R.id.produce_list);
        if (description.getVisibility() == View.VISIBLE){
            description.setVisibility(View.GONE);
        }
        else {
            description.setVisibility(View.VISIBLE);
        }
    }
}
