package com.application.jorge.whereappu.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.application.jorge.whereappu.Classes.DBManager;
import com.application.jorge.whereappu.Classes.GCMFunctions;
import com.application.jorge.whereappu.Connections.IRetroFit;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.Tornado.TornadoServer;
import de.greenrobot.event.EventBus;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends Activity {

    //project number = 238220266388
    public static DBManager db;
    public static GCMFunctions GCMF;
    public static IRetroFit serverComm;
    public Button pushButton, clearDataButton;
    AtomicInteger msgId = new AtomicInteger();
    private EventBus bus = EventBus.getDefault();

    public static long myUserId;
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String TAG = "HelloWorld";
        bus.register(this);
        // create a manager
        try {
            App.context = this;
            GCMF = new GCMFunctions(this);
            //ActiveAndroid.initialize(this);


            Thread t = new Thread() {
                public void run() {
                    try {
                        TornadoServer.init("ws://192.168.1.3:8888/ws/12345", MainActivity.this);
                        TornadoServer.TestClass.tast(5,6,7);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();

            /*User u = new User("jorge", "jorge.girazabal@gmail.com", "+34653961314", GCMF.getRegId());
            u.save();
            User u2 = new Select().from(User.class).where("nick like '%org%'").executeSingle();

            db = new DBManager();
            RestAdapter ra = new RestAdapter.Builder().setEndpoint(IRetroFit.SERVER_HOST).build();
            serverComm = ra.create(IRetroFit.class);
            serverComm.getData("login", "testingData", new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.d(TAG, s + "\t" + response.toString());
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, error.toString());
                }
            });
            if (App.getUserId() == 0) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(i);
                finish();
            }
            setUpUi();*/
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
                    /*final String result = MainActivity.serverComm.postComm("getRelatedContacts", App.join(PhoneContact.GetAllPhones(), ","));
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
                    });*/
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

    public void  onEvent(String test){
        App.softAlert(test);
    }
}
