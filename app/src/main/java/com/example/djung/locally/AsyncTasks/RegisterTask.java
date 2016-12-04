package com.example.djung.locally.AsyncTasks;

import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.example.djung.locally.AWS.AppHelper;

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

    public RegisterTask(RegisterTaskCallback callback, CognitoUserAttributes userAttributes) {
        mSyncObject = new Object();
        mCallback = callback;
        mResult = REGISTER_CODES.PENDING;
        mMessage = "";
        mUserAttributes = userAttributes;
    }

    @Override
    protected REGISTER_CODES doInBackground(String... params) {
        String username = params[0];
        String password = params[1];

        AppHelper.getCognitoUserPool().signUpInBackground(username, password, mUserAttributes, null, mSignupHandler);

        synchronized (mSyncObject) {
            try {
                mSyncObject.wait();
            } catch (InterruptedException e) {
                mMessage = "Thread interrupted!";
                mResult = RegisterTask.REGISTER_CODES.FAIL;
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
}
