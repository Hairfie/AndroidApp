package com.hairfie.hairfie;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.Category;
import com.hairfie.hairfie.models.GeoPoint;
import com.hairfie.hairfie.models.ResultCallback;
import com.squareup.okhttp.Call;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchResultsActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SEARCH = 101;
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
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        mContainer.setAdapter(mPagerAdapter);

        Log.d(Application.TAG, String.format(Locale.ENGLISH, "Searching with query %s, location %s, categories %s", mQuery != null ? mQuery : "null", mGeoPoint != null ? mGeoPoint.toLocation().toString() : "null", mCategories != null ? mCategories.toString(): "null"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_take_picture) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        finish();
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
                        new AlertDialog.Builder(SearchResultsActivity.this).setTitle(String.format(Locale.getDefault(), getString(R.string.cant_locate_name), mLocationName)).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
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

        startActivityForResult(intent, REQUEST_CODE_SEARCH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEARCH && RESULT_OK == resultCode)
            handleIntent(data);
    }

    FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mListFragment;
            }
            return mMapFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.results);
                case 1:
                    return getString(R.string.map);
            }
            return null;
        }

    };

    void setSpinning(boolean spinning) {
        mContainer.setVisibility(spinning ? View.INVISIBLE : View.VISIBLE);
    }
}
