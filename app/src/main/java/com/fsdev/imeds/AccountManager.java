package com.fsdev.imeds;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

import static fsdev.imeds.Signup_User.FOLDER_PROFILE_AVATAR;

public class AccountManager extends AppCompatActivity {

    private PreferencesHandler prefHandler;
    private int PICK_AVATAR_REQUEST = 0;
    @InjectView(R.id.firstNameField)
    TextInputEditText firstName;
    @InjectView(R.id.lastNameField)
    TextInputEditText lastName;
    @InjectView(R.id.emailField)
    TextInputEditText email;
    @InjectView(R.id.maleRadio)
    RadioButton maleRadio;
    @InjectView(R.id.femaleRadio)
    RadioButton femaleRadio;
    @InjectView(R.id.ageField)
    TextInputEditText age;
    @InjectView(R.id.passField)
    TextInputEditText password;
    @InjectView(R.id.profilePic)
    CircleImageView thumbAvatar;
    @InjectView(R.id.saveButton)
    Button saveBut;
    @InjectView(R.id.profileProgress)
    ProgressBar avatarProgress;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private ImageView expandedImageView;
    LinearLayout dockingAvatarMenu;
    private Bitmap avatarBitmap, picassoBit;
    private String userName;
    FloatingActionButton fab;
    private String previousImageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!firstName.isEnabled()) {
                    firstName.setEnabled(true);
                    lastName.setEnabled(true);
                    saveBut.setEnabled(true);
                    email.setEnabled(true);
                    age.setEnabled(true);
                    maleRadio.setEnabled(true);
                    femaleRadio.setEnabled(true);
                    fab.setImageResource(R.drawable.ic_edit_on);
                    Snackbar.make(view, "The fields are now editable", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    fab.setImageResource(R.drawable.ic_edit_off);
                    firstName.setEnabled(false);
                    lastName.setEnabled(false);
                    email.setEnabled(false);
                    saveBut.setEnabled(false);
                    age.setEnabled(false);
                    maleRadio.setEnabled(false);
                    femaleRadio.setEnabled(false);
                }
            }
        });
        dockingAvatarMenu = (LinearLayout) findViewById(R.id.dockingAvatarMenu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        expandedImageView = (ImageView) findViewById(R.id.expandedAvatar);
        prefHandler = new PreferencesHandler(AccountManager.this);
        ButterKnife.inject(this);

        firstName.setText(prefHandler.getAccountData(PreferencesHandler.KEY_USER_fName));
        lastName.setText(prefHandler.getAccountData(PreferencesHandler.KEY_USER_lName));
        userName = prefHandler.getAccountData(PreferencesHandler.KEY_USER_EMAIL);
        previousImageUri = prefHandler.getAccountData(PreferencesHandler.KEY_USER_AVATAR_LOC);
        email.setText(userName);
        age.setText(prefHandler.getAccountData(PreferencesHandler.KEY_USER_AGE));
        password.setText(prefHandler.getAccountData(PreferencesHandler.KEY_USER_PASSWORD));


        if (prefHandler.getAccountData(PreferencesHandler.KEY_USER_GENDER).equalsIgnoreCase("female"))
            femaleRadio.setChecked(true);
        else
            maleRadio.setChecked(true);

        setDefaultPImage();
    }
    private void setDefaultPImage() {

        if (previousImageUri.length() > 10) {
            thumbAvatar.setImageURI(Uri.parse(previousImageUri));
            expandedImageView.setImageURI(Uri.parse(previousImageUri));
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        picassoBit = Picasso.with(AccountManager.this)
                                .load("http://192.168.137.1/iMeds/avatars/" + email.getText().toString()).placeholder(R.drawable.ic_account_circle_black_48dp)
                                .error(R.drawable.ic_error_outline_black_24dp).get();
                    } catch (IOException e) {
                        picassoBit=defaultImageBitmap();
                    } finally {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                thumbAvatar.setImageBitmap(picassoBit);
                                expandedImageView.setImageBitmap(picassoBit);
                            }
                        });
                    }
                }
            }).start();

        }
    }

    public void changeUserImage(View v) {
        zoomImageFromThumb(thumbAvatar);
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
            if (ActivityCompat.checkSelfPermission(AccountManager.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(thumbAvatar, "Please enable permission to access  external storage!", Snackbar.LENGTH_LONG)
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

    public void saveNewAvatar(View v) {
        if (avatarBitmap == null) {
            Toast.makeText(this, "Please pick an image First", Toast.LENGTH_LONG).show();
            return;
        }
        String avatarString = getStringImage(avatarBitmap);

        if (checkNetwork()) {
            avatarProgress(true);
            UploadProfileImage(avatarString, userName);
        }
    }

    private void UploadProfileImage(final String image, final String userId) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, App_Urls.url_upload_pImage, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Boolean.valueOf(response)) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String imageUriSaved = new Bitmaps_Cache(avatarBitmap, FOLDER_PROFILE_AVATAR, userId).createImageFile();
                            prefHandler.saveSingleAccountData(imageUriSaved, PreferencesHandler.KEY_USER_AVATAR_LOC);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    avatarProgress(false);
                                    Snackbar.make(thumbAvatar, "Profile image changed successfully", Toast.LENGTH_LONG).show();
                                    if (imageUriSaved.length() < 3) {
                                        Toast.makeText(AccountManager.this, "Failed to Cache Image Internally", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }).start();
                } else {
                    avatarProgress(false);
                    Toast.makeText(AccountManager.this, "Unable to set profile, problem with the server", Toast.LENGTH_LONG).show();
                    setDefaultPImage();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                avatarProgress(false);
                Snackbar.make(thumbAvatar, "I was unable to connect to the server, error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                setDefaultPImage();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Email", userId);
                params.put("Avatar", image);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private Boolean checkNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Boolean networkState;

        if ((networkInfo != null && networkInfo.isConnected())) {
            networkState = true;
        } else {
            networkState = false;
            if (networkInfo != null)
                Snackbar.make(thumbAvatar, "There seems to be no internet access! " + networkInfo.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Goto settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        }).setActionTextColor(getResources().getColor(android.R.color.holo_orange_dark)).show();
        }
        return networkState;
    }

    private void avatarProgress(Boolean state) {
        if (state)
            avatarProgress.setVisibility(View.VISIBLE);
        else
            avatarProgress.setVisibility(View.GONE);
    }

    private Bitmap defaultImageBitmap() {
        Drawable imageDrawable = getResources().getDrawable(R.drawable.ic_account_circle_black_48dp);
        return ((BitmapDrawable) imageDrawable).getBitmap();
    }

    public void saveNewData(View v) {// Reset errors.
        firstName.setError(null);
        lastName.setError(null);
        email.setError(null);
        age.setError(null);
        password.setError(null);

        String fname = firstName.getText().toString();
        String lname = lastName.getText().toString();
        String emailTxt = email.getText().toString();
        String ageTxt = age.getText().toString();
        String passwordTxt = password.getText().toString();
        String gend;
        if(maleRadio.isChecked()){
            gend="Male";
        }else
            gend="Female";

        boolean cancel = false;
        View focusView = null;

        if (fname.length() < 3) {
            firstName.setError("Enter a valid name");
            focusView = firstName;
            cancel = true;
        }
        if (lname.length() < 3) {
            lastName.setError("Enter a valid name");
            focusView = lastName;
            cancel = true;
        }
        if (!TextUtils.isEmpty(ageTxt)) {
            if (Integer.parseInt(ageTxt) < 5 || Integer.parseInt(ageTxt) > 120) {
                age.setError("Age is invalid");
                focusView = age;
                cancel = true;
            }
        } else {
            age.setError("Age is invalid");
            focusView = age;
            cancel = true;
        }
        if (TextUtils.isEmpty(emailTxt)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        }
        if (isPasswordValid(passwordTxt)) {
            password.setError("Password length must be at least 6 characters long");
            focusView = password;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (checkNetwork()) {
                showProgress(true);
                saveNewAccountData(fname, lname, userName, ageTxt, passwordTxt,gend);
            }
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() < 6;
    }

    private void showProgress(Boolean state) {
        if (state)
            progressDialog = ProgressDialog.show(this, "Updating Account Data...", "Just a second...", true);
        else
            progressDialog.dismiss();
    }
    private void saveNewAccountData(final String fname, final String  lname, final String emailTxt, final String ageTxt, final String passwordTxt, final String gend){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, App_Urls.url_update_account, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Boolean.valueOf(response)) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            prefHandler.updateAccountData(fname, lname, emailTxt, passwordTxt, ageTxt, gend);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    fab.setImageResource(R.drawable.ic_edit_off);
                                    firstName.setEnabled(false);
                                    lastName.setEnabled(false);
                                    email.setEnabled(false);
                                    age.setEnabled(false);
                                    maleRadio.setEnabled(false);
                                    femaleRadio.setEnabled(false);
                                    saveBut.setEnabled(false);
                                    showProgress(false);
                                    Toast.makeText(AccountManager.this, "Account has been updated", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }).start();
                }
                else{
                    showProgress(false);
                    Snackbar.make(dockingAvatarMenu, "Unable to update the account at this time: " , Snackbar.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Snackbar.make(dockingAvatarMenu, "I was unable to connect to the server, error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("fName", fname);
                params.put("lName",lname);
                params.put("Email",emailTxt);
                params.put("Password", passwordTxt);
                params.put("Age", ageTxt);
                params.put("Gender",gend);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
