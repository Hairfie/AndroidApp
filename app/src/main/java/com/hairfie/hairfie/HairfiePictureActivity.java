package com.hairfie.hairfie;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.hairfie.hairfie.helpers.BitmapUtil;
import com.hairfie.hairfie.helpers.CameraUtil;
import com.hairfie.hairfie.helpers.GalleryUtil;
import com.hairfie.hairfie.helpers.RoundedCornersTransform;
import com.squareup.picasso.MemoryPolicy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HairfiePictureActivity extends AppCompatActivity {
    private static final int ACTION_PICK = 1001;

    private List<ImageView> mPictureImageViews = new ArrayList<>();
    private View mAddPictureView;
    private ImageView mSelectedPictureImageView;
    private View mRemoveSelectedPictureView;
    private View mNextStepView;
    private View mTakePictureView;
    private ImageView mGalleryView;
    private FrameLayout mCameraContainer;
    private View mMaskView;

    private boolean mDisableCamera = false;


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
        mGalleryView = (ImageView)findViewById(R.id.gallery);
        mCameraContainer = (FrameLayout)findViewById(R.id.camera_container);
        mMaskView = findViewById(R.id.mask);
        updateUserInterface();

        if (!CameraUtil.checkCameraHardware(this)) {
            mDisableCamera = true;
            errorAlert(getString(R.string.couldnt_access_camera));
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCamera();
        updateUserInterface();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCameraPreview();
        releaseCamera();
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
    public void touchAddPicture(View v) {
        mSelectedPictureFile = null;
        updateUserInterface();

    }
    public void touchNextStep(View v) {
        Intent intent = new Intent(this, HairfiePostActivity.class);
        String[] filenames = new String[mPictureFiles.size()];
        for (int i = 0; i < mPictureFiles.size(); i++) {
            filenames[i] = mPictureFiles.get(i).getAbsolutePath();
        }
        intent.putExtra(HairfiePostActivity.ARG_PICTUREFILEPATHS, filenames);
        startActivity(intent);
    }
    public void touchGallery(View v) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, ACTION_PICK);

    }

    public void touchPicture1(View v) {
        if (0 < mPictureFiles.size()) {
            mSelectedPictureFile = mPictureFiles.get(0);
            updateUserInterface();
        }
    }
    public void touchPicture2(View v) {
        if (1 < mPictureFiles.size()) {
            mSelectedPictureFile = mPictureFiles.get(1);
            updateUserInterface();
        }
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
            bitmapPicked(originalBitmap);

            // Update UI
            updateUserInterface();
        } catch (FileNotFoundException e) {
            Log.e(Application.TAG, "Error getting picked image", e);
        } catch (IOException e) {
            Log.e(Application.TAG, "Error getting picked image", e);
        }
    }

    private void bitmapPicked(Bitmap originalBitmap) {
        try {
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
            File pictureFile = null;
            pictureFile = BitmapUtil.saveToFile(Application.getInstance(), croppedBitmap);

            Log.d(Application.TAG, "Picked image saved at:" + pictureFile.getAbsolutePath());

            // Add to pictures
            mPictureFiles.add(pictureFile);

            // Select
            mSelectedPictureFile = pictureFile;

        } catch (IOException e) {
            Log.e(Application.TAG, "Error getting picked bitmap", e);
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

        // Camera mask
        mMaskView.setVisibility(mSelectedPictureFile == null ? View.VISIBLE : View.GONE);

        // Bottom nav
        mTakePictureView.setVisibility(mSelectedPictureFile == null && mCamera != null ? View.VISIBLE : View.GONE);
        mGalleryView.setVisibility(mSelectedPictureFile == null ? View.VISIBLE : View.GONE);

        // Remove camera preview if appropriate
        if (null != mSelectedPictureFile || null != mCameraPreview) {
            releaseCameraPreview();
        }

        // Setup camera preview
        if (null == mSelectedPictureFile && null != mCamera) {
            mCameraPreview = new CameraPreview(this, mCamera);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            mCameraPreview.setLayoutParams(layoutParams);
            mCameraContainer.addView(mCameraPreview);

        }

        // Update the gallery button
        Bitmap lastPicture = GalleryUtil.lastPictureTaken(Application.getInstance());
        if (null != lastPicture)
            mGalleryView.setImageBitmap(lastPicture);

    }


    private Camera mCamera;
    private int mWhichCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
    private CameraPreview mCameraPreview;

    public void touchSwitchCamera(View v) {
        switch (mWhichCamera) {
            case Camera.CameraInfo.CAMERA_FACING_BACK:
                mWhichCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;
                break;

            default:
                mWhichCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        setupCamera();
    }


    private void setupCamera() {
        if (mDisableCamera)
            return;

        releaseCamera();

        mCamera = CameraUtil.getCameraInstance(mWhichCamera);
        if (null == mCamera)
            return;

        // Select the right picture size
        Camera.Parameters cameraParams = mCamera.getParameters();
        if (null != cameraParams) {
            List<Camera.Size> sizes = cameraParams.getSupportedPictureSizes();

            // Sort sizes by width ascending
            Collections.sort(sizes, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size lhs, Camera.Size rhs) {
                    // We use the height here because we rotated 90°
                    if (lhs.height < rhs.height)
                        return -1;
                    if (lhs.height > rhs.height)
                        return 1;
                    return 0;
                }
            });
            Camera.Size selectedSize = null;

            // Take the smallest size if available
            if (0 < sizes.size())
                selectedSize = sizes.get(0);

            // Take the first size that is wider than the Hairfie size
            for (Camera.Size size : sizes) {

                selectedSize = size;

                // We use the height here because we rotated 90°
                if (size.height > Config.instance.getHairfiePixelSize()) {
                    break;
                }
            }

            if (null != selectedSize) {
                Log.d(Application.TAG, "Selecting picture size "+ selectedSize.width + ":" + selectedSize.height);
                cameraParams.setPictureSize(selectedSize.width, selectedSize.height);
            }
        }


    }

    public void touchTakePicture(View v) {
        if (null == mCamera)
            return;

        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                GalleryUtil.insertImage(getContentResolver(), rotatedBitmap, "", "");
                bitmap.recycle();
                bitmapPicked(rotatedBitmap);
                updateUserInterface();

            }
        });
    }

    private void releaseCamera() {
        if (mDisableCamera)
            return;

        if (null != mCamera)
            mCamera.release();

    }

    private void releaseCameraPreview() {
        if (null != mCameraPreview) {
            ViewParent parent = mCameraPreview.getParent();
            if (null != parent)
                ((ViewGroup)parent).removeView(mCameraPreview);
            mCameraPreview = null;
        }

    }

}
