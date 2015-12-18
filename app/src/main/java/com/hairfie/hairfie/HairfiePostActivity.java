package com.hairfie.hairfie;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hairfie.hairfie.models.Business;
import com.hairfie.hairfie.models.BusinessMember;
import com.hairfie.hairfie.models.Hairfie;
import com.hairfie.hairfie.models.Picture;
import com.hairfie.hairfie.models.Price;
import com.hairfie.hairfie.models.ResultCallback;
import com.hairfie.hairfie.models.Tag;
import com.hairfie.hairfie.models.TagCategory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HairfiePostActivity extends AppCompatActivity {

    public static final String ARG_PICTUREFILEPATHS = "picture-file-paths";
    private static final int REQUEST_CODE_CHOOSE_BUSINESS = 1002;

    private List<File> mPictureFiles = new ArrayList<>();
    private List<Picture> mPictures = new ArrayList<>();
    private List<Tag> mSelectedTags = new ArrayList<>();
    private ViewGroup mContainer;

    private Button mWhoButton;
    private Button mWhereButton;
    private EditText mPriceEditText;
    private EditText mCustomerEmailEditText;
    private Business mBusiness;
    private BusinessMember mBusinessMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hairfie_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView titleTextView = (TextView)findViewById(R.id.appbar_title);
        if (null != titleTextView)
            titleTextView.setText(R.string.post_hairfie);



        String[] pictureFilePaths = getIntent().getStringArrayExtra(ARG_PICTUREFILEPATHS);
        if (null == pictureFilePaths) {
            Log.e(Application.TAG, "No picture file passed");
            finish();
        }
        for (int i = 0; i < pictureFilePaths.length; i++) {
            mPictureFiles.add(new File(pictureFilePaths[i]));
        }

        // Price
        mPriceEditText = (EditText)findViewById(R.id.price);

        // Customer email
        mCustomerEmailEditText = (EditText)findViewById(R.id.email);

        // Who & Where
        mWhoButton = (Button)findViewById(R.id.who);
        mWhereButton = (Button)findViewById(R.id.where);
        mWhoButton.setVisibility(View.GONE);


        // Fill image views
        ImageView picture1ImageView = (ImageView)findViewById(R.id.picture1);
        if (null != picture1ImageView && mPictureFiles.size() > 0)
            Application.getPicasso().load(mPictureFiles.get(0)).centerCrop().fit().into(picture1ImageView);

        ImageView picture2ImageView = (ImageView)findViewById(R.id.picture2);
        if (null != picture2ImageView && mPictureFiles.size() > 1)
            Application.getPicasso().load(mPictureFiles.get(1)).centerCrop().fit().into(picture2ImageView);
        else if (null != picture2ImageView)
            picture2ImageView.setVisibility(View.GONE);

        mContainer = (ViewGroup)findViewById(R.id.container);

        // Fill tag categories
        TagCategory.ordered(new ResultCallback.Single<List<TagCategory>>() {
            @Override
            public void onComplete(@Nullable List<TagCategory> object, @Nullable ResultCallback.Error error) {
                for (TagCategory category : object) {
                    View categoryView = getLayoutInflater().inflate(R.layout.fragment_tag_category, mContainer, false);
                    mContainer.addView(categoryView);
                    TextView categoryTextView = (TextView) categoryView.findViewById(R.id.category);
                    categoryTextView.setText(category.name);

                    ViewGroup keywordContainer = (ViewGroup)categoryView.findViewById(R.id.tags);
                    for (final Tag tag : category.tags) {
                        TextView tagTextView = (TextView) getLayoutInflater().inflate(R.layout.fragment_tag, keywordContainer, false);
                        tagTextView.setText(tag.name);
                        tagTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                touchTag(tag, v);
                            }
                        });
                        keywordContainer.addView(tagTextView);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.getInstance().trackScreenName("HairfiePostActivity");

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

    public void touchPost(View v) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.sending));
        progressDialog.setCancelable(false);
        progressDialog.show();
        uploadPictures(new UploadCallback() {
            @Override
            public void onUploadComplete(ResultCallback.Error error) {
                if (null != error) {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(HairfiePostActivity.this).setTitle(error.message).setPositiveButton(R.string.ok, null).show();
                    return;
                }

                // Price
                Price price = null;
                CharSequence priceString = mPriceEditText.getText();
                if (null != priceString && 0 < priceString.length()) {
                    price = new Price();
                    price.amount = Double.parseDouble(priceString.toString());
                    price.currency = "EUR";
                }


                Hairfie.post(mPictures, price, mBusiness, mBusinessMember, mCustomerEmailEditText.getText(), mSelectedTags, new ResultCallback.Single<Hairfie>() {
                    @Override
                    public void onComplete(@Nullable Hairfie object, @Nullable ResultCallback.Error error) {
                        progressDialog.dismiss();
                        if (null != error) {
                            new AlertDialog.Builder(HairfiePostActivity.this).setTitle(error.message).setPositiveButton(R.string.ok, null).show();
                            return;
                        }
                        setResult(RESULT_OK);
                        Intent intent = new Intent(HairfiePostActivity.this, HairfieActivity.class);
                        intent.putExtra(HairfieActivity.ARG_HAIRFIE, object);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });


    }

    private interface UploadCallback {
        void onUploadComplete(ResultCallback.Error error);
    }
    private void uploadPictures(final UploadCallback uploadCallback) {
        if (mPictureFiles.size() <= mPictures.size()) {
            if (null != uploadCallback)
                uploadCallback.onUploadComplete(null);
            return;
        }

        File pictureToUpload = mPictureFiles.get(mPictures.size());
        Picture.upload(pictureToUpload, Picture.HAIRFIES_CONTAINER, new ResultCallback.Single<Picture>(){

            @Override
            public void onComplete(@Nullable Picture object, @Nullable ResultCallback.Error error) {
                if (null != error || null == object) {
                    if (null != uploadCallback)
                        uploadCallback.onUploadComplete(error);
                    return;
                }

                mPictures.add(object);
                uploadPictures(uploadCallback);
            }
        });


    }

    public void touchWho(View v) {
        if (null == mBusiness || null == mBusiness.activeHairdressers || 0 == mBusiness.activeHairdressers.length)
            return;

        final CharSequence[] hairdressers = new CharSequence[mBusiness.activeHairdressers.length];
        for (int i = 0; i < mBusiness.activeHairdressers.length; i++)
            hairdressers[i] = mBusiness.activeHairdressers[i].getFullname();

        new AlertDialog.Builder(this).setTitle(getString(R.string.choose_hairdresser)).setItems(hairdressers, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBusinessMember = mBusiness.activeHairdressers[which];
                mWhoButton.setText(hairdressers[which]);
            }
        }).show();


    }

    public void touchWhere(View v) {
        new AlertDialog.Builder(this).setItems(R.array.where_choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {

                    case 0:
                        mWhereButton.setText(R.string.i_did_it);
                        mBusiness = null;
                        mWhoButton.setVisibility(View.GONE);
                        break;

                    default:
                        Intent intent = new Intent(HairfiePostActivity.this, BusinessSearchActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_CHOOSE_BUSINESS);
                        break;

                }
            }
        }).show();

    }

    private void touchTag(Tag tag, View v) {

        if (v.isSelected()) {
            v.setSelected(false);
            mSelectedTags.remove(tag);
        } else {
            v.setSelected(true);
            if (!mSelectedTags.contains(tag))
                mSelectedTags.add(tag);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_CHOOSE_BUSINESS == requestCode) {
            if (RESULT_OK == resultCode && null != data) {
                mBusiness = (Business)data.getParcelableExtra(BusinessSearchActivity.RESULT_BUSINESS);
                mBusinessMember = null;
                mWhoButton.setVisibility(View.GONE);
                if (null != mBusiness) {
                    mWhereButton.setText(mBusiness.name);
                    if (mBusiness.activeHairdressers != null && mBusiness.activeHairdressers.length > 0) {
                        mWhoButton.setVisibility(View.VISIBLE);
                        mWhoButton.setText(R.string.who);
                    }

                }
            }
        }
    }
}
