package com.application.jorge.whereappu.DataBase;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Jorge on 20/06/2015.
 */
public class WAUModel extends Model {
    protected static Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .serializeNulls()
            .setDateFormat("yyy/MM/dd HH:mm:ss S")
            .create();

    private static AtomicInteger atomicIDDecrementing = new AtomicInteger(-1);

    @Column(name = "ServerId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, index = true)
    public long ID = -1;

    @Column(name = "__Updated", index = true)
    public int __Updated = 0;

    public static int getNotUploadedServerId(Class<? extends WAUModel> table) {
        if (atomicIDDecrementing.get() == -1) {
            WAUModel lastItem = new Select().from(table).orderBy("serverID ASC").executeSingle();
            int lastId = lastItem == null || lastItem.ID >= 0 ? -1 : (int) lastItem.ID;
            atomicIDDecrementing.set(lastId);
        }
        return atomicIDDecrementing.decrementAndGet();
    }

    public Long write() throws Exception {
        this.__Updated = 0;
        Long id = save();
        if(id == -1) throw new Exception("Unable to save item in data base");
        return id;
    }

    public Long update(long serverId) throws Exception {
        this.__Updated = 1;
        this.ID = serverId;
        Long id = save();
        if(id == -1) throw new Exception("Unable to save item in data base");
        return id;
    }

    public static <MODEL_TYPE extends WAUModel> List<MODEL_TYPE> getUpdatedRows(Class<MODEL_TYPE> klass){
        return new Select().from(klass).where("__Updated = 0").execute();
    }

    public static boolean canBeUpdated(Class<? extends WAUModel> klass, long serverId) {
        try {
            WAUModel item = new Select().from(klass).where("serverId = ?", serverId).executeSingle();
            return item == null || item.__Updated == 1;
        } catch (Exception e) {
            return false;
        }

    }

}
