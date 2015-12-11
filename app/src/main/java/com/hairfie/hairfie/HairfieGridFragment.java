package com.hairfie.hairfie;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hairfie.hairfie.helpers.CircleTransform;
import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.BusinessMember;
import com.hairfie.hairfie.models.Hairfie;
import com.hairfie.hairfie.models.Picture;
import com.hairfie.hairfie.models.ResultCallback;
import com.squareup.okhttp.Call;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnHairfieGridFragmentInteractionListener  }
 * interface.
 */
public class HairfieGridFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_BUSINESS= "business";
    private static final String ARG_BUSINESSMEMBER= "business-member";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private Business mBusiness;
    private BusinessMember mBusinessMember;
    private OnHairfieGridFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HairfieGridFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HairfieGridFragment newInstance(int columnCount, Business business, BusinessMember businessMember) {
        HairfieGridFragment fragment = new HairfieGridFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelable(ARG_BUSINESS, business);
        args.putParcelable(ARG_BUSINESSMEMBER, businessMember);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mBusiness = (Business)getArguments().getParcelable(ARG_BUSINESS);
            mBusinessMember = (BusinessMember)getArguments().getParcelable(ARG_BUSINESSMEMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hairfie_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new HairfieRecyclerViewAdapter(mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHairfieGridFragmentInteractionListener) {
            mListener = (OnHairfieGridFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHairfieGridFragmentInteractionListener  ");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnHairfieGridFragmentInteractionListener {
        // TODO: Update argument type and name
        void onTouchHairfie(Hairfie item);
    }

    public class HairfieRecyclerViewAdapter extends RecyclerView.Adapter<HairfieRecyclerViewAdapter.ViewHolder> {

        private List<Hairfie> mValues = new ArrayList<Hairfie>();
        private final OnHairfieGridFragmentInteractionListener mListener;

        public HairfieRecyclerViewAdapter(OnHairfieGridFragmentInteractionListener listener) {
            mListener = listener;
            loadNextItems();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_hairfie, parent, false);
            return new ViewHolder(view);
        }

        private Call mCurrentCall;

        private boolean mNoMoreItems;

        public void reset() {
            mNoMoreItems = false;
            mValues = new ArrayList<Hairfie>();
            notifyDataSetChanged();
        }
        private void loadNextItems() {
            if (mNoMoreItems)
                return;

            if (mCurrentCall != null && !mCurrentCall.isCanceled()) {
                mCurrentCall.cancel();
            }
            final int limit = 10;
            ResultCallback.Single<List<Hairfie>> callback = new ResultCallback.Single<List<Hairfie>>() {
                @Override
                public void onComplete(@Nullable List<Hairfie> object, @Nullable ResultCallback.Error error) {
                    mCurrentCall = null;
                    if (null != error) {
                        Log.w(Application.TAG, "Could not get hairfies", error.cause);
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
            if (null != mBusiness)
                mCurrentCall = Hairfie.latest(mBusiness, limit, mValues.size(), callback);
            else if (null != mBusinessMember)
                mCurrentCall = Hairfie.latest(mBusinessMember, limit, mValues.size(), callback);
            else
                mCurrentCall = Hairfie.latest(limit, mValues.size(), callback);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (position + 1 == mValues.size()) {
                loadNextItems();
            }
            holder.setItem(mValues.get(position));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onTouchHairfie(holder.getItem());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private final TextView mAuthorNameTextView;
            private final ImageView mPictureImageView;
            private final ImageView mSecondaryPictureImageView;
            private final ImageView mAuthorPicture;
            private final View mLikeContainerView;
            private final TextView mLikeCountTextView;
            private final TextView mPriceTextView;
            private Hairfie mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mPictureImageView = (ImageView) view.findViewById(R.id.picture);
                mSecondaryPictureImageView = (ImageView) view.findViewById(R.id.secondary_picture);
                mAuthorPicture = (ImageView) view.findViewById(R.id.author_picture);
                mAuthorNameTextView = (TextView) view.findViewById(R.id.author_name);
                mLikeContainerView = view.findViewById(R.id.like_container);
                mLikeCountTextView = (TextView) view.findViewById(R.id.like_count);
                mPriceTextView = (TextView) view.findViewById(R.id.price);

            }

            public Hairfie getItem() {
                return mItem;
            }

            public void setItem(Hairfie item) {
                mItem = item;
                if (item.pictures != null && item.pictures.length > 1) {
                    mSecondaryPictureImageView.setVisibility(View.VISIBLE);
                    Application.getPicasso().load(Uri.parse(item.pictures[0].url)).fit().centerCrop().into(mSecondaryPictureImageView);
                    Application.getPicasso().load(Uri.parse(item.pictures[1].url)).fit().centerCrop().into(mPictureImageView);
                } else if (item.picture != null) {
                    Application.getPicasso().load(Uri.parse(item.picture.url)).fit().centerCrop().into(mPictureImageView);
                    mSecondaryPictureImageView.setVisibility(View.GONE);
                } else {
                    mSecondaryPictureImageView.setVisibility(View.GONE);
                    mPictureImageView.setImageDrawable(null);
                }

                if (item.author != null && item.author.picture != null) {
                    mAuthorPicture.setVisibility(View.VISIBLE);
                    Picture authorPicture = item.author.picture;
                    Application.getPicasso().load(Uri.parse(authorPicture.url)).placeholder(R.drawable.default_user_picture_circle).fit().centerCrop().transform(new CircleTransform()).into(mAuthorPicture);

                } else {
                    mAuthorPicture.setVisibility(View.GONE);
                }

                mAuthorNameTextView.setText(item.author != null ? item.author.getFullname() : "");
                if (item.numLikes > 0) {
                    mLikeContainerView.setVisibility(View.VISIBLE);
                    mLikeCountTextView.setText(String.format(Locale.getDefault(), "%d", item.numLikes));
                } else {
                    mLikeContainerView.setVisibility(View.GONE);
                }

                if (item.price != null) {
                    mPriceTextView.setVisibility(View.VISIBLE);
                    mPriceTextView.setText(item.price.localizedString());
                } else {
                    mPriceTextView.setVisibility(View.GONE);
                }

            }

            @Override
            public String toString() {
                return super.toString() + " '" + mItem.id + "'";
            }
        }
    }

}
