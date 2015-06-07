package com.application.jorge.whereappu.DataBase;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.R;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Jorge on 05/06/2015.
 */
@Table(name = "Place")
public class Place extends Model {
    protected static Gson gson = new Gson();
    private static AtomicInteger atomicIDDecrementing = new AtomicInteger(-1);

    @Column(name = "ServerId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, index = true)
    public long ID = -1;

    @Column(name = "Name")
    public String Name;

    @Column(name = "IconURI")
    public String IconURL = utils.getUri(R.drawable.icon_material_place).toString();

    @Column(name = "User")
    public User user;

    @Column(name = "CreatedTime")
    public Date CreatedOn = new Date();

    @Column(name = "Type")
    public String Type = "Public";

    public Drawable getIcon() {
        return utils.getDrawable(Uri.parse(IconURL));
    }

     private static int getNotUploadedServerId() {
        if(atomicIDDecrementing.get() == -1){
            Place lastPlace =new Select().from(Place.class).orderBy("serverID ASC").executeSingle();
            int lastId = lastPlace == null || lastPlace.ID >= 0 ? -1: (int) lastPlace.ID;
            atomicIDDecrementing.set(lastId);
        }
        return atomicIDDecrementing.decrementAndGet();
    }

    public Place(String name, int iconId) {
        super();
        this.ID = getNotUploadedServerId();
        this.Name = name;
        this.IconURL = utils.getUri(iconId).toString();
    }
    public static List<Place> getMyPlaces() {
        return new Select().from(Place.class)
                .where("User = ?", User.getMySelf().ID)
                .orderBy("CreatedTime ASC")
                .execute();
    }

    public static User getUserByID(long serverId) {
        return new Select().from(User.class).where("ID = ?", serverId).executeSingle();
    }

    public Place() {
        super();
    }
}