package com.example.djung.locally.View;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
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
import com.example.djung.locally.AsyncTasks.LoginTask;
import com.example.djung.locally.R;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginTask.LoginTaskCallback {
    private final String TAG = "LoginActivity";

    // Fields
    private EditText mEditTextUsername;
    private EditText mEditTextPassword;

    // Buttons
    private TextView mTextViewSignUp;
    private TextView mTextViewForgotPassword;
    private Button mButtonLogin;

    // Dialogs
    private ProgressBar mWaitDialog;
    private Dialog mLoginDialog;

    private String mUsername;
    private String mPassword;

    //Continuations
    private ForgotPasswordContinuation forgotPasswordContinuation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViews();

        // Initialize app helper
        AppHelper.initialize(getApplicationContext());

        clearCachedLogins();
    }

    /**
     * Clear cached logins
     */
    private void clearCachedLogins() {
        AppHelper.getCredentialsProvider().clear();
        AppHelper.getCredentialsProvider().clearCredentials();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 3:
                // Forgot password
                if(resultCode == RESULT_OK) {
                    String newPass = data.getStringExtra("newPass");
                    String code = data.getStringExtra("code");
                    if (newPass != null && code != null) {
                        if (!newPass.isEmpty() && !code.isEmpty()) {
                            forgotPasswordContinuation.setPassword(newPass);
                            forgotPasswordContinuation.setVerificationCode(code);
                            forgotPasswordContinuation.continueTask();
                        }
                    }
                }
                break;
        }
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
                forgotPassword();
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
     * Launch vendor sign in
     */
    private void signinVendor() {
        mUsername = mEditTextUsername.getText().toString();

        if(mUsername == null || mUsername.length() < 1) {
            TextView label = (TextView) findViewById(R.id.text_view_username_message);
            label.setText(mEditTextUsername.getHint()+" cannot be empty");
            //mEditTextPassword.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        AppHelper.setUser(mUsername);

        mPassword = mEditTextPassword.getText().toString();

        if(mPassword == null || mPassword.length() < 1) {
            TextView label = (TextView) findViewById(R.id.text_view_password_message);
            label.setText(mEditTextPassword.getHint()+" cannot be empty");
            //mEditTextPassword.setBackground(getDrawable(R.drawable.text_border_error));
            return;
        }

        showWaitDialog("Signing in...");
        new LoginTask(this).execute(mUsername,mPassword);
    }

    /**
     * Launch forgot password activity
     */
    private void forgotPassword() {
        String username = mEditTextUsername.getText().toString();

        if(username == null || username.length() < 1) {
            TextView label = (TextView) findViewById(R.id.text_view_username_message);
            label.setText(mEditTextUsername.getHint()+" cannot be empty");
            return;
        }
        AppHelper.setUser(username);
        AppHelper.getCognitoUserPool().getUser(username).forgotPasswordInBackground(forgotPasswordHandler);
    }

    /**
     * Login Async Task callback
     * @param loginCodes possible codes for logins
     * @param message to display
     */
    @Override
    public void done(LoginTask.LOGIN_CODES loginCodes, String message) {
        closeWaitDialog();
        if(loginCodes == LoginTask.LOGIN_CODES.SUCCESS) {
            showDialogMessage("Sign-in successful!", message);
            launchVendor();
        } else if(loginCodes == LoginTask.LOGIN_CODES.FAIL) {
            showDialogMessage("Sign-in failed", message);
        }
    }

    // Callbacks
    ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
        @Override
        public void onSuccess() {
            showDialogMessage("Password successfully changed!","");
            mEditTextPassword.setText("");
            mEditTextPassword.requestFocus();
        }

        @Override
        public void getResetCode(ForgotPasswordContinuation forgotPasswordContinuation) {
            getForgotPasswordCode(forgotPasswordContinuation);
        }

        @Override
        public void onFailure(Exception e) {
            if(e.getMessage().contains("registered")) {
                verifyUser();
            }
            Log.e(TAG,e.getMessage());
        }
    };

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
     * Launch verify username
     */
    private void verifyUser() {
        Intent verifyActivity = new Intent(this, VerifyActivity.class);
        startActivityForResult(verifyActivity, 1);
    }

    /**
     * Launch forgot password activity
     */
    private void getForgotPasswordCode(ForgotPasswordContinuation forgotPasswordContinuation) {
        this.forgotPasswordContinuation = forgotPasswordContinuation;
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra("destination",forgotPasswordContinuation.getParameters().getDestination());
        intent.putExtra("deliveryMed", forgotPasswordContinuation.getParameters().getDeliveryMedium());
        startActivityForResult(intent, 3);
    }

    // Dialog stuff below here
    private void showWaitDialog(String message) {
        closeWaitDialog();
        if(mWaitDialog == null)
            mWaitDialog = new ProgressBar(this);
        mWaitDialog.setVisibility(View.VISIBLE);
    }

    private void closeWaitDialog() {
        try {
            mWaitDialog.setVisibility(View.GONE);
        }
        catch (Exception e) {
            //
        }
    }

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
}
