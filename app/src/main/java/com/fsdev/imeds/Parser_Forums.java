package com.fsdev.imeds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Frankline Sable on 03/11/2017.
 */

public class Parser_Forums {
    public static String[] titles;
    public static String[] descriptions;
    public static String[] imgUrls;
    public static String[] username;
    public static String[] dates;
    public static String[] ids;
    public static String[] emails;
    public static long[] uniques;
    public static long[] prevIds;
    public static int[] comments;

    public static final String JSON_ARRAY="forums";
    public static final String KEY_USERNAME="username";
    public static final String KEY_TITLE="Title";
    public static final String KEY_DESC="Description";
    public static final String KEY_PROFILE="profileImage";
    public static final String KEY_EMAIL="email";
    public static final String KEY_DATE="Date";
    public static final String KEY_ID="pid";
    public static final String KEY_UNIQUE="unique";
    public static final String KEY_PREV_ID="prev_ID";
    public static final String KEY_COMMENT="comment";

    private JSONArray forums=null;
    private  JSONObject jsonObject=null;

    public Parser_Forums(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    protected void parseForums(){

        try {
            forums=jsonObject.getJSONArray(JSON_ARRAY);
            titles=new String[forums.length()];
            descriptions=new String[forums.length()];
            imgUrls=new String[forums.length()];
            username=new String[forums.length()];
            dates=new String[forums.length()];
            ids=new String[forums.length()];
            emails=new String[forums.length()];
            uniques=new long[forums.length()];
            comments=new int[forums.length()];
            prevIds=new long[forums.length()];

            for(int i=0; i<forums.length();i++){
                JSONObject object=forums.getJSONObject(i);
                titles[i]=object.getString(KEY_TITLE);
                descriptions[i]=object.getString(KEY_DESC);
                username[i]=object.getString(KEY_USERNAME);
                dates[i]=object.getString(KEY_DATE);
                ids[i]=object.getString(KEY_ID);
                emails[i]=object.getString(KEY_EMAIL);
                imgUrls[i]=App_Urls.url_prefix + "avatars/" + emails[i] + ".jpeg";
                uniques[i]=object.getLong(KEY_UNIQUE);
                prevIds[i]=object.getLong(KEY_PREV_ID);
                comments[i]=object.getInt(KEY_COMMENT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
