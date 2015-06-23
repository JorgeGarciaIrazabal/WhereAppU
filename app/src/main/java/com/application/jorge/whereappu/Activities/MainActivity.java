package com.application.jorge.whereappu.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.application.jorge.whereappu.Classes.GCMFunctions;
import com.application.jorge.whereappu.Classes.MyWSEventHandler;
import com.application.jorge.whereappu.Classes.alert;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.WebSocket.WSHubsApi;

public class MainActivity extends AppCompatActivity {
    public Button pushButton, clearDataButton;
    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.context = MainActivity.this;
        try {
            App.wsHubsApi = new WSHubsApi("ws://192.168.1.3:8888/ws/12345", new MyWSEventHandler());
            App.GCMF = new GCMFunctions(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (App.getUserId() == 0 || User.getMySelf() == null) {
            Intent i = new Intent(MainActivity.this, LoggingActivity.class);
            MainActivity.this.startActivity(i);
        } else {
            Intent i = new Intent(MainActivity.this, TabsActivity.class);
            MainActivity.this.startActivity(i);
        }
        setUpUi();
        connectWsHubsApi();
    }

    private void connectWsHubsApi() {
        try {
            Thread t = new Thread() {
                public void run() {
                    try {
                        App.wsHubsApi.wsClient.connect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpUi() {
        pushButton = (Button) findViewById(R.id.sendButton);
        clearDataButton = (Button) findViewById(R.id.clearDataButton);
        clearDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.storeUserId(0);
                Log.i(TAG, "Owner Info Cleared");
                Intent i = new Intent(MainActivity.this, LoggingActivity.class);
                MainActivity.this.startActivity(i);
                alert.soft("Owner Info Cleared");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
