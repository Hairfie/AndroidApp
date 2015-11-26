package com.hairfie.hairfie;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.androidpagecontrol.PageControl;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        PagerAdapter adapter = new PagerAdapter(this.getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        final PageControl pageControl = (PageControl) findViewById(R.id.page_control);
        pageControl.setViewPager(viewPager);
        pageControl.setPosition(0);
    }

    public void touchSkip(View v) {

    }

    public void touchLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void touchFacebook(View v) {

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
}
