package com.example.djung.locally.AsyncTasks;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.example.djung.locally.AWS.AppHelper;
import com.example.djung.locally.R;
import com.example.djung.locally.View.VendorActivity;

import java.util.Locale;

/**
 * Async Task for logging into the application
 *
 * Created by David Jung on 24/11/16.
 */

public class LoginTask extends AsyncTask<String,Void, LoginTask.LOGIN_CODES> {
    private final String TAG = "LoginTask";

    private String mUsername;
    private String mPassword;

    // Stores the login messages
    private String mErrorDetails;

    // Stores the result of the login
    private LOGIN_CODES mResult;

    // Call back to activity that uses this task
    private LoginTaskCallback mCallback;

    // Synchronized object
    private final Object syncObject;

    public enum LOGIN_CODES {
        SUCCESS,
        FAIL,
        PENDING
    }

    public LoginTask(LoginTaskCallback callback) {
        mCallback = callback;
        mResult = LOGIN_CODES.PENDING;
        syncObject = new Object();
    }

    public interface LoginTaskCallback{
        void done(LOGIN_CODES loginCodes, String message);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected LOGIN_CODES doInBackground(String... params) {
        mUsername = params[0];
        mPassword = params[1];

        AppHelper.getCognitoUserPool().getUser(mUsername).getSessionInBackground(authenticationHandler);

        synchronized (syncObject) {
            try {
                syncObject.wait();
            } catch (InterruptedException e) {
                mErrorDetails = "Thread interrupted!";
                mResult = LOGIN_CODES.FAIL;
            }
        }

        return mResult;
    }

    protected void onPostExecute(LOGIN_CODES result) {
        switch (result) {
            case SUCCESS:
                mCallback.done(LOGIN_CODES.SUCCESS, mErrorDetails);
                break;
            case FAIL:
                mCallback.done(LOGIN_CODES.FAIL, mErrorDetails);
                break;
        }
    }

    // Authenticates in the background
    private AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String s) {
            Locale.setDefault(Locale.CANADA);
            // Get user authentication
            getUserAuthentication(authenticationContinuation, s);
        }

        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice cognitoDevice) {
            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.setNewDevice(cognitoDevice);
            mResult = LOGIN_CODES.SUCCESS;
            mErrorDetails = " ";
            // Values have been set stop waiting
            synchronized (syncObject) {
                syncObject.notify();
            }
        }

        @Override
        public void onFailure(Exception e) {
            mResult = LOGIN_CODES.FAIL;
            mErrorDetails = AppHelper.formatException(e);
            // Values have been set stop waiting
            synchronized (syncObject) {
                syncObject.notify();
            }
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            // We do not use this
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation challengeContinuation) {
            // We do not use this
        }
    };

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, mPassword, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }
}
