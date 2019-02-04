package com.fsdev.imeds;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

/**
 * Created by Frankline Sable on 15/10/2017.
 */

public class PreferencesHandler {
    public static final String PREFS_PRIVATE = "PREFS_PRIVATE";
    public static final String PREFS_PRIVATE_COUNTS = "PREFS_FOR_MODULES_COUNT";
    private static final String KEY_PRIVATE_ACCOUNT = "KEY_PRIVATE_GET_ACCOUNT";

    private static final String KEY_PRIVATE_CONFIRMATION = "KEY_PRIVATE_CONFIRMATION";

    public static final String KEY_USER_fName = "KEY_USER_fName";
    public static final String KEY_USER_lName = "KEY_USER_lName";
    public static final String KEY_USER_EMAIL = "KEY_USER_EMAIL";
    public static final String KEY_USER_PASSWORD = "KEY_USER_PASS";
    public static final String KEY_USER_GENDER = "KEY_USER_GEN";
    public static final String KEY_USER_AGE = "KEY_USER_AGE";
    public static final String KEY_USER_AVATAR_LOC = "KEY_USER_AVATAR";
    public static final String KEY_USER_DATE_JOIN = "KEY_USER_DATE";

    public static final String KEY_TRENDING_TITLE = "KEY_TRENDING_TITLE";
    public static final String KEY_TRENDING_SUB = "KEY_TRENDING_SUB";
    public static final String KEY_TRENDING_IMG = "KEY_TRENDING_IMG";

    /**All About Dealing with new feeds notes**/
    public static final String KEY_NEW_FEED = "KEY_NEW_FEED";
    public static final String KEY_NEW_EVENT= "KEY_NEW_EVENT";
    public static final String KEY_NEW_FORUM = "KEY_NEW_DISCUSSION";
    public static final String KEY_NEW_FIND= "KEY_NEW_FIND";

    public static final String KEY_NEW_WIKI= "KEY_NEW_WIKI";

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private Context mContext;

    public PreferencesHandler(Context mContext) {
        this.mContext = mContext;
        sharedPrefs = mContext.getSharedPreferences(PREFS_PRIVATE, Context.MODE_PRIVATE);
    }

    public void SaveAccountState(Boolean loggedIn) {
        prefsEditor = sharedPrefs.edit();
        prefsEditor.putBoolean(KEY_PRIVATE_ACCOUNT, loggedIn);
        prefsEditor.apply();
    }

    public Boolean getAccountState() {
        return sharedPrefs.getBoolean(KEY_PRIVATE_ACCOUNT, false);
    }

    public Boolean saveAccountData(final String mFirstName, final String mLastName, final String mEmail, final String mPassword, final String mAge, final String avatarLoc, final String mGender, final String mDate) {
        prefsEditor = sharedPrefs.edit();
        prefsEditor.putString(KEY_USER_fName, mFirstName);
        prefsEditor.putString(KEY_USER_lName, mLastName);
        prefsEditor.putString(KEY_USER_EMAIL, mEmail);
        prefsEditor.putString(KEY_USER_PASSWORD, mPassword);
        prefsEditor.putString(KEY_USER_GENDER, mGender);
        prefsEditor.putString(KEY_USER_AGE, mAge);
        prefsEditor.putString(KEY_USER_AVATAR_LOC, avatarLoc);
        prefsEditor.putString(KEY_USER_DATE_JOIN, mDate);
        prefsEditor.apply();
        return true;
    }
    //updateAccountData(fname, lname, emailTxt, passwordTxt, age, gend);
    public void updateAccountData(final String mFirstName, final String mLastName, final String mEmail, final String mPassword, final String mAge, final String mGender) {
        prefsEditor = sharedPrefs.edit();
        prefsEditor.putString(KEY_USER_fName, mFirstName);
        prefsEditor.putString(KEY_USER_lName, mLastName);
        prefsEditor.putString(KEY_USER_EMAIL, mEmail);
        prefsEditor.putString(KEY_USER_PASSWORD, mPassword);
        prefsEditor.putString(KEY_USER_GENDER, mGender);
        prefsEditor.putString(KEY_USER_AGE, mAge);
        prefsEditor.apply();
    }
    public void saveSingleAccountData(final String data, final String key) {
        prefsEditor = sharedPrefs.edit();
        prefsEditor.putString(key, data);
        prefsEditor.apply();
    }

    public String getAccountData(String data) {
        return sharedPrefs.getString(data, "NOT SPECIFIED");
    }

    public void trendingFeed(String tTitle, String tSub, String tImg) {
        prefsEditor = sharedPrefs.edit();
        prefsEditor.putString(KEY_TRENDING_TITLE, tTitle);
        prefsEditor.putString(KEY_TRENDING_SUB, tSub);
        prefsEditor.putString(KEY_TRENDING_IMG, tImg);
        prefsEditor.apply();
    }
    public String getTrendingData(String data) {
        return sharedPrefs.getString(data, "0");
    }



    public Boolean getRemState() {
        return sharedPrefs.getBoolean(KEY_PRIVATE_CONFIRMATION, true);
    }

    public void SaveAccountState(Boolean NoAskAgain, Boolean getAccount) {
        prefsEditor = sharedPrefs.edit();
        prefsEditor.putBoolean(KEY_PRIVATE_CONFIRMATION, NoAskAgain);
        prefsEditor.putBoolean(KEY_PRIVATE_ACCOUNT, getAccount);
        prefsEditor.apply();
    }
    public void clearAll(){
        prefsEditor = sharedPrefs.edit();
        prefsEditor.clear();
        prefsEditor.apply();
    }
    public void updateNewCount(String key,int count){
        prefsEditor = sharedPrefs.edit();
        prefsEditor.putInt(key, count);
        prefsEditor.apply();
    }
    public int getUpdatesCount(String key){
       return sharedPrefs.getInt(key,0);
    }

    public void updateWikiCount(String key,int count){
        prefsEditor = sharedPrefs.edit();
        prefsEditor.putInt(key, count);
        prefsEditor.apply();
    }
    public int getWikiCount(String key){
       return sharedPrefs.getInt(key,0);
    }
}
