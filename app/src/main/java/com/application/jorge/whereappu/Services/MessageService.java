package com.application.jorge.whereappu.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Classes.MyWSEventHandler;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.WebSocket.WSHubsApi;
import com.google.android.gms.location.Geofence;

import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geofencing.model.GeofenceModel;

public class MessageService extends Service {
    private static final String LOG_TAG = "MessageService";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public WSHubsApi wsHubsApi;
    private final IBinder binder = new MyBinder();
    private LocationManager locationManager;
    private MyLocationListener listener;
    public Location previousBestLocation = null;

    public class MyBinder extends Binder {
        public MessageService getService() {
            return MessageService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        utils.log("Creating messageService");
        GeofenceModel home = new GeofenceModel.Builder("Home")
                .setTransition(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLatitude(40.47777)
                .setLongitude(-3.72705)
                .setRadius(1000)
                .build();

        GeofenceModel work = new GeofenceModel.Builder("Work")
                .setTransition(Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLatitude(40.53531)
                .setLongitude(-3.64245)
                .setRadius(1000)
                .build();

        SmartLocation.with(this).geofencing()
                .add(home)
                .add(work)
                .remove("already_existing_geofence_id")
                .start(new OnGeofencingTransitionListener() {
                    @Override
                    public void onGeofenceTransition(Geofence geofence, int i) {
                        utils.log("Geofence Event:\n\tName: " + geofence.getRequestId() + "\n\tEvent: " + String.valueOf(i));
                    }
                });
        utils.log("Creating Geofencing");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 100, listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        utils.log("Starting messageService");
        try {
            App.setContextIfNull(this);
            if (wsHubsApi == null) {
                String ID = User.getMySelf() == null ? "" : String.valueOf(User.getMySelf().ID);
                utils.log("ID = " + ID);
                wsHubsApi = new WSHubsApi("ws://" + App.hostName + ID, new MyWSEventHandler());
            }
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            if (isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                utils.log("locationEvent:\n\tLatitude: " + loc.getLatitude() + "\n\tLongitude: " + loc.getLongitude()
                        + "\n\tProvider: " + loc.getProvider());
            }
        }

        public void onProviderDisabled(String provider) {
            utils.log("Gps Disabled");
        }


        public void onProviderEnabled(String provider) {
            utils.log("Gps Enabled");
        }


        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }

}
