package com.application.jorge.whereappu.Classes;

import android.app.Activity;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Activities.TabsActivity;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.WebSocket.WSHubsEventHandler;
import com.application.jorge.whereappu.WebSocket.WSHubsApi;
import com.application.jorge.whereappu.WebSocket.WebSocketException;

import org.json.JSONException;

import java.io.IOException;

public class MyWSEventHandler extends WSHubsEventHandler {
    Thread connectionThread = null;

    private void reconstructThread() {
        connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!wsHubsApi.wsClient.isConnected()) {
                    if (utils.isNetworkAvailable())
                        try {
                            wsHubsApi.wsClient.connect();
                        } catch (WebSocketException e) {
                            utils.saveExceptionInFolder(e);
                        }
                    utils.delay(3000);
                }
            }
        });
        connectionThread.start();
    }
    @Override
    public void setWsHubsApi(WSHubsApi wsHubsApi) {
        super.setWsHubsApi(wsHubsApi);
        reconstructThread();
    }

    @Override
    public void onOpen() {
        try {
            if (connectionThread != null)
                connectionThread.interrupt();
            if(User.getMySelf()!= null)
                wsHubsApi.UtilsHub.server.setID(User.getMySelf().ID);
            if(App.isAppRunning()){
                App.getAppActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Activity appActivity = App.getAppActivity();
                        TabsActivity.downloadPlaces(appActivity);
                        TabsActivity.syncPhoneNumbers(appActivity);
                        TabsActivity.syncTasks(appActivity, null);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(IOException exception) {

    }

    @Override
    public void onClose() {
        reconstructThread();
    }
}
