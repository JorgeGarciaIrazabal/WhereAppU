package com.application.jorge.whereappu.Classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.R;
import com.couchbase.lite.util.Log;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {

    protected static String DataBase;
    protected static int version = 2;
    public static Context context;//for debugging
    private final String TAG = "DBManager";

    public DBManager(Context context) {
        super(context, App.AppFolder + File.separator + context.getString(R.string.database_name), null, version);
        this.context = context;
        DataBase = context.getString(R.string.database_name);
        this.onUpgrade(this.getWritableDatabase(), 1, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(context.getString(R.string.message_table));
        db.execSQL(context.getString(R.string.user_table));
        db.execSQL(context.getString(R.string.place_table));
        Log.i(TAG, "Database successfully created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS MESSAGE");
        db.execSQL(context.getString(R.string.message_table));
        db.execSQL("DROP TABLE IF EXISTS USER");
        db.execSQL(context.getString(R.string.user_table));
        db.execSQL("DROP TABLE IF EXISTS PLACE");
        db.execSQL(context.getString(R.string.place_table));
        Log.i(TAG, "Database successfully Upgraded to version " + String.valueOf(newVersion));
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    //Generic
    public QueryTable select(String query, Object... objects) {
        String strList[] = new String[objects.length];
        for (int i = 0; i < objects.length; i++)
            strList[i] = String.valueOf(objects[i]);
        SQLiteDatabase db = this.getReadableDatabase();
        return new QueryTable(db.rawQuery("Select " + query, strList));
    }
}