package com.fsdev.imeds;

import android.content.Intent;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FeedsView extends AppCompatActivity {

    private WebView mWebView;
    private Toolbar toolbar;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String url = intent.getStringExtra(Adapter_Feeds.EXTRA_URL);

        mWebView = (WebView) findViewById(R.id.webViewFeeds);

        webSettings = mWebView.getSettings();

        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);


        WebViewClient webClient = new webViewClient();
        mWebView.setWebViewClient(webClient);

        mWebView.loadUrl(url);
    }

    private class webViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            toolbar.setSubtitle(view.getTitle());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_feeds, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_reload) {
            mWebView.reload();
        } else if (id == R.id.action_javascript) {
            if (item.isChecked()) {
                item.setChecked(false);

                webSettings.setJavaScriptEnabled(false);
            } else {
                item.setChecked(true);
                webSettings.setJavaScriptEnabled(true);
            }
            mWebView.reload();

        } else {//action browse
            Uri webpage = Uri.parse(mWebView.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }

        }

        return super.onOptionsItemSelected(item);
    }

}
