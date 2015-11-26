package com.hairfie.hairfie;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.hairfie.hairfie.helpers.CircleTransform;
import com.squareup.picasso.Picasso;

import java.io.File;

import pl.aprilapps.easyphotopicker.EasyImage;

public class SignupActivity extends AppCompatActivity {

    @NonNull
    ImageButton mPhotoButton;

    @Nullable
    File mPictureFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPhotoButton = (ImageButton) findViewById(R.id.photo);

    }

    public void touchPhoto(View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.add_photo)
                .setItems(R.array.image_picker_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (0 == which) {
                            EasyImage.openCamera(SignupActivity.this);
                        } else {
                            EasyImage.openGallery(SignupActivity.this);
                        }
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                //Handle the image
                onPhotoReturned(imageFile);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource imageSource) {

            }
        });
    }

    void onPhotoReturned(File photo) {
        mPictureFile = photo;
        Picasso.with(this).load(photo).fit().centerCrop().transform(new CircleTransform()).into(mPhotoButton);

    }
}
