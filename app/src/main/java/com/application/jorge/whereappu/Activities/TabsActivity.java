package com.application.jorge.whereappu.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.application.jorge.whereappu.Classes.GCMFunctions;
import com.application.jorge.whereappu.Classes.MyWSEventHandler;
import com.application.jorge.whereappu.Classes.NotificationHandler;
import com.application.jorge.whereappu.Classes.PhoneContact;
import com.application.jorge.whereappu.Classes.alert;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Place;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.Fragments.ContactsTab;
import com.application.jorge.whereappu.Fragments.PlacesTab;
import com.application.jorge.whereappu.Fragments.TasksTab;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.WebSocket.FunctionResult;
import com.application.jorge.whereappu.WebSocket.WSHubsApi;
import com.application.jorge.whereappu.WebSocket.WebSocketException;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.List;

public class TabsActivity extends AppCompatActivity {
    private final ViewPager.OnPageChangeListener pageChangeListener =
            new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {

                }
            };
    private static WSHubsApi.TaskHub taskHub;
    private static WSHubsApi.SyncHub syncHub;
    private static WSHubsApi.PlaceHub placesHub;


    @Override
    protected void onResume() {
        super.onResume();
        App.context = TabsActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        App.context = TabsActivity.this;
        NotificationHandler.cancelAll();
        try {
            startHubsConnection();
            syncHub = App.wsHubsApi.SyncHub;
            taskHub = App.wsHubsApi.TaskHub;
            placesHub = App.wsHubsApi.PlaceHub;
            App.GCMF = new GCMFunctions(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (App.getUserId() == 0 || User.getMySelf() == null) {
            Intent i = new Intent(TabsActivity.this, LoggingActivity.class);
            TabsActivity.this.startActivity(i);
            finish();
            return;
        }
        setUpSmartTabLayout();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clear) {
            App.storeUserId(0);
            Intent i = new Intent(TabsActivity.this, LoggingActivity.class);
            TabsActivity.this.startActivity(i);
            alert.soft("Owner Info Cleared");
        } else if (id == R.id.action_sync) {
            syncPhoneNumbers();
            syncTasks(TabsActivity.this);
        } else if (id == R.id.action_refresh) {
            Intent i = new Intent(TabsActivity.this, TabsActivity.class);
            TabsActivity.this.startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setUpSmartTabLayout() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);


        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of("Contacts", ContactsTab.class));
        pages.add(FragmentPagerItem.of("Tasks", TasksTab.class));
        pages.add(FragmentPagerItem.of("PlaceHub", PlacesTab.class));


        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        viewPagerTab.setOnPageChangeListener(pageChangeListener);
        viewPagerTab.setViewPager(viewPager);
    }

    private void startHubsConnection() throws URISyntaxException {
        final String ID = User.getMySelf() == null ? "" : String.valueOf(User.getMySelf().ID);
        App.wsHubsApi = new WSHubsApi("ws://192.168.1.3:8888/" + ID, new MyWSEventHandler());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!App.wsHubsApi.wsClient.isConnected()) {
                    if (utils.isNetworkAvailable())
                        try {
                            App.wsHubsApi.wsClient.connect();
                            TabsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    downloadPlaces(TabsActivity.this);
                                }
                            });
                        } catch (WebSocketException e) {
                            e.printStackTrace();
                        }
                    utils.delay(3000);
                }
            }
        }).start();
    }

    public void syncPhoneNumbers() {
        try {
            final LoadToast lt = alert.load("syncing contacts");
            syncHub.server.phoneNumbers(PhoneContact.GetAllPhones()).done(new FunctionResult.Handler() {

                public void onSuccess(final Object input) {
                    JSONArray array = (JSONArray) input;
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            User user = User.getFromJson(array.getJSONObject(i));
                            PhoneContact pc = PhoneContact.GetContact(user.PhoneNumber);
                            if (pc != null && pc.getPhoto() != null)
                                user.PhotoURL = pc.getPhoto().toString();
                            user.write();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    TabsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lt.success();
                        }
                    });
                }

                public void onError(final Object input) {
                    TabsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lt.error();
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void syncTasks(final Activity activity) {
        try {
            List<Task> tasks = Task.getUpdatedRows(Task.class);
            for (final Task t : tasks) {
                final LoadToast lToast = alert.load("uploading task to server...");
                try {
                    taskHub.server.addTask(t).done(new FunctionResult.Handler() {
                        public void onSuccess(Object id) {
                            try {
                                Task toUpdateTask = Task.load(Task.class, t.getId());
                                toUpdateTask.update((int) id);

                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        lToast.success();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        public void onError(final Object input) {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    lToast.error();
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void syncPlaces(final Activity activity) {
        try {
            List<Place> places = Place.getUpdatedRows(Place.class);
            for (final Place p : places) {
                final LoadToast lToast = alert.load("uploading place to server...");
                try {
                    placesHub.server.syncPlace(p).done(new FunctionResult.Handler() {
                        public void onSuccess(Object id) {
                            try {
                                Place toUpdatePlace = Place.load(Place.class, p.getId());
                                toUpdatePlace.update((int) id);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    lToast.success();
                                }
                            });
                        }

                        public void onError(final Object input) {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    lToast.error();
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    lToast.error();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadPlaces(final Activity activity) {
        if (User.getMySelf() == null) return;
        try {
            final LoadToast getToast = alert.load("getting placces from server");
            placesHub.server.getPlaces(User.getMySelf().ID).done(new FunctionResult.Handler() {
                @Override
                public void onSuccess(Object places) {
                    JSONArray placesArray = (JSONArray) places;
                    for (int i = 0; i < placesArray.length(); i++) {
                        try {
                            if (Place.canBeUpdated(Place.class, placesArray.getJSONObject(i).getInt("ID"))) {
                                Place place = Place.getFromJson(placesArray.getJSONObject(i));
                                place.update(place.ID);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            getToast.success();
                        }
                    });
                }

                @Override
                public void onError(Object input) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            getToast.error();
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
