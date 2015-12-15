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

    List<CharSequence> mKeywords = new ArrayList<>();

    float mTextSize = 13;


    public void setKeywords(List<CharSequence> keywords) {
        mKeywords.clear();
        mKeywords.addAll(keywords);

        // Remove all children
        removeAllViews();

        // Rebuild
        buildSubviews();
    }
    void buildSubviews() {
        float density = getContext().getResources().getDisplayMetrics().density;
        for (CharSequence keyword : mKeywords) {

            TextView textView = new TextView(getContext());
            textView.setText(keyword);
            textView.setPadding((int) (5 * density), (int) (5 * density), (int) (5 * density), (int) (5 * density));
            textView.setTextSize(mTextSize);
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextColor(getResources().getColor(R.color.colorText));
            textView.setBackgroundResource(R.drawable.keyword_background);
            addView(textView);
        }

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
            if (!(view instanceof TextView))
                continue;

            TextView textView = (TextView)view;
            measureChild(textView, widthMeasureSpec, heightMeasureSpec);
            int measuredWidth = textView.getMeasuredWidth();
            int measuredHeight = textView.getMeasuredHeight();

            if (x != getPaddingLeft() && x + measuredWidth + getPaddingRight() > ourMeasuredWidth) {
                y += lineHeight + verticalSpacing;
                x = getPaddingLeft();
                lineHeight = 0;
            }
            maxY = Math.max(maxY, y + textView.getMeasuredHeight());

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
            if (!(view instanceof TextView))
                continue;

            TextView textView = (TextView)view;
            int measuredWidth = textView.getMeasuredWidth();
            int measuredHeight = textView.getMeasuredHeight();

            if (x != getPaddingLeft() && x + measuredWidth + getPaddingRight() > ourMeasuredWidth) {
                y += lineHeight + verticalSpacing;
                x = getPaddingLeft();
                lineHeight = 0;
            }
            textView.layout(x, y, x + measuredWidth, y + textView.getMeasuredHeight());

            x += measuredWidth + horizontalSpacing;
            lineHeight = Math.max(lineHeight, measuredHeight);
        }
    }
}
