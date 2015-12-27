package com.application.jorge.whereappu.Activities;
/**
 * Created by Jorge on 6/07/13.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.application.jorge.whereappu.Classes.DateTimeFormater;
import com.application.jorge.whereappu.Classes.GCMFunctions;
import com.application.jorge.whereappu.DataBase.DataBaseManager;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.Services.MessageService;
import com.application.jorge.whereappu.WebSocket.WSHubsApi;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;



public class App extends Application {
    public static final String TAG = "APP";
//    public static final String hostName = "192.168.1.3:8844/";
    public static final String hostName = "vps48278.vps.ovh.ca:8844/";
    public static Context activeActivity = null;
    public static DataBaseManager db;
    public static String AppFolder = Environment.getExternalStorageDirectory()
            + File.separator + "WAU";
    public static final String PROPERTY_USER_ID = "user_id";
    //project number = 238220266388
    public static GCMFunctions GCMF;
    public static WSHubsApi wsHubsApi = null;
    public static MessageService messageService;
    private static boolean activityVisible;

    void App() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DataBaseManager(this);
    }

    public static void setContextIfNull(Context context) {
        if (App.activeActivity == null)
            App.activeActivity = context;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public static Context getAppContext() {
        return App.activeActivity;
    }

    public static boolean isAppRunning(){
        return App.activeActivity instanceof Activity;
    }

    public static Activity getAppActivity() {
        return (android.app.Activity) App.activeActivity;
    }

    public static int getResId(String variableName, Class<?> c) throws Exception {
        Field idField = c.getDeclaredField(variableName);
        return idField.getInt(idField);
    }

    public static Drawable getDrawable(String path) {
        File imgFile = new File(path);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return new BitmapDrawable(activeActivity.getResources(), bitmap);
        }
        return null;
    }

    public static long getUserId() {
        final SharedPreferences prefs = getAppContext().getSharedPreferences(TabsActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        long userId = prefs.getLong(PROPERTY_USER_ID, 0);
        if (userId == 0) {
            Log.i(TAG, "UserId not found.");
            return 0;
        }
        return userId;
    }

    public static void storeUserId(long userId) {
        final SharedPreferences prefs = getAppContext().getSharedPreferences(TabsActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        Log.i(TAG, "Saving myUserId");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PROPERTY_USER_ID, userId);
        editor.apply();
    }

    public static String getLastSyncDate(String tableName) {
        final SharedPreferences prefs = getAppContext().getSharedPreferences(TabsActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        return prefs.getString(tableName, DateTimeFormater.toFullDateTime(User.getMySelf().CreatedOn));
    }

    public static void storeLastSyncDate(String tableName) {
        final SharedPreferences prefs = getAppContext().getSharedPreferences(TabsActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(tableName, DateTimeFormater.toFullDateTime(new Date()));
        editor.apply();
    }

}

