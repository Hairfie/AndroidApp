package com.hairfie.hairfie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.hairfie.hairfie.models.Business;

public class BusinessActivity extends AppCompatActivity {

    public static final String EXTRA_BUSINESS = "EXTRA_BUSINESS";

    Business mBusiness;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        mBusiness = null != intent ? (Business)intent.getParcelableExtra(EXTRA_BUSINESS) : null;
        if (null == mBusiness) {
            Log.e(Application.TAG, "Null business");
            finish();
        }

    }
}
