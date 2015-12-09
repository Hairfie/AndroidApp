package com.hairfie.hairfie;

import android.location.Location;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hairfie.hairfie.models.Business;

/**
 * Created by stephh on 09/12/15.
 */
public class BusinessMapFragment extends SupportMapFragment implements OnMapReadyCallback {
    boolean mMapReady;
    private BusinessRecyclerViewAdapter mAdapter;
    private OnMapFragmentInteractionListener mInteractionListener;

    public BusinessMapFragment() {
        super();
        getMapAsync(this);
    }

    private RecyclerView.AdapterDataObserver mAdapterObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateMap();
        }
    };

    public void setAdapter(BusinessRecyclerViewAdapter adapter) {
        if (null != mAdapter)
            mAdapter.unregisterAdapterDataObserver(mAdapterObserver);

        mAdapter = adapter;
        adapter.registerAdapterDataObserver(mAdapterObserver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapReady = true;
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                touchMarker(marker);
            }
        });
        updateMap();
    }

    private void updateMap() {

        if (!mMapReady)
            return;

        if (null == mAdapter)
            return;

        GoogleMap map = getMap();
        if (null == map)
            return;

        if (0 >= mAdapter.getItemCount()) {
            // Center map on user location
            Location location = Application.getInstance().getLastLocation();
            if (null != location)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            return;
        }

        map.clear();
        // Zoom map to bounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            Business business = mAdapter.getItem(i);
            if (null == business.gps)
                continue;
            LatLng latlng = new LatLng(business.gps.lat, business.gps.lng);
            builder.include(latlng);

            map.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title(business.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_salon)));
        }
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20));

    }

    public void setInteractionListener(OnMapFragmentInteractionListener interactionListener) {
        this.mInteractionListener = interactionListener;
    }

    public interface OnMapFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMapFragmentInteraction(Business item);
    }

    private void touchMarker(Marker marker) {

        if (mAdapter == null)
            return;

        Business business = mAdapter.findItemByName(marker.getTitle());
        if (null != business && null != mInteractionListener) {
            mInteractionListener.onMapFragmentInteraction(business);
        }
    }

}
