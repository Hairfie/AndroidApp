package com.hairfie.hairfie;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hairfie.hairfie.helpers.RoundedCornersTransform;
import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.BusinessMember;

import java.util.Locale;


/**
 */
public class BusinessMemberInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BUSINESSMEMBER = "business-member";
    private static final String ARG_BUSINESS = "business";

    // TODO: Rename and change types of parameters
    private BusinessMember mBusinessMember;
    private Business mBusiness;

    public BusinessMemberInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param businessMember Parameter 1.
     * @return A new instance of fragment BusinessMemberInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusinessMemberInfoFragment newInstance(BusinessMember businessMember, Business business) {
        BusinessMemberInfoFragment fragment = new BusinessMemberInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BUSINESSMEMBER, businessMember);
        args.putParcelable(ARG_BUSINESS, business);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBusinessMember = (BusinessMember)getArguments().getParcelable(ARG_BUSINESSMEMBER);
            mBusiness = (Business)getArguments().getParcelable(ARG_BUSINESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_business_member_info, container, false);

        // Business picture
        ImageView businessPictureImageView = (ImageView)view.findViewById(R.id.business_picture);
        Business business = mBusiness;
        if (null != businessPictureImageView && null != business && null != business.pictures && 0 < business.pictures.length) {
            Application.getPicasso().load(business.pictures[0].url).transform(new RoundedCornersTransform(5, 0)).fit().centerCrop().into(businessPictureImageView);
        }

        TextView addressTextView = (TextView)view.findViewById(R.id.business_address);
        if (null != addressTextView && null != business) {
            if (null != business.address)
                addressTextView.setText(String.format(Locale.getDefault(), "%s\n%s %s", business.name, business.address.zipCode, business.address.city));
            else
                addressTextView.setText(business.name);
        }

        return view;
    }

}
