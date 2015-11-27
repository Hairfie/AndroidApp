package com.hairfie.hairfie;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.hairfie.hairfie.models.Callbacks;
import com.hairfie.hairfie.models.Picture;
import com.hairfie.hairfie.models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

import pl.aprilapps.easyphotopicker.EasyImage;

public class SignupActivity extends AppCompatActivity {

    @NonNull
    ImageButton mPhotoButton;

    @Nullable
    File mPictureFile;

    @Nullable
    Picture mPicture;

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

    @Nullable
    ProgressDialog mProgressDialog;

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

        // Clear the uploaded picture object
        mPicture = null;

        // Show
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


        startSpinning(getString(R.string.sending));

        final Runnable signupRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject body = mForm.toJson();
                    if (null != mPicture) {
                        body.put("picture", mPicture.toJson());
                    }

                    User.getCurrentUser().signup(body, new Callbacks.SingleObjectCallback<User>() {
                        @Override
                        public void onComplete(@Nullable User user, @Nullable Callbacks.Error error) {

                            stopSpinning();
                            if (null != error) {
                                showAlert(error.message, getString(R.string.retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        touchSignup(null);
                                    }
                                });
                                return;
                            }

                            if (null != user && user.isAuthenticated()) {
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });


                } catch (JSONException e) {
                    stopSpinning();
                    showAlert(e.getLocalizedMessage(), null, null);
                }

            }
        };

        // Upload if we have a pictureFile and not a picture
        // Do not upload the same picture file twice, rather reuse a successful upload
        if (null == mPicture && null != mPictureFile) {

            User.getCurrentUser().uploadPicture(mPictureFile, new Callbacks.SingleObjectCallback<Picture>() {
                @Override
                public void onComplete(@Nullable Picture picture, @Nullable Callbacks.Error error) {

                    if (null != error) {
                        stopSpinning();
                        showAlert(error.message, getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                touchSignup(null);
                            }
                        });

                        return;
                    }


                    mPicture = picture;

                    signupRunnable.run();

                }
            });

        } else {
            signupRunnable.run();
        }
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

        public JSONObject toJson() throws JSONException {
            JSONObject result = new JSONObject();
            result.put("firstName", getFirstName());
            result.put("lastName", getLastName());
            result.put("email", getEmail());
            result.put("password", getPassword());
            result.put("gender", gender == Gender.Male ? "MALE" : gender == Gender.Female ? "FEMALE" : null);
            result.put("language", Locale.getDefault().getLanguage());
            result.put("newsletter", getSubscribe());
            return result;
        }
    }

    void startSpinning(String message) {
        if (null != mProgressDialog)
            return;

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    void stopSpinning() {
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    void showAlert(String message, String action, View.OnClickListener listener) {
        Snackbar.make(mSubscribeButton, message, Snackbar.LENGTH_LONG)
                .setAction(action, listener).show();
    }

}
