package com.hairfie.hairfie;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.BusinessMember;
import com.hairfie.hairfie.models.BusinessSearchResults;
import com.hairfie.hairfie.models.GeoPoint;
import com.hairfie.hairfie.models.ResultCallback;
import com.squareup.okhttp.Call;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BusinessSearchActivity extends AppCompatActivity {
    public static final String RESULT_BUSINESS = "business";

    EditText mQueryEditText;
    EditText mLocationEditText;
    View mProgressView;
    GeoPoint mGeoPoint;
    private BusinessRecyclerViewAdapter mAdapter = new BusinessRecyclerViewAdapter(new BusinessListFragment.OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(Business item) {
            onTouchBusiness(item);
        }


    }
    );

    private void onTouchBusiness(Business item) {

        Intent data = new Intent();
        data.putExtra(RESULT_BUSINESS, item);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView titleTextView = (TextView)findViewById(R.id.appbar_title);
        if (null != titleTextView)
            titleTextView.setText(R.string.choose_salon);


        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.list);
        recyclerView.setAdapter(mAdapter);

        mProgressView = findViewById(R.id.progress);
        mQueryEditText = (EditText) findViewById(R.id.query);
        mLocationEditText = (EditText) findViewById(R.id.location);


        mQueryEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mLocationEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    touchSearch();
                    return true;
                }
                return false;
            }
        };
        mLocationEditText.setOnEditorActionListener(listener);
        mQueryEditText.setOnEditorActionListener(listener);

        Location lastLocation = Application.getInstance().getLastLocation();
        if (null != lastLocation)
            mGeoPoint = new GeoPoint(lastLocation);

        // If we have a last known location, search immediately
        if (null != mGeoPoint)
            touchSearch();

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

    public void touchSearch() {
        final CharSequence locationName = mLocationEditText.getText();
        if (null == locationName || locationName.length() == 0) {
            Location lastLocation = Application.getInstance().getLastLocation();
            if (null != lastLocation)
                mGeoPoint = new GeoPoint(lastLocation);

            if (null == mGeoPoint) {
                // We have no location
                new AlertDialog.Builder(this).setTitle(getString(R.string.enter_explicit_location)).setPositiveButton(getString(R.string.ok), null).show();
            } else {
                refresh();
            }
        } else {

            setSpinning(true);
            GeoPoint.search(locationName.toString(), new ResultCallback.Single<GeoPoint>() {
                @Override
                public void onComplete(@Nullable GeoPoint object, @Nullable ResultCallback.Error error) {
                    setSpinning(false);
                    if (null == object) {
                        // We have no location
                        new AlertDialog.Builder(BusinessSearchActivity.this).setTitle(String.format(Locale.getDefault(), getString(R.string.cant_locate_name), locationName)).setPositiveButton(getString(R.string.ok), null).show();
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

        final View noResults = findViewById(R.id.no_results);
        if (null != noResults)
            noResults.setVisibility(View.GONE);


        CharSequence query = null != mQueryEditText.getText() ? mQueryEditText.getText() : "";
        mListBusinessesCall = Business.listNearby(mGeoPoint, query.toString(), null, 100, new ResultCallback.Single<BusinessSearchResults>() {
            @Override
            public void onComplete(@Nullable BusinessSearchResults object, @Nullable ResultCallback.Error error) {
                setSpinning(false);

                if (null != error) {
                    Log.e(Application.TAG, "Could not search businesses: " + (error.message != null ? error.message : "null"), error.cause);
//                    finish();
                    new AlertDialog.Builder(BusinessSearchActivity.this).setTitle(error.message).setPositiveButton(getString(R.string.ok), null).show();
                    return;
                }

                if (null != object) {
                    Business[] list = object.hits;
                    if (list.length == 0 && null != noResults) {
                        noResults.setVisibility(View.VISIBLE);
                    }

                    mAdapter.addItems(Arrays.asList(list));
                }


            }
        });
    }

    public void touchClearLocation(View v) {
        mLocationEditText.setText(null);
        touchSearch();
    }

    public void setSpinning(boolean spinning) {
        mProgressView.setVisibility(spinning ? View.VISIBLE : View.GONE);
    }
}
