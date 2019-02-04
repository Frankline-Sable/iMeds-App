package com.fsdev.imeds;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Frankline Sable on 03/12/2017.
 */

public class CheckNetworkAvailability {
    private Context mContext;
    private Boolean networkStateOky=false;

    public CheckNetworkAvailability(Context mContext) {
        this.mContext = mContext;
    }
    public Boolean isNetworkFunctioning(){
        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if ((networkInfo != null && networkInfo.isConnected())) {
            networkStateOky=attemptServerConnection();
        }
        return networkStateOky;
    }
    private Boolean attemptServerConnection(){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, App_Urls.url_signIn, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (Boolean.valueOf(response)) {
                   networkStateOky=true;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);

        return networkStateOky;
    }
}
