package com.hairfie.hairfie;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hairfie.hairfie.helpers.RoundedCornersTransform;
import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.Service;

import org.w3c.dom.Text;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusinessServicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusinessServicesFragment extends Fragment {

    private static final String ARG_BUSINESS = "business";

    private RecyclerView mRecyclerView;

    private Business mBusiness;


    public BusinessServicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param business Parameter 1.
     * @return A new instance of fragment BusinessServicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusinessServicesFragment newInstance(Business business) {
        BusinessServicesFragment fragment = new BusinessServicesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BUSINESS, business);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBusiness = (Business)getArguments().getParcelable(ARG_BUSINESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_services, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            mRecyclerView = recyclerView;

            LinearLayoutManager manager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
                @Override
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.fragment_service, parent, false);
                    return new ViewHolder(view);
                }

                @Override
                public void onBindViewHolder(ViewHolder holder, int position) {

                    holder.setItem(mBusiness.services[position]);

                }

                @Override
                public int getItemCount() {
                    return mBusiness.services.length;
                }
            });
        }
        return view;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private Service mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
        }

        public void setItem(Service item) {
            mItem = item;
            TextView labelView = (TextView) mView.findViewById(R.id.label);
            if (null != labelView)
                labelView.setText(item.label);

            TextView priceView = (TextView) mView.findViewById(R.id.price);
            if (null != priceView)
                priceView.setText(null != item.price ? item.price.localizedString() : "");

        }

    }
}
