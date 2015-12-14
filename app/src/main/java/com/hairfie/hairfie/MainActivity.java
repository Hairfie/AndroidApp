package com.hairfie.hairfie;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.Category;
import com.hairfie.hairfie.models.GeoPoint;
import com.hairfie.hairfie.models.Hairfie;
import com.hairfie.hairfie.models.Picture;
import com.hairfie.hairfie.models.ResultCallback;
import com.hairfie.hairfie.models.User;
import com.squareup.okhttp.Call;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HairfieGridFragment.OnHairfieGridFragmentInteractionListener {
    NavigationView mNavigationView;
    private static final int REQUEST_CODE_MODIFY_FILTERS = 101;
    public static final String EXTRA_CATEGORIES = "EXTRA_CATEGORIES";
    public static final String EXTRA_LOCATION_NAME= "EXTRA_LOCATION_NAME";
    public static final String EXTRA_QUERY = SearchManager.QUERY;

    List<Category> mCategories;
    String mQuery;
    GeoPoint mGeoPoint;
    CharSequence mLocationName;
    private ViewPager mContainer;

    View mNoResults;
    BusinessMapFragment mMapFragment;
    BusinessListFragment mListFragment;
    HairfieGridFragment mHairfiesFragment;
    private BusinessRecyclerViewAdapter mAdapter = new BusinessRecyclerViewAdapter(new BusinessListFragment.OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(Business item) {
            onTouchBusiness(item);
        }


    }
    );




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

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

        mContainer = (ViewPager)findViewById(R.id.container);
        mNoResults = findViewById(R.id.no_results);
        mNoResults.setVisibility(View.GONE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapFragment = new BusinessMapFragment();
        mMapFragment.setAdapter(mAdapter);
        mMapFragment.setInteractionListener(new BusinessMapFragment.OnMapFragmentInteractionListener() {

            @Override
            public void onMapFragmentInteraction(Business item) {
                onTouchBusiness(item);
            }
        });

        handleIntent(getIntent());

        mListFragment = new BusinessListFragment();
        mListFragment.setAdapter(mAdapter);

        mHairfiesFragment = HairfieGridFragment.newInstance(2, null, null);

        mContainer.setAdapter(mPagerAdapter);
        mContainer.setCurrentItem(1);

        Log.d(Application.TAG, String.format(Locale.ENGLISH, "Searching with query %s, location %s, categories %s", mQuery != null ? mQuery : "null", mGeoPoint != null ? mGeoPoint.toLocation().toString() : "null", mCategories != null ? mCategories.toString(): "null"));

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home && !User.getCurrentUser().isAuthenticated()) {
            touchLogin(null);
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
    public void onTouchHairfie(Hairfie item) {
        Intent intent = new Intent(this, HairfieActivity.class);
        intent.putExtra(HairfieActivity.ARG_HAIRFIE, item);
        startActivity(intent);

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

        TextView line1 = (TextView) headerView.findViewById(R.id.nav_header_line1);
        TextView line2 = (TextView) headerView.findViewById(R.id.nav_header_line2);

        line1.setText(profile != null ? profile.getFullname() : null);
        int numHairfies = profile != null ? profile.numHairfies : 0;

        line2.setText(profile != null ? numHairfies == 1 ? "1 hairfie" : String.format(Locale.getDefault(), "%d hairfies", numHairfies) : null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        CharSequence query = intent.getCharSequenceExtra(EXTRA_QUERY);
        mQuery = query != null ? query.toString() : null;
        mCategories = intent.getParcelableArrayListExtra(EXTRA_CATEGORIES);
        mLocationName = intent.getCharSequenceExtra(EXTRA_LOCATION_NAME);

        if (null == mLocationName || mLocationName.length() == 0) {
            Location lastLocation = Application.getInstance().getLastLocation();
            if (null != lastLocation)
                mGeoPoint = new GeoPoint(lastLocation);

            if (null == mGeoPoint) {
                // We have no location
                new AlertDialog.Builder(this).setTitle(getString(R.string.cant_locate_you)).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        touchModifyFilters(null);
                    }
                }).show();
            } else {
                refresh();
            }
        } else {

            GeoPoint.search(mLocationName.toString(), new ResultCallback.Single<GeoPoint>() {
                @Override
                public void onComplete(@Nullable GeoPoint object, @Nullable ResultCallback.Error error) {
                    if (null == object) {
                        // We have no location
                        new AlertDialog.Builder(MainActivity.this).setTitle(String.format(Locale.getDefault(), getString(R.string.cant_locate_name), mLocationName)).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
                        Log.e(Application.TAG, "Error requesting geolocation:" + (error.message != null ? error.message : "null"), error.cause);
                    } else {
                        mGeoPoint = object;
                        refresh();
                    }
                }
            });
        }

    }

    Call mListBusinessesCall;
    private void refresh() {

        if (null == mGeoPoint)
            return;

        if (null != mListBusinessesCall && !mListBusinessesCall.isCanceled())
            mListBusinessesCall.cancel();


        mAdapter.setReferenceLocation(mGeoPoint.toLocation());

        mAdapter.resetItems();

        setSpinning(true);
        mNoResults.setVisibility(View.GONE);

        mListBusinessesCall = Business.listNearby(mGeoPoint, mQuery, mCategories, 100, new ResultCallback.Single<List<Business>>() {
            @Override
            public void onComplete(@Nullable List<Business> object, @Nullable ResultCallback.Error error) {
                setSpinning(false);

                if (null != error) {
                    Log.e(Application.TAG, "Could not search businesses: " + (error.message != null ? error.message : "null"), error.cause);
                    finish();
                    return;
                }

                if (null != object) {
                    if (object.size() == 0) {
                        mNoResults.setVisibility(View.VISIBLE);
                    }
                    mAdapter.addItems(object);
                }


            }
        });

        /*
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //use the query to search your data somehow
        }
        */
    }



    private void onTouchBusiness(Business business) {
        Intent intent = new Intent(this, BusinessActivity.class);
        intent.putExtra(BusinessActivity.EXTRA_BUSINESS, business);
        startActivity(intent);
    }

    public void touchModifyFilters(View v) {
        Intent intent = new Intent(this, SearchFormActivity.class);
        if (null != mQuery)
            intent.putExtra(EXTRA_QUERY, mQuery);
        if (null != mCategories)
            intent.putParcelableArrayListExtra(EXTRA_CATEGORIES, new ArrayList<Category>(mCategories));
        if (null != mLocationName)
            intent.putExtra(EXTRA_LOCATION_NAME, mLocationName);

        startActivityForResult(intent, REQUEST_CODE_MODIFY_FILTERS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MODIFY_FILTERS && RESULT_OK == resultCode)
            handleIntent(data);
    }

    FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mMapFragment;
                case 1:
                    return mListFragment;
                default:
                    return mHairfiesFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return getString(R.string.hairdressers);
                case 0:
                    return getString(R.string.map);
                case 2:
                    return getString(R.string.hairfies);
            }
            return null;
        }

    };

    void setSpinning(boolean spinning) {
        mContainer.setVisibility(spinning ? View.INVISIBLE : View.VISIBLE);
    }

    public void touchNewHairfie(View v) {

    }

}
