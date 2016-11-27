package com.example.djung.locally.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.djung.locally.AWS.AppHelper;
import com.example.djung.locally.R;

/**
 * Forgot password activity.
 *
 * Created by David Jung on 26/11/16.
 */

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditTextNewPassword;
    private EditText mEditTextCode;
    private Button mButtonSetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViews();
    }

    public void forgotPassword(View view) {
        getCode();
    }

    private void initializeViews(){
        mButtonSetPassword = (Button) findViewById(R.id.button_forgot_password);
        mButtonSetPassword.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
            if (extras.containsKey("destination")) {
                String dest = extras.getString("destination");
                String delMed = extras.getString("deliveryMed");
                TextView message = (TextView) findViewById(R.id.text_view_forgot_password_code_message);
                String textToDisplay = "Code to set a new password was sent to " + dest + " via "+delMed;
                message.setText(textToDisplay);
            }
        }

        mEditTextNewPassword = (EditText) findViewById(R.id.edit_text_forgot_password_pass);
        mEditTextNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_forgot_password_user_id_label);
                    label.setText(mEditTextNewPassword.getHint());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.text_view_forgot_password_user_id_message);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_forgot_password_user_id_label);
                    label.setText("");
                }
            }
        });

        mEditTextCode = (EditText) findViewById(R.id.edit_text_forgot_password_code);
        mEditTextCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_forgot_password_code_label);
                    label.setText(mEditTextCode.getHint());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.text_view_forgot_password_code_message);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.text_view_forgot_password_code_label);
                    label.setText("");
                }
            }
        });
    }

    private void getCode() {
        String newPassword = mEditTextNewPassword.getText().toString();

        if (newPassword == null || newPassword.length() < 1) {
            TextView label = (TextView) findViewById(R.id.text_view_forgot_password_user_id_message);
            label.setText(mEditTextNewPassword.getHint() + " cannot be empty");
            return;
        }

        String verCode = mEditTextCode.getText().toString();

        if (verCode == null || verCode.length() < 1) {
            TextView label = (TextView) findViewById(R.id.text_view_forgot_password_code_message);
            label.setText(mEditTextCode.getHint() + " cannot be empty");
            return;
        }
        exit(newPassword, verCode);
    }

    private void exit(String newPass, String code) {
        Intent intent = new Intent();
        if(newPass == null || code == null) {
            newPass = "";
            code = "";
        }
        intent.putExtra("newPass", newPass);
        intent.putExtra("code", code);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_forgot_password) {
            getCode();
        }
    }
}
