package com.hairfie.hairfie;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hairfie.hairfie.models.Hairfie;
import com.hairfie.hairfie.models.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HairfieActivity extends AppCompatActivity {

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
        for (Tag tag : mHairfie.orderedTags())
            keywords.add(tag.name);
        keywordLayout.setKeywords(keywords);

        HairfieLikesView likesView = (HairfieLikesView)findViewById(R.id.like_container);
        if (null != likesView)
            likesView.setHairfie(mHairfie);

        ImageView pictureImageView = (ImageView)findViewById(R.id.picture);
        if (null != pictureImageView && mHairfie.pictures.length > 0) {
            Application.getPicasso().load(mHairfie.pictures[0].url).fit().centerCrop().into(pictureImageView);
        }


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

}
