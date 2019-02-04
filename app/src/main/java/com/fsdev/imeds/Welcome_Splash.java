package com.fsdev.imeds;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Welcome_Splash extends AppCompatActivity {
    private int  count=0;
    private ImageSwitcher smallImg_Switcher;
    private int switch_Img[]={R.drawable.ic_if_medical_icon_2,R.drawable.ic_if_medical_icon_3,R.drawable.ic_if_medical_icon_4,R.drawable.ic_if_medical_icon_6,R.drawable.ic_if_medical_icon_8};

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private PreferencesHandler prefHandler;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome__splash);

        Window window=getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mContentView = findViewById(R.id.appIntro);
        prefHandler=new PreferencesHandler(Welcome_Splash.this);
        smallImg_Switcher = (ImageSwitcher) findViewById(R.id.tinySplashImg);
        final TextView versionView = (TextView) findViewById(R.id.versionText);

        versionView.setText(getString(R.string.version, BuildConfig.VERSION_NAME));
        initialiseImageSwitcher();

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                smallImg_Switcher.setImageResource(switch_Img[count]);
                count++;
                if (count >= (switch_Img.length)) {
                    handler.removeCallbacks(this);

                    if(prefHandler.getAccountState())
                        startActivity(new Intent(Welcome_Splash.this, Main_Tab.class ));
                    else
                        startActivity(new Intent(Welcome_Splash.this, Signin_User.class ));
                    finish();


                }else {
                    handler.postDelayed(this, 1500);
                }
            }//234
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void initialiseImageSwitcher(){
        smallImg_Switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView switcherImageView = new ImageView(Welcome_Splash.this);
                switcherImageView.setLayoutParams(new ImageSwitcher.LayoutParams(ImageSwitcher.LayoutParams.WRAP_CONTENT, ImageSwitcher.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
                switcherImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                switcherImageView.setImageResource(0);
                return switcherImageView;
            }
        });
        Animation animationIn = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Animation animationOut = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        smallImg_Switcher.setInAnimation(animationIn);
        smallImg_Switcher.setOutAnimation(animationOut);
    }
}
