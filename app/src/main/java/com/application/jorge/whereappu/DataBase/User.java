package com.application.jorge.whereappu.DataBase;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.amulyakhare.textdrawable.TextDrawable;
import com.application.jorge.whereappu.Activities.App;
import com.application.jorge.whereappu.Classes.PhoneContact;
import com.application.jorge.whereappu.Classes.QueryTable;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by jgarc on 20/03/2015.
 */
public class User extends WAUModel {
    protected static User mySelf;

    public String Name;
    public String Email;
    public String PhoneNumber;
    public String GCM_ID;
    public String PhotoURL = null;

    public Drawable getPhoto() {
        if (PhotoURL != null)
            return Drawable.createFromPath(PhotoURL);
        else
            return TextDrawable.builder().buildRect(Name.substring(0, 3), App.getAppContext().getResources().getColor(R.color.wau_blue));
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

    public Task getLastTask() throws Exception {
        return WAUModel.getFirst(Task.class, "creatorId = ? order By CreatedOn Desc", ID);
    }

    public void syncFromPhoneBook() {
        try {
            PhoneContact pc = PhoneContact.GetContact(PhoneNumber);
            if (pc != null && pc.getPhoto() != null) {
                Drawable photo = utils.getDrawable(Uri.parse(pc.getPhoto().toString()));
                Bitmap photoBitmap = utils.getBitmap(photo);
                File file = new File(App.AppFolder, "user_" + ID + ".PNG");
                FileOutputStream outStream = new FileOutputStream(file);
                photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
                PhotoURL = App.AppFolder + File.separator + "user_" + ID + ".PNG";
            } else
                PhotoURL = null;
        } catch (Exception e) {
            utils.saveExceptionInFolder(e);
        }
    }

    public int getActiveCommentsCount() throws QueryTable.QTExcep {
        String query = "count(*) from Task as t where t.Type = ? and t.state < ? and t.receiverId = ? and t.creatorId = ?";
        return (int) utils.getLong(db.select(query, Task.TYPE_COMMENT, Task.STATE_READ, User.getMySelf().ID, ID).getData(0));
    }

    public static ArrayList<User> getUsersWithActiveComments() throws Exception {
        String query = "distinct u.* from Task as t join User as u on t.receiverId = u.Id where t.Type = ? and t.state < ?";
        JSONArray jsonArray = db.select(query, Task.TYPE_COMMENT, Task.STATE_READ).getJSONArray();
        ArrayList<User> objectsList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            objectsList.add(gson.fromJson(jsonArray.getJSONObject(i).toString(), User.class));
        }
        return objectsList;
    }

}
