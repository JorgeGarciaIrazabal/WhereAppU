package com.application.jorge.whereappu.DataBase;

import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.application.jorge.whereappu.Services.ScheduleManager;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;


public class Task extends WAUModel {
    public static final String TYPE_COMMENT = "Comment";
    public static final String TYPE_SCHEDULE = "Scheduled";
    public static final String TYPE_PLACE = "Place";


    @Column(name = "CreatorId")
    public User Creator;

    @Column(name = "ReceiverId")
    public User Receiver;

    @Column(name = "CreatedOn")
    public Date CreatedOn = new Date();

    @Column(name = "Body")
    public String Body;

    @Column(name = "Type")
    public String Type = TYPE_COMMENT;

    @Column(name = "Location")
    public Place Location = null;

    @Column(name = "Schedule")
    public Date Schedule = null;


    public Task() {
    }

    @Override
    public Long write() throws Exception {
        long id = super.write();
        ScheduleManager.setUpScheduleTaskNotification(this);
        return id;
    }

    @Override
    public Long update(long serverId) throws Exception {
        long oldId = this.ID;
        long id = super.update(serverId);
        ScheduleManager.setUpScheduleTaskNotification(this, oldId);
        return id;
    }

    public Task(User sender, User receiver, String message) {
        this.ID = getNotUploadedServerId(Task.class);
        this.Creator = sender;
        this.Receiver = receiver;
        this.Body = message;
    }

    public static List<Task> getReceivedTask() {
        return new Select().from(Task.class)
                .where("senderId = ? or receiverId = ?", User.getMySelf().ID, User.getMySelf().ID)
                .orderBy("CreatedOn ASC")
                .execute();
    }

    public static List<Task> getTaskWith(User user) {
        long myId = User.getMySelf().getId();
        long userId = user.getId();
        return new Select().from(Task.class)
                .where("(senderId = ? and receiverId = ?) or (senderId = ? and receiverId = ?)", myId, userId, userId, myId)
                .orderBy("CreatedOn ASC")
                .execute();
    }

    public static Task getTaskWithId(long serverId){
        return new Select().from(Task.class).where("serverID = ?", serverId).executeSingle();
    }

    public static List<Task> getScheduledTaskToNotify() {
        return new Select().from(Task.class)
                .where(" receiverId = ? and Schedule >= ?", User.getMySelf().ID, new Date())
                .orderBy("CreatedOn ASC")
                .execute();
    }

    public static Task getFromJson(JSONObject jObj) {
        User creator = User.getUserByID((int) jObj.remove("Creator"));
        User receiver = User.getUserByID((int) jObj.remove("Receiver"));
        Task task = gson.fromJson(jObj.toString(), Task.class);
        task.Creator = creator;
        task.Creator = receiver;
        return task;
    }
}
