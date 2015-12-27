package com.application.jorge.whereappu.Classes;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Jorge on 30/05/2015.
 */
public class utils {

    public static <T> String join(ArrayList<T> r, String d) {
        if (r.size() == 0) return "";
        if (r.size() == 1) return r.get(0).toString();
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < r.size() - 1; i++)
            sb.append(r.get(i) + d);
        return sb.toString() + r.get(i);
    }

    public static <T> String join(T[] r, String d) {
        List<T> assetList = Arrays.asList(r);
        return join(new ArrayList<>(assetList), d);

    }

    public static void getScreenSize(Display display, Point outSize) throws Exception {
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
        if (!ret && toast)
            alert.soft(App.getAppContext().getString(R.string.no_internet_connection_error_msg));
        return ret;
    }

    public static void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public static boolean createFile(String fi) throws IOException {
        File file = new File(fi);
        File folder = new File(file.getParent());
        if (!folder.exists()) {
            folder.mkdirs();
            file.createNewFile();
        } else {
            if (!file.exists()) {
                file.createNewFile();
            } else if (file.length() > 0) // file is empty
                return false; // file all ready exist, nothing to do
        }
        return true;
    }

    public static boolean writeFile(String fi, String text) {
        try {
            // Create file
            FileWriter fstream = new FileWriter(fi, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(text);
            // Close the output stream
            out.close();
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

    public static float getDistanceFromCoordenates(LatLng latLng1, LatLng latLng2) {
        Location location1 = new Location("");
        location1.setLongitude(latLng1.longitude);
        location1.setLatitude(latLng1.latitude);
        Location location2 = new Location("");
        location2.setLongitude(latLng2.longitude);
        location2.setLatitude(latLng2.latitude);
        return location1.distanceTo(location2);
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

    public static Drawable getDrawable(int id) {
        return App.getAppContext().getResources().getDrawable(id);
    }

    public static Bitmap getBitmap(int id) {
        return BitmapFactory.decodeResource(App.getAppContext().getResources(), id);
    }

    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static int getPx(int dp){
        Resources r = App.getAppContext().getResources();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static Drawable resize(Drawable drawable, int width, int height) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        // Scale it to 50 x 50
        return new BitmapDrawable(App.getAppContext().getResources(), Bitmap.createScaledBitmap(bitmap, width, height, true));
    }

    public static Drawable resize(int drawable, int width, int height) {
        return resize(getDrawable(drawable), width, height);
    }

    public static void saveExceptionInFolder(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        e.printStackTrace();
        writeFile(App.AppFolder + "/logE.txt", DateTimeFormater.toDateTime(new Date()) + ":\n" + sw.toString());
    }

    public static void log(Object message) {
        String totalMessage = DateTimeFormater.toDateTime(new Date()) + ":\n" + String.valueOf(message) + "\n";
        utils.writeFile(App.AppFolder + "/log.txt", totalMessage);
        Log.i("WAU_Utils_log", totalMessage);
    }

    public static long getLong(Object o) {
        if (o.getClass().equals(Long.class))
            return (Long) o;
        else
            return ((Integer) o).longValue();
    }

    public static String[] getStringArrays(Object[] objectArray) {
        ArrayList<Object> objectList = new ArrayList<>(Arrays.asList(objectArray));
        String[] stringArray = new String[objectList.size()];
        for (int i = 0; i < objectList.size(); i++) {
            Object o = objectList.get(i);
            if (o.getClass().equals(Long.class))
                stringArray[i] = String.valueOf(getLong(o));
            else if (o.getClass().equals(Date.class))
                stringArray[i] = DateTimeFormater.toFullDateTime((Date) o);
            else
                stringArray[i] = String.valueOf(o);
        }
        return stringArray;
    }

}
