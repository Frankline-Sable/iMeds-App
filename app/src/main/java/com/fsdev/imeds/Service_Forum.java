package com.fsdev.imeds;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.fsdev.imeds.Db_Forums;

public class Service_Forum extends IntentService {

    public static final String KEY_SERVICE_ACTION = "fsdev.imeds.Service_Forum";
    private Context mContext;
    private Db_Forums db_forums;
    private List<String> imageCache;

    public Service_Forum() {
        super("Service_Forum");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            db_forums = new Db_Forums(mContext);
            fetchNewForums();
        }
    }

    private void fetchNewForums() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, App_Urls.url_forums, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Parser_Forums pfm = new Parser_Forums(response);
                pfm.parseForums();

                db_forums.open();

                for (int i = 0; i < Parser_Forums.ids.length; i++) {

                    int id = Integer.parseInt(Parser_Forums.ids[i]);
                    String title = Parser_Forums.titles[i];
                    String desc = Parser_Forums.descriptions[i];
                    String username = Parser_Forums.username[i];
                    String uiDateFmt = formatForumDates(Parser_Forums.dates[i]);
                    String schedule = Parser_Forums.dates[i];
                    String urls = Parser_Forums.imgUrls[i];
                    String email = Parser_Forums.emails[i];
                    long uniqueID=Parser_Forums.uniques[i];
                    long prevID=Parser_Forums.prevIds[i];
                    int comment=Parser_Forums.comments[i];

                    loadImageForumsURIs(urls, email);

                    Boolean update = db_forums.updateDb(id, title, desc,uniqueID, schedule, uiDateFmt, username, email);
                    if (!update) {
                        db_forums.insertDb(title, desc, "0", uniqueID, schedule, uiDateFmt, username, email,comment,prevID);
                    }
                }
                executeForumUpdate();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("i-meds", "error volley" + error.getMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private String formatForumDates(String date) {
        SimpleDateFormat dateTimeFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String elapsedTime;
        Date dateStart = null;

        try {
            dateStart = dateTimeFmt.parse(date);
        } catch (ParseException e) {
            return "???";
        }

        Calendar forumCalendar = Calendar.getInstance();
        Calendar mCalendar = Calendar.getInstance();
        forumCalendar.setTime(dateStart);

        if (mCalendar.get(Calendar.MONTH) != forumCalendar.get(Calendar.MONTH) || mCalendar.get(Calendar.YEAR) != forumCalendar.get(Calendar.YEAR)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("LLL");
            elapsedTime = dateFormat.format(forumCalendar.getTime());
            return elapsedTime;
        }
        int days = mCalendar.get(Calendar.DAY_OF_MONTH) - forumCalendar.get(Calendar.DAY_OF_MONTH);
        System.out.println(days);

        if (days < 1) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");//1
            elapsedTime = timeFormat.format(forumCalendar.getTime());
        } else if (days == 1) {
            elapsedTime = "Yesterday";
        } else if (days < 8) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE");
            elapsedTime = dateFormat.format(forumCalendar.getTime());
        } else if (days < 15) {
            elapsedTime = "Week Ago";
        } else if (days < 30) {
            String returnDateIndex;
            String checkIndex = String.valueOf(forumCalendar.get(Calendar.DAY_OF_MONTH));
            char dateIndex;

            if (checkIndex.length() == 1) {
                dateIndex = checkIndex.charAt(0);
            } else {
                dateIndex = checkIndex.charAt(1);
            }

            if (dateIndex == '1') {
                returnDateIndex = "st";
            } else if (dateIndex == '2') {
                returnDateIndex = "nd";
            } else if (dateIndex == '3') {
                returnDateIndex = "rd";
            } else {
                returnDateIndex = "th";
            }
            elapsedTime = checkIndex + returnDateIndex;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("LLL", Locale.US);
            elapsedTime = dateFormat.format(forumCalendar.getTime());
        }
        return elapsedTime;
    }

    private void loadImageForumsURIs(final String imgUrl, final String imgName) {

        imageCache = new ArrayList<>();

        if (imageCache.contains(imgName))
            return;
        imageCache.add(imgName);

        Log.i("i-meds","image url is "+imgUrl+" name is "+imgName);

        ImageRequest request = new ImageRequest(imgUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                updateForumImageLoc(response, imgName);
            }
        }, 300, 300, null, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("i-meds", "Error unable to load forum image" + error.getLocalizedMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void updateForumImageLoc(Bitmap bitmap, String email) {
        String imgUri = new Bitmaps_Cache(bitmap, ".Profile Avatars", email).createImageFile();
        if (imgUri.length() < 10) {
            return;
        }
        db_forums.open();
        Boolean update = db_forums.updateImageDb(email, imgUri);
        if (!update) {
            Log.i("i-meds", "Failed to update -->insert");
        }
        db_forums.close();
    }

    private void executeForumUpdate() {
        Intent sevInt = new Intent(KEY_SERVICE_ACTION);
        sevInt.putExtra("resultCode", Activity.RESULT_OK);
        sevInt.putExtra("resultValue", true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(sevInt);
    }
}
