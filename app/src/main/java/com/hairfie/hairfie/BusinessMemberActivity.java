package com.hairfie.hairfie;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hairfie.hairfie.helpers.BlurTransform;
import com.hairfie.hairfie.helpers.CircleTransform;
import com.hairfie.hairfie.models.BusinessMember;
import com.hairfie.hairfie.models.Hairfie;
import com.hairfie.hairfie.models.Picture;

import java.util.Locale;

public class BusinessMemberActivity extends AppCompatActivity implements HairfieGridFragment.OnHairfieGridFragmentInteractionListener {

    public static final String ARG_BUSINESSMEMBER = "business-member";

    private BusinessMember mBusinessMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_member);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mBusinessMember = (BusinessMember)getIntent().getParcelableExtra(ARG_BUSINESSMEMBER);
        if (null == mBusinessMember) {
            Log.e(Application.TAG, "Null business member");
            finish();
        }



        // Name
        TextView nameTextView = (TextView) findViewById(R.id.name);
        if (null != nameTextView)
            nameTextView.setText(mBusinessMember.getFullname());

        final TextView name2TextView = (TextView) findViewById(R.id.name2);
        if (null != name2TextView)
            name2TextView.setText(mBusinessMember.getFullname());

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (collapsingToolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbar)) {
                    name2TextView.animate().alpha(1).setDuration(600);
                } else {
                    name2TextView.animate().alpha(0).setDuration(600);
                }
            }
        });
        // Picture
        Picture picture = mBusinessMember.getPicture();
        ImageView pictureImageView = (ImageView) findViewById(R.id.main_picture);
        ImageView picture2ImageView = (ImageView) findViewById(R.id.circle_picture);

        if (null != picture) {
            if (null != pictureImageView)
                Application.getPicasso().load(mBusinessMember.getPicture().url).transform(new BlurTransform(1.0f, 40)).fit().centerCrop().into(pictureImageView);
            if (null != picture2ImageView)
                Application.getPicasso().load(mBusinessMember.getPicture().url).transform(new CircleTransform()).fit().centerCrop().into(picture2ImageView);
        } else {
            if (null != pictureImageView)
                Application.getPicasso().load(R.drawable.default_user_picture).transform(new BlurTransform(1.0f, 40)).fit().centerCrop().into(pictureImageView);
            if (null != picture2ImageView)
                Application.getPicasso().load(R.drawable.default_user_picture).transform(new CircleTransform()).fit().centerCrop().into(picture2ImageView);

        }

        // View pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        final BusinessMemberInfoFragment infoFragment = BusinessMemberInfoFragment.newInstance(mBusinessMember);
        final HairfieGridFragment hairfiesFragment = HairfieGridFragment.newInstance(2, null, mBusinessMember);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return infoFragment;
                    case 1:
                        return hairfiesFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }

        });


        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        for (int i = 0; i < tabs.getTabCount(); i++) {
            int icon;
            switch (i) {
                case 0:
                    icon = R.drawable.tab_business_infos;
                    break;
                case 1:
                    icon = R.drawable.tab_business_hairfies;
                    break;
                default:
                    continue;
            }
            tabs.getTabAt(i).setIcon(icon);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTouchHairfie(Hairfie item) {
        Log.d(Application.TAG, "Touch hairfie:"+item.id);
    }
}
