package com.application.jorge.whereappu.Classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.activeandroid.query.Select;
import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.Tornado.TornadoServer;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class DBManager {

    protected static String DataBase;
    protected static int version = 2;
    public static Context context;//for debugging
    private final String TAG = "DBManager";

    public DBManager() {
    }

    public void login(User userIn) throws Exception {
        User user = new Select().from(User.class).where("serverId = ?", userIn.serverId).executeSingle();
        if (user == null)
            userIn.save();
        userIn.save();//todo, necessary to check
        Log.d(TAG, "logged in: " + user.toString());
    }
}