package com.application.jorge.whereappu.DataBase;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.application.jorge.whereappu.Classes.utils;
import com.application.jorge.whereappu.R;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by Jorge on 05/06/2015.
 */
@Table(name = "Place")
public class Place extends WAUModel {
    @Column(name = "Name")
    public String Name;

    @Column(name = "IconURI")
    public String IconURI = utils.getUri(R.drawable.icon_material_place).toString();

    @Column(name = "Owner")
    public User Owner;

    @Column(name = "CreatedOn")
    public Date CreatedOn = new Date();

    @Column(name = "Type")
    public String Type = "Public";

    @Column(name = "Longitude")
    public Double Longitude;

    @Column(name = "Latitude")
    public Double Latitude;

    @Column(name = "Range")
    public int Range;

    public Drawable getIcon() {
        return utils.getDrawable(Uri.parse(IconURI));
    }

    public Uri getIconUri(){
        return Uri.parse(IconURI);
    }
    public static Place createPlaceOfMine(){
        Place p = new Place();
        p.ID = getNotUploadedServerId(Place.class);
        p.Owner = User.getMySelf();
        return p;
    }

    public static List<Place> getMyPlaces() {
        return new Select().from(Place.class)
                .where("Owner = ?", User.getMySelf().getId())
                .orderBy("CreatedOn ASC")
                .execute();
    }

    public static Place getFromJson(JSONObject jObj) {
        User owner = User.getUserByID((Integer) jObj.remove("Owner"));
        Place place = gson.fromJson(jObj.toString(), Place.class);
        place.Owner = owner;
        return place;
    }

    public Place() {
        super();
    }
}