package com.hairfie.hairfie;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidpagecontrol.PageControl;
import com.hairfie.hairfie.helpers.CircleTransform;
import com.hairfie.hairfie.models.BusinessMember;
import com.hairfie.hairfie.models.Hairfie;
import com.hairfie.hairfie.models.Tag;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HairfieActivity extends AppCompatActivity {
    private static final int CALL_PERMISSION_REQUEST = 101;

    public static final String ARG_HAIRFIE = "hairfie";
    private Hairfie mHairfie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hairfie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mHairfie = (Hairfie)getIntent().getParcelableExtra(ARG_HAIRFIE);

        if (null == mHairfie) {
            Log.e(Application.TAG, "null hairfie");
            finish();
        }

        TextView titleTextView = (TextView)findViewById(R.id.appbar_title);
        if (null != titleTextView) {

            String format = getString(R.string.x_hairfie);
            if (null != mHairfie.author && null != mHairfie.author.firstName)
                titleTextView.setText(String.format(Locale.getDefault(), format, mHairfie.author.firstName));
            else if (null != mHairfie.business && null != mHairfie.business.name)
                titleTextView.setText(String.format(Locale.getDefault(), format, mHairfie.business.name));
        }

//        CharSequence[] keywords = { "Lorem", "ipsum", "dolor", "sit", "amet,", "consectetur", "qkjwehqkjwhe kqjwhe kjqhw ekjhq wkejh qkwjhe kqjhw ekjqhw ekjhq wkejh qkwjhe kqjwhe kqjhwe ", "adipiscing", "elit.", "Ut", "euismod", "sapien", "eleifend", "nunc", "scelerisque", "tincidunt.", "Nunc", "diam", "arcu,", "tempor", "ut", "leo", "sit", "amet,", "vestibulum", "luctus", "dui.", "Duis", "rhoncus", "porta", "metus,", "nec", "elementum", "ante", "pulvinar", "vitae.", "Interdum", "et", "malesuada", "fames", "ac", "ante", "ipsum", "primis", "in", "faucibus.", "Cras", "bibendum", "consequat", "dui", "eu", "pellentesque.", "Quisque", "dignissim,", "ligula", "quis", "tincidunt", "interdum,", "lorem", "tortor", "venenatis", "purus,", "vel", "suscipit", "sem", "lectus", "ac", "mauris.", "Praesent", "felis", "neque,", "viverra", "nec", "lacinia", "non,", "tempus", "non", "ligula.", "Nunc", "ultricies", "ipsum", "non", "leo", "dapibus,", "nec", "cursus", "massa", "rhoncus.", "Phasellus", "sit", "amet", "risus", "tristique", "dolor", "egestas", "pharetra", "ac", "vulputate", "diam.", "Curabitur", "eget", "nisl", "ipsum.", "Fusce", "pretium", "pulvinar", "auctor", "Lorem", "ipsum", "dolor", "sit", "amet,", "consectetur", "adipiscing", "elit.", "Ut", "euismod", "sapien", "eleifend", "nunc", "scelerisque", "tincidunt.", "Nunc", "diam", "arcu,", "tempor", "ut", "leo", "sit", "amet,", "vestibulum", "luctus", "dui.", "Duis", "rhoncus", "porta", "metus,", "nec", "elementum", "ante", "pulvinar", "vitae.", "Interdum", "et", "malesuada", "fames", "ac", "ante", "ipsum", "primis", "in", "faucibus.", "Cras", "bibendum", "consequat", "dui", "eu", "pellentesque.", "Quisque", "dignissim,", "ligula", "quis", "tincidunt", "interdum,", "lorem", "tortor", "venenatis", "purus,", "vel", "suscipit", "sem", "lectus", "ac", "mauris.", "Praesent", "felis", "neque,", "viverra", "nec", "lacinia", "non,", "tempus", "non", "ligula.", "Nunc", "ultricies", "ipsum", "non", "leo", "dapibus,", "nec", "cursus", "massa", "rhoncus.", "Phasellus", "sit", "amet", "risus", "tristique", "dolor", "egestas", "pharetra", "ac", "vulputate", "diam.", "Curabitur", "eget", "nisl", "ipsum.", "Fusce", "pretium", "pulvinar", "auctor" };

        KeywordLayout keywordLayout = (KeywordLayout)findViewById(R.id.keywords);
        List<CharSequence> keywords = new ArrayList<>();
        float density = getResources().getDisplayMetrics().density;
        for (Tag tag : mHairfie.orderedTags()) {
            TextView textView = (TextView)getLayoutInflater().inflate(R.layout.fragment_tag, keywordLayout, false);
            textView.setText(tag.name);
            keywordLayout.addView(textView);
        }


        HairfieLikesView likesView = (HairfieLikesView) findViewById(R.id.like_container);
        if (null != likesView)
            likesView.setHairfie(mHairfie);

        // Pictures
        // Pictures
        final List<PictureFragment> pictureFragments = new ArrayList<>();
        for (int i = 0; i < mHairfie.pictures.length; i++) {
            pictureFragments.add(PictureFragment.newInstance(mHairfie.pictures[i].url));
        }
        ViewPager picturesViewPager = (ViewPager)findViewById(R.id.pictures_view_pager);
        picturesViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pictureFragments.get(position);
            }

            @Override
            public int getCount() {
                return pictureFragments.size();
            }
        });


        // Page control
        PageControl pageControl = (PageControl)findViewById(R.id.page_control);
        pageControl.setViewPager(picturesViewPager);
        pageControl.setVisibility(mHairfie.pictures.length > 1 ? View.VISIBLE : View.GONE);


        ImageView authorPictureImageView = (ImageView)findViewById(R.id.author_picture);
        if (null != authorPictureImageView) {
            RequestCreator creator;
            if (null != mHairfie.author && null != mHairfie.author.picture)
                creator = Application.getPicasso().load(mHairfie.author.picture.url);
            else
                creator = Application.getPicasso().load(R.drawable.default_user_picture);

            creator.fit().centerCrop().transform(new CircleTransform()).into(authorPictureImageView);
        }

        TextView authorNameTextView = (TextView)findViewById(R.id.author_name);
        if (null != authorNameTextView && null != mHairfie.author)
            authorNameTextView.setText(mHairfie.author.getFullname());


        TextView authorNumHairfiesTextView = (TextView) findViewById(R.id.author_num_hairfies);
        if (null != authorNumHairfiesTextView && null != mHairfie.author)
            authorNumHairfiesTextView.setText(String.format(Locale.getDefault(), getString(R.string.x_hairfies), mHairfie.author.numHairfies));

        TextView dateTextView = (TextView) findViewById(R.id.date);
        if (null != dateTextView && null != mHairfie.createdAt)
            dateTextView.setText(DateUtils.getRelativeTimeSpanString(mHairfie.createdAt.getTime(), new Date().getTime(), 0, DateUtils.FORMAT_ABBREV_RELATIVE));

        View addressContainer = findViewById(R.id.address_container);
        if (null != mHairfie.business && null != mHairfie.business.address) {

            TextView businessNameTextView = (TextView) findViewById(R.id.business_name);
            if (null != businessNameTextView && null != mHairfie.business.name)
                businessNameTextView.setText(mHairfie.business.name);

            TextView businessAddressTextView = (TextView) findViewById(R.id.business_address);
            if (null != businessAddressTextView)
                businessAddressTextView.setText(mHairfie.business.address.toString());

            addressContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HairfieActivity.this, BusinessActivity.class);
                    intent.putExtra(BusinessActivity.EXTRA_BUSINESS, mHairfie.business);
                    startActivity(intent);
                }
            });
        } else {
            if (null != addressContainer)
                addressContainer.setVisibility(View.GONE);
        }

        View businessMemberContainer = findViewById(R.id.business_member_container);
        if (null != mHairfie.businessMember) {
            TextView businessMemberNameTextView = (TextView)findViewById(R.id.business_member_name);
            if (null != businessMemberNameTextView)
                businessMemberNameTextView.setText(mHairfie.businessMember.getAbbreviatedName());

            if (null != mHairfie.business)
                businessMemberContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HairfieActivity.this, BusinessMemberActivity.class);
                        intent.putExtra(BusinessMemberActivity.ARG_BUSINESSMEMBER, mHairfie.businessMember);
                        intent.putExtra(BusinessMemberActivity.ARG_BUSINESS, mHairfie.business);
                        startActivity(intent);
                    }
                });
        } else {
            if (null != businessMemberContainer)
                businessMemberContainer.setVisibility(View.GONE);
        }

        TextView priceTextView = (TextView)findViewById(R.id.price);
        if (null != mHairfie.price) {
            if (null != priceTextView)
                priceTextView.setText(mHairfie.price.localizedString());
        } else {
            View priceContainer = findViewById(R.id.price_container);
            if (null != priceContainer)
                priceContainer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.getInstance().trackScreenName("HairfieActivity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hairfie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, mHairfie.landingPageUrl);
                startActivity(Intent.createChooser(i, getString(R.string.share)));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void touchCall(View v) {
        if (null != mHairfie.business && null != mHairfie.business.phoneNumber) {
            new AlertDialog.Builder(this).setTitle(String.format(getString(R.string.call_x), mHairfie.business.phoneNumber)).setNegativeButton(getString(R.string.no), null).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mHairfie.business.phoneNumber));
                    if (ActivityCompat.checkSelfPermission(HairfieActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        String[] permissions = {Manifest.permission.CALL_PHONE};
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(permissions, CALL_PERMISSION_REQUEST);
                        }
                        return;
                    }
                    startActivity(intent);
                }
            }).show();

        }
    }

}
