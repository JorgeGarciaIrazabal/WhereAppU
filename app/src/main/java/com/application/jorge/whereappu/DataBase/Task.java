package com.application.jorge.whereappu.DataBase;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class Task extends WAUModel {

    @Column(name = "CreatorId")
    public User Creator;

    @Column(name = "ReceiverId")
    public User Receiver;

    @Column(name = "CreatedOn")
    public Date CreatedOn = new Date();

    @Column(name = "Body")
    public String Body;


    public Task() {
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

    public static List<Task> getTaskWith(User other) {
        long myId = User.getMySelf().getId();
        long otherId = other.getId();
        return new Select().from(Task.class)
                .where("(senderId = ? and receiverId = ?) or (senderId = ? and receiverId = ?)", myId, otherId, otherId, myId)
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
