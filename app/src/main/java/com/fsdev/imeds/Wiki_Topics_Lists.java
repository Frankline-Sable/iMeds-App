package com.fsdev.imeds;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Wiki_Topics_Lists extends AppCompatActivity {

    private List<Model_Wiki> wikiList = new ArrayList<>();
    private Adapter_Wiki mAdapter;
    private Db_Wiki dbHandler;
    private ListView listView;
    public int wikiTopicPos = 0;
    private TextView wikiCounts,wikiHeaderTxt;
    private int topicsDrawables[]={R.drawable.icon_nutrition,R.drawable.icon_lifecycle,R.drawable.icon_aids,R.drawable.icon_exercise,R.drawable.icon_mental_state,R.drawable.icon_more_stacks};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki__topics__lists);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.wikiListView);
        View header = getLayoutInflater().inflate(R.layout.header_wikis, null);
        listView.addHeaderView(header);
        TextView emptyView = (TextView) findViewById(R.id.wikiEmpty);
        listView.setEmptyView(emptyView);

        TypeFace_Handler tp = new TypeFace_Handler(this);
        Typeface typeface = tp.setFont("Roboto Light (Open Type).ttf");
        wikiHeaderTxt = (TextView) header.findViewById(R.id.headerTxt);
        wikiHeaderTxt.setTypeface(typeface);
        wikiCounts = (TextView) header.findViewById(R.id.countView);
        wikiCounts.setTypeface(typeface);
        emptyView.setTypeface(typeface);
        dbHandler = new Db_Wiki(this);

        if (savedInstanceState != null) {
            wikiTopicPos = savedInstanceState.getInt(Db_Wiki.struct_wiki.KEY_CATS);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Wiki_Topics_Lists.this, Reading_Wiki.class);
                intent.putExtra(Db_Wiki.struct_wiki.KEY_PID, id);
                //intent.putExtra(KEY_WIKI_POS, position);

                startActivity(intent);
            }
        });
    }

    private class AsyncFetchWiki extends AsyncTask<Void, Cursor, Cursor> {
        @Override
        protected Cursor doInBackground(Void... params) {
            dbHandler.open();
            Cursor cursor = dbHandler.readDb(Db_Wiki.struct_wiki.KEY_CATS + " = " + wikiTopicPos, null, null);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            wikiCounts.setText(getString(R.string.countView,String.valueOf(cursor.getCount())));
            wikiHeaderTxt.setText(getString(R.string.topicList,(getResources().getStringArray(R.array.wikiTopicsArrays)[wikiTopicPos])));
            wikiHeaderTxt.setCompoundDrawablesWithIntrinsicBounds(topicsDrawables[wikiTopicPos],0,0,0);
            Adapter_Wiki cursorAdapter = new Adapter_Wiki(Wiki_Topics_Lists.this, cursor, 0);
            listView.setAdapter(cursorAdapter);
            dbHandler.close();
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
        outState.putInt(Db_Wiki.struct_wiki.KEY_CATS, wikiTopicPos);
    }

    public void populateFields() {
        AsyncFetchWiki fetchWiki = new AsyncFetchWiki();
        fetchWiki.execute();
    }

    private void setRowIdFromIntent() {

        Bundle extras = getIntent().getExtras();
        wikiTopicPos = extras != null ? extras.getInt(Db_Wiki.struct_wiki.KEY_CATS) : 0;
    }

}
