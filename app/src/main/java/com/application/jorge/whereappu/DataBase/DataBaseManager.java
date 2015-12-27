package com.application.jorge.whereappu.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Classes.QueryTable;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.DataBase.Place;
import com.application.jorge.whereappu.DataBase.Task;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.DataBase.WAUModel;
import com.application.jorge.whereappu.R;


/**
 * Created by Jorge on 21/08/13.
 */
public class DataBaseManager extends SQLiteOpenHelper {
    protected static int version = 7;
    public Context context;//for debugging
    public static boolean logQueries = false;

    public DataBaseManager(Context context) {
        super(context, context.getString(R.string.database_name), null, version);
        this.context = context;
        //this.onUpgrade(this.getWritableDatabase(),1,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(App.getAppContext().getString(R.string.create_table_user));
        db.execSQL(App.getAppContext().getString(R.string.create_table_place));
        db.execSQL(App.getAppContext().getString(R.string.create_table_task));
        db.execSQL(App.getAppContext().getString(R.string.create_table_index1));
        db.execSQL(App.getAppContext().getString(R.string.create_tables_index2));
        db.execSQL(App.getAppContext().getString(R.string.create_tables_index3));
        db.execSQL(App.getAppContext().getString(R.string.create_tables_index4));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int earlierVersion, int newVersion) {
        refreshDatabase();
    }

    //Generic
    public void refreshDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WAUModel.getTableName(Task.class),null, null);
        db.delete(WAUModel.getTableName(Place.class), null, null);
        db.delete(WAUModel.getTableName(User.class), null, null);

        /*db.execSQL(App.getAppContext().getString(R.string.create_table_user));
        db.execSQL(App.getAppContext().getString(R.string.create_table_place));
        db.execSQL(App.getAppContext().getString(R.string.create_table_task));*/
    }

    public Cursor rawSelect(String query, Object... args) {
        SQLiteDatabase db = this.getReadableDatabase();
        if(logQueries)
            utils.log("Raw query in SQL:\nSelect: " + query + "\nArgs: " + utils.join(args, ", "));
        return db.rawQuery("Select " + query, utils.getStringArrays(args));
    }

    public QueryTable select(String query, Object... args) {
        SQLiteDatabase db = this.getReadableDatabase();
        if(logQueries)
            utils.log("Query in SQL:\nSelect: " + query + "\nArgs: " + utils.join(args, ", "));
        return new QueryTable(db.rawQuery("Select " + query, utils.getStringArrays(args)));
    }

    public QueryTable selectFirst(String query, Object... args) {
        return select(query + " limit 1 ", args);
    }

    public QueryTable tableInfo(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        return new QueryTable(db.rawQuery("PRAGMA table_info(" + table + ")", null));
    }

    public QueryTable getEmptyQueryTable(String table) {
        return select("* FROM " + table + " LIMIT 0");
    }

    public Cursor query(String query, Object... args) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(query, utils.getStringArrays(args));
    }

    public long insert(String tableName, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(tableName, null, values);
    }

    public long update(String tableName, ContentValues values, long originalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(tableName, values, "ID = " + String.valueOf(originalId), null);
    }

    public long update(String tableName, ContentValues values) {
        long id  = values.getAsLong("ID");
        return update(tableName, values, id);
    }

    public long delete(String tableName, long id){
        SQLiteDatabase db = this.getWritableDatabase();
        return (long)db.delete(tableName,"ID = "+String.valueOf(id), null);
    }

}
