package com.hairfie.hairfie;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hairfie.hairfie.models.Hairfie;

import java.util.Locale;

/**
 * Created by stephh on 15/12/15.
 */
public class HairfieLikesView extends LinearLayout {
    private View mLikeContainerView;
    private TextView mLikeCountTextView;
    private Hairfie mHairfie;
    public HairfieLikesView(Context context) {
        super(context);
        init();
    }

    public HairfieLikesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HairfieLikesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        View view = View.inflate(getContext(), R.layout.fragment_hairfie_likes, this);
        mLikeContainerView = view.findViewById(R.id.like_container);
        mLikeCountTextView = (TextView) view.findViewById(R.id.like_count);
        update();
    }

    public void setHairfie(Hairfie hairfie) {
        mHairfie = hairfie;
        update();
    }

    private void update() {
        if (null == mHairfie)
            return;

        if (mHairfie.numLikes > 0) {
            mLikeContainerView.setVisibility(View.VISIBLE);
            mLikeCountTextView.setText(String.format(Locale.getDefault(), "%d", mHairfie.numLikes));
        } else {
            mLikeContainerView.setVisibility(View.GONE);
        }
    }
}
