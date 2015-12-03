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

import com.hairfie.hairfie.models.Category;
import com.hairfie.hairfie.models.ResultCallback;
import com.squareup.okhttp.Call;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnCategoryPictoFragmentInteractionListener}
 * interface.
 */
public class CategoryPictoFragment extends Fragment {

    private OnCategoryPictoFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CategoryPictoFragment() {
    }

    @SuppressWarnings("unused")
    public static CategoryPictoFragment newInstance() {
        CategoryPictoFragment fragment = new CategoryPictoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_picto_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            LinearLayoutManager layout = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layout);
            recyclerView.setAdapter(new CategoryPictoRecyclerViewAdapter(mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoryPictoFragmentInteractionListener) {
            mListener = (OnCategoryPictoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategoryPictoFragmentInteractionListener");
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
    public interface OnCategoryPictoFragmentInteractionListener {
        // TODO: Update argument type and name
        void onTouchCategoryPicto(Category item);
    }


    public static class CategoryPictoRecyclerViewAdapter extends RecyclerView.Adapter<CategoryPictoRecyclerViewAdapter.ViewHolder> {

        private List<Category> mValues = new ArrayList<Category>();
        private final OnCategoryPictoFragmentInteractionListener mListener;

        private Call mCurrentCall;
        public CategoryPictoRecyclerViewAdapter(OnCategoryPictoFragmentInteractionListener listener) {
            mListener = listener;
            if (null != mCurrentCall && !mCurrentCall.isCanceled())
                mCurrentCall.cancel();

            mCurrentCall = Category.fetchAll(new ResultCallback.Single<List<Category>>() {

                @Override
                public void onComplete(@Nullable List<Category> object, @Nullable ResultCallback.Error error) {
                    // Ignore errors
                    if (null != error) {
                        Log.w(Application.TAG, "Could not fetch categories", error.cause);
                        return;
                    }

                    if (null != object) {
                        List<Category> newValues = new ArrayList<Category>();
                        newValues.addAll(object);
                        Collections.sort(newValues, new Comparator<Category>() {
                            @Override
                            public int compare(Category lhs, Category rhs) {
                                if (lhs.position < rhs.position) {
                                    return -1;
                                }
                                if (lhs.position > rhs.position)
                                    return 1;
                                return 0;
                            }
                        });
                        mValues = newValues;
                        notifyDataSetChanged();
                    }

                }
            });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_category_picto, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Category category = mValues.get(position);
            holder.setItem(category);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onTouchCategoryPicto(holder.mItem);
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
            private final ImageView mImageView;
            private final TextView mNameView;
            private Category mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.image);
                mNameView = (TextView) view.findViewById(R.id.name);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mNameView.getText() + "'";
            }

            public void setItem(Category category) {
                mNameView.setText(category.name);
                mItem = category;
                Picasso.with(Application.getInstance()).load(Uri.parse(category.picture.url)).fit().centerCrop().into(mImageView);

            }
        }
    }

}
