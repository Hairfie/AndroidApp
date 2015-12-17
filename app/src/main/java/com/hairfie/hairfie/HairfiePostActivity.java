package com.hairfie.hairfie;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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


}
