package com.application.jorge.whereappu.DataBase;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Classes.PhoneContact;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.R;

import org.json.JSONObject;

/**
 * Created by jgarc on 20/03/2015.
 */
public class User extends WAUModel {
    protected static User mySelf;

    public String Name;
    public String Email;
    public String PhoneNumber;
    public String GCM_ID;
    public String PhotoURL = utils.getUri(R.drawable.unknown_contact).toString();

    public Drawable getPhoto() {
        return utils.getDrawable(Uri.parse(PhotoURL));
    }

    public User() {
        super();
        tableName = "User";
    }

    public User(String nick, String email, String phoneNumber, String GCMID, long serverId) {
        super();
        this.Name = nick;
        this.Email = email;
        this.PhoneNumber = phoneNumber;
        this.GCM_ID = GCMID;
        this.ID = serverId;
        tableName = "User";
    }

    public static User getMySelf(boolean refresh) {
        try {
            if (mySelf == null || refresh)
                mySelf = getFirst(User.class, ID_NAME + " = ?", String.valueOf(App.getUserId()));
            return mySelf;
        } catch (Exception e) {
            return null;
        }
    }

    public static User getMySelf() {
        return getMySelf(false);
    }

    public static User getById(long id) {
        return User.getById(User.class, id);
    }

    public static User getFromJson(JSONObject jObj) {
        return gson.fromJson(jObj.toString(), User.class);
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

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", Name='" + Name + '\'' +
                ", Email='" + Email + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", GCM_ID='" + GCM_ID + '\'' +
                '}';
    }

    public String getLastTask() throws Exception {
        Task t = WAUModel.getFirst(Task.class, "creatorId = ? order By CreatedOn Desc", ID);
        return t == null ? "--no message--" : t.Body;
    }

    public void syncFromPhoneBook() {
        PhoneContact pc = PhoneContact.GetContact(PhoneNumber);
        if (pc != null && pc.getPhoto() != null)
            PhotoURL = pc.getPhoto().toString();
    }

}
