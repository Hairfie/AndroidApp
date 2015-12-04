package com.hairfie.hairfie;

import android.app.SearchManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.Category;
import com.hairfie.hairfie.models.GeoPoint;
import com.hairfie.hairfie.models.ResultCallback;
import com.hairfie.hairfie.models.User;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    public static final String EXTRA_CATEGORIES = "EXTRA_CATEGORIES";
    public static final String EXTRA_GEOPOINT= "EXTRA_GEOPOINT";

    List<Category> mCategories;
    String mQuery;
    GeoPoint mGeoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handleIntent(getIntent());
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

        mCategories = intent.getParcelableArrayListExtra(EXTRA_CATEGORIES);
        mQuery = intent.getStringExtra(SearchManager.QUERY);
        mGeoPoint = (GeoPoint)intent.getParcelableExtra(EXTRA_GEOPOINT);


        if (null == mGeoPoint) {
            Location lastLocation = Application.getInstance().getLastLocation();
            if (null != lastLocation)
                mGeoPoint = new GeoPoint(lastLocation);
        }

        Business.listNearby(mGeoPoint, mQuery, mCategories, 100, new ResultCallback.Single<List<Business>>() {
            @Override
            public void onComplete(@Nullable List<Business> object, @Nullable ResultCallback.Error error) {

                if (null != error) {
                    Log.e(Application.TAG, "Could not search businesses: "+(error.message != null ? error.message : "null"), error.cause);
                    finish();
                    return;
                }

                if (null != object) {

                }


            }
        });

        /*
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //use the query to search your data somehow
        }
        */
    }

}
