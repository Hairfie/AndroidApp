package com.hairfie.hairfie;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hairfie.hairfie.models.ResultCallback;
import com.hairfie.hairfie.models.User;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText mEmailEditText;
    Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView titleTextView = (TextView)findViewById(R.id.appbar_title);
        if (null != titleTextView)
            titleTextView.setText(R.string.forgotten_password);

        mButton = (Button)findViewById(R.id.submit);
        mButton.setEnabled(false);

        mEmailEditText = (EditText)findViewById(R.id.email);
        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mButton.setEnabled(s != null && s.length() > 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.getInstance().trackScreenName("ForgotPasswordActivity");

    }

    public void touchReset(View view) {
        CharSequence email = mEmailEditText.getText();
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.sending));
        progress.setCancelable(false);
        progress.setIndeterminate(true);

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }


        if (null != User.getCurrentUser().resetPassword(email, new ResultCallback.Single<Void>() {
            @Override
            public void onComplete(@Nullable Void object, @Nullable ResultCallback.Error error) {
                progress.dismiss();
                if (error != null) {
                    Snackbar.make(mButton, error.message, Snackbar.LENGTH_LONG).show();
                    return;
                } else {
                    mButton.setEnabled(false);
                    Snackbar.make(mButton, R.string.check_your_email, Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            finish();
                        }

                        @Override
                        public void onShown(Snackbar snackbar) {
                            super.onShown(snackbar);
                        }
                    }).show();

                }
            }
        })) {
            progress.show();

        }

    }

}
