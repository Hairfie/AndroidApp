package com.hairfie.hairfie;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.Review;


public class BusinessReviewsFragment extends Fragment {

    private static final String ARG_BUSINESS = "business";

    private Business mBusiness;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    public BusinessReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param business Parameter 1.
     * @return A new instance of fragment BusinessReviewsFragment.
     */
    public static BusinessReviewsFragment newInstance(Business business) {
        BusinessReviewsFragment fragment = new BusinessReviewsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BUSINESS, business);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBusiness = getArguments().getParcelable(ARG_BUSINESS);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_reviews, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.list);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progress);
        // Set the adapter
        if (null != mRecyclerView) {
            Context context = view.getContext();

            LinearLayoutManager manager = new LinearLayoutManager(context);
            mRecyclerView.setLayoutManager(manager);
            ReviewRecyclerViewAdapter adapter = new ReviewRecyclerViewAdapter(mBusiness);
            mRecyclerView.setAdapter(adapter);
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    if (null != mProgressBar)
                        mProgressBar.setVisibility(View.GONE);
                }
            });
        }
        return view;
    }


}
