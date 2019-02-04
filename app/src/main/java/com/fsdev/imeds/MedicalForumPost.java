package com.fsdev.imeds;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;

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

public class MedicalForumPost extends AppCompatActivity {

    @InjectView(R.id.postTitle)
    AutoCompleteTextView postTitle;
    @InjectView(R.id.postDescription)
    AutoCompleteTextView postDesc;
    private PreferencesHandler prefHandler;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_forum_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefHandler = new PreferencesHandler(this);
        ButterKnife.inject(this);

        Window window = getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressDialog=new ProgressDialog(MedicalForumPost.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting data to forum...");
        progressDialog.setTitle("Wait a minute");
        progressDialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_med_forum_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void postMessage(View v){

        progressDialog.show();
        final String title = postTitle.getText().toString();
        final String description = postDesc.getText().toString();
        final long uniqueMessageID=System.currentTimeMillis();
        final String emailAccount = prefHandler.getAccountData(PreferencesHandler.KEY_USER_EMAIL);
        final String accountName = prefHandler.getAccountData(PreferencesHandler.KEY_USER_fName) + " " + prefHandler.getAccountData(PreferencesHandler.KEY_USER_lName);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, App_Urls.url_forum_post, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if(Boolean.valueOf(response)){
                    setResult(RESULT_OK);
                    finish();
                }else{
                    Snackbar.make(postDesc,"Cannot post to forum right now, \nThere is a problem with the server",Snackbar.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(postDesc,"Cannot post to forum right now\n(Check your internet connection!)",Snackbar.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("PostTitle", title);
                params.put("PostDesc", description);
                params.put("FullName", accountName);
                params.put("Email", emailAccount);
                params.put("UniqueID",String.valueOf(uniqueMessageID));
                params.put("P_URL",App_Urls.url_prefix+"avatars/"+emailAccount+".jpeg");
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
