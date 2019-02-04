package com.fsdev.imeds;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import static android.app.Activity.RESULT_OK;

/**
 * A fragment representing a list of Items. `
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class Fragment_ForumList extends Fragment {

    private Adapter_Forums mAdapter;
    private Db_Forums db_forums;
    private LinearLayout forumListProgress;
    private String sortOrder = Db_Forums.struct_forums.KEY_SCHEDULED + " DESC";
    private ListView listView;
    private TextView emptyView;
    private Context mContext;

    private OnListFragmentInteractionListener listListener;

    AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(listListener!=null){
                listListener.itemClicked(id,position);
            }

        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Fragment_ForumList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forumslist_list, container, false);
        mContext = view.getContext();

        listView = (ListView) view.findViewById(R.id.forumListView);
        initialiseListHeader(mContext);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(listItemClickListener);

        forumListProgress = (LinearLayout) view.findViewById(R.id.forumLoader);
        emptyView = (TextView) view.findViewById(R.id.emptyView);
        listView.setEmptyView(emptyView);

        db_forums = new Db_Forums(mContext);
        refresh();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listListener = (OnListFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void itemClicked(long id, int position);
    }

    private void initialiseListHeader(Context mContext) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup listHeader = (ViewGroup) inflater.inflate(R.layout.forum_list_header, listView, false);
        listView.addHeaderView(listHeader, null, false);
    }

    public void refresh() {
        AsyncRefreshForums asyncRefresh = new AsyncRefreshForums();
        asyncRefresh.execute();
    }

    public class AsyncRefreshForums extends AsyncTask<Void, Cursor, Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            db_forums.open();
            db_forums.updateWhetherViewed(0,true);
            return db_forums.readDb(Db_Forums.struct_forums.KEY_DELETED + "!=" + 1 + " AND " + Db_Forums.struct_forums.KEY_ISCOMMENT + " = " + 0, null, sortOrder);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mAdapter = new Adapter_Forums(mContext, cursor, true);
            listView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            if (mAdapter.getCount() > 0) {
                forumListProgress.setVisibility(View.GONE);
            }
            db_forums.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(Service_Forum.KEY_SERVICE_ACTION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(forumsReceiver, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast

    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(forumsReceiver);
        // or `unregisterReceiver(testReceiver)` for a normal broadcast
    }

    // Define the callback for what to do when data is received
    private BroadcastReceiver forumsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                 Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                refresh();
            }
        }
    };
}
