package com.hairfie.hairfie;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictureFragment extends Fragment {

    private static final String ARG_PICTURE_URL = "picture-url";

    private CharSequence mPictureUrl;


    public PictureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pictureUrl Parameter 1.
     * @return A new instance of fragment PictureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PictureFragment newInstance(CharSequence pictureUrl) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putCharSequence(ARG_PICTURE_URL, pictureUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPictureUrl = getArguments().getCharSequence(ARG_PICTURE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_picture, container, false);
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView)view;
            Application.getPicasso().load(mPictureUrl.toString()).into(imageView);
        }
        return view;
    }

}
