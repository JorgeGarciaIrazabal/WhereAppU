package com.application.jorge.whereappu.Activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.application.jorge.whereappu.Classes.DBManager;
import com.application.jorge.whereappu.Classes.GCMFunctions;
import com.application.jorge.whereappu.Classes.PhoneContact;
import com.application.jorge.whereappu.Classes.QueryTable;
import com.application.jorge.whereappu.Connections.ServerComm;
import com.application.jorge.whereappu.R;
import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends ActionBarActivity {

    //project number = 238220266388
    public static DBManager db;
    public static GCMFunctions GCMF;
    public static ServerComm serverComm;
    public Button pushButton, clearDataButton;
    AtomicInteger msgId = new AtomicInteger();

    public static int userId;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String TAG = "HelloWorld";
        // create a manager
        try {
            App.context = this;
            GCMF = new GCMFunctions(this);
            db = new DBManager(MainActivity.this);
            serverComm = new ServerComm(this);
            if (App.getUserId() == 0) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(i);
                finish();
            }
            setUpUi();
            getPhoneNumbers();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpUi() {
        pushButton = (Button) findViewById(R.id.sendButton);
        clearDataButton = (Button) findViewById(R.id.clearDataButton);
        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String msg = "";
                        try {
                            Bundle data = new Bundle();
                            data.putString("my_message", "Hello World");
                            data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
                            String id = Integer.toString(msgId.incrementAndGet());
                            GCMF.getGcm(MainActivity.this).send(GCMFunctions.SENDER_ID + "@gcm.googleapis.com", id, data);
                            msg = "Sent message";
                        } catch (IOException ex) {
                            msg = "Error :" + ex.getMessage();
                        }
                        return msg;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        Log.i("GCM", msg);
                    }
                }.execute(null, null, null);
            }
        });
        clearDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.storeUserId(0);
                MainActivity.db.clearDataBase();
                Log.i(TAG, "User Info Cleared");
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                MainActivity.this.startActivity(i);
                finish();
                App.softAlert("User Info Cleared");
            }
        });
    }

    public void getPhoneNumbers() throws Exception {
        Thread t = new Thread() {
            public void run() {
                try {
                    final String result = MainActivity.serverComm.postComm("getRelatedContacts", App.join(PhoneContact.GetAllPhones(), ","));
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                QueryTable qt = new QueryTable(result);
                                pushButton.setText(result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
