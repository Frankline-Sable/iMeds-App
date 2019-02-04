package com.fsdev.imeds;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_ForumView extends Fragment {
    private long forumId;
    private int pos;
    private Db_Forums db_forums;
    private String sortOrder = null;
    private ListView listView;
    private TextView emptyView;
    private Context mContext;
    private TextView titleForum, descForum, timeForum;
    TypeFace_Handler tp;
    private PreferencesHandler prefsHandler;
    private long uniqueID;
    public Fragment_ForumView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            forumId = savedInstanceState.getLong(Db_Feeds.struct_feeds.KEY_PID);

        View view = inflater.inflate(R.layout.fragment_forum_data, container, false);
        mContext = view.getContext();

        prefsHandler = new PreferencesHandler(mContext);

        db_forums = new Db_Forums(mContext);
        tp = new TypeFace_Handler(mContext);
        listView = (ListView) view.findViewById(R.id.commList);
        listView.setClickable(false);
        emptyView = (TextView) view.findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);
        initialiseListHeader(mContext);
        return view;
    }

    public void setForumIdAndPos(long forumId, int pos) {
        this.forumId = forumId;
        this.pos = pos;
        refresh();
    }

    private void initialiseListHeader(Context mContext) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup listHeader = (ViewGroup) inflater.inflate(R.layout.header_forum_data, listView, false);
        titleForum = (TextView) listHeader.findViewById(R.id.titleForum);
        descForum = (TextView) listHeader.findViewById(R.id.description);
        timeForum = (TextView) listHeader.findViewById(R.id.timeForum);
        listView.addHeaderView(listHeader, null, false);

        titleForum.setTypeface(tp.setFont("HelveticaNeue Bold.ttf"));
        descForum.setTypeface(tp.setFont("Helvetica-Normal.ttf"));
        timeForum.setTypeface(tp.setFont("Helvetica Neue Light (Open Type).ttf"));
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            //update fragment variables variables
        }
        //  refresh();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        outState.putLong("listid", forumId);
    }

    public void refresh() {
        AsyncRefreshForum asyncRefresh = new AsyncRefreshForum();
        asyncRefresh.execute();
    }

    public class AsyncRefreshForum extends AsyncTask<Void, Cursor, Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            db_forums.open();
            Cursor cursor = null;
            cursor = db_forums.readDb(Db_Forums.struct_forums.KEY_PID + "==" + forumId, null, null);
            db_forums.close();
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String tit = cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_TITLE));
                    String desc = cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_DESCRIPTION));
                    String tim = cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_ViewTime));
                    uniqueID = cursor.getLong(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_UNIQUE));

                    titleForum.setText(tit);
                    descForum.setText(desc);
                    timeForum.setText(tim);
                    cursor.moveToNext();
                }

                cursor.close();
                AsyncFetchComm fetchComm = new AsyncFetchComm();
                fetchComm.execute();
            }
        }
    }


    private class AsyncFetchComm extends AsyncTask<Void, Cursor, Cursor> {
        @Override
        protected Cursor doInBackground(Void... params) {
            db_forums.open();
            return db_forums.readDb(Db_Forums.struct_forums.KEY_ISCOMMENT + " = "+1+" AND "+Db_Forums.struct_forums.KEY_PREV_ID+" = "+uniqueID, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            Adapter_Comments cursorAdapter = new Adapter_Comments(getContext(), cursor, 0);
            if(cursorAdapter.getCount()>0){
                listView.setAdapter(cursorAdapter);
                cursorAdapter.notifyDataSetChanged();
            }
            db_forums.close();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(Service_Forum.KEY_SERVICE_ACTION);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(forumsReceiver, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast

    }
    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(forumsReceiver);
        // or `unregisterReceiver(testReceiver)` for a normal broadcast
    }
    // Define the callback for what to do when data is received
    private BroadcastReceiver forumsReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                // String resultValue = intent.getStringExtra("resultValue");
                Toast.makeText(mContext, "r/w", Toast.LENGTH_SHORT).show();
                refresh();
            }
        }
    };
}
