package com.fsdev.imeds;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.FragmentTransaction;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MedicalForum extends AppCompatActivity implements Fragment_ForumList.OnListFragmentInteractionListener{

    private String sortOrder=null;
    private PreferencesHandler prefsHandler;
    private FloatingActionButton fab;
    public static final int KEY_ACTION_POST  =48;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_forum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MedicalForum.this,MedicalForumPost.class),KEY_ACTION_POST );
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefsHandler=new PreferencesHandler(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forum, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(long id, int pos) {
       View fragmentContainer=findViewById(R.id.fragmentDataContainer);
        if(fragmentContainer!=null){
            Fragment_ForumView dataFragment=new Fragment_ForumView();
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            dataFragment.setForumIdAndPos(id,pos);
            ft.replace(R.id.fragmentDataContainer, dataFragment);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
        else{
            Intent intent=new Intent(this,MedicalForumView.class);
            intent.putExtra(Db_Feeds.struct_feeds.KEY_PID, id);
            intent.putExtra("pos",pos);
            startActivity(intent);
        }
    }

    public void executeForumService() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if ((networkInfo != null && networkInfo.isConnected())) {
            RequestQueue queue = Volley.newRequestQueue(MedicalForum.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, App_Urls.url_check_ntw, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (Boolean.valueOf(response)) {
                        Intent i = new Intent(MedicalForum.this, Service_Forum.class);
                        startService(i);
                    }
                    else{
                        userIsOffline();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    userIsOffline();
                }
            });
            queue.add(stringRequest);
        }else{
            userIsOffline();
        }
    }
    private void userIsOffline(){
        Snackbar.make(fab,"Cannot update the forum right now\n(Check your internet connection!)",Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==KEY_ACTION_POST && resultCode==RESULT_OK){
            executeForumService();
        }
    }
}
