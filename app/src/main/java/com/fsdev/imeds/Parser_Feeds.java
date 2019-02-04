package com.fsdev.imeds;

import android.graphics.LinearGradient;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Frankline Sable on 03/11/2017.
 */

public class Parser_Feeds {
    public static String[] titles;
    public static String[] descriptions;
    public static String[] imgUrls;
    public static String[] urls;
    public static String[] dates;
    public static String[] ids;

    public static String metaTitle,metaSubtitle,metaUrl;

    public static final String JSON_ARRAY="feeds";
    public static final String KEY_TITLE="Title";
    public static final String KEY_DESC="Description";
    public static final String KEY_IMAGE="ImageURL";
    public static final String KEY_URL="Url";
    public static final String KEY_DATE="Date";
    public static final String KEY_ID="pid";

    private JSONArray feeds=null;
    private JSONObject jsonObject=null;

    public Parser_Feeds(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    protected void parseFeeds(){

        try {
            feeds=jsonObject.getJSONArray(JSON_ARRAY);
            ids=new String[feeds.length()];
            titles=new String[feeds.length()];
            descriptions=new String[feeds.length()];
            imgUrls=new String[feeds.length()];
            urls=new String[feeds.length()];
            dates=new String[feeds.length()];

            for(int i=0; i<feeds.length();i++){
                JSONObject object=feeds.getJSONObject(i);
                ids[i]=object.getString(KEY_ID);
                titles[i]=object.getString(KEY_TITLE);
                descriptions[i]=object.getString(KEY_DESC);
                imgUrls[i]=object.getString(KEY_IMAGE);
                urls[i]=object.getString(KEY_URL);
                dates[i]=object.getString(KEY_DATE);
            }

            metaTitle=jsonObject.getString("dTitle");
            metaSubtitle=jsonObject.getString("dSub");
            metaUrl=jsonObject.getString("dImage");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
