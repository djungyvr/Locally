package com.example.djung.locally.View;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.example.djung.locally.AWS.AppHelper;
import com.example.djung.locally.R;

/**
 * Activity to verify the user's email
 *
 * Created by djung on 26/11/16.
 */

public class VerifyActivity extends AppCompatActivity {
    private Button mButtonRequestEmailVerification;
    private Button mSendVerificationCode;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;

    private EditText mEditTextVerificationCode;

    private CognitoUserAttributes mUserAttributes;

    private String attrReqCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit(true);
            }
        });

        init();
    }

    private void init() {
        mEditTextVerificationCode = (EditText) findViewById(R.id.edit_text_verify_code);
        mButtonRequestEmailVerification = (Button) findViewById(R.id.button_verify_request_code);
        mSendVerificationCode = (Button) findViewById(R.id.button_verify_check_code);
            if (AppHelper.isEmailVerified()) {
                mButtonRequestEmailVerification.setClickable(false);
                mButtonRequestEmailVerification.setText("Email verified");
                mButtonRequestEmailVerification.setTextColor(Color.parseColor("#37A51C"));
            } else {
                mButtonRequestEmailVerification.setText("Send code");
                mButtonRequestEmailVerification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reqEmailCode();
                    }
                });
            }
        hideCodeTX();
    }

    private void reqEmailCode() {
        attrReqCode = "email";
        mButtonRequestEmailVerification.setText("Resend code");
        mButtonRequestEmailVerification.setTextColor(Color.parseColor("#2A5C91"));
        reqVerfCode();
    }

    private void reqVerfCode() {
        showWaitDialog("Requesting verification code...");
        AppHelper.getCognitoUserPool().getUser(AppHelper.getUser()).getAttributeVerificationCodeInBackground(attrReqCode, verReqHandler);
    }

    private void sendVerfCode() {
        String code = mEditTextVerificationCode.getText().toString();

        if (code == null || code.length() < 1) {
            TextView label = (TextView) findViewById(R.id.text_view_verify_code_message);
            label.setText(mEditTextVerificationCode.getHint() + " cannot be empty");
            return;
        }

        showWaitDialog("Verifying...");
        hideCodeTX();
        AppHelper.getCognitoUserPool().getUser(AppHelper.getUser()).verifyAttributeInBackground(attrReqCode, code, verHandler);
    }

    private void getDetails() {
        showWaitDialog("Refreshing...");
        AppHelper.getCognitoUserPool().getUser(AppHelper.getUser()).getDetailsInBackground(detailsHandler);
    }

    VerificationHandler verReqHandler = new VerificationHandler() {
        @Override
        public void onSuccess(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Show message
            closeWaitDialog();
            showCodeTX();
            mEditTextVerificationCode.requestFocus();
            showDialogMessage("Verification code sent",
                    "Code was sent to " + cognitoUserCodeDeliveryDetails.getDestination() + " via " + cognitoUserCodeDeliveryDetails.getDeliveryMedium(),
                    false);
        }

        @Override
        public void onFailure(Exception exception) {
            // Show error
            closeWaitDialog();
            showDialogMessage("Verfication code request failed!", exception.toString(), false);
        }
    };

    GenericHandler verHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            // Refresh the screen
            getDetails();
        }

        @Override
        public void onFailure(Exception exception) {
            // Show error
            closeWaitDialog();
            showDialogMessage("Verification failed", AppHelper.formatException(exception), false);
        }
    };

    GetDetailsHandler detailsHandler = new GetDetailsHandler() {
        @Override
        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
            closeWaitDialog();
            // Store details in the AppHandler
            AppHelper.setUserDetails(cognitoUserDetails);
            if (attrReqCode.equals("email")) {
                mButtonRequestEmailVerification.setText("Email verified");
                mButtonRequestEmailVerification.setTextColor(Color.parseColor("#37A51C"));
                mButtonRequestEmailVerification.setClickable(false);
                Toast.makeText(getApplicationContext(), "Email verified", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();

            // Attributes were verified but user detals read was not successful
            if (attrReqCode.equals("email")) {
                mButtonRequestEmailVerification.setText("Email verified");
                mButtonRequestEmailVerification.setTextColor(Color.parseColor("#37A51C"));
                mButtonRequestEmailVerification.setClickable(false);
                Toast.makeText(getApplicationContext(), "Email verified", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void hideCodeTX() {
        mEditTextVerificationCode.setText("");
        mEditTextVerificationCode.setVisibility(View.INVISIBLE);
        mSendVerificationCode.setClickable(false);
        mSendVerificationCode.setVisibility(View.INVISIBLE);
    }

    private void showCodeTX() {
        mEditTextVerificationCode.setVisibility(View.VISIBLE);
        mSendVerificationCode.setClickable(true);
        mSendVerificationCode.setVisibility(View.VISIBLE);
        mSendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerfCode();
            }
        });

        mEditTextVerificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_verify_code_label);
                    label.setText(mEditTextVerificationCode.getHint());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.text_view_verify_code_message);
                label.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_verify_code_label);
                    label.setText("");
                }
            }
        });
    }

    private void showDialogMessage(String title, String body, final boolean exitActivity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if (exitActivity) {
                        exit(true);
                    }
                } catch (Exception e) {
                    exit(true);
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        } catch (Exception e) {
            //
        }
    }

    private void exit(boolean refresh) {
        Intent intent = new Intent();
        intent.putExtra("refresh", refresh);
        setResult(RESULT_OK, intent);
        finish();
    }
}
