package com.application.jorge.whereappu.DataBase;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Classes.DateTimeFormater;
import com.application.jorge.whereappu.Classes.QueryTable;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.WebSocket.WSHubsApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Jorge on 20/06/2015.
 */
public class WAUModel {
    public static final String ID_NAME = "ID";
    public static Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC, Modifier.PRIVATE, Modifier.PROTECTED)
            .serializeNulls()
            .setDateFormat(DateTimeFormater.fullDateTimeFormat)
            .create();
    public static DataBaseManager db = App.db;
    private static AtomicInteger atomicIDDecrementing = new AtomicInteger(-1);
    private static HashMap<Class, String> tablesClassNameHash;
    private static HashMap<String, Class> tablesNameClassHash;

    static {
        tablesClassNameHash = new HashMap<>();
        tablesClassNameHash.put(Task.class, "Task");
        tablesClassNameHash.put(User.class, "User");
        tablesClassNameHash.put(Place.class, "Place");
    }

    static {
        tablesNameClassHash = new HashMap<>();
        tablesNameClassHash.put("Task", Task.class);
        tablesNameClassHash.put("User", User.class);
        tablesNameClassHash.put("Place", Place.class);
    }

    protected String tableName;

    public long ID = Integer.MIN_VALUE;
    public int __Updated = 0;
    public Date CreatedOn = new Date();
    public Date UpdatedOn = new Date();

    public String toString(){
        return toJson().toString();
    }

    public static int getNotUploadedId(String tableName) {
        if (atomicIDDecrementing.get() == -1) {
            QueryTable qt = db.selectFirst(ID_NAME + " from " + tableName + " order by ID Desc");
            int lastId = qt.height() == 0 || qt.getLong(0) >= 0 ? -2 : (int) qt.getLong(0);
            atomicIDDecrementing.set(lastId);
        }
        return atomicIDDecrementing.decrementAndGet();
    }

    public Long write() throws Exception {
        this.__Updated = 0;
        Long id = save();
        if (id == -1) throw new Exception("Unable to save item in data base");
        return id;
    }

    public long save() throws Exception {
        QueryTable qt = new QueryTable(toJson());
        long id = qt.getLong(ID_NAME);
        if (isInserted() && getById(tablesNameClassHash.get(getTableName()), id) != null) {
            id = db.update(getTableName(), qt.getContentValues(0)) - 1;
        } else {
            if (!isInserted())
                qt.setData(ID_NAME, 0, getNotUploadedId(getTableName()));
            id = db.insert(getTableName(), qt.getContentValues(0));
        }
        if (id == -1) throw new Exception("Unable to save row");
        this.ID = id;
        return id;
    }

    public  boolean isInserted() {
        return ID != Integer.MIN_VALUE;
    }

    public long update(long serverId) throws Exception {
        Long oldId = this.ID;
        this.ID = serverId;
        this.__Updated = 1;
        QueryTable qt = new QueryTable(toJson());
        db.update(getTableName(), qt.getContentValues(0),oldId);
        return this.ID;
    }

    public static <MODEL_TYPE extends WAUModel> List<MODEL_TYPE> getNotUpdatedRows(Class<MODEL_TYPE> klass) throws Exception {
        return where(klass, "__Updated = 0");
    }

    public static <MODEL_TYPE extends WAUModel> ArrayList<MODEL_TYPE> where(Class<MODEL_TYPE> klass, String where, Object... args) throws Exception {
        where = checkWhereString(where);
        JSONArray jsonArray = db.select(" * from " + getTableName(klass) + " where " + where, args).getJSONArray();
        ArrayList<MODEL_TYPE> objectsList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            objectsList.add(gson.fromJson(jsonArray.getJSONObject(i).toString(), klass));
        }
        return objectsList;
    }

    public static <MODEL_TYPE extends WAUModel> MODEL_TYPE getFirst(Class<MODEL_TYPE> klass, String where, Object... args) throws Exception {
        where = checkWhereString(where);
        JSONArray jsonArray = db.select(" * from " + getTableName(klass) + " where " + where + " limit 1", args).getJSONArray();
        if (jsonArray.length() == 0) return null;
        return gson.fromJson(jsonArray.getJSONObject(0).toString(), klass);
    }

    public static <MODEL_TYPE extends WAUModel> boolean canBeUpdated(Class<MODEL_TYPE> klass, long id) {
        try {
            MODEL_TYPE item = getById(klass, id);
            return item == null || item.__Updated == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public static <MODEL_TYPE extends WAUModel> MODEL_TYPE getById(Class<MODEL_TYPE> klass, long id) {
        try {
            return getFirst(klass, " ID = ?", String.valueOf(id));
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
            return null;
        }
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static <MODEL_TYPE extends WAUModel> List<MODEL_TYPE> getAll(Class<MODEL_TYPE> klass) throws Exception {
        return where(klass, null);
    }

    public static String getTableName(Class<? extends WAUModel> klass) {
        return tablesClassNameHash.get(klass);
    }

    public String getTableName() {
        return tableName;
    }

    protected static String checkWhereString(String where) {
        if (where == null || where.trim().equals(""))
            where = " 1 = 1 ";
        return where;
    }

    public static Date getLastUpdatedOn(Class<? extends  WAUModel> klass){
        return (Date)db.select(" UpdatedOn from " + getTableName(klass) + "Order by UpdatedOn DESC limit 1").getData(0);
    }
}
