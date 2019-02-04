package com.fsdev.imeds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Frankline Sable on 28/11/2017.
 */

public class Parser_Wiki {
    public static String[] titles;
    public static String[] descriptions;
    public static String[] dates;
    public static int[] ids;
    public static String[] metas;
    public static int[] cats;

    public static final String JSON_ARRAY = "wiki";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESC = "desc";
    public static final String KEY_DATE = "date";
    public static final String KEY_ID = "id";
    public static final String KEY_CAT = "cat";
    public static final String KEY_META = "meta";

    private JSONArray wiki = null;
    private JSONObject jsonObject = null;

    public Parser_Wiki(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    protected void parseWiki() {

        try {
            wiki = jsonObject.getJSONArray(JSON_ARRAY);
            titles = new String[wiki.length()];
            descriptions = new String[wiki.length()];
            dates = new String[wiki.length()];
            metas = new String[wiki.length()];
            ids= new int[wiki.length()];
            cats = new int[wiki.length()];

            for (int i = 0; i < wiki.length(); i++) {
                JSONObject object = wiki.getJSONObject(i);
                titles[i] = object.getString(KEY_TITLE);
                descriptions[i] = object.getString(KEY_DESC);
                dates[i] = object.getString(KEY_DATE);
                metas[i] = object.getString(KEY_META);
                ids[i]=object.getInt(KEY_ID);
                cats[i]=object.getInt(KEY_CAT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
