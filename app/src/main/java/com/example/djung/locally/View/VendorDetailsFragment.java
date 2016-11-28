package com.example.djung.locally.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.djung.locally.Presenter.VendorDetailsPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.View.Adapters.VendorProduceListAdapter;

import java.util.List;

/**
 * Created by Andy Lin on 2016-11-07.
 */

public class VendorDetailsFragment extends Fragment implements VendorDetailsView{
    private VendorDetailsPresenter vendorDetailsPresenter;
    private MainActivity mainActivity;
    private RecyclerView recyclerView;

    private Button hoursButton;
    private Button descriptionButton;
    private Button produceListButton;
    private Button phoneButton;
    private Button emailButton;
    private Button vendorLocationButton;

    private TextView vendorNameView;
    private TextView vendorDescriptionView;
    private TextView vendorStatusView;
    private TextView vendorHoursView ;


    @Override
    public void setActionBarTitle(String title) {
        mainActivity.setActionBarTitle(title);
        mainActivity.setAppBarElevation(4);
    }

    /**
     * Populate the RecyclerView with the produce names in the given produceList
     * @param produceList
     */
    @Override
    public void showProduceList(List<String> produceList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        VendorProduceListAdapter adapter = new VendorProduceListAdapter(produceList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showVendorName(String vendorName) {
        vendorNameView.setText(vendorName);
    }

    @Override
    public void showVendorDescription(String vendorDescription) {
        vendorDescriptionView.setText(vendorDescription);
    }

    @Override
    public void showVendorLocation(String vendorLocation) {
        vendorLocationButton.setText(vendorLocation);
    }

    @Override
    public void showVendorStatus(String vendorStatus) {
        vendorStatusView.setText(vendorStatus);
    }

    @Override
    public void showVendorHours(String vendorHours) {
        vendorHoursView.setText(vendorHours);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vendor_details, container, false);

        vendorDetailsPresenter = new VendorDetailsPresenter(getActivity(), this, getArguments());
        this.mainActivity = (MainActivity) getActivity();
        this.recyclerView = (RecyclerView) view.findViewById(R.id.produce_list);

        //Set up Text Views
        this.vendorNameView = (TextView) view.findViewById(R.id.vendor_detail_banner_name);
        this.vendorDescriptionView = (TextView) view.findViewById(R.id.vendor_detail_description);
        this.vendorLocationButton = (Button) view.findViewById(R.id.vendor_detail_location_button);
        this.vendorStatusView = (TextView) view.findViewById(R.id.vendor_detail_banner_status);
        this.vendorHoursView = (TextView) view.findViewById(R.id.vendor_detail_hours);

        //Set up Buttons
        this.hoursButton = (Button) view.findViewById(R.id.vendor_detail_hours_button);
        hoursButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleHours();
            }
        });

        this.descriptionButton = (Button) view.findViewById(R.id.vendor_detail_description_button);
        descriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleDescription();
            }
        });

        this.produceListButton = (Button) view.findViewById(R.id.vendor_detail_produce_list_button);
        produceListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleProduceList();
            }
        });

        this.phoneButton = (Button) view.findViewById(R.id.vendor_detail_phone_button);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vendorDetailsPresenter.callVendor();
            }
        });

        this.emailButton = (Button) view.findViewById(R.id.vendor_detail_email_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vendorDetailsPresenter.emailVendor();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        vendorDetailsPresenter.setActionBar();
        vendorDetailsPresenter.getVendor();
        vendorDetailsPresenter.setViews();
        vendorDetailsPresenter.populateProduceList();
    }

    /**
     * Toggle the visibility of the description
     */
    public void toggleDescription(){
        if (vendorDescriptionView.getVisibility() == View.VISIBLE){
            vendorDescriptionView.setVisibility(View.GONE);
        }
        else {
            vendorDescriptionView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Toggle the visibility of the hours
     */
    public void toggleHours(){
        if (vendorHoursView.getVisibility() == View.VISIBLE){
            vendorHoursView.setVisibility(View.GONE);
        }
        else {
            vendorHoursView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Toggle the visibility of the produce list
     */
    public void toggleProduceList(){
        if (recyclerView.getVisibility() == View.VISIBLE){
            recyclerView.setVisibility(View.GONE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
