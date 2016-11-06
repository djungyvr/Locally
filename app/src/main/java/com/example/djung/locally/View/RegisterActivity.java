package com.example.djung.locally.View;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText mEditTextUserName;
    private TextInputEditText mEditTextPassword;
    private TextInputEditText mEditTextVendorName;
    private TextInputEditText mEditTextEmail;
    private TextInputEditText mEditTextPhoneNumber;
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
        mEditTextUserName = (TextInputEditText) findViewById(R.id.edit_text_reg_username);
        mEditTextPassword = (TextInputEditText) findViewById(R.id.edit_text_reg_password);
        mEditTextVendorName = (TextInputEditText) findViewById(R.id.edit_text_reg_vendor_name);
        mEditTextEmail = (TextInputEditText) findViewById(R.id.edit_text_reg_email);
        mEditTextPhoneNumber = (TextInputEditText) findViewById(R.id.edit_text_reg_phone);

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


        mButtonSignup = (Button) findViewById(R.id.button_reg_signup);
        mButtonSignup.setOnClickListener(this);
    }

    // Callbacks
    private SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Check signUpConfirmationState to see if the user is already confirmed
            closeWaitDialog();

            showDialogMessage("Sign up successful!", usernameInput + " has been added. You will be able to sign in once we confirm you as a vendor.", true);
        }

        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();
            TextView label = (TextView) findViewById(R.id.text_view_reg_username_message);
            label.setText("Sign up failed");
            //username.setBackground(getDrawable(R.drawable.text_border_error));
            showDialogMessage("Sign up failed", AppHelper.formatException(exception), false);
        }
    };

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
        } catch (Exception e) {
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
                    if (exit) {
                        exit(usernameInput);
                    }
                } catch (Exception e) {
                    if (exit) {
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

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_reg_signup:
                // Read user data and register
                CognitoUserAttributes userAttributes = new CognitoUserAttributes();

                usernameInput = mEditTextUserName.getText().toString();
                if (usernameInput == null || usernameInput.isEmpty()) {
                    TextView message = (TextView) findViewById(R.id.text_view_reg_username_message);
                    message.setText(mEditTextUserName.getHint() + " cannot be empty");
                    //username.setBackground(getDrawable(R.drawable.text_border_error));
                    return;
                }

                String userpasswordInput = mEditTextPassword.getText().toString();
                userPassword = userpasswordInput;
                if (userpasswordInput == null || userpasswordInput.isEmpty()) {
                    TextView message = (TextView) findViewById(R.id.text_view_password_message);
                    message.setText(mEditTextPassword.getHint() + " cannot be empty");
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

                break;
        }
    }
}
