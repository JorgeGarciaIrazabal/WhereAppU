package com.application.jorge.whereappu.Classes;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.WebSocket.WSHubsEventHandler;
import com.application.jorge.whereappu.WebSocket.WSHubsApi;
import com.application.jorge.whereappu.WebSocket.WebSocketException;

import java.io.IOException;

public class MyWSEventHandler extends WSHubsEventHandler {
    Thread connectionThread = null;

    private Thread reconstructThread() {
        connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!App.wsHubsApi.wsClient.isConnected()) {
                    if (utils.isNetworkAvailable())
                        try {
                            App.wsHubsApi.wsClient.connect();
                        } catch (WebSocketException e) {
                            e.printStackTrace();
                        }
                    utils.delay(3000);
                }
            }
        });
        return connectionThread;
    }

    @Override
    public void onOpen() {
        if (connectionThread != null)
            connectionThread.interrupt();
    }

    @Override
    public void onError(IOException exception) {

    }

    @Override
    public void onClose() {
        reconstructThread().start();
    }
}
