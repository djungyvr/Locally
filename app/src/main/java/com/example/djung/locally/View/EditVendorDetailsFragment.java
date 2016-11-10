package com.example.djung.locally.View;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;

import java.util.concurrent.ExecutionException;

/**
 * This fragment is for vendors to edit their own details
 *
 * Created by David Jung on 09/11/16.
 */

public class EditVendorDetailsFragment extends Fragment implements View.OnClickListener{

    private final String TAG = "EditDetailsFragment";

    private FloatingActionButton mFabSaveDetails;
    private TextInputEditText mEditTextDescription;
    private String mVendorName;
    private String mMarketName;
    private String mPastVendorDescription;
    private AlertDialog mSaveDescriptionDialog;

    public EditVendorDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_vendor_details_fragment, container, false);
        mFabSaveDetails = (FloatingActionButton) view.findViewById(R.id.fab_save_vendor_details);
        mFabSaveDetails.setOnClickListener(this);

        mEditTextDescription = (TextInputEditText) view.findViewById(R.id.edit_text_vendor_description);

        mVendorName = getArguments().getString("vendor_name");
        mMarketName = getArguments().getString("market_name");
        mPastVendorDescription = getArguments().getString("vendor_description");

        if(mPastVendorDescription != null && !mPastVendorDescription.isEmpty()) {
            mEditTextDescription.setText(mPastVendorDescription);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.fab_save_vendor_details:
                String description = mEditTextDescription.getText().toString();

                VendorPresenter vendorPresenter = new VendorPresenter(getContext());
                try {
                    boolean successfullyAdded = vendorPresenter.updateVendorDetails(mMarketName,mVendorName,description);
                    if(successfullyAdded)
                        Toast.makeText(getContext(),"Updated Description",Toast.LENGTH_SHORT).show();
                } catch (ExecutionException | InterruptedException e) {
                    showDialogMessage("Error", "Failed to update description");
                    Log.e(TAG,"Couldn't update : " + mVendorName);
                }
                break;
        }
    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    mSaveDescriptionDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        mSaveDescriptionDialog = builder.create();
        mSaveDescriptionDialog.show();
    }
}
