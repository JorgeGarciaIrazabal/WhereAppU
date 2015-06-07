package com.application.jorge.whereappu.DataBase;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class Task extends Model {
    private static AtomicInteger atomicIDDecrementing = new AtomicInteger(-1);

    @Column(name = "serverID", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long ID = -1;

    @Column(name = "CreatorId")
    public User Creator;

    @Column(name = "ReceiverId")
    public User Receiver;

    @Column(name = "CreatedTime")
    public Date CreatedOn = new Date();

    @Column(name = "Body")
    public String Body;


    public Task() {
    }

    public Task(User sender, User receiver, String message) {
        this.ID = getNotUploadedServerId();
        this.Creator = sender;
        this.Receiver = receiver;
        this.Body = message;
    }

    private static int getNotUploadedServerId() {
        if(atomicIDDecrementing.get() == -1){
            Task lastTask =new Select().from(Task.class).orderBy("serverID ASC").executeSingle();
            int lastId = lastTask == null || lastTask.ID >= 0 ? -1: (int) lastTask.ID;
            atomicIDDecrementing.set(lastId);
        }
        return atomicIDDecrementing.decrementAndGet();
    }

    public static List<Task> getReceivedTask() {
        return new Select().from(Task.class)
                .where("senderId = ? or receiverId = ?", User.getMySelf().ID, User.getMySelf().ID)
                .orderBy("CreatedTime ASC")
                .execute();
    }

    public static List<Task> getTaskWith(User other) {
        long myId = User.getMySelf().getId();
        long otherId = other.getId();
        return new Select().from(Task.class)
                .where("(senderId = ? and receiverId = ?) or (senderId = ? and receiverId = ?)", myId, otherId, otherId, myId)
                .orderBy("CreatedTime ASC")
                .execute();
    }
}
