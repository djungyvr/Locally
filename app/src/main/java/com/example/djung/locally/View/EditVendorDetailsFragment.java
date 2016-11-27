package com.example.djung.locally.View;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;
import com.squareup.picasso.Picasso;

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
    private TextInputEditText mEditTextEmail;
    private TextInputEditText mEditTextPhoneNumber;
    private ImageView mImageViewVendor;

    private String mVendorName;
    private String mMarketName;
    private AlertDialog mSaveDescriptionDialog;

    public EditVendorDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_vendor_details_fragment, container, false);
        mFabSaveDetails = (FloatingActionButton) view.findViewById(R.id.fab_save_vendor_details);
        mFabSaveDetails.setOnClickListener(this);

        mEditTextDescription = (TextInputEditText) view.findViewById(R.id.edit_text_vendor_description);
        mEditTextEmail = (TextInputEditText) view.findViewById(R.id.edit_text_edit_email);
        mEditTextPhoneNumber = (TextInputEditText) view.findViewById(R.id.edit_text_edit_phone);
        mImageViewVendor = (ImageView) view.findViewById(R.id.image_view_edit_vendor_image);

        // For phone number formatting see here http://stackoverflow.com/a/34907607
        mEditTextPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            //we need to know if the user is erasing or inputing some new character
            private boolean backspacingFlag = false;
            //we need to block the :afterTextChanges method to be called again after we just replaced the EditText text
            private boolean editedFlag = false;
            //we need to mark the cursor position and restore it after the edition
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //we store the cursor local relative to the end of the string in the EditText before the edition
                cursorComplement = s.length() - mEditTextPhoneNumber.getSelectionStart();
                //we check if the user ir inputing or erasing a character
                if (count > after) {
                    backspacingFlag = true;
                } else {
                    backspacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                //what matters are the phone digits beneath the mask, so we always work with a raw string with only digits
                String phone = string.replaceAll("[^\\d]", "");

                //if the text was just edited, :afterTextChanged is called another time... so we need to verify the flag of edition
                //if the flag is false, this is a original user-typed entry. so we go on and do some magic
                if (!editedFlag) {

                    //we start verifying the worst case, many characters mask need to be added
                    //example: 999999999 <- 6+ digits already typed
                    // masked: (999) 999-999
                    if (phone.length() >= 6 && !backspacingFlag) {
                        //we will edit. next call on this textWatcher will be ignored
                        editedFlag = true;
                        //here is the core. we substring the raw digits and add the mask as convenient
                        String ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6);
                        mEditTextPhoneNumber.setText(ans);
                        //we deliver the cursor to its original position relative to the end of the string
                        mEditTextPhoneNumber.setSelection(mEditTextPhoneNumber.getText().length() - cursorComplement);
                        //we end at the most simple case, when just one character mask is needed
                        //example: 99999 <- 3+ digits already typed
                        // masked: (999) 99
                    } else if (phone.length() >= 3 && !backspacingFlag) {
                        editedFlag = true;
                        String ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3);
                        mEditTextPhoneNumber.setText(ans);
                        mEditTextPhoneNumber.setSelection(mEditTextPhoneNumber.getText().length() - cursorComplement);
                    }
                } else {
                    editedFlag = false;
                }
            }
        });

        mVendorName = getArguments().getString("vendor_name");
        mMarketName = getArguments().getString("market_name");

        String vendorDescription = getArguments().getString("vendor_description");
        String vendorPhoneNumber = getArguments().getString("vendor_phone_number");
        String vendorEmail = getArguments().getString("vendor_email");
        String vendorPhotoUrl = getArguments().getString("vendor_photo_url");

        if(vendorDescription != null && !vendorDescription.isEmpty()) {
            mEditTextDescription.setText(vendorDescription);
        }

        if(vendorPhoneNumber != null && !vendorPhoneNumber.isEmpty()) {
            mEditTextPhoneNumber.setText(vendorPhoneNumber);
        }

        if(vendorEmail != null && !vendorPhoneNumber.isEmpty()) {
            mEditTextEmail.setText(vendorEmail);
        }

        if(vendorPhotoUrl != null && !vendorPhotoUrl.equals("PLACEHOLDER")) {
            Picasso.with(getContext()).setIndicatorsEnabled(true);
            Picasso.with(getContext()).load(vendorPhotoUrl)
                    .error(R.drawable.default_market_image)
                    .into(mImageViewVendor);
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
                String phoneNumber = mEditTextPhoneNumber.getText().toString();
                String email = mEditTextEmail.getText().toString();

                VendorPresenter vendorPresenter = new VendorPresenter(getContext());
                try {
                    boolean successfullyAdded = vendorPresenter.updateVendorDetails(mMarketName,mVendorName,description,phoneNumber,email);
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
