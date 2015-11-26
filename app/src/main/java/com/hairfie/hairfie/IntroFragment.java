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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IntroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IntroFragment extends Fragment {
    private static final String ARG_IMAGE_RESOURCE_ID = "ARG_IMAGE_RESOURCE_ID";
    private static final String ARG_LEGEND_TEXT = "ARG_LEGEND_TEXT";

    private int mImageResourceId;
    private String mLegendText;

    public IntroFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageResourceId Resource ID for the image displayed above the text.
     * @param legendText Text appearing below the image.
     * @return A new instance of fragment IntroFragment.
     */
    public static IntroFragment newInstance(int imageResourceId, String legendText) {
        IntroFragment fragment = new IntroFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RESOURCE_ID, imageResourceId);
        args.putString(ARG_LEGEND_TEXT, legendText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageResourceId = getArguments().getInt(ARG_IMAGE_RESOURCE_ID);
            mLegendText = getArguments().getString(ARG_LEGEND_TEXT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intro, container, false);
        TextView textView = (TextView)view.findViewById(R.id.text);
        if (null != textView)
            textView.setText(mLegendText);

        ImageView imageView = (ImageView)view.findViewById(R.id.image);
        if (null != imageView)
            imageView.setImageResource(mImageResourceId);
        return view;
    }
}
