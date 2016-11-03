package com.example.djung.locally.View;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.example.djung.locally.AWS.AppHelper;
import com.example.djung.locally.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private EditText mEditTextVendorName;
    private EditText mEditTextEmail;
    private EditText mEditTextPhoneNumber;
    private Button mButtonSignup;

    private AlertDialog mDialog;
    private ProgressDialog mWaitDialog;

    private String usernameInput;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initializeFieldsAndViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // get back to main screen
            String value = extras.getString("TODO");
            if (value.equals("exit")) {
                onBackPressed();
            }
        }
    }

    private void initializeFieldsAndViews() {
        mEditTextUserName = (EditText) findViewById(R.id.edit_text_reg_username);
        mEditTextUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_reg_username_label);
                    label.setText(mEditTextUserName.getHint());
                    //mEditTextUserName.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.text_view_reg_username_message);
                label.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_username_label);
                    label.setText("");
                }
            }
        });

        mEditTextPassword = (EditText) findViewById(R.id.edit_text_reg_password);
        mEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_reg_password_label);
                    label.setText(mEditTextPassword.getHint());
                    //mEditTextPassword.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.text_view_reg_password_message);
                label.setText("");

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_reg_password_label);
                    label.setText("");
                }
            }
        });

        mEditTextVendorName = (EditText) findViewById(R.id.edit_text_reg_vendor_name);
        mEditTextVendorName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_reg_vendor_name_label);
                    label.setText(mEditTextVendorName.getHint());
                    //mEditTextVendorName.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.text_view_reg_vendor_name_message);
                label.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_reg_vendor_name_label);
                    label.setText("");
                }
            }
        });

        mEditTextEmail = (EditText) findViewById(R.id.edit_text_reg_email);
        mEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_reg_email_label);
                    label.setText(mEditTextEmail.getHint());
                    //mEditTextEmail.setBackground(getDrawable(R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.text_view_reg_email_message);
                label.setText("");

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_reg_email_label);
                    label.setText("");
                }
            }
        });

        mEditTextPhoneNumber = (EditText) findViewById(R.id.edit_text_reg_phone);
        mEditTextPhoneNumber.addTextChangedListener(new TextWatcher() {
                                         @Override
                                         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                             if (s.length() == 0) {
                                                 TextView label = (TextView) findViewById(R.id.text_view_reg_phone_label);
                                                 label.setText(mEditTextPhoneNumber.getHint() + " with country code and no seperators");
                                                 //mEditTextPhoneNumber.setBackground(getDrawable(R.drawable.text_border_selector));
                                             }
                                         }

                                         @Override
                                         public void onTextChanged(CharSequence s, int start, int before, int count) {
                                             TextView label = (TextView) findViewById(R.id.text_view_reg_phone_message);
                                             label.setText("");
                                         }

                                         @Override
                                         public void afterTextChanged(Editable s) {
                                             if (s.length() == 0) {
                                                 TextView label = (TextView) findViewById(R.id.text_view_reg_phone_label);
                                                 label.setText("");
                                             }
                                         }
        });


        mButtonSignup = (Button) findViewById(R.id.button_reg_signup);
        mButtonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read user data and register
                CognitoUserAttributes userAttributes = new CognitoUserAttributes();

                usernameInput = mEditTextUserName.getText().toString();
                if (usernameInput == null || usernameInput.isEmpty()) {
                    TextView view = (TextView) findViewById(R.id.text_view_reg_username_message);
                    view.setText(mEditTextUserName.getHint() + " cannot be empty");
                    //username.setBackground(getDrawable(R.drawable.text_border_error));
                    return;
                }

                String userpasswordInput = mEditTextPassword.getText().toString();
                userPassword = userpasswordInput;
                if (userpasswordInput == null || userpasswordInput.isEmpty()) {
                    TextView view = (TextView) findViewById(R.id.text_view_password_message);
                    view.setText(mEditTextPassword.getHint() + " cannot be empty");
                    //mEditTextPassword.setBackground(getDrawable(R.drawable.text_border_error));
                    return;
                }

                String userInput = mEditTextVendorName.getText().toString();
                if (userInput != null) {
                    if (userInput.length() > 0) {
                        userAttributes.addAttribute(AppHelper.getSignUpFieldsC2O().get(mEditTextVendorName.getHint()).toString(), userInput);
                    }
                }

                userInput = mEditTextEmail.getText().toString();
                if (userInput != null) {
                    if (userInput.length() > 0) {
                        userAttributes.addAttribute(AppHelper.getSignUpFieldsC2O().get(mEditTextEmail.getHint()).toString(), userInput);
                    }
                }

                userInput = mEditTextPhoneNumber.getText().toString();
                if (userInput != null) {
                    if (userInput.length() > 0) {
                        userAttributes.addAttribute(AppHelper.getSignUpFieldsC2O().get(mEditTextPhoneNumber.getHint()).toString(), userInput);
                    }
                }

                showWaitDialog("Signing up...");

                AppHelper.getCognitoUserPool().signUpInBackground(usernameInput, userpasswordInput, userAttributes, null, signUpHandler);

            }
        });
    }

    // Callbacks
    private SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Check signUpConfirmationState to see if the user is already confirmed
            closeWaitDialog();
            Boolean regState = signUpConfirmationState;
            if (signUpConfirmationState) {
                // User is already confirmed
                showDialogMessage("Sign up successful!",usernameInput+" has been Confirmed", true);
            }
            else {
                // User is not confirmed
                confirmSignUp(cognitoUserCodeDeliveryDetails);
            }
        }

        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();
            TextView label = (TextView) findViewById(R.id.text_view_reg_username_message);
            label.setText("Sign up failed");
            //username.setBackground(getDrawable(R.drawable.text_border_error));
            showDialogMessage("Sign up failed", AppHelper.formatException(exception),false);
        }
    };

    // Activities
    private void confirmSignUp(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
//        Intent intent = new Intent(this, SignUpConfirm.class);
//        intent.putExtra("source","signup");
//        intent.putExtra("name", usernameInput);
//        intent.putExtra("destination", cognitoUserCodeDeliveryDetails.getDestination());
//        intent.putExtra("deliveryMed", cognitoUserCodeDeliveryDetails.getDeliveryMedium());
//        intent.putExtra("attribute", cognitoUserCodeDeliveryDetails.getAttributeName());
//        startActivityForResult(intent, 10);
    }

    // Dialog
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

    private void showDialogMessage(String title, String body, final boolean exit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    mDialog.dismiss();
                    if(exit) {
                        exit(usernameInput);
                    }
                } catch (Exception e) {
                    if(exit) {
                        exit(usernameInput);
                    }
                }
            }
        });
        mDialog = builder.create();
        mDialog.show();
    }

    private void exit(String uname) {
        exit(uname, null);
    }

    private void exit(String uname, String password) {
        Intent intent = new Intent();
        if (uname == null) {
            uname = "";
        }
        if (password == null) {
            password = "";
        }
        intent.putExtra("name", uname);
        intent.putExtra("password", password);
        setResult(RESULT_OK, intent);
        finish();
    }
}
