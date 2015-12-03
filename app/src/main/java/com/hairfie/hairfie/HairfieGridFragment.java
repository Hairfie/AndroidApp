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

import com.hairfie.hairfie.models.Hairfie;
import com.hairfie.hairfie.models.Picture;
import com.hairfie.hairfie.models.ResultCallback;
import com.squareup.okhttp.Call;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnHairfieGridFragmentInteractionListener  }
 * interface.
 */
public class HairfieGridFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnHairfieGridFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HairfieGridFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HairfieGridFragment newInstance(int columnCount) {
        HairfieGridFragment fragment = new HairfieGridFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
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

    public static class HairfieRecyclerViewAdapter extends RecyclerView.Adapter<HairfieRecyclerViewAdapter.ViewHolder> {

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
            mCurrentCall = Hairfie.latest(limit, mValues.size(), new ResultCallback.Single<List<Hairfie>>() {
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
            });
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
            private final ImageView mPictureImageView;
            private Hairfie mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mPictureImageView = (ImageView) view.findViewById(R.id.picture);
            }

            public Hairfie getItem() {
                return mItem;
            }

            public void setItem(Hairfie item) {
                mItem = item;
                if (item.pictures != null && item.pictures.length > 1)
                    Picasso.with(Application.getInstance()).load(Uri.parse(item.pictures[1].url)).fit().centerCrop().into(mPictureImageView);
                else if (item.picture != null)
                    Picasso.with(Application.getInstance()).load(Uri.parse(item.picture.url)).fit().centerCrop().into(mPictureImageView);
                else
                    mPictureImageView.setImageDrawable(null);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mItem.id + "'";
            }
        }
    }

}
