package com.application.jorge.whereappu.DataBase;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.R;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by Jorge on 05/06/2015.
 */
public class Place extends WAUModel {
    public String Name;
    public String IconURI = utils.getUri(R.drawable.icon_material_place).toString();
    public long OwnerId;
    public String Type = "Public";
    public Double Longitude;
    public Double Latitude;
    public int Range;
    public Date DeletedOn = null;

    public User getOwner(){
        return User.getById(OwnerId);
    }

    public Place() {
        super();
        tableName = "Place";
    }

    public static Place getById(long id) {
        return Place.getById(Place.class, id);
    }

    public Drawable getIcon() {
        return utils.getDrawable(Uri.parse(IconURI));
    }

    public Uri getIconUri(){
        return Uri.parse(IconURI);
    }

    public static Place createPlaceOfMine() throws Exception {
        Place p = new Place();
        p.ID = getNotUploadedId(p.tableName);
        p.OwnerId = User.getMySelf().ID;
        return p;
    }

    public static List<Place> getMyPlaces() throws Exception {
        return getPlacesFrom(User.getMySelf());
    }

    public static List<Place> getMyActivePlaces() throws Exception {
        return where(Place.class, "OwnerId = ? and __Updated != 0 and DeletedOn is null Order by CreatedOn ASC", String.valueOf(User.getMySelf().ID));
    }

    public static List<Place> getPlacesFrom(User owner) throws Exception {
        String ownerId = String.valueOf(owner.ID);
        return where(Place.class, "OwnerId = ? and DeletedOn is null Order by CreatedOn ASC", ownerId);
    }

    public static Place getFromJson(JSONObject jObj) {
        return gson.fromJson(jObj.toString(), Place.class);
    }
}