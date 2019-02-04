package com.fsdev.imeds;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class Signup_User extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public static final String FOLDER_PROFILE_AVATAR = ".Profile Avatars";
    public static final String KEY_USER_NAME = "mUserName";
    private AutoCompleteTextView mNameView, mEmailView;
    private TextInputEditText mPasswordView, mPasswordCfmView, mAgeView;
    private CircleImageView thumbAvatar;
    private String gender = "";

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private final static int PICK_AVATAR_REQUEST = 3243;
    private Bitmap avatarBitmap = null, defaultAvatar;
    private LinearLayout dockingAvatarMenu;
    private ImageView expandedImageView;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup__user);
        setupActionBar();
        Window window = getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Set up the sign up form.
        mNameView = (AutoCompleteTextView) findViewById(R.id.fullname);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mAgeView = (TextInputEditText) findViewById(R.id.age);
        thumbAvatar = (CircleImageView) findViewById(R.id.thumbAvatar);
        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mPasswordCfmView = (TextInputEditText) findViewById(R.id.passwordConfirm);
        mPasswordCfmView = (TextInputEditText) findViewById(R.id.passwordConfirm);
        dockingAvatarMenu = (LinearLayout) findViewById(R.id.dockingAvatarMenu);
        expandedImageView = (ImageView) findViewById(R.id.expandedAvatar);

        Drawable avatarDrawable = getResources().getDrawable(R.drawable.ic_account_circle_black_48dp);
        defaultAvatar = ((BitmapDrawable) avatarDrawable).getBitmap();

        populateAutoComplete();

        Button mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        //mAgeView.requestFocus();
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        thumbAvatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(thumbAvatar);
            }
        });
        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordCfmView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String age = mAgeView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordCfm = mPasswordCfmView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if (name.length() < 3) {
            mNameView.setError("Enter a valid name");
            focusView = mNameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        if (isPasswordValid(password)) {
            mPasswordView.setError("Password length must be at least 6 characters long");
            focusView = mPasswordView;
            cancel = true;
        }
        if (isPasswordMatch(password, passwordCfm) || TextUtils.isEmpty(passwordCfm)) {
            mPasswordCfmView.setError("Passwords do not match!");
            focusView = mPasswordCfmView;
            cancel = true;
        }
        if (avatarBitmap == null) {
            avatarBitmap = defaultAvatar;
        }
        String avatar = getStringImage(avatarBitmap);

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if ((networkInfo != null && networkInfo.isConnected())) {
                // perform the user register attempt.
                showProgress(true);
                execSignUp(name, email, password, age, avatar, gender);

            } else {
                if (networkInfo != null)
                    Snackbar.make(mEmailView, "There seems to be no internet access! " + networkInfo.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Goto settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            }).setActionTextColor(getResources().getColor(android.R.color.holo_orange_dark)).show();
            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() < 6;
    }

    private boolean isPasswordMatch(String password1, String password2) {
        //TODO: Replace this with your own logic
        return (!password1.equals(password2));
    }

    private void showProgress(final boolean show) {
        if (show)
            progressDialog = ProgressDialog.show(this, "Getting you connected...", "Just a second...", true);
        else
            progressDialog.dismiss();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(Signup_User.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        // --Commented out by Inspection (15/10/2017 14:28):int IS_PRIMARY = 1;
    }

    private void execSignUp(final String mFullName, final String mEmail, final String mPassword, final String mAge, final String avatarData, final String mGender) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, App_Urls.url_signUp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equalsIgnoreCase("exist")) {
                    showProgress(false);
                    Snackbar.make(mEmailView, "The email account already exists in our servers!", Snackbar.LENGTH_LONG)
                            .setAction("Recover Account", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(Signup_User.this, Recover_Account.class));
                                }
                            }).setActionTextColor(getResources().getColor(android.R.color.holo_red_dark)).show();
                    mEmailView.setError("Email already exist!");
                    mEmailView.requestFocus();
                } else if (Boolean.valueOf(response)) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String imageState = new Bitmaps_Cache(avatarBitmap, FOLDER_PROFILE_AVATAR, mEmail).createImageFile();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    showProgress(false);
                                    Toast.makeText(Signup_User.this, "Registration is successful, please proceed to signin", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Signup_User.this, Signin_User.class);
                                    intent.putExtra(KEY_USER_NAME, mFullName);
                                    startActivity(intent);

                                    if (imageState.length()<3) {
                                        Log.i("i-meds", "Failed to save image");
                                    }
                                }
                            });
                        }
                    }).start();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Snackbar.make(mEmailView, "I was unable to connect to the server, error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("Fullname", mFullName);
                params.put("Email", mEmail);
                params.put("Password", mPassword);
                params.put("Age", mAge);
                params.put("Avatar", avatarData);
                params.put("Gender", mGender);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void launchRecovery(View v) {
        Intent intent = new Intent(Signup_User.this, Recover_Account.class);
        startActivity(intent);
    }

    public void setGender(View v) {
        RadioButton genderRadio = (RadioButton) v;
        gender = genderRadio.getText().toString();
    }

    public void launchGallery(View v) {
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(photoPickerIntent, "Select avatar from: "), PICK_AVATAR_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AVATAR_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri avatarURI = data.getData();

            BitmapWorkerTask workerTask = new BitmapWorkerTask();
            workerTask.execute(avatarURI);
        }
    }

    private class BitmapWorkerTask extends AsyncTask<Uri, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Uri... params) {
            return decodeUri(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                avatarBitmap = bitmap;
                thumbAvatar.setImageBitmap(bitmap);
                expandedImageView.setImageBitmap(bitmap);
            }
            if (ActivityCompat.checkSelfPermission(Signup_User.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mEmailView, "Please enable permission to access  external storage!", Snackbar.LENGTH_LONG)
                        .setAction("Goto settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                            }
                        }).setActionTextColor(getResources().getColor(android.R.color.holo_orange_dark)).show();
            }
        }
    }

    private Bitmap decodeUri(Uri selectedImage) {
        Bitmap bm = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 128, 96);
            String imageType = options.outMimeType;
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {//256,227,4=4*4
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public void defaultAvatar(View v) {
        thumbAvatar.setImageBitmap(defaultAvatar);
        expandedImageView.setImageBitmap(defaultAvatar);
    }


    private void zoomImageFromThumb(final View thumbView) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);
        dockingAvatarMenu.setVisibility(View.VISIBLE);
        dockingAvatarMenu.animate().alpha(.7f).rotationXBy(720).setDuration(1000);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        dockingAvatarMenu.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        dockingAvatarMenu.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}

