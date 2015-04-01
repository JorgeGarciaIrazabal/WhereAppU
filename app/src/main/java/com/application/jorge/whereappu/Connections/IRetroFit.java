package com.application.jorge.whereappu.Connections;

import com.application.jorge.whereappu.Classes.QueryTable;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by jgarc on 20/03/2015.
 */
public interface IRetroFit {
    public static final String TAG = "SERVER_COM";
    public static String SERVER_HOST = "http://192.168.1.139:8080/";
    public static String postQuery = "data";

    @POST("{path}")
    QueryTable getData(@Path("path") String path, @Field("data") String data, Callback<String> callback);
}
