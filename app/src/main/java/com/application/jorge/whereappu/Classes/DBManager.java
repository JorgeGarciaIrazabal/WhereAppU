package com.application.jorge.whereappu.Classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.R;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class DBManager extends SQLiteOpenHelper {

    protected static String DataBase;
    protected static int version = 2;
    public static Context context;//for debugging
    private final String TAG = "DBManager";

    public DBManager(Context context) {
        super(context, App.AppFolder + File.separator + context.getString(R.string.database_name), null, version);
        this.context = context;
        DataBase = context.getString(R.string.database_name);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*db.execSQL(context.getString(R.string.message_table));
        db.execSQL(context.getString(R.string.user_table));
        db.execSQL(context.getString(R.string.place_table));*/
        Log.i(TAG, "Database successfully created");
    }

    private String[] formatQueryArguments(Object[] objects) {
        String strList[] = new String[objects.length];
        for (int i = 0; i < objects.length; i++)
            strList[i] = String.valueOf(objects[i]);
        return strList;
    }

    /*private query*/
    public void query(String query, Object... objects) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query, objects);
    }

    //Generic
    public QueryTable select(String query, Object... objects) {
        String[] strList = formatQueryArguments(objects);
        SQLiteDatabase db = this.getReadableDatabase();
        return new QueryTable(db.rawQuery("Select " + query, strList));
    }

    public QueryTable tableInfo(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        return new QueryTable(db.rawQuery("PRAGMA table_info(" + table + ")", null));
    }

    public QueryTable getEmptyQueryTable(String table) {
        return select("* FROM " + table + " LIMIT 0");
    }

    public void clearDataBase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS MESSAGE");
        db.execSQL(context.getString(R.string.message_table));
        db.execSQL("DROP TABLE IF EXISTS USER");
        db.execSQL(context.getString(R.string.user_table));
        db.execSQL("DROP TABLE IF EXISTS PLACE");
        db.execSQL(context.getString(R.string.place_table));
    }

    public String getInsertQueryString(String table, QueryTable qt) throws Exception {
        ArrayList<String> questionMarks = new ArrayList<>();
        for (String header : qt.headers)
            questionMarks.add("?");
        return "INSERT INTO " + table + " (" + App.join(qt.headers, ", ") + " ) VALUES (" + App.join(questionMarks, ",") + ")";
    }

    public String getUploadQueryString(String table, QueryTable qt) {
        ArrayList<String> questionMarks = new ArrayList<>();
        for (String header : qt.headers)
            questionMarks.add(header + " = ?");
        return "UPDATE " + table + "SET (" + App.join(questionMarks, ",") + ")";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearDataBase();
        Log.i(TAG, "Database successfully Upgraded to version " + String.valueOf(newVersion));
    }


    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }


    public boolean insertUser(QueryTable user) throws Exception {
        if (user.height() == 1) {
            //if no Id provided, we add a provisional one as the number of places
            if (user.getData("ID") == null) {
                App.softAlert("user must have a ID to be inserted");
                return false;
            }
            String insertString = getInsertQueryString("USER", user);
            query(insertString, user.table.get(0));
        } else {
            App.softAlert("DEBUG: to add a user necessary to pass a Query table with just one row");
            return false;
        }
        return true;
    }

    public boolean login(QueryTable user) throws Exception {
        if (!insertUser(user)) {
            this.onUpgrade(this.getWritableDatabase(), 1, version);
            return false;
        }
        return true;

    }


}