package com.application.jorge.whereappu.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Classes.MyWSEventHandler;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.WebSocket.WSHubsApi;
import com.google.android.gms.location.Geofence;

public class MessageService extends Service {
    private static final String LOG_TAG = "MessageService";
    public WSHubsApi wsHubsApi;
    private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        public MessageService getService() {
            return MessageService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        utils.log("Creating messageService");
        GeofenceModel mestalla = new GeofenceModel.Builder("id_mestalla")
                .setTransition(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setLatitude(39.47453120000001)
                .setLongitude(-0.358065799999963)
                .setRadius(500)
                .build();

        GeofenceModel cuenca = new GeofenceModel.Builder("id_cuenca")
                .setTransition(Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLatitude(40.0703925)
                .setLongitude(-2.1374161999999615)
                .setRadius(2000)
                .build();

        SmartLocation.with(context).geofencing()
                .add(mestalla)
                .add(cuenca)
                .remove("already_existing_geofence_id")
                .start(new OnGeofencingTransitionListener() {
                    ...
                });
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
}
