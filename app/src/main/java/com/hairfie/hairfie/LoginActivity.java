package com.hairfie.hairfie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.hairfie.hairfie.models.Callbacks;
import com.hairfie.hairfie.models.User;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    CallbackManager mCallbackManager = CallbackManager.Factory.create();

    Button mFacebookLoginButton;

    EditText mEmailEditText;
    EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmailEditText = (EditText)findViewById(R.id.email);
        mPasswordEditText = (EditText)findViewById(R.id.password);

        Button facebookLoginButton = (Button)findViewById(R.id.facebook_login);
        mFacebookLoginButton = facebookLoginButton;

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginWithFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Snackbar.make(mFacebookLoginButton, error.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

            }
        });
    }

    public void touchFacebook(View view) {

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void touchLogin(View view) {

        CharSequence email = mEmailEditText.getText();
        CharSequence password = mPasswordEditText.getText();

        if (null == email || 0 == email.length() || null == password || 0 == password.length()) {
            Snackbar.make(mFacebookLoginButton, R.string.please_enter_email, Snackbar.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.logging_in));
        progress.setIndeterminate(true);
        progress.show();

        User.getCurrentUser().login(email.toString(), password.toString(), new Callbacks.SingleObjectCallback<User>() {
            @Override
            public void onComplete(@Nullable User user, @Nullable Callbacks.Error error) {

                progress.dismiss();

                if (error != null) {
                    Snackbar.make(mFacebookLoginButton, error.message, Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (null != user && user.isAuthenticated()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    void loginWithFacebookAccessToken(AccessToken accessToken) {
        User.getCurrentUser().loginWithFacebook(accessToken, new Callbacks.SingleObjectCallback<User>() {
            @Override
            public void onComplete(@Nullable User user, @Nullable Callbacks.Error error) {

                if (null != error) {
                    Snackbar.make(mFacebookLoginButton, error.message, Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (user.isAuthenticated()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
