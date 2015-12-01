package com.hairfie.hairfie;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidpagecontrol.PageControl;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.hairfie.hairfie.models.Callbacks;
import com.hairfie.hairfie.models.User;

import java.util.Arrays;

public class IntroActivity extends AppCompatActivity {

    CallbackManager mCallbackManager = CallbackManager.Factory.create();

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        PagerAdapter adapter = new PagerAdapter(this.getSupportFragmentManager());
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        mViewPager = viewPager;

        final PageControl pageControl = (PageControl) findViewById(R.id.page_control);
        pageControl.setViewPager(viewPager);
        pageControl.setPosition(0);

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
                Snackbar.make(viewPager, error.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void touchSkip(View v) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void touchLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void touchFacebook(View v) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

    }

    public void touchSignup(View v) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
    public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String text;
            int imageResourceId;
            if (0 == position) {
                text = getString(R.string.intro_text_1);
                imageResourceId = R.drawable.tuto_page_1;
            } else if (1 == position) {
                text = getString(R.string.intro_text_2);
                imageResourceId = R.drawable.tuto_page_2;
            } else if (2 == position) {
                text = getString(R.string.intro_text_3);
                imageResourceId = R.drawable.tuto_page_3;
            } else {
                return null;
            }

            return IntroFragment.newInstance(imageResourceId, text);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void loginWithFacebookAccessToken(AccessToken accessToken) {
        User.getCurrentUser().loginWithFacebook(accessToken, new Callbacks.SingleObjectCallback<User>() {
            @Override
            public void onComplete(@Nullable User user, @Nullable Callbacks.Error error) {

                if (null != error) {
                    Snackbar.make(mViewPager, error.message, Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (user.isAuthenticated()) {
                    Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
