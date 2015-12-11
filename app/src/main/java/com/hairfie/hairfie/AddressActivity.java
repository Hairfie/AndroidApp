package com.hairfie.hairfie;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hairfie.hairfie.models.Address;
import com.hairfie.hairfie.models.GeoPoint;
import com.hairfie.hairfie.models.ResultCallback;

public class AddressActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String ARG_ADDRESS = "address";
    public static final String ARG_TITLE = "title";

    private GoogleMap mMap;
    private Address mAddress;
    private CharSequence mTitle;
    private GeoPoint mGeoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        mAddress = (Address)getIntent().getParcelableExtra(ARG_ADDRESS);
        if (null == mAddress) {
            Log.e(Application.TAG, "No address provided");
            finish();
        }
        mTitle = getIntent().getCharSequenceExtra(ARG_TITLE);

        TextView titleTextView = (TextView)findViewById(R.id.appbar_title);
        if (null != titleTextView)
            titleTextView.setText(mTitle != null ? mTitle : mAddress.toString());

        GeoPoint.search(mAddress.toString(), new ResultCallback.Single<GeoPoint>() {
            @Override
            public void onComplete(@Nullable GeoPoint object, @Nullable ResultCallback.Error error) {

                if (null != error) {
                    Log.e(Application.TAG, "Could not geocode address: " + (error.message != null ? error.message : "null"), error.cause);
                    new AlertDialog.Builder(AddressActivity.this).setTitle(error.message).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
                    return;
                }
                mGeoPoint = object;
                centerMap();

            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        centerMap();
    }

    private void centerMap() {
        if (null != mGeoPoint && null != mMap) {
            // Add a marker in Sydney and move the camera
            LatLng address = new LatLng(mGeoPoint.lat, mGeoPoint.lng);
            mMap.addMarker(new MarkerOptions().position(address).title(mTitle != null ? mTitle.toString() : mAddress.toString()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 12));

        }
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

}
