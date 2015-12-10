package com.hairfie.hairfie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hairfie.hairfie.models.Address;
import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.Hairfie;
import com.hairfie.hairfie.models.Timetable;
import com.hairfie.hairfie.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Locale;

public class BusinessActivity extends AppCompatActivity implements BusinessInfoFragment.OnFragmentInteractionListener, HairfieGridFragment.OnHairfieGridFragmentInteractionListener {

    public static final String EXTRA_BUSINESS = "EXTRA_BUSINESS";

    Business mBusiness;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mBusiness = null != intent ? (Business)intent.getParcelableExtra(EXTRA_BUSINESS) : null;
        if (null == mBusiness) {
            Log.e(Application.TAG, "Null business");
            finish();
        }

        // Stars
        StarLayout starLayout = (StarLayout) findViewById(R.id.stars);

        View ratingContainer = findViewById(R.id.rating_container);
        if (mBusiness.rating == null) {
            if (null != ratingContainer)
                ratingContainer.setVisibility(View.GONE);
        } else {
            if (null != ratingContainer)
                ratingContainer.setVisibility(View.VISIBLE);
            starLayout.setRating(mBusiness.rating);
        }

        // Num reviews
        TextView numReviews = (TextView)findViewById(R.id.num_reviews);
        if (null != numReviews) {
            if (mBusiness.numReviews != 1) {
                numReviews.setText(String.format(Locale.getDefault(), getString(R.string.x_reviews), mBusiness.numReviews));
            } else {
                numReviews.setText(String.format(Locale.getDefault(), getString(R.string.one_review), mBusiness.numReviews));
            }
        }

        // Name
        TextView nameTextView = (TextView)findViewById(R.id.business_name);
        if (null != nameTextView)
            nameTextView.setText(mBusiness.name);

        final TextView name2TextView = (TextView)findViewById(R.id.business_name2);
        if (null != name2TextView)
            name2TextView.setText(mBusiness.name);

        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(collapsingToolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbar)) {
                    name2TextView.animate().alpha(1).setDuration(600);
                } else {
                    name2TextView.animate().alpha(0).setDuration(600);
                }
            }
        });
        // Picture
        final ImageView pictureImageView = (ImageView) findViewById(R.id.main_picture);
        if (null != pictureImageView && null != mBusiness.pictures && 0 < mBusiness.pictures.length) {
            Application.getPicasso().load(mBusiness.pictures[0].url).into(pictureImageView);
        }

        // Info fragment
        final BusinessInfoFragment infoFragment = BusinessInfoFragment.newInstance(mBusiness);
        final BusinessReviewsFragment reviewsFragment = BusinessReviewsFragment.newInstance(mBusiness);
        final HairfieGridFragment hairfiesFragment = HairfieGridFragment.newInstance(2, mBusiness);
        // View pager
        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: return infoFragment;
                    case 1: return reviewsFragment;
                    case 2: return hairfiesFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 3;
            }

        });


        TabLayout tabs = (TabLayout)findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        for (int i = 0; i < tabs.getTabCount(); i++) {
            int icon;
            switch(i) {
                case 0: icon = R.drawable.tab_business_infos; break;
                case 1: icon = R.drawable.tab_business_reviews; break;
                case 2: icon = R.drawable.tab_business_hairfies; break;
                default: continue;
            }
            tabs.getTabAt(i).setIcon(icon);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_business, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTouchHairfie(Hairfie item) {

    }

    @Override
    public void onTouchAddress(Address address) {
        Log.d(Application.TAG, "Touch address:"+address.toString());
    }

    @Override
    public void onTouchPhone(String phoneNumber) {
        Log.d(Application.TAG, "Touch phone:"+phoneNumber);
    }

    @Override
    public void onTouchTimetable(Timetable timetable) {
        Log.d(Application.TAG, "Touch timetable:"+timetable.toString());

    }
}