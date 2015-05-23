package com.walts.ideas.helpers;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.walts.ideas.helpers.Dialogs;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class LocationHelper {

    private static final String TAG = "LocationHelper";

    private Timer timer;

    private final LocationManager locationManager;
    private LocationResult locationResult;

    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;

    private final Geocoder geocoder;

    //buffer address search
    private double lastLatitude;
    private double lastLongitude;
    private String lastAddress;

    private final AppCompatActivity activity;

    public LocationHelper(AppCompatActivity activity) {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(activity, Locale.getDefault());
        this.activity = activity;
    }

    public boolean requestLocation(LocationResult result) {
        locationResult = result;

        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isNetworkAvailable(activity)) {
            Dialogs.showAlertMessage(activity, new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK), "Your Internet access seems to be disabled, do you want to enable it?");
            return false;
        } else {
            if (!gpsEnabled && !networkEnabled) {
                Log.d(TAG, "GPS and network provider not enabled.");
                Dialogs.showAlertMessage(activity, new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), "Your GPS seems to be disabled, do you want to enable it?");
                return false;
            } else {
                if (gpsEnabled) {
                    Log.d(TAG, "GPS provider enabled. Requesting updates from GPS provider.");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
                }
                if (networkEnabled) {
                    Log.d(TAG, "Network provider enabled. Requesting updates from network provider.");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
                }
                final long delayTime = 5000;
                timer = new Timer();
                timer.schedule(new GetLastLocation(), delayTime);
                return true;
            }
        }
    }

    //you may need to call this onPause() in activity
    public void cancel() {
        timer.cancel();
        locationManager.removeUpdates(locationListenerGps);
        locationManager.removeUpdates(locationListenerNetwork);
    }

    private final LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Location changed in GPS listener. Coordinates are : latitude - " + location.getLatitude() + ", longitude - " + location.getLongitude());
            timer.cancel();
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Location changed in network listener. Coordinates are : latitude - " + location.getLatitude() + ", longitude - " + location.getLongitude());
            timer.cancel();
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private class GetLastLocation extends TimerTask {

        private static final String TAG = "GetLastLocation";

        @Override
        public void run() {
            locationManager.removeUpdates(locationListenerGps);
            locationManager.removeUpdates(locationListenerNetwork);
            Location gpsLocation = null;
            Location networkLocation = null;
            if (gpsEnabled) {
                Log.d(TAG, "GPS provider enabled. Getting last known location from GPS provider.");
                gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (networkEnabled) {
                Log.d(TAG, "Network provider enabled. Getting last known location form network provider.");
                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (gpsLocation != null && networkLocation != null) {
                if (gpsLocation.getTime() > networkLocation.getTime()) {
                    Log.d(TAG, "GPS location is newer than network location. Returning GPS location.");
                    locationResult.gotLocation(gpsLocation);
                } else {
                    Log.d(TAG, "Network location is newer than gps location. Returning network location.");
                    locationResult.gotLocation(networkLocation);
                }
                return;
            }
            if (gpsLocation != null) {
                Log.d(TAG, "Returning GPS location.");
                locationResult.gotLocation(gpsLocation);
                return;
            }
            if (networkLocation != null) {
                Log.d(TAG, "Returning network location.");
                locationResult.gotLocation(networkLocation);
                return;
            }
            Log.d(TAG, "Location is null.");
            locationResult.gotLocation(null);
        }
    }

    public String getAddress(double latitude, double longitude) {
        if (lastLatitude == latitude && lastLongitude == longitude && lastAddress != null) {
            Log.d(TAG, "Returning buffered address");
            return lastAddress;
        } else if (!isNetworkAvailable(activity)) {
            Log.d(TAG, "Network is not available, cannot request address");
            return null;
        } else {
            String address = null;
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(listAddresses != null && listAddresses.size() > 0) {
                    address = listAddresses.get(0).getAddressLine(0);
                    //buffer address
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

    private boolean isNetworkAvailable(AppCompatActivity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean status = networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable();
        Log.d(TAG, "Network is available? : " + status);
        return status;
    }

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }

}
