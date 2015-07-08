package com.application.jorge.whereappu.Activities;
/**
 * Created by Jorge on 6/07/13.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.application.jorge.whereappu.DataBase.DataBaseManager;
import com.application.jorge.whereappu.Classes.GCMFunctions;
import com.application.jorge.whereappu.WebSocket.WSHubsApi;

import java.io.File;
import java.lang.reflect.Field;


public class App extends Application {
    public static final String TAG = "APP";
    public static final String hostName = "192.168.1.3:8888/";//"192.168.1.3:8888/";//"vps48278.vps.ovh.ca:8044/";
    public static Context activeActivity = null;
    public static DataBaseManager db;
    public static Context Activity;
    public static String AppFolder = Environment.getExternalStorageDirectory()
            + File.separator + "WAU";
    public static final String PROPERTY_USER_ID = "user_id";
    //project number = 238220266388
    public static GCMFunctions GCMF;
    public static WSHubsApi wsHubsApi;

    void App() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DataBaseManager(this);
    }

    public static void setContextIfNull(Context context) {
        if(App.activeActivity == null)
            App.activeActivity = context;
    }

    public static Context getAppContext() {
        return App.activeActivity;
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, final View progressView) {

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = App.getAppContext().getResources().getInteger(android.R.integer.config_longAnimTime);
            if (!show)
                progressView.setVisibility(View.GONE);
            else {
                progressView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        //Prevent touch event in progress
                        return true;
                    }
                });
                progressView.setVisibility(View.VISIBLE);
                progressView.animate()
                        .setDuration(shortAnimTime)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                            }
                        });
            }
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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

}

