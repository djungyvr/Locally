package com.example.djung.locally.View;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.example.djung.locally.AWS.AppHelper;
import com.example.djung.locally.R;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "LoginActivity";



    // The Idling Resource which will be null in production.
    @Nullable
    private CountingIdlingResource mIdlingResource = new CountingIdlingResource("LOGIN IDLING RESOURCE");

    // Fields
    private EditText mEditTextUsername;
    private EditText mEditTextPassword;

    // Text buttons
    private TextView mTextViewSignUp;
    private TextView mTextViewForgotPassword;

    private Button mButtonLogin;

    private ProgressDialog mWaitDialog;

    private String username;
    private String password;

    //Continuations
    private MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation;
    private ForgotPasswordContinuation forgotPasswordContinuation;
    private NewPasswordContinuation newPasswordContinuation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViews();

        // Initialize app helper
        // used for testing
        AppHelper.initialize(getApplicationContext());
    }

    /**
     * Initialize the views on this activity
     */
    private void initializeViews() {
        mEditTextUsername = (EditText) findViewById(R.id.edit_text_username);
        mEditTextPassword = (EditText) findViewById(R.id.edit_text_password);

        mButtonLogin = (Button) findViewById(R.id.button_login);
        mButtonLogin.setOnClickListener(this);

        mTextViewSignUp = (TextView) findViewById(R.id.text_view_signup);
        mTextViewSignUp.setOnClickListener(this);

        mTextViewForgotPassword = (TextView) findViewById(R.id.text_view_forgot_password);
        mTextViewForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.text_view_forgot_password:
                Snackbar.make(view, "Replace with forgot password", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //forgotPassword();
                break;
            case R.id.text_view_signup:
                signupVendor();
                break;
            case R.id.button_login:
                signinVendor();
                break;
        }
    }

    /**
     * Launch vendor sign
     */
    private void signinVendor() {
        String username = mEditTextUsername.getText().toString();

        if(username == null || username.length() < 1) {
            TextView label = (TextView) findViewById(R.id.text_view_username_message);
            label.setText(mEditTextUsername.getHint()+" cannot be empty");
            //mEditTextPassword.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        AppHelper.setUser(username);

        String password = mEditTextPassword.getText().toString();

        if(password == null || password.length() < 1) {
            TextView label = (TextView) findViewById(R.id.text_view_password_message);
            label.setText(mEditTextPassword.getHint()+" cannot be empty");
            //mEditTextPassword.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        showWaitDialog("Signing in...");
        // Background work starting so increment the idler
        mIdlingResource.increment();
        AppHelper.getCognitoUserPool().getUser(username).getSessionInBackground(authenticationHandler);
    }

    /**
     * Launch forgot password activity
     */
    private void forgotPassword() {
        String username = mEditTextUsername.getText().toString();

        if(username == null || username.length() < 1) {
            TextView label = (TextView) findViewById(R.id.text_view_username_message);
            label.setText(mEditTextUsername.getHint()+" cannot be empty");
            //mEditTextPassword.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        showWaitDialog("");
        AppHelper.getCognitoUserPool().getUser(username).forgotPasswordInBackground(forgotPasswordHandler);
    }

    /**
     * Launch confirm vendor activity
     */
    private void confirmVendor() {
        throw new UnsupportedOperationException("confirmVendor not implemented");
    }

    // Callbacks
    ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
        @Override
        public void onSuccess() {
            closeWaitDialog();
            showDialogMessage("Password successfully changed!","");
            mEditTextPassword.setText("");
            mEditTextPassword.requestFocus();
        }

        @Override
        public void getResetCode(ForgotPasswordContinuation forgotPasswordContinuation) {
            closeWaitDialog();
            getForgotPasswordCode(forgotPasswordContinuation);
        }

        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            showDialogMessage("Forgot password failed",AppHelper.formatException(e));
        }
    };

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice cognitoDevice) {

            Log.e(TAG,"Authentication Success");
            showDialogMessage("Sign-in successful!", " ");
            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.setNewDevice(cognitoDevice);
            closeWaitDialog();

            // Login successful but background task done so we decrement the idler
            mIdlingResource.decrement();

            // Launch the vendor activity
            launchVendor();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String s) {
            closeWaitDialog();
            Locale.setDefault(Locale.CANADA);
            // Get user authentication
            getUserAuthentication(authenticationContinuation, s);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            closeWaitDialog();
            // Multifactor authentication
            showDialogMessage("Message MFA","Get MFA Code");
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation challengeContinuation) {
            if ("NEW_PASSWORD_REQUIRED".equals(challengeContinuation.getChallengeName())) {
                // This is the first sign-in attempt for an admin created user
                newPasswordContinuation = (NewPasswordContinuation) challengeContinuation;
                AppHelper.setUserAttributeForDisplayFirstLogIn(newPasswordContinuation.getCurrentUserAttributes(),
                        newPasswordContinuation.getRequiredAttributes());
                closeWaitDialog();
                firstTimeSignIn();
            }
        }

        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            TextView label = (TextView) findViewById(R.id.text_view_username_message);
            label.setText("Sign-in failed");
            //mEditTextPassword.setBackground(getDrawable(R.drawable.text_border_error))

            label = (TextView) findViewById(R.id.text_view_username_message);
            label.setText("Sign-in failed");

            showDialogMessage("Sign-in failed", AppHelper.formatException(e));

            // Login failed but background task done so we decrement the idler
            mIdlingResource.decrement();
        }
    };


    // Dialog stuff below here
    private void showWaitDialog(String message) {
        closeWaitDialog();
        mWaitDialog = new ProgressDialog(this);
        mWaitDialog.setTitle(message);
        mWaitDialog.show();
    }

    private void closeWaitDialog() {
        try {
            mWaitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }

    private Dialog mLoginDialog;

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    mLoginDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        mLoginDialog = builder.create();
        mLoginDialog.show();
    }

    // Launch other activities from here

    /**
     * Launch vendor activity
     */
    private void launchVendor() {
        Intent vendorActivity = new Intent(this, VendorActivity.class);
        startActivityForResult(vendorActivity, 1);
        finish();
    }

    /**
     * Launch vendor sign up activity
     */
    private void signupVendor() {
        Intent registerActivity = new Intent(this, RegisterActivity.class);
        startActivityForResult(registerActivity, 1);
    }

    /**
     * Launch first time sign in activity
     */
    private void firstTimeSignIn() {
        //Intent newPasswordActivity = new Intent(this, NewPassword.class);
        //startActivityForResult(newPasswordActivity, 6);
    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if(username != null) {
            this.username = username;
            AppHelper.setUser(username);
        }
        if(this.password == null) {
            mEditTextUsername.setText(username);
            password = mEditTextPassword.getText().toString();
            if(password == null) {
                TextView label = (TextView) findViewById(R.id.text_view_password_message);
                label.setText(mEditTextPassword.getHint()+" enter password");
                //mEditTextPassword.setBackground(getDrawable(R.drawable.text_border_error));
                return;
            }

            if(password.length() < 1) {
                TextView label = (TextView) findViewById(R.id.text_view_password_message);
                label.setText(mEditTextPassword.getHint()+" enter password");
                //mEditTextPassword.setBackground(getDrawable(R.drawable.text_border_error));
                return;
            }
        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    /**
     * Launch forgot password activity
     */
    private void getForgotPasswordCode(ForgotPasswordContinuation forgotPasswordContinuation) {
//        this.forgotPasswordContinuation = forgotPasswordContinuation;
//        Intent intent = new Intent(this, ForgotPasswordActivity.class);
//        intent.putExtra("destination",forgotPasswordContinuation.getParameters().getDestination());
//        intent.putExtra("deliveryMed", forgotPasswordContinuation.getParameters().getDeliveryMedium());
//        startActivityForResult(intent, 3);
    }

    public CountingIdlingResource getIdlingResource() {
        return mIdlingResource;
    }
}
