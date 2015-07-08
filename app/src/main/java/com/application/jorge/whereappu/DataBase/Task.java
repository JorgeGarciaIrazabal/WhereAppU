package com.application.jorge.whereappu.DataBase;

import com.application.jorge.whereappu.Classes.DateTimeFormater;
import com.application.jorge.whereappu.Services.ScheduleManager;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;


public class Task extends WAUModel {
    public static final String TYPE_COMMENT = "Comment";
    public static final String TYPE_SCHEDULE = "Scheduled";
    public static final String TYPE_PLACE = "Place";

    public static final int STATE_CREATED = 0;
    public static final int STATE_UPLOADED = 1;
    public static final int STATE_ARRIVED = 2;
    public static final int STATE_READ = 3;
    public static final int STATE_COMPLETED = 4;
    public static final int STATE_DISMISSED = 5;

    public int Notified = 0;

    public Long CreatorId;
    public Long ReceiverId;
    public String Body;
    public String Type = TYPE_COMMENT;
    public Long LocationId = null;
    public Date Schedule = null;
    public int State = STATE_CREATED;

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
        if (State == STATE_CREATED) State = STATE_UPLOADED;
        long id = super.update(serverId);
        if (ReceiverId == User.getMySelf().ID && Type.equals(Task.TYPE_SCHEDULE))
            ScheduleManager.setUpScheduleTaskNotification(this, oldId);
        return id;
    }

    @Override
    public long save() throws Exception {
        long id = super.save();
        if (Notified == 0 && ReceiverId == User.getMySelf().ID && Type.equals(Task.TYPE_SCHEDULE))
            ScheduleManager.setUpScheduleTaskNotification(this);
        return id;
    }

    public User getCreator() {
        return User.getById(CreatorId);
    }

    public User getReceiver() {
        return User.getById(ReceiverId);
    }

    public Place getLocation() {
        return Place.getById(LocationId);
    }

    public static List<Task> getReceivedTask() throws Exception {
        long id = User.getMySelf().ID;
        return where(Task.class, "(CreatorId = ? or receiverId = ?)" +
                " and Type != ? order by CreatedOn ASC", id, id, Task.TYPE_COMMENT);
    }

    public static List<Task> getTasksWith(User user) throws Exception {
        long myId = User.getMySelf().ID;
        String query = "(CreatorId = ? and receiverId = ?) or (CreatorId = ? and receiverId = ?) order by createdOn ASC";
        return where(Task.class, query, myId, user.ID, user.ID, myId);
    }

    public static Task getById(long id) {
        return Task.getById(Task.class, id);
    }

    public static List<Task> getScheduledTaskToNotify() throws Exception {
        String query = "receiverId = ? and Type = ? and Notified = 0 and Schedule >= ? order By CreatedOn ASC";
        return where(Task.class, query, User.getMySelf().ID, Task.TYPE_SCHEDULE, new Date());

    }

    public static List<Task> getTaskToNotify() throws Exception {
        long myId = User.getMySelf().ID;
        String query = "receiverId = ? and " + //only tasks received
                "((Type != ? and State <= ?) or (Type == ? and State <= ?)) and (" + //generic status filter
                "(Type = ? and Schedule < ?) or " + //schedule filter
                "(Type = ? and LocationId is not NULL) or " + //location filter //todo add location checker
                "(Type = ?))" + //comment
                " order By CreatedOn ASC";
        return where(Task.class, query, myId,
                Task.TYPE_COMMENT, Task.STATE_READ, Task.TYPE_COMMENT, Task.STATE_UPLOADED, //generic status
                Task.TYPE_SCHEDULE, new Date(),
                Task.TYPE_PLACE,
                Task.TYPE_COMMENT);

    }

    public static Task getFromJson(JSONObject jObj) {
        return gson.fromJson(jObj.toString(), Task.class);
    }

}
