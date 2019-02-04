package com.fsdev.imeds;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MedicalForumView extends AppCompatActivity {

    private Db_Forums db_forums;
    @InjectView(R.id.posterImg)
    CircleImageView posterImg;
    @InjectView(R.id.posterName)
    TextView posterName;
    @InjectView(R.id.repyField)
    TextInputEditText replyField;
    private Long forumDataID;
    private int forumDataPos;
    Fragment_ForumView dataFragment;
    private Long forumUniqueId;
    private String forumUniqueEmail;
    private PreferencesHandler prefHandler;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_data_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        window = getWindow();
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.inject(this);
        db_forums = new Db_Forums(this);
        prefHandler = new PreferencesHandler(this);

        if (savedInstanceState != null) {
            forumDataID = savedInstanceState.getLong(Db_Forums.struct_forums.KEY_PID);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRowIdFromIntent();
        dataFragment = (Fragment_ForumView) getFragmentManager().findFragmentById(R.id.fragmentData);
        dataFragment.setForumIdAndPos(forumDataID, forumDataPos);
        refreshActivityData(forumDataID);

    }

    private void setRowIdFromIntent() {
        if (forumDataID == null) {
            Bundle extras = getIntent().getExtras();
            forumDataID = extras != null ? extras.getLong(Db_Feeds.struct_feeds.KEY_PID) : null;
            forumDataPos = extras != null ? extras.getInt("pos") : 0;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Db_Feeds.struct_feeds.KEY_PID, forumDataID);
    }


    private void refreshActivityData(long id) {
        AsyncRefreshData asyncRefresh = new AsyncRefreshData();
        asyncRefresh.execute(id);
    }

    public class AsyncRefreshData extends AsyncTask<Long, Cursor, Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Cursor doInBackground(Long... params) {
            db_forums.open();
            Cursor cursor = null;
            cursor = db_forums.readDb(Db_Forums.struct_forums.KEY_PID + "==" + params[0], null, null);
            db_forums.close();
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_USERNAME));
                    String img = cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_IMAGE));
                    forumUniqueId = cursor.getLong(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_UNIQUE));
                    forumUniqueEmail = cursor.getString(cursor.getColumnIndexOrThrow(Db_Forums.struct_forums.KEY_EMAIL));
                    posterImg.setImageURI(Uri.parse(img));
                    posterName.setText(name);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
    }


    public void postForumReply(View v) {
        final String replyDesc = replyField.getText().toString();
        if (TextUtils.isEmpty(replyDesc)) {
           return;
        }
        final String emailAccount = prefHandler.getAccountData(PreferencesHandler.KEY_USER_EMAIL);
        final String accountName = prefHandler.getAccountData(PreferencesHandler.KEY_USER_fName) + " " + prefHandler.getAccountData(PreferencesHandler.KEY_USER_lName);
        final long uniqueMessageID=System.currentTimeMillis();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, App_Urls.url_forum_reply, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Boolean.valueOf(response)) {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    //finish();
                } else {
                    Snackbar.make(replyField, "Cannot reply to forum post right now\n(Server Eror!)", Snackbar.LENGTH_LONG).show();
                    Log.i("i-meds","error is "+response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(replyField, "Cannot reply to forum post right now\n(Check your internet connection!)", Snackbar.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("PostDesc", replyDesc);
                params.put("FullName", accountName);
                params.put("Email", emailAccount);
                params.put("UniqueID", String.valueOf(uniqueMessageID));
                params.put("prevID", String.valueOf(forumUniqueId));
                params.put("P_URL", App_Urls.url_prefix + "avatars/" + emailAccount + ".jpeg");
                return params;
            }
        };
        queue.add(stringRequest);
        Intent i = new Intent(MedicalForumView.this, Service_Forum.class);
        startService(i);
    }
}
