package com.example.djung.locally.View.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.djung.locally.AWS.AppHelper;
import com.example.djung.locally.AWS.AwsConfiguration;
import com.example.djung.locally.AsyncTasks.UploadImageTask;
import com.example.djung.locally.Presenter.VendorPresenter;
import com.example.djung.locally.R;
import com.example.djung.locally.Utils.FileUtils;
import com.example.djung.locally.Utils.VendorUtils;
import com.example.djung.locally.View.Activities.VendorActivity;
import com.example.djung.locally.View.Interfaces.VendorSaveView;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * This fragment is for vendors to edit their own details
 *
 * Created by David Jung on 09/11/16.
 */

public class VendorEditDetailsFragment extends Fragment implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks, UploadImageTask.UploadImageCallback, VendorSaveView{
    private static final int REQUEST_READ_EXTERNAL_FILES_KIT_KAT = 2001;
    private static final int REQUEST_READ_EXTERNAL_FILES = 2002;

    private final String TAG = "EditDetailsFragment";

    private FloatingActionButton mFabSaveDetails;
    private TextInputEditText mEditTextDescription;
    private TextInputEditText mEditTextEmail;
    private TextInputEditText mEditTextPhoneNumber;
    private ImageView mImageViewVendor;

    private String mVendorName;
    private String mMarketName;
    private AlertDialog mSaveDescriptionDialog;
    private Uri mImageUri;
    private String mImageUrl;
    private boolean mImageChanged = false;
    private boolean mDescriptionChanged = false;

    public VendorEditDetailsFragment() {
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
        mImageViewVendor.setOnClickListener(this);

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

        mEditTextDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDescriptionChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
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
            vendorPhotoUrl = VendorUtils.getS3Url(mMarketName,mVendorName);
        }

        // Load the image
        Picasso.with(getContext()).setIndicatorsEnabled(true);
        Picasso.with(getContext()).setLoggingEnabled(true);
        Picasso.with(getContext()).load(vendorPhotoUrl)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.ic_broken_image)
                .into(mImageViewVendor);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
        ((VendorActivity) getActivity()).setActionBarTitle("Edit Details");
        ((VendorActivity) getActivity()).setAppBarElevation(4);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.fab_save_vendor_details:
                saveVendorDetails();
                break;
            case R.id.image_view_edit_vendor_image:
                fetchImage();
                break;
        }
    }

    public void saveVendorDetails() {
        String description = mEditTextDescription.getText().toString();
        String phoneNumber = mEditTextPhoneNumber.getText().toString();
        String email = mEditTextEmail.getText().toString();
        mImageUrl = VendorUtils.getS3Url(mMarketName,mVendorName);
        if(mImageChanged)
            readFile();

        VendorPresenter vendorPresenter = new VendorPresenter(getContext());
        try {
            boolean successfullyAdded = vendorPresenter.updateVendorDetails(mMarketName,mVendorName,description,phoneNumber,email, mImageUrl);
            if(successfullyAdded)
                Toast.makeText(getContext(),"Updated Information",Toast.LENGTH_SHORT).show();
        } catch (ExecutionException | InterruptedException e) {
            //TODO: Figure out what's causing this exception
            showDialogMessage("Error", AppHelper.formatException(e));
            //showDialogMessage("Error", "Failed to update description");
            Log.e(TAG,"Couldn't update : " + mImageUrl);
            Log.e(TAG,"Couldn't update : " + mVendorName);
        }
        mImageChanged = false;
        mDescriptionChanged = false;
    }

    @SuppressLint("NewApi")
    private void fetchImage() {
        if (Build.VERSION.SDK_INT <19){
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "SelectPicture"),REQUEST_READ_EXTERNAL_FILES);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            startActivityForResult(intent, REQUEST_READ_EXTERNAL_FILES_KIT_KAT);
        }
    }

    /**
     * Called when the image is chosen
     *
     * @param requestCode permission code
     * @param resultCode result code of selecting image
     * @param data contains the Uri of the selected image
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) {
            mImageChanged = false;
            return;
        }
        if(null == data) {
            mImageChanged = false;
            return;
        }

        // Used to check if an image was actually selected
        mImageChanged = true;

        if(requestCode == REQUEST_READ_EXTERNAL_FILES) {
            mImageUri = data.getData();
        } else if(requestCode == REQUEST_READ_EXTERNAL_FILES_KIT_KAT){
            mImageUri = data.getData();

            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION  | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            getActivity().getContentResolver().takePersistableUriPermission(mImageUri, takeFlags);
        }


        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageUri);
            mImageViewVendor.setImageBitmap(bitmap);
        }catch (IOException e) {
            showDialogMessage("Error", "Error uploading image");
            Log.e(TAG, "Error Uploading " + e.getMessage());
        }

        Log.e(TAG,"Filepath: " + mImageUri);
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

    @AfterPermissionGranted(REQUEST_READ_EXTERNAL_FILES)
    private void readFile() {
        if (EasyPermissions.hasPermissions(
                getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Begin asynchronous task of uploading to the s3 bucket
            new UploadImageTask(mMarketName,mVendorName,FileUtils.getPath(getContext(),mImageUri),this).execute(getContext());
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_READ_EXTERNAL_FILES,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Handles the callback from the UploadImageTask
     *
     * @param code can either be Fail or Success
     * @param message carries the error messsage
     */
    @Override
    public void finishUpload(UploadImageTask.UploadCodes code, String message) {
        if(code == UploadImageTask.UploadCodes.FAIL) {
          //  showDialogMessage("Error",message);
            Log.e(TAG, "Error: " + message);
        } else if(code == UploadImageTask.UploadCodes.SUCCESS) {
          //  showDialogMessage("Success","Image Updated");
            Log.e(TAG, "Success: Image updated");
        }
    }

    @Override
    public boolean needSave(){
        return (mImageChanged || mDescriptionChanged);
    }

    @Override
    public void saveChanges() {
        saveVendorDetails();
    }
}
