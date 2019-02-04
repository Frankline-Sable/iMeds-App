package com.fsdev.imeds;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Using the location manager, we are able to obtain the following services:
 * -Obtain your current location
 * -follow movement
 * -set proximity alerts for detecting movements into and out of specified area
 * -finding available location providers
 * -monitor the status of the GPS receiver
 */
public class Fragment_GeoHome extends Fragment {
    private LocationManager locationManager;
    private Context mContext;
    private String serviceString;

    public Fragment_GeoHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext=getContext();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_geo_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        locationBasedHandler();
    }

    public void locationBasedHandler() {
        locationManager=(LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);

        List<String> providers=locationManager.getProviders(true);
        for(int i=0;i<providers.size();i++){
            if(providers.get(i).equalsIgnoreCase(LocationManager.GPS_PROVIDER)){
                workWithSetProvider(LocationManager.GPS_PROVIDER);
            }
            else{
                String matchingProvider=locationManager.getBestProvider(autoLocationProvider(),false);
               // workWithSetProvider(matchingProvider);
            }
        }
    }
    public void workWithSetProvider(String provider){
        Location location=locationManager.getLastKnownLocation(provider);
        if(location!=null){
            double lat=location.getLatitude();
            double lng=location.getLongitude();
            Log.i("i-meds","Your current position is: Latitude & longitude: "+lat+", "+lng);
        }

        if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(mContext,"Location Access permission denied",Toast.LENGTH_LONG).show();
        }
        else{
            locationManager.requestLocationUpdates(provider, 10000, 0, new locationHandler());
        }
    }
    public Criteria autoLocationProvider(){
        Criteria criteria=new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);//latitude & longitude
        criteria.setVerticalAccuracy(Criteria.ACCURACY_MEDIUM); //elevation 100-500m
        criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);

        return criteria;
    }
    private class locationHandler implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            Log.i("i-meds","Location changed ");
            geoCoding();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
    public void geoCoding(){
        Geocoder geocoder=new Geocoder(mContext, Locale.getDefault());
    }

}
