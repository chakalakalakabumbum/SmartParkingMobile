package com.example.dominator.smartparkinginterface.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.dominator.smartparkinginterface.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.Timer;
import java.util.TimerTask;

public class MyLocation {

    private Timer timer1;
    private LocationManager lm;
    private LocationResult locationResult;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private static final LatLng DEFAULT_LOCATION = new LatLng(10.852711, 106.626786);

    private LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    private LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public boolean getLocation(Context context, LocationResult result) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, R.string.Request_location_permission, Toast.LENGTH_SHORT).show();
            return false;
        }

        //exceptions will be thrown if provider is not permitted.
        try {
            //I use LocationResult callback class to pass location value from MyLocation to user code.
            locationResult = result;
            if (lm == null) {
                lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            }
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        Toast.makeText(context, gps_enabled + " " + network_enabled, Toast.LENGTH_LONG).show();

        //don't start listeners if no provider is enabled
        if (!gps_enabled && !network_enabled) {
            return false;
        }

        if (gps_enabled) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        }
        if (network_enabled) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        }

        timer1 = new Timer();
        timer1.schedule(new GetLastLocation(context), 10000);
        return true;
    }

    class GetLastLocation extends TimerTask {

        private Context context;

        private GetLastLocation(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, R.string.Request_location_permission, Toast.LENGTH_SHORT).show();
                return;
            }

            Location net_loc = null, gps_loc = null;
            if (gps_enabled) {
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (network_enabled) {
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            //if there are both values use the latest one
            if (gps_loc != null && net_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime())
                    locationResult.gotLocation(gps_loc);
                else
                    locationResult.gotLocation(net_loc);
                return;
            }

            if (gps_loc != null) {
                locationResult.gotLocation(gps_loc);
                return;
            }
            if (net_loc != null) {
                locationResult.gotLocation(net_loc);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    public static LatLng getCurrentLocation(Context context) {
        SharedPreferences locationPref = context.getSharedPreferences("location", Context.MODE_PRIVATE);
//        return new LatLng(
//                locationPref.getFloat("Latitude", (float) DEFAULT_LOCATION.latitude),
//                locationPref.getFloat("Longitude", (float) DEFAULT_LOCATION.longitude)
//        );
        return new LatLng(
                NumberUtil.tryParseDouble(locationPref.getString("Latitude", DEFAULT_LOCATION.latitude + ""), DEFAULT_LOCATION.latitude),
                NumberUtil.tryParseDouble(locationPref.getString("Longitude", DEFAULT_LOCATION.longitude + ""), DEFAULT_LOCATION.longitude)
        );
    }

}
