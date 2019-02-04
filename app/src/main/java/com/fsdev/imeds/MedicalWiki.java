package com.fsdev.imeds;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MedicalWiki extends AppCompatActivity {
    private List<Model_Topics> topicsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter_Topics mAdapter;
    private PreferencesHandler prefsHandler;
    private Db_Wiki dbHandler;
    private int arrayWikiCounts[];
    private String arrayWikiTopics[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_wiki);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arrayWikiTopics=getResources().getStringArray(R.array.wikiTopicsArrays);
        arrayWikiCounts=new int[arrayWikiTopics.length];

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Soon you will be able to contribute to the wiki.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefsHandler = new PreferencesHandler(this);
        mAdapter = new Adapter_Topics(topicsList, this);
        dbHandler = new Db_Wiki(this);

        TextView wikiHeader = (TextView) findViewById(R.id.wikiHeader);
        wikiHeader.setTypeface(new TypeFace_Handler(this).setFont("Roboto Light (Open Type).ttf"));

        recyclerView = (RecyclerView) findViewById(R.id.wikiTopicList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new Adapter_Topics.RecyclerTouchListener(getApplicationContext(), recyclerView, new Adapter_Topics.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Model_Topics topics = topicsList.get(position);
                Intent intent = new Intent(MedicalWiki.this, Wiki_Topics_Lists.class);
                intent.putExtra(Db_Wiki.struct_wiki.KEY_CATS, position);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerView.setAdapter(mAdapter);

        AsyncFetchWiki fetchWiki = new AsyncFetchWiki();
        fetchWiki.execute();
    }

    private void prepareTopicsData() {
        topicsList.clear();
        Model_Topics topics = new Model_Topics(arrayWikiTopics[0], "Last Viewed on Aug", arrayWikiCounts[0], R.drawable.icon_nutrition);
        topicsList.add(topics);

        topics = new Model_Topics(arrayWikiTopics[1], "Last Viewed on Aug", arrayWikiCounts[1], R.drawable.icon_lifecycle);
        topicsList.add(topics);

        topics = new Model_Topics(arrayWikiTopics[2], "Last Viewed on 11/21/2009", arrayWikiCounts[2], R.drawable.icon_aids);
        topicsList.add(topics);

        topics = new Model_Topics(arrayWikiTopics[3], "More to come", arrayWikiCounts[3], R.drawable.icon_exercise);
        topicsList.add(topics);

        topics = new Model_Topics(arrayWikiTopics[4], "Improve your memory", arrayWikiCounts[4], R.drawable.icon_mental_state);
        topicsList.add(topics);

        topics = new Model_Topics(arrayWikiTopics[5], "Last Viewed on Sep", arrayWikiCounts[5], R.drawable.icon_more_stacks);
        topicsList.add(topics);

        mAdapter.notifyDataSetChanged();
    }

    private class AsyncFetchWiki extends AsyncTask<Void, Cursor, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            dbHandler.open();
            Cursor cursor = null;
            for (int i = 0; i < arrayWikiTopics.length; i++) {

                cursor = dbHandler.countDbViewed(Db_Events.struct_events.KEY_VIEWED  + "=" + 0 + " AND " + Db_Wiki.struct_wiki.KEY_CATS + " = " + i, null);
                if (cursor != null) {
                    arrayWikiCounts[i] =cursor.getCount();
                }
            }
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            cursor.close();
            dbHandler.close();
            prepareTopicsData();
        }
    }
}
