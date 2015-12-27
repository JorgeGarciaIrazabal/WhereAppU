package com.application.jorge.whereappu.Services;

import android.app.PendingIntent;
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

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Activities.TabsActivity;
import com.application.jorge.whereappu.Classes.MyWSEventHandler;
import com.application.jorge.whereappu.Classes.NotificationHandler;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Place;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.WebSocket.WSHubsApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MessageService extends Service implements GoogleApiClient.ConnectionCallbacks {
    private static final String LOG_TAG = "MessageService";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public WSHubsApi wsHubsApi;
    private final IBinder binder = new MyBinder();
    private Location previousBestLocation = null;
    private ArrayList<Geofence> geofenceList = new ArrayList<>();
    private GoogleApiClient apiClient;
    private PendingIntent geofenceRequestIntent;

    @Override
    public void onConnected(Bundle bundle) {
        // Get the PendingIntent for the geofence monitoring request.
        // Send a request to add the current geofences.
        geofenceRequestIntent = getGeofenceTransitionPendingIntent();
        LocationServices.GeofencingApi.addGeofences(apiClient, geofenceList, geofenceRequestIntent);

        getGeoFencingRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (null != geofenceRequestIntent) {
            LocationServices.GeofencingApi.removeGeofences(apiClient, geofenceRequestIntent);
        }
    }


    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public class MyBinder extends Binder {
        public MessageService getService() {
            return MessageService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            MyLocationListener locationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1200000, 200, locationListener);

            collectGeofences();

            apiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            int errorCode = connectionResult.getErrorCode();
                            utils.log("Connection to Google Play services failed with error code " + errorCode);
                        }
                    })
                    .build();
            apiClient.connect();
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    private void collectGeofences() throws Exception {
        geofenceList.clear();
        List<Place> places = Place.getMyActivePlaces();
        for (Place place : places) {
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(String.valueOf(place.ID))
                    .setCircularRegion(
                            place.Latitude,
                            place.Longitude,
                            place.Range
                    )
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build());
        }
    }

    public void recalculateGeofences() throws Exception {
        LocationServices.GeofencingApi.removeGeofences(apiClient, geofenceRequestIntent);
        collectGeofences();
        LocationServices.GeofencingApi.addGeofences(apiClient, geofenceList, geofenceRequestIntent);
        Location location = utils.getLocation();
        location = location == null ? previousBestLocation : location;
        calculateActivePlace(location, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            App.setContextIfNull(this);
            if (wsHubsApi == null) {
                String ID = User.getMySelf() == null ? "" : String.valueOf(User.getMySelf().ID);
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
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    private GeofencingRequest getGeoFencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    public static void calculateActivePlace(Location location, Context context) {
        try {
            List<Place> places = Place.getMyActivePlaces();
            boolean placeFound = false;
            for (Place place : places) {
                float distance = place.getLocation().distanceTo(location);
                if (distance < place.Range) {
                    Place.activePlace = place;
                    NotificationHandler.showNotification(context);
                    utils.log("We are now in: " + place.Name);
                    placeFound = true;
                    break;
                }
            }
            if (!placeFound) {
                Place.activePlace = null;
                utils.log("We are in no place");
            }
            if (App.isAppRunning() && App.activeActivity.getClass().equals(TabsActivity.class)) {
                App.getAppActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TabsActivity) App.activeActivity).setActivePlaceIcon();
                    }
                });
            }
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    public class MyLocationListener implements LocationListener {
        private int cont = 0;

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

        private boolean isSameProvider(String provider1, String provider2) {
            if (provider1 == null) {
                return provider2 == null;
            }
            return provider1.equals(provider2);
        }

        public void onLocationChanged(final Location loc) {
            utils.log(cont++);
            if (isBetterLocation(loc, previousBestLocation)) {
                previousBestLocation = loc;
                calculateActivePlace(previousBestLocation, MessageService.this);
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

    private boolean checkGooglePlayServices() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        }
        return false;
    }

}
