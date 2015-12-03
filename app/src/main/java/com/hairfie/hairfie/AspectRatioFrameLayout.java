package com.hairfie.hairfie;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by stephh on 03/12/15.
 */
public class AspectRatioFrameLayout extends FrameLayout {

    float mAspectRatio;
    public AspectRatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AspectRatioFrameLayout,
                0, 0);

        try {
            mAspectRatio = a.getFraction(R.styleable.AspectRatioFrameLayout_aspectRatio, 1, 1, 1);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = (int)  ((float)widthSize/mAspectRatio);
        int newHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, newHeightSpec);
    }
}
