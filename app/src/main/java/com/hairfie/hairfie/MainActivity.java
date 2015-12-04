package com.hairfie.hairfie;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.hairfie.hairfie.helpers.BlurTransform;
import com.hairfie.hairfie.helpers.CircleTransform;
import com.hairfie.hairfie.models.Category;
import com.hairfie.hairfie.models.Hairfie;
import com.hairfie.hairfie.models.Picture;
import com.hairfie.hairfie.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HairfieGridFragment.OnHairfieGridFragmentInteractionListener, CategoryPictoFragment.OnCategoryPictoFragmentInteractionListener {

    ViewPager mViewPager;
    PagerTitleStrip mPagerTitleStrip;
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerTitleStrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        boolean authenticated = User.getCurrentUser().isAuthenticated();

        if (authenticated) {

            // Show drawer toggle
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        } else {
            toolbar.setNavigationIcon(R.drawable.login_icon);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Navigation header
        setupNavigationHeader();

        // Profile updated
        Application.getBroadcastManager().registerReceiver(mProfileUpdatedBroadcastReceiver, new IntentFilter(User.PROFILE_UPDATED_BROADCAST_INTENT));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(Application.getInstance()).unregisterReceiver(mProfileUpdatedBroadcastReceiver);
    }

    BroadcastReceiver mProfileUpdatedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setupNavigationHeader();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home && !User.getCurrentUser().isAuthenticated()) {
            touchLogin(null);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_take_picture) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.nav_logout:
                User.getCurrentUser().logout(null);
                Intent intent = new Intent(this, IntroActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_copyright:
                return false;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void touchLogin(View view) {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);

    }

    @Override
    public void onTouchCategoryPicto(Category item) {

        Intent intent = new Intent(this, SearchResultsActivity.class);
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(item);
        intent.putExtra(SearchResultsActivity.EXTRA_CATEGORIES, categories);
        startActivity(intent);
    }

    @Override
    public void onTouchHairfie(Hairfie item) {

    }

    public class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CategoryPictoFragment.newInstance();
                case 1:
                    return HairfieGridFragment.newInstance(2);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.book);
                case 1:
                    return getString(R.string.hairfies);
            }
            return null;
        }
    }


    private void setupNavigationHeader() {
        User.Profile profile = User.getCurrentUser().getProfile();
        View headerView = mNavigationView.getHeaderView(0);
        Picture picture = profile != null ? profile.picture : null;

        ImageView image = (ImageView) headerView.findViewById(R.id.nav_header_image_view);
        ImageView background = (ImageView) headerView.findViewById(R.id.nav_header_background_image_view);
        if (picture != null && picture.url != null) {

            // Show

            Application.getPicasso().load(Uri.parse(picture.url)).placeholder(R.drawable.default_user_picture).fit().centerCrop().transform(new BlurTransform(1.0f, 40)).into(background);
            Application.getPicasso().load(Uri.parse(picture.url)).placeholder(R.drawable.default_user_picture_bg).fit().centerCrop().transform(new CircleTransform()).into(image);

        } else {
            Application.getPicasso().load(R.drawable.default_user_picture).fit().centerCrop().transform(new BlurTransform(1.0f, 40)).into(background);
            Application.getPicasso().load(R.drawable.default_user_picture_bg).fit().centerCrop().transform(new CircleTransform()).into(image);
        }

        TextView line1 = (TextView)headerView.findViewById(R.id.nav_header_line1);
        TextView line2 = (TextView)headerView.findViewById(R.id.nav_header_line2);

        line1.setText(profile != null ? profile.getFullname() : null);
        int numHairfies = profile != null ? profile.numHairfies : 0;

        line2.setText(profile != null ? numHairfies == 1 ? "1 hairfie" : String.format(Locale.getDefault(), "%d hairfies", numHairfies) : null);
    }
}
