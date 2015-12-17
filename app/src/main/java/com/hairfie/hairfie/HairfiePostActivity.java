package com.hairfie.hairfie;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HairfiePostActivity extends AppCompatActivity {

    public static final String ARG_PICTUREFILEPATHS = "picture-file-paths";

    private List<File> mPictureFiles = new ArrayList<>();


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

        // Fill image views
        ImageView picture1ImageView = (ImageView)findViewById(R.id.picture1);
        if (null != picture1ImageView && mPictureFiles.size() > 0)
            Application.getPicasso().load(mPictureFiles.get(0)).centerCrop().fit().into(picture1ImageView);

        ImageView picture2ImageView = (ImageView)findViewById(R.id.picture2);
        if (null != picture2ImageView && mPictureFiles.size() > 1)
            Application.getPicasso().load(mPictureFiles.get(1)).centerCrop().fit().into(picture2ImageView);
        else if (null != picture2ImageView)
            picture2ImageView.setVisibility(View.GONE);
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

    }

}
