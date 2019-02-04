package com.fsdev.imeds;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Frankline Sable on 15/10/2017.
 */

public class Parser_AccountObjects {
    public static String[] f_name;
    public static String[] l_name;
    public static String[] email;
    public static String[] password;
    public static String[] gender;
    public static String[] age;
    public static String[] avLocation;
    public static String[] dateJoined;

    private static final String KEY_F_NAME = "fName";
    private static final String KEY_L_NAME = "lName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "Password";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_AGE = "age";
    private static final String KEY_AvLOC = "avLocation";
    private static final String KEY_DateJoined = "dateJoined";

    private static final String JSON_ARRAY = "Credentials";
    private String json;

    public Parser_AccountObjects(String json) {
        this.json = json;
    }

    public void ParseJSON() throws JSONException {
        JSONObject jsonObject = null;

        jsonObject = new JSONObject(json);
        JSONArray user = jsonObject.getJSONArray(JSON_ARRAY);
        f_name = new String[user.length()];
        l_name = new String[user.length()];
        email = new String[user.length()];
        password = new String[user.length()];
        gender = new String[user.length()];
        age = new String[user.length()];
        avLocation = new String[user.length()];
        dateJoined = new String[user.length()];

        for (int i = 0; i < user.length(); i++) {
            JSONObject object = user.getJSONObject(i);
            f_name[i] = object.getString(KEY_F_NAME);
            l_name[i] = object.getString(KEY_L_NAME);
            email[i] = object.getString(KEY_EMAIL);
            password[i] = object.getString(KEY_PASSWORD);
            gender[i] = object.getString(KEY_GENDER);
            age[i] = object.getString(KEY_AGE);
            avLocation[i] = object.getString(KEY_AvLOC);
            dateJoined[i] = object.getString(KEY_DateJoined);
        }
    }
}
