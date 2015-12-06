package com.hairfie.hairfie;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.Category;
import com.hairfie.hairfie.models.GeoPoint;
import com.hairfie.hairfie.models.ResultCallback;
import com.hairfie.hairfie.models.User;
import com.squareup.okhttp.Call;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_CATEGORIES = "EXTRA_CATEGORIES";
    public static final String EXTRA_GEOPOINT= "EXTRA_GEOPOINT";

    List<Category> mCategories;
    String mQuery;
    GeoPoint mGeoPoint;
    private GoogleMap mMap;
    private Layout mContainer;
    private BusinessRecyclerViewAdapter mAdapter = new BusinessRecyclerViewAdapter(new BusinessFragment.OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(Business item) {
            onTouchBusiness(item);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        handleIntent(getIntent());

        BusinessFragment businessFragment = (BusinessFragment) getSupportFragmentManager().findFragmentById(R.id.results);
        businessFragment.setAdapter(mAdapter);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateMap();
                mContainer.setVisibility(View.VISIBLE);
            }
        });
        mContainer = (Layout)findViewById(R.id.container);
        mContainer.setVisibility(View.INVISIBLE);

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
        mGeoPoint = (GeoPoint) intent.getParcelableExtra(EXTRA_GEOPOINT);
        if (null == mGeoPoint) {
            Location lastLocation = Application.getInstance().getLastLocation();
            if (null != lastLocation)
                mGeoPoint = new GeoPoint(lastLocation);
        }

        if (null == mGeoPoint) {
            // We have no location
            new AlertDialog.Builder(this).setTitle("We can't locate you, please choose a location").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        }
        refresh();
    }

    Call mListBusinessesCall;
    private void refresh() {

        if (null == mGeoPoint)
            return;

        if (null != mListBusinessesCall && !mListBusinessesCall.isCanceled())
            mListBusinessesCall.cancel();


        mListBusinessesCall = Business.listNearby(mGeoPoint, mQuery, mCategories, 100, new ResultCallback.Single<List<Business>>() {
            @Override
            public void onComplete(@Nullable List<Business> object, @Nullable ResultCallback.Error error) {

                if (null != error) {
                    Log.e(Application.TAG, "Could not search businesses: " + (error.message != null ? error.message : "null"), error.cause);
                    finish();
                    return;
                }

                if (null != object) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
    }

    private void updateMap() {

        if (0 >= mAdapter.getItemCount()) {
            // Center map on user location
            Location location = Application.getInstance().getLastLocation();
            if (null != location)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            return;
        }

        mMap.clear();
        // Zoom map to bounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            Business business = mAdapter.getItem(i);
            if (null == business.gps)
                continue;
            LatLng latlng = new LatLng(business.gps.lat, business.gps.lng);
            builder.include(latlng);

            mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title(business.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_salon)));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20));

    }

    private void onTouchBusiness(Business business) {
        // TODO: code me
    }

    public void touchModifyFilters(View v) {
        // TODO: code me
    }


    public static class Layout extends ViewGroup {

        Button mButton;
        RecyclerView mRecyclerView;
        View mMapView;
        public Layout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        private int dY = 0;
        @Override
        public void addView(View child, int index, LayoutParams params) {
            super.addView(child, index, params);
            if (child instanceof Button)
                mButton = (Button)child;
            else if (child instanceof RecyclerView) {
                mRecyclerView = (RecyclerView) child;
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        float buttonTop = mButton.getTop() + mButton.getTranslationY();
                        if (buttonTop - dy < 0)
                            dy = Math.round(buttonTop);

                        float mapViewTop =  mMapView.getTop() + mMapView.getTranslationY();
                        if (mapViewTop - dy > 0)
                            dy = Math.round(mapViewTop);

                        mButton.setTranslationY(mButton.getTranslationY() - dy);
                        mMapView.setTranslationY(mMapView.getTranslationY()-dy);
                    }
                });
            } else
                mMapView = child;
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (null == mButton || null == mRecyclerView || null == mMapView) {
                Log.e(Application.TAG, "SearchResultsActivity$Layout Refusing to layout as it is incomplete.");
                return;
            }
            LayoutParams buttonLayoutParams = mButton.getLayoutParams();
            int desiredButtonHeight =  buttonLayoutParams.height;



            int height = getMeasuredHeight();
            int thirdOfHeight = Math.round((float) height / 3.0f);
            int desiredMapHeight = thirdOfHeight;
            int width = getMeasuredWidth();

            mMapView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(desiredMapHeight, MeasureSpec.EXACTLY));
            mButton.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(desiredButtonHeight, MeasureSpec.EXACTLY));
            mRecyclerView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

            mMapView.layout(0,0,width,desiredMapHeight);
            mButton.layout(0, desiredMapHeight, width, desiredMapHeight + desiredButtonHeight);
            mRecyclerView.layout(0, 0, width, height);
            mRecyclerView.setPadding(0, desiredButtonHeight + desiredMapHeight, 0, 0);
        }
    }
}
