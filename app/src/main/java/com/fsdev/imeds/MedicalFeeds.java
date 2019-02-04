package com.fsdev.imeds;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MedicalFeeds extends AppCompatActivity {
    private List<Model_Feeds> feedsList=new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter_Feeds mAdapter;
    private String sortOrder=null;
    private Db_Feeds db_feeds;
    private LinearLayout feedListProgress;
    private TextView emptyView,trendingTitle,trendingSub;
    private ImageView trendingImage;
    private PreferencesHandler prefsHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_feeds);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=(RecyclerView)findViewById(R.id.feedsRecyleView);
        mAdapter=new Adapter_Feeds(this,feedsList);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new Adapter_Feeds.RecyclerTouchListener(getApplicationContext(), recyclerView, new Adapter_Feeds.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        feedListProgress=(LinearLayout)findViewById(R.id.feedLoader);
        trendingTitle=(TextView)findViewById(R.id.trendingTitle);
        trendingSub=(TextView)findViewById(R.id.trendingSub);
        trendingImage=(ImageView) findViewById(R.id.trendingImage);
        emptyView=(TextView) findViewById(R.id.emptyView);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //doMySearch(query);
        }
        prefsHandler=new PreferencesHandler(this);
        loadTrendingFeed();

        db_feeds = new Db_Feeds(this);
        refresh();
    }
    private void loadTrendingFeed(){
        trendingTitle.setText(prefsHandler.getTrendingData(PreferencesHandler.KEY_TRENDING_TITLE));
        trendingSub.setText(prefsHandler.getTrendingData(PreferencesHandler.KEY_TRENDING_SUB));
        Log.i("i-meds","loading "+prefsHandler.getTrendingData(PreferencesHandler.KEY_TRENDING_IMG));
        Picasso.with(this).load(prefsHandler.getTrendingData(PreferencesHandler.KEY_TRENDING_IMG)).error(R.drawable.testingthis).into(trendingImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feeds, menu);

        /*SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setChecked(true);
        if (id == R.id.sortTitle) {
            sortOrder = Db_Feeds.struct_feeds.KEY_TITLE + " ASC";

        } else if (id == R.id.sortDate) {
            sortOrder = Db_Feeds.struct_feeds.KEY_SCHEDULED + " ASC";

        } else if (id == R.id.sortStatus) {
            sortOrder = Db_Feeds.struct_feeds.KEY_URL + " ASC";
        }
        refresh();
        return super.onOptionsItemSelected(item);
    }
    public void refresh() {
        AsyncRefreshFeeds asyncRefresh = new AsyncRefreshFeeds();
        asyncRefresh.execute();
    }
    public class AsyncRefreshFeeds extends AsyncTask<Void, Cursor, Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            db_feeds.open();
            db_feeds.updateWhetherViewed(0,true);
            Cursor cursor = db_feeds.readDb(Db_Feeds.struct_feeds.KEY_DELETED+"!="+1, null, sortOrder);
            cursor.moveToFirst();
            feedsList.clear();
            db_feeds.close();
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null) {
                int tit = cursor.getColumnIndexOrThrow(Db_Feeds.struct_feeds.KEY_TITLE);
                int desc = cursor.getColumnIndexOrThrow(Db_Feeds.struct_feeds.KEY_DESCRIPTION);
                int sch = cursor.getColumnIndexOrThrow(Db_Feeds.struct_feeds.KEY_SCHEDULED);
                int img = cursor.getColumnIndexOrThrow(Db_Feeds.struct_feeds.KEY_IMAGE);
                int tim = cursor.getColumnIndexOrThrow(Db_Feeds.struct_feeds.KEY_ViewTime);
                int url = cursor.getColumnIndexOrThrow(Db_Feeds.struct_feeds.KEY_URL);
                int del= cursor.getColumnIndexOrThrow(Db_Feeds.struct_feeds.KEY_DELETED);

                while (!cursor.isAfterLast()) {
                    Model_Feeds feeds = new Model_Feeds(cursor.getString(tit),cursor.getString(desc),cursor.getString(tim),cursor.getString(url),cursor.getString(img));
                    feedsList.add(feeds);
                    cursor.moveToNext();
                }
                cursor.close();
                mAdapter.notifyDataSetChanged();
                feedListProgress.setVisibility(View.GONE);

                if (mAdapter.getItemCount() < 1) {
                    emptyView.setVisibility(View.VISIBLE);
                }else{
                    emptyView.setVisibility(View.GONE);
                }
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
         prefsHandler.updateNewCount(PreferencesHandler.KEY_NEW_FEED,0);
    }

}
