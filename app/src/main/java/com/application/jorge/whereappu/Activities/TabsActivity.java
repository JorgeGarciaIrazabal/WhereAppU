package com.application.jorge.whereappu.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.application.jorge.whereappu.Classes.GCMFunctions;
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
import com.application.jorge.whereappu.Services.MessageService;
import com.application.jorge.whereappu.WebSocket.FunctionResult;
import com.application.jorge.whereappu.WebSocket.WSHubsApi;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuPopup;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TabsActivity extends AppCompatActivity {
    private final ViewPager.OnPageChangeListener pageChangeListener =
            new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {

                }
            };

    public FragmentManager fragmentManager;
    private static WSHubsApi.TaskHub taskHub;
    private static WSHubsApi.SyncHub syncHub;
    private static WSHubsApi.PlaceHub placesHub;
    private static boolean serviceBind = false;
    public TasksTab tasksTabFragment;
    public PlacesTab placesTabFragment;
    public ContactsTab contactsTabFragment;
    DroppyMenuPopup.Builder contextMenu;

    @InjectView(R.id.actionContextButton)
    ImageView actionContextButton;

    @InjectView(R.id.actionWsConnection)
    public ImageView actionWsConnection;

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        ButterKnife.inject(this);
        init();
        startService(new Intent(this, MessageService.class));
        NotificationHandler.cancelAll();
        try {
            App.GCMF = new GCMFunctions(this);
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }

        if (App.getUserId() == 0 || User.getMySelf() == null) {
            Intent i = new Intent(TabsActivity.this, LoggingActivity.class);
            TabsActivity.this.startActivity(i);
            finish();
            return;
        }
        contextMenu = new DroppyMenuPopup.Builder(this, actionContextButton);
        contextMenu.fromMenu(R.menu.menu_tabs)
                .setOnClick(new DroppyClickCallbackInterface() {
                    @Override
                    public void call(View v, int id) {
                        if (id == R.id.action_clear) {
                            App.storeUserId(0);
                            Intent i = new Intent(TabsActivity.this, LoggingActivity.class);
                            TabsActivity.this.startActivity(i);
                            alert.soft("OwnerId Info Cleared");
                            App.db.refreshDatabase();
                        } else if (id == R.id.action_sync) {
                            syncPhoneNumbers(TabsActivity.this);
                            syncTasks(TabsActivity.this, null);
                        } else if (id == R.id.action_refresh) {
                            Intent i = new Intent(TabsActivity.this, TabsActivity.class);
                            TabsActivity.this.startActivity(i);
                            finish();
                        }
                    }
                }).build();

        setUpSmartTabLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*int id = item.getItemId();
        if (id == R.id.action_clear) {
            App.storeUserId(0);
            Intent i = new Intent(TabsActivity.this, LoggingActivity.class);
            TabsActivity.this.startActivity(i);
            alert.soft("OwnerId Info Cleared");
            App.db.refreshDatabase();
        } else if (id == R.id.action_sync) {
            syncPhoneNumbers(TabsActivity.this);
            syncTasks(TabsActivity.this, null);
        } else if (id == R.id.action_refresh) {
            Intent i = new Intent(TabsActivity.this, TabsActivity.class);
            TabsActivity.this.startActivity(i);
            finish();
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment.getClass().equals(TasksTab.class))
            this.tasksTabFragment = (TasksTab) fragment;
        else if (fragment.getClass().equals(PlacesTab.class))
            this.placesTabFragment = (PlacesTab) fragment;
        else if (fragment.getClass().equals(ContactsTab.class))
            this.contactsTabFragment = (ContactsTab) fragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(mConnection);
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    private void setUpSmartTabLayout() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);


        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of("Contacts", ContactsTab.class));
        pages.add(FragmentPagerItem.of("Tasks", TasksTab.class));
        pages.add(FragmentPagerItem.of("Places", PlacesTab.class));

        fragmentManager = getSupportFragmentManager();
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(fragmentManager, pages);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        viewPagerTab.setOnPageChangeListener(pageChangeListener);
        viewPagerTab.setViewPager(viewPager);
    }

    private void startHubsConnection() throws URISyntaxException {
        syncContactsFromPhoneBook();
    }

    public static void syncPhoneNumbers(final Activity activity) {
        try {
            final LoadToast lt = alert.load("syncing contacts");
            syncHub.server.phoneNumbers(PhoneContact.GetAllPhones()).done(new FunctionResult.Handler() {

                public void onSuccess(final Object input) {
                    JSONArray array = (JSONArray) input;
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            User user = User.getFromJson(array.getJSONObject(i));
                            user.syncFromPhoneBook();
                            user.write();
                        } catch (Exception e) {
                            utils.saveExceptionInFolder(e);
                        }
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lt.success();
                        }
                    });
                }

                public void onError(final Object input) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lt.error();
                        }
                    });
                }
            });

        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    public static void syncTasks(final Activity activity, final Runnable runnable) {
        try {
            if (runnable != null)
                runnable.run();
            if (utils.isNetworkAvailable() && App.wsHubsApi.wsClient.isConnected()) {
                List<Task> tasks = Task.getNotUpdatedRows(Task.class);
                for (final Task t : tasks) {
                    final LoadToast lToast = alert.load("uploading task to server...");
                    try {
                        taskHub.server.addTask(t).done(new FunctionResult.Handler() {
                            public void onSuccess(Object id) {
                                try {
                                    Task toUpdateTask = Task.getById(t.ID);
                                    toUpdateTask.update(utils.getLong(id));

                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (runnable != null)
                                                runnable.run();
                                            lToast.success();
                                        }
                                    });
                                } catch (Exception e) {
                                    utils.saveExceptionInFolder(e);
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
                        utils.saveExceptionInFolder(e);
                    }
                }
            }
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    public static void syncPlaces(final Activity activity) {
        try {
            if (utils.isNetworkAvailable() && App.wsHubsApi.wsClient.isConnected()) {
                List<Place> places = Place.getNotUpdatedRows(Place.class);
                for (final Place p : places) {
                    final LoadToast lToast = alert.load("uploading place to server...");
                    try {
                        placesHub.server.syncPlace(p).done(new FunctionResult.Handler() {
                            public void onSuccess(Object id) {
                                try {
                                    Place toUpdatePlace = Place.getById(p.ID);
                                    toUpdatePlace.update(utils.getLong(id));
                                } catch (Exception e) {
                                    utils.saveExceptionInFolder(e);
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
                        utils.saveExceptionInFolder(e);
                    }
                }
            }
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    public static void downloadPlaces(final Activity activity) {
        if (User.getMySelf() == null) return;
        try {
            if (utils.isNetworkAvailable() && App.wsHubsApi.wsClient.isConnected()) {
                final LoadToast getToast = alert.load("getting places from server");
                List<User> users = User.getAll(User.class);
                for (User user : users) {
                    placesHub.server.getPlaces(user.ID).done(new FunctionResult.Handler() {
                        @Override
                        public void onSuccess(Object places) {
                            JSONArray placesArray = (JSONArray) places;
                            for (int i = 0; i < placesArray.length(); i++) {
                                try {
                                    if (Place.canBeUpdated(Place.class, placesArray.getJSONObject(i).getInt("ID"))) {
                                        Place place = Place.getFromJson(placesArray.getJSONObject(i));
                                        place.__Updated = 1;
                                        place.save();
                                    }
                                } catch (Exception e) {
                                    utils.saveExceptionInFolder(e);
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
                }
            }
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    public static void syncContactsFromPhoneBook() {
        try {
            List<User> users = User.getAll(User.class);
            for (User user : users) {
                user.syncFromPhoneBook();
                user.save();
            }
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    public ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            if (App.wsHubsApi == null) {
                try {
                    MessageService.MyBinder b = (MessageService.MyBinder) binder;
                    App.messageService = b.getService();
                    App.wsHubsApi = App.messageService.wsHubsApi;
                    syncHub = App.wsHubsApi.SyncHub;
                    taskHub = App.wsHubsApi.TaskHub;
                    placesHub = App.wsHubsApi.PlaceHub;
                    if(App.wsHubsApi.isConnected())
                        actionWsConnection.setImageResource(android.R.drawable.presence_online);
                    else
                        actionWsConnection.setImageResource(android.R.drawable.presence_busy);
                    startHubsConnection();
                } catch (URISyntaxException e) {
                    utils.saveExceptionInFolder(e);
                }
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            App.messageService = null;
        }

    };

    private void init() {
        App.activeActivity = TabsActivity.this;
        if (!serviceBind && App.messageService == null) {
            serviceBind = true;
            Intent intent = new Intent(this, MessageService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }
}
