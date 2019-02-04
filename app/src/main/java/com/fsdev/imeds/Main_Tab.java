package com.fsdev.imeds;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main_Tab extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<Model_Menu> menuList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter_Menu mAdapter;
    private PreferencesHandler prefsHandler;
    private ProgressDialog progressDialog;
    private String modulesTitles[];
    private int arrayModuleCounts[];
    private DatabaseManager dbManager[];
    private Boolean networkStateOky;
    private LinearLayout infoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__tab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefsHandler = new PreferencesHandler(this);
        modulesTitles = getResources().getStringArray(R.array.mainMenuTitles);
        arrayModuleCounts = new int[modulesTitles.length];

        String userId = getIntent().getStringExtra(Signup_User.KEY_USER_NAME);
        if (userId != null) {
            loadUserAccount(userId);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        infoView=(LinearLayout)findViewById(R.id.mainInfoView);

        recyclerView = (RecyclerView) findViewById(R.id.MenuRecyleView);
        mAdapter = new Adapter_Menu(menuList, this);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new Adapter_Menu.RecyclerTouchListener(getApplicationContext(), recyclerView, new Adapter_Menu.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Model_Menu menus = menuList.get(position);
                if (position == 0) {
                    Intent i = new Intent(Main_Tab.this, MedicalFeeds.class);
                    startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(Main_Tab.this, MedicalEvents.class);
                    startActivity(i);
                } else if (position == 2) {
                    Intent i = new Intent(Main_Tab.this, MedicalForum.class);
                    startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(Main_Tab.this, iMedsFinder.class);
                    startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(Main_Tab.this, AccountManager.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(Main_Tab.this, MedicalWiki.class);
                    startActivity(i);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recyclerView.setAdapter(mAdapter);

        prepareMenuData();
        launchService();
    }

    private void prepareMenuData() {
        menuList.clear();
        Model_Menu menus = new Model_Menu(modulesTitles[0], "", R.drawable.ic_worldwide, arrayModuleCounts[0], R.color.menu1);
        menuList.add(menus);

        menus = new Model_Menu(modulesTitles[1], "Don’t miss out, get to join the meds events and be aware", R.drawable.ic_if_medical_icon_2, arrayModuleCounts[1], R.color.menu2);
        menuList.add(menus);

        menus = new Model_Menu(modulesTitles[2], "", R.drawable.ic_if_medical_icon_4, arrayModuleCounts[2], R.drawable.menu_bg13);
        menuList.add(menus);

        menus = new Model_Menu(modulesTitles[3], "View and locate medical services that are in your area", R.drawable.ic_search, 0, R.color.menu5);
        menuList.add(menus);

        menus = new Model_Menu(modulesTitles[4], "Manage your i-Meds account(change public & private settings)", R.drawable.ic_user_6, 0, R.color.menu4);
        menuList.add(menus);

        menus = new Model_Menu(modulesTitles[5], "View the vast variety of medical data shared publicly", R.drawable.ic_wiki_layers, arrayModuleCounts[3], R.color.menu8);
        menuList.add(menus);

        menus = new Model_Menu(modulesTitles[6], "Contribute to the app develpment progress", R.drawable.ic_if_medical_icon_7, 0, R.color.menu9);
        menuList.add(menus);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main__tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Main_Tab.this, R.style.MaterialDialogStyle);
            LayoutInflater inflater = getLayoutInflater();
            final View layout = inflater.inflate(R.layout.logout_dialog, null);
            final CheckBox checkBox = (CheckBox) layout.findViewById(R.id.checkBoxLogout);

            if (prefsHandler.getRemState()) {
                builder.setCancelable(false).setView(layout).setIcon(R.drawable.ic_report_black_24dp)
                        .setPositiveButton("SURE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                executeSignOut(!checkBox.isChecked());
                            }
                        }).setNegativeButton("NO WAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setTitle("Do you want to logout?");
                builder.create().show();
            } else {
                executeSignOut(false);
            }

            return true;
        }
        else{
            prepareMenuData();
            launchService();
            AsyncCountViewedModules viewedModules=new AsyncCountViewedModules();
            viewedModules.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void launchService() {
        infoView.setVisibility(View.GONE);
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if ((networkInfo != null && networkInfo.isConnected())) {
                RequestQueue queue = Volley.newRequestQueue(Main_Tab.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, App_Urls.url_check_ntw, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (Boolean.valueOf(response)) {
                            Intent i = new Intent(Main_Tab.this, Service_Main.class);
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
        infoView.setVisibility(View.VISIBLE);
    }

    private void executeSignOut(final Boolean rem) {

        progressDialog = ProgressDialog.show(this, "Please wait", "Logging out...", true, false);

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (DatabaseManager aDbManager : dbManager) {
                    try {
                        aDbManager.open();
                        aDbManager.clearTablesData();
                    } catch (SQLException e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main_Tab.this, "Error while opening a database!", Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                }

                prefsHandler.clearAll();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        for (DatabaseManager aDbManager : dbManager) {
                            aDbManager.close();
                        }
                        startActivity(new Intent(Main_Tab.this, Signin_User.class));
                        finish();
                    }
                });
            }
        }).start();
    }

    private void loadUserAccount(final String userId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, App_Urls.url_fetch_account, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Boolean.valueOf(response.trim())) {
                    showMainsProgress(false);
                } else {
                    preparingCurrentAccount(response, userId);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("i-meds", "response " + error.getMessage());
                showMainsProgress(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Email", userId);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void preparingCurrentAccount(String response, final String email) {
        try {
            Parser_AccountObjects parser_Acc = new Parser_AccountObjects(response);
            parser_Acc.ParseJSON();
            prefsHandler.SaveAccountState(true);

            String imgUrl = Parser_AccountObjects.avLocation[0];
            if (imgUrl.length() < 10) {
                saveAccountCurrent("0");
                return;
            }
            ImageRequest request = new ImageRequest(imgUrl, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(final Bitmap response) {
                    String imgUri = new Bitmaps_Cache(response, Signup_User.FOLDER_PROFILE_AVATAR, email).createImageFile();
                    saveAccountCurrent(imgUri);
                }
            }, 300, 300, null, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    saveAccountCurrent("0");
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);
        } catch (JSONException e) {
            showMainsProgress(false);
            prefsHandler.SaveAccountState(false);
        }
    }

    private void saveAccountCurrent(final String imgUri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                prefsHandler.saveAccountData(Parser_AccountObjects.f_name[0], Parser_AccountObjects.l_name[0], Parser_AccountObjects.email[0], Parser_AccountObjects.password[0], Parser_AccountObjects.age[0], imgUri, Parser_AccountObjects.gender[0], Parser_AccountObjects.dateJoined[0]);
            }
        }).start();
    }

    private void showMainsProgress(Boolean state) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(Service_Main.KEY_SERVICE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiveUpdates, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast
        AsyncCountViewedModules viewedModules=new AsyncCountViewedModules();
        viewedModules.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiveUpdates);
        // or `unregisterReceiver(testReceiver)` for a normal broadcast
    }

    private BroadcastReceiver receiveUpdates = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                AsyncCountViewedModules viewedModules=new AsyncCountViewedModules();
                viewedModules.execute();
            }
        }
    };

    private class AsyncCountViewedModules extends AsyncTask<Void, Cursor, Cursor> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dbManager = new DatabaseManager[4];
            dbManager[0] = new Db_Feeds(Main_Tab.this);
            dbManager[1] = new Db_Events(Main_Tab.this);
            dbManager[2] = new Db_Forums(Main_Tab.this);
            dbManager[3] = new Db_Wiki(Main_Tab.this);
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            Cursor cursor = null;
            for (int i = 0; i < dbManager.length; i++) {
                try {
                    dbManager[i].open();
                    cursor = dbManager[i].countDbViewed(Db_Events.struct_events.KEY_VIEWED+ "=" + 0, null);
                    if (cursor != null) {
                        arrayModuleCounts[i] = cursor.getCount();
                        cursor.close();
                        dbManager[i].close();
                    }

                } catch (SQLException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main_Tab.this, "Error while opening a database!", Toast.LENGTH_LONG).show();
                        }
                    });
                    return null;
                }
            }
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
           prepareMenuData();
        }
    }


/*When you are done with the database (e.g., your activity is being closed), simply call
close()
on your
SQLiteOpenHelper
to release your connection, as
ConstantsFragment
does (among other things) in
onDestroy()
:

    @Override
    public void onDestroy() {
        super.onDestroy();((CursorAdapter) getListAdapter()).getCursor().close();
        db.close();
    }

    lv.setOnItemClickListener(new OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> parent, View view, int pos,
long id) {
// Move the cursor to the selected item
c.moveToPosition(pos);
// Extract the row id.
int rowId = c.getInt(c.getColumnIndexOrThrow("_id"));
// Construct the result URI.
Uri outURI = Uri.parse(data.toString() + rowId);
Intent outData = new Intent();
outData.setData(outURI);
setResult(Activity.RESULT_OK, outData);
finish();
}
});

final Uri data = Uri.parse(dataPath + "people/");
final Cursor c = managedQuery(data, null, null, null);
String[] from = new String[] {People.NAME};
int[]  to = new int[] { R.id.itemTextView };
SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
R.layout.listitemlayout,
c,
from,
to);
ListView lv = (ListView)findViewById(R.id.contactListView);
lv.setAdapter(adapter)*/
}
