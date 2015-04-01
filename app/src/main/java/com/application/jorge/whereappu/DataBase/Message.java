package com.application.jorge.whereappu.DataBase;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

import java.util.Date;

/**
 * Created by jgarc on 20/03/2015.
 */
public class Message extends Model {
    @Column(name = "ServerId", unique = true)
    public long serverId = -1;

    @Column(name = "SenderId")
    public User sender;

    @Column(name = "ReceiverId")
    public User receiver;

    @Column(name = "CreatedTime")
    public Date CreatedTime = new Date();

    @Column(name = "Message")
    public String message;

    public Message(User sender, User receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }
}
