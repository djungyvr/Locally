package com.example.djung.locally.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Presenter.ThreadUtils;
import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Andy Lin on 2016-11-07.
 */

public class VendorDetailsFragment extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String marketName = getArguments().getString("marketName");
        String vendorName = getArguments().getString("vendorName");

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

        Button hoursButton = (Button) view.findViewById(R.id.vendor_detail_hours_button);
        hoursButton.setOnClickListener(this);

        Button descriptionButton = (Button) view.findViewById(R.id.vendor_detail_description_button);
        descriptionButton.setOnClickListener(this);

        Button produceListButton = (Button) view.findViewById(R.id.vendor_detail_produce_list_button);
        produceListButton.setOnClickListener(this);

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
        RecyclerView description = (RecyclerView)  getView().findViewById(R.id.produce_list);
        if (description.getVisibility() == View.VISIBLE){
            description.setVisibility(View.GONE);
        }
        else {
            description.setVisibility(View.VISIBLE);
        }
    }
}
