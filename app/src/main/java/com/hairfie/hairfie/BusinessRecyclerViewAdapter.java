package com.hairfie.hairfie;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hairfie.hairfie.helpers.RoundedCornersTransform;
import com.hairfie.hairfie.models.Business;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusinessRecyclerViewAdapter extends RecyclerView.Adapter<BusinessRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Business> mValues = new ArrayList<>();
    private final BusinessListFragment.OnListFragmentInteractionListener mListener;

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

    public void addItems(List<Business> items) {
        mValues.addAll(items);
        notifyDataSetChanged();
    }
    public void resetItems() {
        mValues.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_business, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mValues.get(position));
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private Business mItem;
        public ImageView mPictureImageView;
        public TextView mNameTextView;
        public TextView mDistanceTextView;
        public TextView mNumHairfiesTextView;
        public ImageView[] mStarImageViews;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPictureImageView = (ImageView)view.findViewById(R.id.picture);
            mNameTextView = (TextView)view.findViewById(R.id.name);
            mDistanceTextView = (TextView)view.findViewById(R.id.distance);
            mNumHairfiesTextView = (TextView)view.findViewById(R.id.num_hairfies);
            mStarImageViews = new ImageView[5];
            mStarImageViews[0] = (ImageView)view.findViewById(R.id.star1);
            mStarImageViews[1] = (ImageView)view.findViewById(R.id.star2);
            mStarImageViews[2] = (ImageView)view.findViewById(R.id.star3);
            mStarImageViews[3] = (ImageView)view.findViewById(R.id.star4);
            mStarImageViews[4] = (ImageView)view.findViewById(R.id.star5);
        }

        public void setItem(Business item) {
            mItem = item;
            if (item.pictures != null && item.pictures.length > 0 && item.pictures[0].url != null) {
                Application.getPicasso().load(item.pictures[0].url).fit().centerCrop().transform(new RoundedCornersTransform(5,0)).into(mPictureImageView);
            } else {
                mPictureImageView.setImageBitmap(null);
            }

            mNameTextView.setText(item.name);

            // Distance
            Location lastLocation = Application.getInstance().getLastLocation();
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
                Float ratingOn5 = (item.rating / 100.0f) * mStarImageViews.length;
                for (int i = 0; i < mStarImageViews.length; i++) {
                    ImageView starImageView = mStarImageViews[i];
                    starImageView.setVisibility(View.VISIBLE);
                    if ((i + 1) <= ratingOn5)
                        starImageView.setImageResource(R.drawable.selected_star);
                    else if ((i + 0.5f) <= ratingOn5)
                        starImageView.setImageResource(R.drawable.half_selected_star);
                    else
                        starImageView.setImageResource(R.drawable.not_selected_star);
                }
            } else {
                for (int i = 0; i < mStarImageViews.length; i++) {
                    mStarImageViews[i].setVisibility(View.GONE);
                }
            }
        }

    }
}
