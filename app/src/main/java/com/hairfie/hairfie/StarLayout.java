package com.hairfie.hairfie;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by stephh on 10/12/15.
 */
public class StarLayout extends LinearLayout {
    public StarLayout(Context context) {
        super(context);
        prepare();
    }

    public StarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        prepare();
    }

    public StarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        prepare();
    }


    private static int[] identifiers = {
            R.id.star1,
            R.id.star2,
            R.id.star3,
            R.id.star4,
            R.id.star5,
    };

    void prepare() {

        float density = getContext().getResources().getDisplayMetrics().density;

        setOrientation(HORIZONTAL);

        for (int i = 0; i < identifiers.length; i++) {
            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, Math.round(5 * density), 0);
            imageView.setLayoutParams(layoutParams);
            imageView.setId(identifiers[i]);
            imageView.setImageResource(R.drawable.not_selected_star);
            addView(imageView);
        }
    }

    public void setRating(Float rating) {
        Float ratingOn5 = rating * identifiers.length;
        for (int i = 0; i < identifiers.length; i++) {
            ImageView starImageView = (ImageView)findViewById(identifiers[i]);
            starImageView.setVisibility(View.VISIBLE);
            if ((i + 1) <= ratingOn5)
                starImageView.setImageResource(R.drawable.selected_star);
            else if ((i + 0.5f) <= ratingOn5)
                starImageView.setImageResource(R.drawable.half_selected_star);
            else
                starImageView.setImageResource(R.drawable.not_selected_star);
        }

    }

}
