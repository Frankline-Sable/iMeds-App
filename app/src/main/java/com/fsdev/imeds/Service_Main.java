package com.fsdev.imeds;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.internal.cache.DiskLruCache;

import static android.os.Environment.isExternalStorageRemovable;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * This class handles all the main communications that are taking place inside the program, this is the central manager class
 */
public class Service_Main extends IntentService {

    private Context mContext;
    private Db_Events db_events;
    private Db_Feeds db_feeds;
    private Db_Forums db_forums;
    private Db_Wiki db_wiki;
    private Uri mImagePathUri;
    private String mImagePathString = "0";
    private Parser_Events pe;
    private Parser_Wiki wk;
    private Parser_Feeds pf;
    private Parser_Forums pfm;
    private PreferencesHandler prefHandler;
    private List<String> imageCache;
    private Boolean dataUpdateStatus[]=new Boolean[3];
    private int dataUpdateCount=0;
    public static final String KEY_SERVICE_ACTION="com.fsdev.imeds.Service_Main";

    public Service_Main() {
        super("Service_Main");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        prefHandler = new PreferencesHandler(mContext);
    }

    /**
     * @param intent what will happen once the service is triggered;
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        db_events = new Db_Events(mContext);
        db_feeds = new Db_Feeds(mContext);
        db_forums = new Db_Forums(mContext);
        db_wiki=new Db_Wiki(mContext);


        fetchNewEvents();
        fetchNewFeeds();
        fetchNewForums();
        fetchWiki();

    }

    private void fetchNewEvents() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, App_Urls.url_events, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pe = new Parser_Events(response);
                pe.parseEvents();
                int count=0;

                db_events.open();
                for (int i = 0; i < Parser_Events.ids.length; i++) {
                    int id = Integer.parseInt(Parser_Events.ids[i]);
                    String title = Parser_Events.titles[i];
                    String desc = Parser_Events.descriptions[i];
                    String venue = Parser_Events.venues[i];
                    String schedule = Parser_Events.dates[i];
                    String month = formatEventsDate(1, schedule);
                    String year = formatEventsDate(2, schedule);

                    Boolean update = db_events.updateDb(id, title, desc, venue, schedule, month, year);
                    if (!update) {
                        db_events.insertDb(title, desc, "0", venue, schedule, month, year);
                        loadImageURI(Parser_Events.imgUrls[i], id);
                        count++;
                    }
                    fireDataUpdate();
                }
                db_events.close();
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

    private String formatEventsDate(int dateChosen, String dateString) {

        SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date dateFromSql = null;
        try {
            dateFromSql = sqlDateFormat.parse(dateString);
        } catch (ParseException e) {
            return "???";
        }

        String datePatten, returnDate;
        switch (dateChosen) {
            case 1:
                datePatten = "LLLL";
                break;
            case 2:
                datePatten = "yyyy";
                break;
            default:
                datePatten = "yyyy-MM-dd";
        }

        SimpleDateFormat dateFmtEvents = new SimpleDateFormat(datePatten, Locale.US);
        returnDate = dateFmtEvents.format(dateFromSql);

        return returnDate;
    }

    private String loadImageURI(final String imgUrl, final int imgKey) {

        ImageRequest request = new ImageRequest(imgUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                createImageFile(response, imgKey);
            }
        }, 512, 512, null, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mImagePathString = "0";
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

        return mImagePathString;
    }

    public void createImageFile(Bitmap bitmap, int key) {

        final String imageFileName = "event_" + key + ".jpg";
        File imageFile = null;

        File storageDir = Environment.getExternalStorageDirectory();
        if (storageDir.exists() && storageDir.canWrite()) {

            final File capturedFolder = new File(storageDir.getAbsolutePath() + "/iMeds App/.Event Images/");

            if (!capturedFolder.exists()) {
                capturedFolder.mkdirs();
            }
            if (capturedFolder.exists() && capturedFolder.canWrite()) {
                FileOutputStream outputStream = null;
                try {
                    imageFile = new File(capturedFolder.getAbsolutePath() + "/" + imageFileName);
                    if (imageFile.createNewFile()) {
                        mImagePathUri = Uri.fromFile(imageFile.getAbsoluteFile());
                        mImagePathString = String.valueOf(mImagePathUri);
                        updateImageLoc(mImagePathString, key);
                    } else {
                        updateImageLoc(String.valueOf(Uri.fromFile(imageFile.getAbsoluteFile())), key);
                    }
                    outputStream = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } catch (IOException e) {
                    Log.i("i-meds", e.getMessage());
                    updateImageLoc("0", key);
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            //Swallow
                        }
                    }
                }

            }
        } else {
            Log.i("i-meds", "error io exp");
        }
    }

    private void updateImageLoc(String imageUri, int key) {
        db_events.open();
        Boolean update = db_events.updateImageDb(Integer.parseInt(Parser_Events.ids[key - 1]), imageUri);
        if (!update) {
            Log.i("i-meds", "Failed to update -->insert");
        }
        db_events.close();
    }


    private void fetchNewFeeds() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, App_Urls.url_feeds, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pf = new Parser_Feeds(response);
                pf.parseFeeds();
                int count=0;

                saveTodaySummary(Parser_Feeds.metaTitle, Parser_Feeds.metaSubtitle, Parser_Feeds.metaUrl);
                db_feeds.open();

                for (int i = 0; i < Parser_Feeds.ids.length; i++) {
                    int id = Integer.parseInt(Parser_Feeds.ids[i]);
                    String title = Parser_Feeds.titles[i];
                    String desc = Parser_Feeds.descriptions[i];
                    String uiDateFmt = timeObserverFmt(Parser_Feeds.dates[i]);
                    String schedule = Parser_Feeds.dates[i];
                    String urls = Parser_Feeds.urls[i];

                    Boolean update = db_feeds.updateDb(title, desc, urls, schedule, uiDateFmt, (long) id);
                    if (!update) {
                        long r_id = db_feeds.insertDb(title, desc, "0", urls, schedule, uiDateFmt, (long) id);
                        loadImageFeedsURIs(Parser_Feeds.imgUrls[i], (long) id);
                        count++;
                    }
                }
                db_feeds.close();
                fireDataUpdate();
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

    private String timeObserverFmt(String dateString) {
        SimpleDateFormat dateTimeFmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        Date endDate = new Date();
        String elapsedTime = "??";
        Date startDate = null;

        try {
            startDate = dateTimeFmt.parse(dateString);
        } catch (ParseException e) {
            return elapsedTime;
        }
        long difference = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        final long elapsedDays = difference / daysInMilli;
        difference = difference % daysInMilli;

        final long elapsedHours = difference / hoursInMilli;
        difference = difference % hoursInMilli;

        final long elapsedMinutes = difference / minutesInMilli;
        difference = difference % minutesInMilli;

        final long elapsedSeconds = difference / secondsInMilli;

        final long elapsedMonths = elapsedDays / 31;

        final long elapsedYears = elapsedDays / (long) 365.25;

        if (elapsedYears > 0) {
            elapsedTime = elapsedYears + " Years";
        } else if (elapsedMonths > 0) {
            elapsedTime = elapsedMonths + " Month";
        } else if (elapsedDays > 0) {
            elapsedTime = elapsedDays + " days";
        } else if (elapsedHours > 0) {
            elapsedTime = elapsedHours + " h";
        } else if (elapsedMinutes > 0) {
            elapsedTime = elapsedMinutes + " min";
        } else if (elapsedSeconds > 0) {
            elapsedTime = elapsedSeconds + " sec";
        }
        return elapsedTime;
    }

    private void saveTodaySummary(String dTitle, String dSub, String dUrl) {
        prefHandler.trendingFeed(dTitle, dSub, dUrl);

    }

    private String loadImageFeedsURIs(final String imgUrl, final long imgKey) {

        ImageRequest request = new ImageRequest(imgUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                updateFeedImageLoc(response, imgKey);
            }
        }, 512, 200, null, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mImagePathString = "0";
                Log.i("i-meds", "Error downloading feed images" + error.getLocalizedMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

        return mImagePathString;
    }


    private void updateFeedImageLoc(Bitmap bitmap, long key) {
        String imgUri = new Bitmaps_Cache(bitmap, ".Feed Images", "feed_" + key).createImageFile();
        if (imgUri.length() < 10) {
            return;
        }
        db_feeds.open();
        Boolean update = db_feeds.updateImageDb(key, imgUri);
        if (!update) {
            Log.i("i-meds", "Failed to update -->insert  Feeds Image");
        }
        db_feeds.close();
    }

    private void fetchNewForums() {

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, App_Urls.url_forums, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pfm = new Parser_Forums(response);
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
                db_forums.close();
                fireDataUpdate();
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE",Locale.US);
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

        ImageRequest request = new ImageRequest(imgUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                updateForumImageLoc(response, imgName);
            }
        }, 300, 300, null, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("i-meds", "error volley " + error.getLocalizedMessage());
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

    private void fireDataUpdate(){
        dataUpdateCount++;

        if(dataUpdateCount==dataUpdateStatus.length){
            Intent sevInt=new Intent(KEY_SERVICE_ACTION);
            sevInt.putExtra("resultCode", Activity.RESULT_OK);
            sevInt.putExtra("updateCount", true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(sevInt);
        }
    }
    private void fetchWiki() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, App_Urls.url_fetch_wiki, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                wk = new Parser_Wiki(response);
                wk.parseWiki();

                db_wiki.open();
                for (int i = 0; i < Parser_Wiki.ids.length; i++) {
                    int id = Parser_Wiki.ids[i];
                    String title = Parser_Wiki.titles[i];
                    String meta=Parser_Wiki.metas[i];
                    String desc = Parser_Wiki.descriptions[i];
                    int cats = Parser_Wiki.cats[i];
                    String date = Parser_Wiki.dates[i];

                    Boolean update = db_wiki.updateDb(id, title, desc, cats, date,meta );
                    if (!update) {
                        db_wiki.insertDb(title, desc, cats, date, meta);
                     }
                }
                db_wiki.close();
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
}
