package com.hairfie.hairfie;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hairfie.hairfie.helpers.RoundedCornersTransform;
import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.BusinessSearchResults;
import com.hairfie.hairfie.models.Category;
import com.hairfie.hairfie.models.GeoPoint;
import com.hairfie.hairfie.models.Hairfie;
import com.hairfie.hairfie.models.ResultCallback;
import com.squareup.okhttp.Call;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BusinessRecyclerViewAdapter extends RecyclerView.Adapter<BusinessRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Business> mValues = new ArrayList<>();
    private final BusinessListFragment.OnListFragmentInteractionListener mListener;

    private Call mCurrentCall;
    private GeoPoint mGeoPoint;
    private  String mQuery;
    private List<Category> mCategories;

    private boolean mNoMoreItems;
    public BusinessRecyclerViewAdapter(BusinessListFragment.OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    public Business getItem(int position) {
        return mValues.get(position);
    }

    public Business findItemByName(String name) {
        for (Business value : mValues)
            if (value.name != null && value.name.equals(name))
                return value;
        return null;
    }

    public void resetItems() {
        mValues.clear();
        mNoMoreItems = false;
        notifyDataSetChanged();
    }

    public void search(@NonNull GeoPoint geoPoint, @Nullable String query, @Nullable List<Category> categories) {
        mQuery = query;
        mGeoPoint = geoPoint;
        mCategories = categories;
        mNoMoreItems = false;
        loadNextItems();
    }

    private void loadNextItems() {
        if (null == mGeoPoint) {
            Log.e(Application.TAG, "No geopoint provided");
            return;
        }
        if (mNoMoreItems)
            return;

        if (mCurrentCall != null && !mCurrentCall.isCanceled()) {
            mCurrentCall.cancel();
        }
        final int limit = 5;
        ResultCallback.Single<BusinessSearchResults> callback = new ResultCallback.Single<BusinessSearchResults>() {
            @Override
            public void onComplete(@Nullable BusinessSearchResults object, @Nullable ResultCallback.Error error) {
                mCurrentCall = null;
                if (null != error) {
                    Log.w(Application.TAG, "Could not get businesses", error.cause);
                    return;
                }

                if (object != null) {
                    Business[] list = object.hits;
                    if (list.length < limit)
                        mNoMoreItems = true;
                    mValues.addAll(Arrays.asList(list));
                    notifyDataSetChanged();
                }
            }
        };
        mCurrentCall = Business.listNearby(mGeoPoint, mQuery, mCategories, limit, mValues.size(), callback);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_business, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position + 1 == mValues.size()) {
            loadNextItems();
        }
        holder.setItem(mValues.get(position), mGeoPoint.toLocation());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private Business mItem;
        public ImageView mPictureImageView;
        public TextView mNameTextView;
        public TextView mDistanceTextView;
        public TextView mNumHairfiesTextView;
        public StarLayout mStarLayout;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPictureImageView = (ImageView)view.findViewById(R.id.picture);
            mNameTextView = (TextView)view.findViewById(R.id.name);
            mDistanceTextView = (TextView)view.findViewById(R.id.distance);
            mNumHairfiesTextView = (TextView)view.findViewById(R.id.num_hairfies);
            mStarLayout = (StarLayout)view.findViewById(R.id.stars);
        }

        public void setItem(Business item, Location referenceLocation) {
            mItem = item;
            if (item.pictures != null && item.pictures.length > 0 && item.pictures[0].url != null) {
                Application.getPicasso().load(item.pictures[0].url).placeholder(R.drawable.placeholder_business).fit().centerCrop().transform(new RoundedCornersTransform(5,0)).into(mPictureImageView);
            } else {
                Application.getPicasso().load(R.drawable.placeholder_business).fit().centerCrop().transform(new RoundedCornersTransform(5,0)).into(mPictureImageView);
            }

            mNameTextView.setText(item.name);

            // Distance
            Location lastLocation = referenceLocation;
            if (null != lastLocation && null != item.gps) {
                float distanceKm = lastLocation.distanceTo(item.gps.toLocation()) / 1000.0f;
                mDistanceTextView.setText(String.format(Locale.getDefault(), "%.1f km", distanceKm));
            } else {
                mDistanceTextView.setText("");
            }

            // Num hairfies
            if (item.numHairfies == 1)
                mNumHairfiesTextView.setText(R.string.one_hairfie);
            else
                mNumHairfiesTextView.setText(String.format(Locale.getDefault(), Application.getInstance().getString(R.string.x_hairfies), item.numHairfies));

            // Stars
            if (item.rating != null) {
                mStarLayout.setRating(item.rating / 100.0f);
                mStarLayout.setVisibility(View.VISIBLE);
            } else {
                mStarLayout.setVisibility(View.GONE);
            }
        }

    }
}
