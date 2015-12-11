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

import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.Review;


public class BusinessReviewsFragment extends Fragment {

    private static final String ARG_BUSINESS = "business";

    private Business mBusiness;
    private RecyclerView mRecyclerView;

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

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            mRecyclerView = recyclerView;

            LinearLayoutManager manager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(new ReviewRecyclerViewAdapter(mBusiness));
        }
        return view;
    }


}
