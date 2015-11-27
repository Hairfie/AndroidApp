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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hairfie.hairfie.helpers.CircleTransform;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Locale;

import pl.aprilapps.easyphotopicker.EasyImage;

public class SignupActivity extends AppCompatActivity {

    @NonNull
    ImageButton mPhotoButton;

    @Nullable
    File mPictureFile;

    @NonNull
    Button mSubscribeButton;

    @NonNull
    Form mForm = new Form();

    @NonNull
    Button mGenderButton;

    @NonNull
    EditText mFirstNameEditText;

    @NonNull
    EditText mLastNameEditText;

    @NonNull
    EditText mEmailEditText;

    @NonNull
    EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPhotoButton = (ImageButton) findViewById(R.id.photo);
        mSubscribeButton = (Button) findViewById(R.id.subscribe);
        mGenderButton = (Button) findViewById(R.id.gender);
        mFirstNameEditText = (EditText) findViewById(R.id.first_name);
        mLastNameEditText = (EditText) findViewById(R.id.last_name);
        mEmailEditText = (EditText) findViewById(R.id.email);
        mPasswordEditText = (EditText) findViewById(R.id.password);


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

    public void touchSubscribe(View v) {
        mSubscribeButton.setSelected(!mSubscribeButton.isSelected());
    }

    public void touchGender(View v) {

        final String[] genderOptions = getResources().getStringArray(R.array.gender_options);
        new AlertDialog.Builder(this)
                .setTitle(R.string.civility)
                .setItems(genderOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (0 == which) {
                            mForm.gender = Gender.Female;
                            mGenderButton.setText(genderOptions[0]);
                        } else {
                            mForm.gender = Gender.Male;
                            mGenderButton.setText(genderOptions[1]);
                        }
                    }
                })
                .show();
    }

    public void touchSignup(View v) {
        Log.d(Application.TAG, mForm.toString());
    }

    enum Gender {
        Male, Female
    }


    class Form {
        Gender gender = null;
        boolean getSubscribe() {
            return mSubscribeButton.isSelected();
        }
        String getFirstName() {
            Editable editable = mFirstNameEditText.getText();
            return editable != null ? editable.toString() : null;
        }
        String getLastName() {
            Editable editable = mLastNameEditText.getText();
            return editable != null ? editable.toString() : null;
        }
        String getEmail() {
            Editable editable = mEmailEditText.getText();
            return editable != null ? editable.toString() : null;
        }
        String getPassword() {
            Editable editable = mPasswordEditText.getText();
            return editable != null ? editable.toString() : null;
        }

        @Override
        public String toString() {
            return String.format(Locale.ENGLISH, "Gender:%s Subscribe:%s First name:%s Last name:%s Email:%s Password:%s", gender == Gender.Male ? "Man" : gender == Gender.Female ? "Woman" : "Undefined",
                    getSubscribe() ? "Yes" : "No", getFirstName(), getLastName(), getEmail(), getPassword());
        }
    }


}
