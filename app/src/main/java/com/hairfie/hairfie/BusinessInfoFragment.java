package com.hairfie.hairfie;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hairfie.hairfie.helpers.CircleTransform;
import com.hairfie.hairfie.models.Address;
import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.BusinessMember;
import com.hairfie.hairfie.models.Timetable;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BusinessInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BusinessInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusinessInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BUSINESS = "business";

    // TODO: Rename and change types of parameters
    private Business mBusiness;

    private OnFragmentInteractionListener mListener;

    public BusinessInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param business Parameter 1.
     * @return A new instance of fragment BusinessInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusinessInfoFragment newInstance(Business business) {
        BusinessInfoFragment fragment = new BusinessInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_business_info, container, false);

        Button addressButton = (Button)view.findViewById(R.id.address);
        if (null != addressButton) {
            addressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener)
                        mListener.onTouchAddress(mBusiness.address);
                }
            });
            if (mBusiness.address != null) {
                addressButton.setText(mBusiness.address.toString());
                addressButton.setVisibility(View.VISIBLE);
            } else
                addressButton.setVisibility(View.GONE);

        }

        Button timetableButton = (Button)view.findViewById(R.id.timetable);
        if (null != timetableButton) {
            timetableButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener)
                        mListener.onTouchTimetable(mBusiness.timetable);
                }
            });

            if (null == mBusiness.timetable) {
                timetableButton.setVisibility(View.GONE);
            } else {
                timetableButton.setVisibility(View.VISIBLE);
                if (mBusiness.timetable.isOpenToday()) {
                    timetableButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGreen));
                    timetableButton.setText(R.string.open_today);
                    timetableButton.setBackgroundResource(R.drawable.open_background);

                } else {
                    timetableButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorText));
                    timetableButton.setText(R.string.closed_today);
                    timetableButton.setBackgroundResource(R.drawable.closed_background);

                }
            }
        }

        // Hairdressers
        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.linear_layout);
        if (null != linearLayout ) {
            TextView title = (TextView)view.findViewById(R.id.hairdressers_title);
            if (null != title)
                title.setVisibility(mBusiness.activeHairdressers.length > 0 ? View.VISIBLE : View.GONE);
            for (int i = 0; i < mBusiness.activeHairdressers.length; i++) {
                final BusinessMember hairdresser = mBusiness.activeHairdressers[i];
                View hairdresserView = inflater.inflate(R.layout.fragment_business_member, null , false);
                linearLayout.addView(hairdresserView);

                hairdresserView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        touchHairdresser(hairdresser);
                    }
                });
                ImageView imageView = (ImageView)hairdresserView.findViewById(R.id.picture);
                if (null != imageView) {
                    if (null != hairdresser.getPicture()) {
                        imageView.setVisibility(View.VISIBLE);
                        Application.getPicasso().load(hairdresser.getPicture().url).transform(new CircleTransform()).fit().centerCrop().into(imageView);
                    } else
                        imageView.setVisibility(View.INVISIBLE);
                }

                TextView textView = (TextView)hairdresserView.findViewById(R.id.name);
                if (null != textView) {
                    textView.setText(hairdresser.getFullname());
                }

            }

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void touchHairdresser(BusinessMember hairdresser) {
        if (null != mListener)
            mListener.onTouchBusinessMember(hairdresser);
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
    public interface OnFragmentInteractionListener {
        void onTouchAddress(Address address);

        void onTouchTimetable(Timetable timetable);

        void onTouchBusinessMember(BusinessMember hairdresser);
    }
}
