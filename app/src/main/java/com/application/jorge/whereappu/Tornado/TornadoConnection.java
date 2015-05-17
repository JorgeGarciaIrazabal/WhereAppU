package com.application.jorge.whereappu.Tornado;

import android.app.Activity;
import android.util.Log;
import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.weberknecht.WebSocket;
import com.application.jorge.whereappu.weberknecht.WebSocketEventHandler;
import com.application.jorge.whereappu.weberknecht.WebSocketMessage;
import de.greenrobot.event.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

public class TornadoConnection extends WebSocket {
    private EventBus bus = EventBus.getDefault();
    public static final String TAG = "Websocket";
    public Activity mainActivity;
    public TornadoConnection(String uri) throws URISyntaxException {
        super(new URI(uri));
        this.setEventHandler(new WebSocketEventHandler() {
            @Override
            public void onOpen() {
                Log.i(TAG, "Opened");
            }

            @Override
            public void onMessage(WebSocketMessage message) {
                try {
                    JSONObject msgObj = new JSONObject(message.getText());
                    Class<?> c = Class.forName(TornadoClient.class.getCanonicalName()+ "$" + msgObj.getString("hub"));
                    Method[] methods = c.getDeclaredMethods();
                    String functionName = msgObj.getString("function").toUpperCase();
                    for (Method m : methods) {

                        if (m.getName().toUpperCase().equals(functionName)) {
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    App.softAlert("test");
                                }
                            });
                            return;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error " + e.getMessage());
                }
            }

            @Override
            public void onError(IOException ex) {
                Log.e(TAG, "Error " + ex.getMessage());
            }

            @Override
            public void onClose() {
                Log.i(TAG, "Closed ");
            }

            @Override
            public void onPing() {
            }

            @Override
            public void onPong() {
            }
        });
    }
}