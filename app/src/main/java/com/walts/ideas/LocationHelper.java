package com.walts.ideas;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {

    private static final String TAG = "LocationHelper";

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Geocoder geocoder;

    //buffer address search
    private double lastLatitude;
    private double lastLongitude;
    private String lastAddress;

    public LocationHelper(ActionBarActivity activity) {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        geocoder = new Geocoder(activity, Locale.getDefault());
    }

    public void startLocationUpdates() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    public String getLocation() {
        double latitude;
        double longitude;
        try {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Location location = null;
            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.d(TAG, "!isGPSEnabled && !isNetworkEnabled");
            } else {
                if (isGPSEnabled) {
                    Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (gpsLocation != null) {
                        Log.d(TAG, "Setting gps location");
                        location = gpsLocation;
                    } else {
                        Log.d(TAG, "Could not get GPS location");
                    }
                } else {
                    Log.d(TAG, "GPS is not enabled");
                }
                if (isNetworkEnabled && location == null) {
                    Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (networkLocation != null) {
                        Log.d(TAG, "Setting network location");
                        location = networkLocation;
                    } else {
                        Log.d(TAG, "Could not get network location");
                    }
                } else {
                    Log.d(TAG, "Network is not enabled");
                }
            }
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                return getAddress(latitude, longitude);
            } else {
                Log.d(TAG, "Could not get location");
            }
        } catch (Exception e) {
            Log.d(TAG, "Could not get location, exception");
            e.printStackTrace();
        }
        return null;
    }

    public String getAddress(double latitude, double longitude) {
        if (lastLatitude == latitude && lastLongitude == longitude && lastAddress != null) {
            Log.d(TAG, "Returning buffered address");
            return lastAddress;
        } else {
            String address = null;
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(listAddresses != null && listAddresses.size() > 0) {
                    address = listAddresses.get(0).getAddressLine(0);
                    lastAddress = address;
                    lastLatitude = latitude;
                    lastLongitude = longitude;
                } else {
                    Log.d(TAG, "Could not get address");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Could not get address, exception");
            }
            return address;
        }
    }

}
