package com.example.djung.locally.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.example.djung.locally.AWS.AppHelper;
import com.example.djung.locally.Model.Vendor;
import com.example.djung.locally.Presenter.VendorPresenter;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by David Jung on 25/11/16.
 */

public class RegisterTask extends AsyncTask<String,Void, RegisterTask.REGISTER_CODES> {
    public enum REGISTER_CODES {
        SUCCESS,
        FAIL,
        PENDING
    }

    public interface RegisterTaskCallback {
        void done(REGISTER_CODES code, String message);
    }

    private final String TAG = "RegisterTask";

    // Synchronized object
    private final Object mSyncObject;

    // Callback
    private RegisterTaskCallback mCallback;

    // Signup result
    private REGISTER_CODES mResult;
    private String mMessage;

    // User attributes
    private CognitoUserAttributes mUserAttributes;

    private Context mContext;

    public RegisterTask(RegisterTaskCallback callback, CognitoUserAttributes userAttributes, Context context) {
        mSyncObject = new Object();
        mCallback = callback;
        mResult = REGISTER_CODES.PENDING;
        mMessage = "";
        mUserAttributes = userAttributes;
        mContext = context;
    }

    @Override
    protected REGISTER_CODES doInBackground(String... params) {
        String username = params[0];
        String password = params[1];

        boolean isUnique = isVendorNameUnique(mUserAttributes.getAttributes().get("custom:market_name"),mUserAttributes.getAttributes().get("custom:vendor_name"));

        if(isUnique) {
            AppHelper.getCognitoUserPool().signUpInBackground(username, password, mUserAttributes, null, mSignupHandler);

            synchronized (mSyncObject) {
                try {
                    mSyncObject.wait();
                } catch (InterruptedException e) {
                    mMessage = "Thread interrupted!";
                    mResult = RegisterTask.REGISTER_CODES.FAIL;
                }
            }
        }

        return mResult;
    }

    @Override
    protected void onPostExecute(REGISTER_CODES register_codes) {
        switch (register_codes) {
            case SUCCESS:
                mCallback.done(mResult, mMessage);
                break;
            case FAIL:
                mCallback.done(mResult, mMessage);
                break;
        }
        super.onPostExecute(register_codes);
    }

    // Callbacks
    private SignUpHandler mSignupHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            mMessage = "Success";
            mResult = REGISTER_CODES.SUCCESS;
            // Values have been set stop waiting
            synchronized (mSyncObject) {
                mSyncObject.notify();
            }
        }

        @Override
        public void onFailure(Exception exception) {
            mMessage = AppHelper.formatException(exception);
            mResult = REGISTER_CODES.FAIL;
            // Values have been set stop waiting
            synchronized (mSyncObject) {
                mSyncObject.notify();
            }
        }
    };

    /**
     * @param marketName name of market chosen
     * @param vendorName name of vendor
     * @return true if vendorName is unique within the market, false otherwise
     */
    private boolean isVendorNameUnique(String marketName, String vendorName) {
        String vendorLowerCase = vendorName.toLowerCase();
        try {
            List<Vendor> vendorList = new VendorPresenter(mContext).fetchVendors(marketName);
            for(Vendor v : vendorList) {
                if(v.getName().toLowerCase().equals(vendorLowerCase)) {
                    mMessage = "Vendor not unique to market";
                    mResult = REGISTER_CODES.FAIL;
                    return false;
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, AppHelper.formatException(e));
        }

        mMessage = "";
        mResult = REGISTER_CODES.SUCCESS;
        return true;
    }
}
