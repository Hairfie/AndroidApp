package com.hairfie.hairfie;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.okhttp.Request;
import com.squareup.picasso.RequestCreator;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictureFragment extends Fragment {

    private static final String ARG_PICTURE_URL = "picture-url";
    private static final String ARG_PLACEHOLDER_RESOURCE = "placeholder-resource";
    private RequestCreator mRequestCreator;

    public PictureFragment() {
        // Required empty public constructor
    }
    public static PictureFragment newInstance(CharSequence pictureUrl) {
        return newInstance(pictureUrl, 0);
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pictureUrl Parameter 1.
     * @return A new instance of fragment PictureFragment.
     */

    public static PictureFragment newInstance(CharSequence pictureUrl, int placeholderResource) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putCharSequence(ARG_PICTURE_URL, pictureUrl);
        args.putInt(ARG_PLACEHOLDER_RESOURCE, placeholderResource);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            CharSequence url = getArguments().getCharSequence(ARG_PICTURE_URL);
            int placeholderResource = getArguments().getInt(ARG_PLACEHOLDER_RESOURCE);
            if (null != url) {
                mRequestCreator = Application.getPicasso().load(url.toString());
                if (0 != placeholderResource)
                    mRequestCreator = mRequestCreator.placeholder(placeholderResource);
                mRequestCreator.fetch();
            } else if (0 != placeholderResource) {
                mRequestCreator = Application.getPicasso().load(placeholderResource);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_picture, container, false);
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView)view;
            if (null != mRequestCreator)
                mRequestCreator.into(imageView);
        }
        return view;
    }

}
