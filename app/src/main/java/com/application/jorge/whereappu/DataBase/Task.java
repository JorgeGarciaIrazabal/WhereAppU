package com.application.jorge.whereappu.DataBase;

import com.application.jorge.whereappu.Classes.DateTimeFormater;
import com.application.jorge.whereappu.Services.ScheduleManager;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;


public class Task extends WAUModel {
    public static final String TYPE_COMMENT = "Comment";
    public static final String TYPE_SCHEDULE = "Scheduled";
    public static final String TYPE_PLACE = "Place";

    public static final String STATE_CREATED = "Created";
    public static final String STATE_UPLOADED = "Uploaded";
    public static final String STATE_ARRIVED = "Arrived";
    public static final String STATE_READ = "Read";
    public static final String STATE_COMPLETED = "Completed";
    public static final String STATE_DISMISSED = "Dismissed";

    public int Notified = 0;

    public Long CreatorId;
    public Long ReceiverId;
    public String Body;
    public String Type = TYPE_COMMENT;
    public Long LocationId = null;
    public Date Schedule = null;
    public String State = STATE_CREATED;

    public Task() {
        super();
        tableName = "Task";
    }

    public Task(User creator, User receiver, String message) {
        super();
        tableName = "Task";
        this.CreatorId = creator.ID;
        this.ReceiverId = receiver.ID;
        this.Body = message;
    }

    @Override
    public long update(long serverId) throws Exception {
        long oldId = this.ID;
        if(State.equals(STATE_CREATED)) State = STATE_UPLOADED;
        long id = super.update(serverId);
        if(ReceiverId == User.getMySelf().ID && Type.equals(Task.TYPE_SCHEDULE))
            ScheduleManager.setUpScheduleTaskNotification(this, oldId);
        return id;
    }

    @Override
    public long save() throws Exception {
        long id = super.save();
        if(Notified == 0 && ReceiverId == User.getMySelf().ID && Type.equals(Task.TYPE_SCHEDULE))
            ScheduleManager.setUpScheduleTaskNotification(this);
        return id;
    }

    public User getCreator(){
        return User.getById(CreatorId);
    }

    public User getReceiver(){
        return User.getById(ReceiverId);
    }

    public Place getLocationId() {
        return Place.getById(LocationId);
    }

    public static List<Task> getReceivedTask() throws Exception {
        String myIdStr = String.valueOf(User.getMySelf().ID);
        return where(Task.class, "CreatorId = ? or receiverId = ? order by CreatedOn ASC", myIdStr, myIdStr);
    }

    public static List<Task> getTaskWith(User user) throws Exception {
        String myId = String.valueOf(User.getMySelf().ID);
        String userId = String.valueOf(user.ID);
        String query = "(CreatorId = ? and receiverId = ?) or (CreatorId = ? and receiverId = ?) order by createdOn ASC";
        return where(Task.class, query, myId, userId, userId, myId);
    }

    public static Task getById(long id){
        return Task.getById(Task.class, id);
    }

    public static List<Task> getScheduledTaskToNotify() throws Exception {
        String myId = String.valueOf(User.getMySelf().ID);
        String date = DateTimeFormater.toDateTime(new Date());
        return where(Task.class, "receiverId = ? and Schedule >= ? order By CreatedOn ASC", myId, date);

    }

    public static Task getFromJson(JSONObject jObj) {
        return gson.fromJson(jObj.toString(), Task.class);
    }

}
