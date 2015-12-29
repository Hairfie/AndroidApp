package com.hairfie.hairfie;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hairfie.hairfie.helpers.CircleTransform;
import com.hairfie.hairfie.models.Address;
import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.BusinessMember;
import com.hairfie.hairfie.models.ResultCallback;
import com.hairfie.hairfie.models.Timetable;

import org.w3c.dom.Text;

import java.util.List;


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

    private View mView;

    private ProgressBar mProgressBar;
    private boolean mHairdressersFetched;
    private boolean mSimilarBusinessesFetched;

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
        mView = view;
        mProgressBar = (ProgressBar)mView.findViewById(R.id.progress);

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

        fetchHairdressers();


        fetchSimilarBusinesses();
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

    void fetchHairdressers() {
        BusinessMember.activeInBusiness(mBusiness, new ResultCallback.Single<List<BusinessMember>>() {
            @Override
            public void onComplete(@Nullable List<BusinessMember> object, @Nullable ResultCallback.Error error) {

                mHairdressersFetched = true;
                updateProgressView();
                if (null != error) {
                    Log.e(Application.TAG, "Could not get active hairdressers:" + error.message, error.cause);
                    return;
                }
                setupActiveHairdressers(object);
            }
        });
    }

    private void setupActiveHairdressers(List<BusinessMember> object) {
        View view = getView();
        if (null == view)
            return;

        BusinessMember[] activeHairdressers = new BusinessMember[object.size()];
        object.toArray(activeHairdressers);

        // Hairdressers
        LinearLayout hairdressersContainer = (LinearLayout)view.findViewById(R.id.hairdressers_container);
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (null != hairdressersContainer ) {
            TextView title = (TextView)view.findViewById(R.id.hairdressers_title);
            if (null != title)
                title.setVisibility(null != activeHairdressers && activeHairdressers.length > 0 ? View.VISIBLE : View.GONE);
            for (int i = 0; null != activeHairdressers && i < activeHairdressers.length; i++) {
                final BusinessMember hairdresser = activeHairdressers[i];
                View hairdresserView = inflater.inflate(R.layout.fragment_business_member, null, false);
                hairdressersContainer.addView(hairdresserView);

                hairdresserView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        touchHairdresser(hairdresser);
                    }
                });
                ImageView imageView = (ImageView)hairdresserView.findViewById(R.id.picture);
                imageView.setVisibility(View.VISIBLE);
                if (null != imageView) {
                    if (null != hairdresser.getPicture())
                        Application.getPicasso().load(hairdresser.getPicture().url).transform(new CircleTransform()).fit().centerCrop().into(imageView);
                    else
                        Application.getPicasso().load(R.drawable.default_user_picture).transform(new CircleTransform()).fit().centerCrop().into(imageView);
                }

                TextView textView = (TextView)hairdresserView.findViewById(R.id.name);
                if (null != textView) {
                    textView.setText(hairdresser.getAbbreviatedName());
                }

            }

        }
    }

    void fetchSimilarBusinesses() {
        // Similar business
        if (!mBusiness.isPremium()) {
            Business.listSimilar(mBusiness, new ResultCallback.Single<List<Business>>() {
                @Override
                public void onComplete(@Nullable List<Business> object, @Nullable ResultCallback.Error error) {
                    mSimilarBusinessesFetched = true;
                    updateProgressView();

                    if (null != error) {
                        Log.e(Application.TAG, "Error getting similar businesses:" + error.message, error.cause);
                        return;
                    }
                    setupSimilarBusinesses(object);
                }
            });
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

        void onTouchSimilarBusiness(Business business);
        void onTouchAddress(Address address);

        void onTouchTimetable(Timetable timetable);

        void onTouchBusinessMember(BusinessMember hairdresser);
    }

    void setupSimilarBusinesses(List<Business> businesses) {
        // Hairdressers
        LinearLayout container = (LinearLayout)mView.findViewById(R.id.similar_businesses_container);
        if (null != container ) {
            container.setVisibility(View.VISIBLE);
            TextView title = (TextView)mView.findViewById(R.id.similar_businesses_title);
            if (null != title)
                title.setVisibility(businesses.size() > 0 ? View.VISIBLE : View.GONE);
            int counter = 0;
            for (final Business business : businesses) {
                if (counter >= 3)
                    break;

                View hairdresserView = getActivity().getLayoutInflater().inflate(R.layout.fragment_business, null, false);
                BusinessRecyclerViewAdapter.ViewHolder holder = new BusinessRecyclerViewAdapter.ViewHolder(hairdresserView);
                holder.setItem(business, Application.getInstance().getLastLocation(), false);
                container.addView(hairdresserView);

                hairdresserView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener)
                            mListener.onTouchSimilarBusiness(business);
                    }
                });
                counter++;

            }

        }
    }

    void updateProgressView() {
        mProgressBar.setVisibility(mHairdressersFetched && mSimilarBusinessesFetched ? View.GONE : View.VISIBLE);
    }

}
