package com.application.jorge.whereappu.Activities;
/**
 * Created by Jorge on 6/07/13.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.application.jorge.whereappu.R;


public class App extends Application {
    public static final String TAG = "APP";
    public static Context context;
    private static long startTime = System.currentTimeMillis();
    public static Context Activity;
    public static String AppFolder = Environment.getExternalStorageDirectory()
            + File.separator + "WAU";
    public static final String PROPERTY_USER_ID = "user_id";


    void App() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static void softAlert(String ms, int time) {
        if (time == 0)
            Toast.makeText(App.getAppContext(), ms, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(App.getAppContext(), ms, Toast.LENGTH_LONG).show();
    }

    public static void softAlert(String ms) {
        softAlert(ms, Toast.LENGTH_SHORT);
    }

    public static void softAlert(int ms) {
        softAlert(String.valueOf(ms));
    }

    public static void softAlert(float ms) {
        softAlert(String.valueOf(ms));
    }

    public static void softAlert(ArrayList<String> mss) {
        softAlert(App.join(mss, ", "));
    }

    public static void DEBUG(int ms) {
        softAlert("" + ms, Toast.LENGTH_SHORT);
    }

    public static boolean createFile(String fi) {
        File file = new File(fi);
        File folder = new File(file.getParent());
        if (!folder.exists()) {
            try {
                App.softAlert("Creando carpeta y archivo");
                folder.mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                App.softAlert("ERROR CREANDO Carpeta");
                return false;
            }

        } else {
            if (!file.exists()) {
                try {
                    App.softAlert("Creando archivo");
                    file.createNewFile();
                } catch (Exception e) {
                    App.softAlert("ERROR CREANDO Archivo");
                    return false;
                }
            } else if (file.length() == 0) // file is empty
            {
                try {
                    App.softAlert("Archivo vacio");
                } catch (Exception e) {
                    App.softAlert("ERROR escribiendo Archivo");
                    return false;
                }
            } else
                return false; // file all ready exist, nothing to do
        }
        return true;
    }

    public static boolean writeFile(String fi, String text) {
        try {
            // Create file
            FileWriter fstream = new FileWriter(fi);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(text);
            // Close the output stream
            out.close();
            App.softAlert("Archivo Creado");
            return true;
        } catch (IOException e) {// Catch exception if any
            App.softAlert("Error: " + e.getMessage());
            return false;
        }
    }

    public static ArrayList<String> filesInFolder(String file) {
        File dir = new File(file);
        ArrayList<String> ret = new ArrayList<String>();
        if (dir.exists()) {
            FileFilter fileFilter = new FileFilter() {
                public boolean accept(File file) {
                    return !file.isDirectory();
                }
            };
            File[] files = dir.listFiles(fileFilter);
            for (int i = 0; i < files.length; i++)
                ret.add(files[i].getName());
        }
        return ret;
    }

    public static NetworkInfo[] getAllNetworkInfo() {
        ConnectivityManager connec = (ConnectivityManager) getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connec.getAllNetworkInfo();
    }

    public static ArrayList<String> getStoredWifiNetworks() {
        WifiManager wifiMgr = (WifiManager) getAppContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiMgr.isWifiEnabled()) return null;
        ArrayList<String> list = new ArrayList<String>();
        List<WifiConfiguration> wifiList = wifiMgr.getConfiguredNetworks();
        for (WifiConfiguration wifi : wifiList)
            list.add(wifi.SSID.replace("\"", ""));
        return list;
    }

    public static ArrayList<String> getScanWifiNetworks() {
        WifiManager wifiMgr = (WifiManager) getAppContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiMgr.isWifiEnabled()) return null;
        ArrayList<String> list = new ArrayList<String>();
        List<ScanResult> wifiList = wifiMgr.getScanResults();
        for (ScanResult wifi : wifiList)
            list.add(wifi.SSID.replace("\"", ""));
        return list;
    }

    public static Location getLocation() {
        LocationManager locationManager = (LocationManager) App.getAppContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return location;
    }

    public static void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public static int getdp(int px) {
        return (int) (px * context.getResources().getDisplayMetrics().density);
    }

    public static int getmm(int mm) {
        return (int) (mm * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, context.getResources().getDisplayMetrics()));
    }

    public static void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public static long getTimer() {
        return System.currentTimeMillis() - startTime;
    }

    public static void alert(Context c, String text) {
        //TODO reimplement alert function in App
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setTitle("alert");
        alertDialogBuilder.setMessage(text);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void alert(Context c, ArrayList<String> text) {
        alert(c, App.join(text, ", "));
    }

    public static void alert(String text) {
        alert(Activity, text);
    }

    public static void getScreenSize(Display display, Point outSize) {
        try {
            // test for new method to trigger exception
            Class pointClass = Class.forName("android.graphics.Point");
            Method newGetSize = Display.class.getMethod("getSize", new Class[]{pointClass});

            // no exception, so new method is available, just use it
            newGetSize.invoke(display, outSize);
        } catch (NoSuchMethodException ex) {
            // new method is not available, use the old ones
            outSize.x = display.getWidth();
            outSize.y = display.getHeight();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static boolean isNetworkAvailable(boolean toast) {
        boolean ret = isNetworkAvailable();
        if (!ret && toast) App.softAlert(App.getAppContext().getString(R.string.no_internet_connection_error_msg));
        return ret;
    }

    public static int getResId(String variableName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Drawable getDrawable(String path) {
        File imgFile = new File(path);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return new BitmapDrawable(context.getResources(), bitmap);
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

    public static <T> String join(ArrayList<T> r, String d) {
        if (r.size() == 0) return "";
        if (r.size() == 1) return r.get(0).toString();
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < r.size() - 1; i++)
            sb.append(r.get(i) + d);
        return sb.toString() + r.get(i);
    }

    public static JSONArray parseJSONArray(String stringJSON) {
        try {
            return (JSONArray) new JSONTokener(stringJSON).nextValue();
        } catch (JSONException e) {
            return null;
        }
    }

    public static int getUserId() {
        final SharedPreferences prefs = getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        int userId = prefs.getInt(PROPERTY_USER_ID, 0);
        if (userId == 0) {
            Log.i(TAG, "UserId not found.");
            return 0;
        }
        return userId;
    }

    public static void storeUserId(long userId) {
        final SharedPreferences prefs = getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        Log.i(TAG, "Saving myUserId");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PROPERTY_USER_ID, userId);
        editor.commit();
    }

}

