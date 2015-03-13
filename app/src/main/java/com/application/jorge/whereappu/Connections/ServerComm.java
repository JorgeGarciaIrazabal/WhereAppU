package com.application.jorge.whereappu.Connections;

import android.content.Context;
import android.util.Log;
import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.R;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 10/03/15.
 */
public class ServerComm {
    public interface CallBack {
        public void execute(String input);
    }

    private static final String TAG = "SERVER_COM";
    private static HttpClient client = new DefaultHttpClient();
    private static HttpPost httppost;
    public static String SERVER_HOST = "http://192.168.1.139:8080/";
    public static String postQuery;

    public ServerComm(Context context) {
        postQuery = context.getString(R.string.post_query);
    }

    public boolean DownloadFile(String url, String file) {
        try {
            java.net.URL url1 = new java.net.URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(url1.openStream()));
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(file), true));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                bw.write(inputLine);
                bw.newLine();
            }
            bw.close();
            in.close();

            App.softAlert(file + " descargado correctamente");
            return true;
        } catch (Exception e) {
            App.softAlert("ERROR: " + e.getMessage());
            return false;
        }
    }

    public <I, O> void postCommInBackground(final String function, final String input, final CallBack callBack) throws Exception {
        Thread t = new Thread() {
            public void run() {
                try {
                    callBack.execute(postComm(function, input));
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.execute(null);
                }
            }
        };
        t.start();
    }

    public synchronized <I, O> String postComm(final String function, final String input) throws Exception {
        String msg = "";
        if (App.isNetworkAvailable(true)) {
            List<NameValuePair> val = getPostPair(input);
            String url = SERVER_HOST + function;
            httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(val));
            HttpResponse httpResponse = client.execute(httppost);
            HttpEntity responseEntity = httpResponse.getEntity();
            String result = EntityUtils.toString(responseEntity);
            if (responseEntity != null)
                msg = "called function: " + function + "\tString input: " + input + "\tResult: " + result;
            Log.d(TAG, msg);
            return result;
        }
        return null;
    }

    private List<NameValuePair> getPostPair(String input) {
        List<NameValuePair> pairData = new ArrayList<NameValuePair>();
        pairData.add(new BasicNameValuePair(postQuery, input));
        return pairData;
    }
}
