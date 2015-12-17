package com.hairfie.hairfie;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephh on 14/12/15.
 */
public class KeywordLayout extends ViewGroup {
    public KeywordLayout(Context context) {
        super(context);
    }

    public KeywordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeywordLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    int mHorizontalSpacing = 10;
    int mVerticalSpacing = 5;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        int maxY = 0;

        float density = getResources().getDisplayMetrics().density;
        int horizontalSpacing = Math.round(density * mHorizontalSpacing);
        int verticalSpacing = Math.round(density * mVerticalSpacing);
        int ourMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int x = getPaddingLeft();
        int y = getPaddingTop();
        int lineHeight = 0;

        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);

            measureChild(view, widthMeasureSpec, heightMeasureSpec);
            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();

            if (x != getPaddingLeft() && x + measuredWidth + getPaddingRight() > ourMeasuredWidth) {
                y += lineHeight + verticalSpacing;
                x = getPaddingLeft();
                lineHeight = 0;
            }
            maxY = Math.max(maxY, y + view.getMeasuredHeight());

            x += measuredWidth + horizontalSpacing;
            lineHeight = Math.max(lineHeight, measuredHeight);
        }

        setMeasuredDimension(ourMeasuredWidth, maxY + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();

        float density = getResources().getDisplayMetrics().density;
        int horizontalSpacing = Math.round(density * mHorizontalSpacing);
        int verticalSpacing = Math.round(density * mVerticalSpacing);
        int ourMeasuredWidth = getMeasuredWidth();
        ;
        int x = getPaddingLeft();
        int y = getPaddingTop();
        int lineHeight = 0;

        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);

            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();

            if (x != getPaddingLeft() && x + measuredWidth + getPaddingRight() > ourMeasuredWidth) {
                y += lineHeight + verticalSpacing;
                x = getPaddingLeft();
                lineHeight = 0;
            }
            view.layout(x, y, x + measuredWidth, y + view.getMeasuredHeight());

            x += measuredWidth + horizontalSpacing;
            lineHeight = Math.max(lineHeight, measuredHeight);
        }
    }
}
