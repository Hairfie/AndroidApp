package com.hairfie.hairfie;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hairfie.hairfie.models.BusinessMember;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BusinessMemberInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BusinessMemberInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusinessMemberInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BUSINESSMEMBER = "business-member";

    // TODO: Rename and change types of parameters
    private BusinessMember mBusinessMember;

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
    public static BusinessMemberInfoFragment newInstance(BusinessMember businessMember) {
        BusinessMemberInfoFragment fragment = new BusinessMemberInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BUSINESSMEMBER, businessMember);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBusinessMember = (BusinessMember)getArguments().getParcelable(ARG_BUSINESSMEMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_business_member_info, container, false);
        return view;
    }

}
