package com.fsdev.imeds;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Reading_Wiki extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
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
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
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
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private Long wikiID = null;
    private Db_Wiki dbHandler;
    public static final String WIKI_WEB_DOC = "file:///android_asset/Wiki/wiki.html";
    private WebView webViewControl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reading_wikis);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        webViewControl = (WebView) findViewById(R.id.webViewWiki);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        dbHandler = new Db_Wiki(this);
        if (savedInstanceState != null) {
            wikiID = savedInstanceState.getLong(Db_Wiki.struct_wiki.KEY_PID);
        }

    }

    private void setRowIdFromIntent() {

        if (wikiID == null) {
            Bundle extras = getIntent().getExtras();
            wikiID = extras != null ? extras.getLong(Db_Wiki.struct_wiki.KEY_PID) : null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRowIdFromIntent();
        populateFields();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Db_Wiki.struct_wiki.KEY_PID, wikiID);
    }

    public void populateFields() {
        if (wikiID != null) {
            populateAsyncTask asyncTask = new populateAsyncTask();
            asyncTask.execute();
        }
    }

    private class populateAsyncTask extends AsyncTask<Void, Cursor, Cursor> {

        public populateAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            dbHandler.open();
            dbHandler.updateWikiStatusDb(wikiID, true);
            return dbHandler.readDb(Db_Wiki.struct_wiki.KEY_PID + "=" + wikiID, null, null);

        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            setUpWikiWebView(cursor.getString(cursor.getColumnIndexOrThrow(Db_Wiki.struct_wiki.KEY_TITLE)),cursor.getString(cursor.getColumnIndexOrThrow(Db_Wiki.struct_wiki.KEY_DESCRIPTION)),cursor.getString(cursor.getColumnIndexOrThrow(Db_Wiki.struct_wiki.KEY_META)));
            cursor.close();
            dbHandler.close();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private class JavaScriptHandler {
        @JavascriptInterface
        public void messageToReturn(String message) {
            Toast.makeText(Reading_Wiki.this, message, Toast.LENGTH_LONG).show();
        }

    }

    private class UAWebChromeClient extends WebChromeClient{
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Toast.makeText(Reading_Wiki.this, message, Toast.LENGTH_SHORT).show();
            result.confirm();
            return true;
        }
    }
    private class UAWebViewClient extends WebViewClient{
        private String title,desc,meta;

        public UAWebViewClient(String title, String desc, String meta) {
            this.title = title;
            this.desc = desc;
            this.meta = meta;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webViewControl.loadUrl("javascript:updateWiki(\"" + title + "\",\"" + desc + "\",\"" + meta + "\");");
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void setUpWikiWebView(String title,String desc, String meta) {
        WebSettings browserSettings = webViewControl.getSettings();
        browserSettings.setJavaScriptEnabled(true);
        browserSettings.setAllowContentAccess(true);
        browserSettings.setSupportZoom(true);

        webViewControl.clearCache(true);
        webViewControl.setWebChromeClient(new UAWebChromeClient());
        webViewControl.addJavascriptInterface(new JavaScriptHandler(), "wiki");
        webViewControl.setWebViewClient(new UAWebViewClient(title,desc,meta));
        webViewControl.loadUrl(WIKI_WEB_DOC);
    }
}