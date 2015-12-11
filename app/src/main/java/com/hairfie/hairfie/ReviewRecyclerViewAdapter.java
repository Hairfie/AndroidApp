package com.hairfie.hairfie;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hairfie.hairfie.helpers.CircleTransform;
import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.Hairfie;
import com.hairfie.hairfie.models.Picture;
import com.hairfie.hairfie.models.ResultCallback;
import com.hairfie.hairfie.models.Review;
import com.squareup.okhttp.Call;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by stephh on 11/12/15.
 */
public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    private List<Review> mValues = new ArrayList<Review>();

    private Business mBusiness;
    public ReviewRecyclerViewAdapter(Business business) {
        mBusiness = business;
        loadNextItems();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_review, parent, false);
        return new ViewHolder(view);
    }

    private Call mCurrentCall;

    private boolean mNoMoreItems;

    public void reset() {
        mNoMoreItems = false;
        mValues = new ArrayList<Review>();
        notifyDataSetChanged();
    }
    private void loadNextItems() {
        if (mNoMoreItems)
            return;

        if (mCurrentCall != null && !mCurrentCall.isCanceled()) {
            mCurrentCall.cancel();
        }
        final int limit = 10;
        ResultCallback.Single<List<Review>> callback = new ResultCallback.Single<List<Review>>() {
            @Override
            public void onComplete(@Nullable List<Review> object, @Nullable ResultCallback.Error error) {
                mCurrentCall = null;
                if (null != error) {
                    Log.w(Application.TAG, "Could not get reviews", error.cause);
                    return;
                }

                if (object != null) {
                    if (object.size() < limit)
                        mNoMoreItems = true;
                    mValues.addAll(object);
                    notifyDataSetChanged();
                }
            }
        };
            mCurrentCall = Review.fetch(mBusiness, limit, mValues.size(), callback);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position + 1 == mValues.size()) {
            loadNextItems();
        }
        holder.setItem(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private Review mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

        }

        public Review getItem() {
            return mItem;
        }

        public void setItem(Review item) {
            mItem = item;

            ImageView pictureImageView = (ImageView)mView.findViewById(R.id.picture);
            TextView nameTextView = (TextView)mView.findViewById(R.id.user_name);
            StarLayout starLayout = (StarLayout)mView.findViewById(R.id.stars);
            TextView dateTextView = (TextView)mView.findViewById(R.id.date);
            TextView messageTextView = (TextView)mView.findViewById(R.id.message);

            if (null != pictureImageView) {
                if (null != mItem.author && null != mItem.author.picture)
                    Application.getPicasso().load(mItem.author.picture.url).transform(new CircleTransform()).fit().centerCrop().into(pictureImageView);
                else
                    Application.getPicasso().load(R.drawable.default_user_picture).transform(new CircleTransform()).fit().centerCrop().into(pictureImageView);
            }
            if (null != nameTextView)
                nameTextView.setText(mItem.author != null ? mItem.author.getFullname() : "");

            if (null != starLayout)
                starLayout.setRating(mItem.rating / 100.0f);

            if (null != dateTextView)
                dateTextView.setText(mItem.createdAt);

            if (null != messageTextView)
                messageTextView.setText(mItem.comment);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.id + "'";
        }
    }
}
