package com.fsdev.imeds;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Frankline Sable on 03/11/2017.
 */

public class Parser_Events {
    public static String[] ids;
    public static String[] titles;
    public static String[] descriptions;
    public static String[] imgUrls;
    public static String[] venues;
    public static String[] dates;
    public static String[] urls;

    public static final String JSON_ARRAY="events";
    public static final String KEY_ID="id";
    public static final String KEY_TITLE="title";
    public static final String KEY_DESC="desc";
    public static final String KEY_VENUES="venue";
    public static final String KEY_DATE="dateTime";
    public static final String KEY_IMG="eventImg";
    public static final String KEY_URLS="eventUrl";

    private JSONArray Events;
    private  JSONObject jsonObject=null;

    public Parser_Events(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    protected void parseEvents(){

        try {
            Events=jsonObject.getJSONArray(JSON_ARRAY);
            ids=new String[Events.length()];
            titles=new String[Events.length()];
            descriptions=new String[Events.length()];
            imgUrls=new String[Events.length()];
            venues=new String[Events.length()];
            dates=new String[Events.length()];
            urls=new String[Events.length()];

            for(int i=0; i<Events.length();i++){
                JSONObject object=Events.getJSONObject(i);
                ids[i]=object.getString(KEY_ID);
                titles[i]=object.getString(KEY_TITLE);
                descriptions[i]=object.getString(KEY_DESC);
                urls[i]=object.getString(KEY_URLS);
                imgUrls[i]=object.getString(KEY_IMG);
                venues[i]=object.getString(KEY_VENUES);
                dates[i]=object.getString(KEY_DATE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
