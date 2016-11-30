package com.example.djung.locally.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.djung.locally.AWS.AppHelper;
import com.example.djung.locally.AWS.AwsConfiguration;
import com.example.djung.locally.Utils.FileUtils;
import com.example.djung.locally.Utils.VendorUtils;

import java.io.File;

/**
 * Task that handles uploading files to the s3 bucket
 *
 * Created by David Jung on 28/11/16.
 */

public class UploadImageTask extends AsyncTask<Context,Void, UploadImageTask.UploadCodes> {
    private static final String TAG = "UploadImageTask";

    public enum UploadCodes {
        SUCCESS,
        FAIL
    }

    public interface UploadImageCallback {
        void finishUpload(UploadCodes code, String message);
    }

    private UploadImageCallback mCallback;

    private String mMarketName;
    private String mVendorName;
    private String mImagePath;
    private String mMessage;

    public UploadImageTask(String marketName, String vendorName, String imagePath, UploadImageCallback callback) {
        mMarketName = marketName;
        mVendorName = vendorName;
        mImagePath = imagePath;
        mCallback = callback;
    }

    @Override
    protected UploadCodes doInBackground(Context ... params) {
        Context context = params[0];
        // Upload to S3 bucket
        AmazonS3 s3 = new AmazonS3Client(new AnonymousAWSCredentials());
        TransferUtility transferUtility = new TransferUtility(s3, context);
        try {
            TransferObserver observer = transferUtility.upload(
                    AwsConfiguration.AMAZON_S3_VENDOR_BUCKET,                      // The bucket to upload to
                    VendorUtils.getS3FileNameJpeg(mMarketName, mVendorName),  // The key for the uploaded object
                    new File(mImagePath)  // The file where the data to upload exists
            );
        } catch (Exception e) {
            Log.e(TAG,"Error : \n" + AppHelper.formatException(e));
            mMessage = AppHelper.formatException(e);
            return UploadCodes.FAIL;
        }
        return UploadCodes.SUCCESS;
    }

    @Override
    protected void onPostExecute(UploadCodes uploadCodes) {
        // Call the callback so fragment/activity can handle it
        mCallback.finishUpload(uploadCodes,mMessage);
        super.onPostExecute(uploadCodes);
    }
}
