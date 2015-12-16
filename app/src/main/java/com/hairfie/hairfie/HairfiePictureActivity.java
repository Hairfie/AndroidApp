package com.hairfie.hairfie;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.hairfie.hairfie.helpers.BitmapUtil;
import com.hairfie.hairfie.helpers.FileUtils;
import com.hairfie.hairfie.helpers.RoundedCornersTransform;
import com.squareup.picasso.MemoryPolicy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HairfiePictureActivity extends AppCompatActivity {
    private static final int ACTION_PICK = 1001;

    private List<ImageView> mPictureImageViews = new ArrayList<>();
    private View mAddPictureView;
    private ImageView mSelectedPictureImageView;
    private View mRemoveSelectedPictureView;
    private View mNextStepView;
    private View mTakePictureView;
    private View mGalleryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hairfie_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mPictureImageViews.add((ImageButton) findViewById(R.id.picture1));
        mPictureImageViews.add((ImageButton) findViewById(R.id.picture2));
        mAddPictureView = findViewById(R.id.add_picture);
        mRemoveSelectedPictureView = findViewById(R.id.remove_selected_picture);
        mSelectedPictureImageView = (ImageView)findViewById(R.id.selected_picture);
        mNextStepView = findViewById(R.id.next_step);
        mTakePictureView = findViewById(R.id.take_picture);
        mGalleryView = findViewById(R.id.gallery);

        updateUserInterface();
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

    public void touchRemoveSelectedPicture(View v) {
        if (null == mSelectedPictureFile)
            return;

        mPictureFiles.remove(mSelectedPictureFile);
        mSelectedPictureFile = mPictureFiles.size() == 0 ? null : mPictureFiles.get(mPictureFiles.size() - 1);
        updateUserInterface();
    }
    public void touchSwitchCamera(View v) {

    }
    public void touchAddPicture(View v) {
        mSelectedPictureFile = null;
        updateUserInterface();

    }
    public void touchNextStep(View v) {

    }
    public void touchGallery(View v) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, ACTION_PICK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ACTION_PICK == requestCode) {
            if (RESULT_OK == resultCode)
                imagePicked(data);
            return;
        }
    }

    private void imagePicked(Intent data) {

        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(data.getData());

            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

            // Crop to square
            Bitmap croppedBitmap = BitmapUtil.cropToSquare(originalBitmap);
            originalBitmap.recycle();

            // Resize
            if (croppedBitmap.getWidth() > Config.instance.getHairfiePixelSize()) {
                Bitmap scaled = Bitmap.createScaledBitmap(croppedBitmap, Config.instance.getHairfiePixelSize(), Config.instance.getHairfiePixelSize(), false);
                croppedBitmap.recycle();
                croppedBitmap = scaled;
            }

            // Save to file
            File pictureFile = BitmapUtil.saveToFile(Application.getInstance(), croppedBitmap);

            Log.d(Application.TAG, "Picked image saved at:" + pictureFile.getAbsolutePath());

            // Add to pictures
            mPictureFiles.add(pictureFile);

            // Select
            mSelectedPictureFile = pictureFile;

            // Update UI
            updateUserInterface();
        } catch (FileNotFoundException e) {
            Log.e(Application.TAG, "Error getting picked image", e);
        } catch (IOException e) {
            Log.e(Application.TAG, "Error getting picked image", e);
        }
    }

    void errorAlert(String message) {
        new AlertDialog.Builder(this).setTitle(message).setPositiveButton(R.string.ok, null).show();
    }


    private List<File> mPictureFiles = new ArrayList<>();
    private File mSelectedPictureFile;

    void updateUserInterface() {

        // Top nav pix
        for (int i = 0; i < mPictureImageViews.size(); i++) {
            ImageView pictureImageView = mPictureImageViews.get(i);
            if (i < mPictureFiles.size()) {
                File file = mPictureFiles.get(i);
                pictureImageView.setVisibility(View.VISIBLE);
                Application.getPicasso().load(file).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).fit().centerCrop().transform(new RoundedCornersTransform(3, 0)).into(pictureImageView);
            } else {
                pictureImageView.setVisibility(View.GONE);
            }
        }

        // Topnav buttons
        mAddPictureView.setVisibility(mSelectedPictureFile == null || mPictureFiles.size() == mPictureImageViews.size() ? View.GONE : View.VISIBLE);
        mNextStepView.setVisibility(mPictureFiles.size() > 0 ? View.VISIBLE : View.GONE);

        // Selected picture area
        mSelectedPictureImageView.setVisibility(mSelectedPictureFile != null ? View.VISIBLE : View.GONE);
        mRemoveSelectedPictureView.setVisibility(mSelectedPictureFile != null ? View.VISIBLE : View.GONE);
        if (null != mSelectedPictureFile)
            Application.getPicasso().load(mSelectedPictureFile).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).centerCrop().fit().into(mSelectedPictureImageView);

        // Bottom nav
        mTakePictureView.setVisibility(mSelectedPictureFile != null ? View.GONE : View.VISIBLE);
        mGalleryView.setVisibility(mSelectedPictureFile == null ? View.VISIBLE : View.GONE);
    }


}
