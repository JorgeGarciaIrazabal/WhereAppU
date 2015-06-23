package com.application.jorge.whereappu.DataBase;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.R;
import com.google.gson.Gson;
import org.json.JSONObject;

/**
 * Created by jgarc on 20/03/2015.
 */
@Table(name = "Owner")
public class User extends WAUModel {
    protected static User mySelf;

    @Column(name = "Name")
    public String Name;

    @Column(name = "Email")
    public String Email;

    @Column(name = "PhoneNumber")
    public String PhoneNumber;

    @Column(name = "GCM_ID")
    public String GCM_ID;

    @Column(name = "PhotoURL")
    public String PhotoURL = utils.getUri(R.drawable.unknown_contact).toString();

    public Drawable getPhoto() {
        return utils.getDrawable(Uri.parse(PhotoURL));
    }

    public User(String nick, String email, String phoneNumber, String GCMID, long serverId) {
        super();
        this.Name = nick;
        this.Email = email;
        this.PhoneNumber = phoneNumber;
        this.GCM_ID = GCMID;
        this.ID = serverId;
    }

    public static User getMySelf(boolean refresh) {
        if (mySelf == null || refresh)
            mySelf = new Select().from(User.class).where("ServerId = ?", App.getUserId()).executeSingle();
        return mySelf;
    }

    public static User getMySelf() {
        return getMySelf(false);
    }

    public static User getUserByID(long serverId) {
        return new Select().from(User.class).where("serverID = ?", serverId).executeSingle();
    }


    public static User getFromJson(JSONObject jObj){
        User user = gson.fromJson(jObj.toString(), User.class);
        return user;
    }
    public User(JSONObject jObj) {
        super();
        User user = gson.fromJson(jObj.toString(), User.class);
        this.Name = user.Name;
        this.Email = user.Email;
        this.PhoneNumber = user.PhoneNumber;
        this.GCM_ID = user.GCM_ID;
        this.ID = user.ID;
    }

    public User() {
        super();
    }

    @Override
    public String toString() {
        return "Owner{" +
                "ID=" + ID +
                ", Name='" + Name + '\'' +
                ", Email='" + Email + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", GCM_ID='" + GCM_ID + '\'' +
                '}';
    }

    public String getLastTask() {
        Task t =  new Select().from(Task.class)
                .where("creatorId = ?", getId())
                .orderBy("CreatedOn ASC")
                .executeSingle();
        return t == null ? "--no message--":t.Body;
    }
}
