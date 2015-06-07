package com.application.jorge.whereappu.Classes;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.TypedValue;
import android.view.Display;
import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorge on 30/05/2015.
 */
public class utils {
    private static long startTime = System.currentTimeMillis();

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
                = (ConnectivityManager) App.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static boolean isNetworkAvailable(boolean toast) {
        boolean ret = isNetworkAvailable();
        if (!ret && toast) alert.soft(App.getAppContext().getString(R.string.no_internet_connection_error_msg));
        return ret;
    }

    public static void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public static int getdp(int px) {
        return (int) (px * App.getAppContext().getResources().getDisplayMetrics().density);
    }

    public static int getmm(int mm) {
        return (int) (mm * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, App.getAppContext().getResources().getDisplayMetrics()));
    }

    public static void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public static long getTimer() {
        return System.currentTimeMillis() - startTime;
    }

    public static boolean createFile(String fi) {
        File file = new File(fi);
        File folder = new File(file.getParent());
        if (!folder.exists()) {
            try {
                alert.soft("Creando carpeta y archivo");
                folder.mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                alert.soft("ERROR CREANDO Carpeta");
                return false;
            }

        } else {
            if (!file.exists()) {
                try {
                    alert.soft("Creando archivo");
                    file.createNewFile();
                } catch (Exception e) {
                    alert.soft("ERROR CREANDO Archivo");
                    return false;
                }
            } else if (file.length() == 0) // file is empty
            {
                try {
                    alert.soft("Archivo vacio");
                } catch (Exception e) {
                    alert.soft("ERROR escribiendo Archivo");
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
            alert.soft("Archivo Creado");
            return true;
        } catch (IOException e) {// Catch exception if any
            alert.soft("Error: " + e.getMessage());
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
        ConnectivityManager connec = (ConnectivityManager) App.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connec.getAllNetworkInfo();
    }

    public static ArrayList<String> getStoredWifiNetworks() {
        WifiManager wifiMgr = (WifiManager) App.getAppContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiMgr.isWifiEnabled()) return null;
        ArrayList<String> list = new ArrayList<String>();
        List<WifiConfiguration> wifiList = wifiMgr.getConfiguredNetworks();
        for (WifiConfiguration wifi : wifiList)
            list.add(wifi.SSID.replace("\"", ""));
        return list;
    }

    public static ArrayList<String> getScanWifiNetworks() {
        WifiManager wifiMgr = (WifiManager) App.getAppContext().getSystemService(Context.WIFI_SERVICE);
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

    public static Uri getUri(int drawable) {
        Resources r = App.getAppContext().getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                r.getResourcePackageName(drawable) + '/' +
                r.getResourceTypeName(drawable) +
                '/' + r.getResourceEntryName(drawable));
    }

    public static Drawable getDrawable(Uri uri) {
        try {
            InputStream inputStream = App.getAppContext().getContentResolver().openInputStream(uri);
            return Drawable.createFromStream(inputStream, uri.toString());
        } catch (FileNotFoundException e) {
            return App.getAppContext().getResources().getDrawable(R.drawable.ic_launcher);
        }
    }

    public static Drawable getDrawable(int id){
        return App.getAppContext().getResources().getDrawable(id);
    }

    public static Drawable resize(Drawable drawable, int width, int height){
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        // Scale it to 50 x 50
        return new BitmapDrawable(App.getAppContext().getResources(), Bitmap.createScaledBitmap(bitmap, width, height, true));
    }

    public static Drawable resize(int  drawable, int width, int height){
        return resize(getDrawable(drawable), width, height);
    }

}
