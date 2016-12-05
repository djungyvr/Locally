package com.example.djung.locally.View.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.djung.locally.R;
import com.example.djung.locally.Utils.DateUtils;
import com.example.djung.locally.Utils.VendorUtils;
import com.example.djung.locally.View.Activities.VendorActivity;
import com.example.djung.locally.View.Interfaces.VendorSaveView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Angy Chung on 2016-12-04.
 */

public class VendorDashboardFragment extends Fragment implements VendorSaveView {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vendor_dashboard, container, false);
        initializeGreeting(view);
        initializePhoto(view);
        initializeCardButtons(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        ((VendorActivity) getActivity()).setActionBarTitle("Locally");
        ((VendorActivity) getActivity()).setAppBarElevation(4);
    }

    private void initializeGreeting(View view) {
        String username =  getArguments().getString("vendor_username");
        String greeting = DateUtils.getTimeGreeting();
        greeting = greeting + ", " + username;

        TextView tv = (TextView) view.findViewById(R.id.vendor_dashboard_greeting);
        tv.setText(greeting);
    }

    private void initializePhoto(View view) {
        String vendorName = getArguments().getString("vendor_name");
        String marketName = getArguments().getString("market_name");
        String vendorPhotoUrl = getArguments().getString("vendor_photo_url");

        if(vendorPhotoUrl != null && !vendorPhotoUrl.equals("PLACEHOLDER")) {
            vendorPhotoUrl = VendorUtils.getS3Url(marketName, vendorName);
        }

        ImageView iv = (ImageView) view.findViewById(R.id.vendor_dashboard_photo);
        Picasso.with(getContext()).setIndicatorsEnabled(true);
        Picasso.with(getContext()).setLoggingEnabled(true);
        Picasso.with(getContext()).load(vendorPhotoUrl)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.ic_broken_image)
                .into(iv);
    }

    private void initializeCardButtons(View view) {
        CardView cv1 = (CardView) view.findViewById(R.id.vendor_dashboard_card_edit_info);
        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((VendorActivity) getActivity()).launchEditDetailsFragment();
            }
        });

        CardView cv2 = (CardView) view.findViewById(R.id.vendor_dashboard_card_edit_list);
        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((VendorActivity) getActivity()).launchEditGoodsFragment();
            }
        });

        CardView cv3 = (CardView) view.findViewById(R.id.vendor_dashboard_card_sign_out);
        cv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((VendorActivity) getActivity()).showSignOutDialog();
            }
        });
    }

   @Override
    public boolean needSave() {
       return false;
   }

    @Override
    public void saveChanges() {
        // do nothing
    }
}

