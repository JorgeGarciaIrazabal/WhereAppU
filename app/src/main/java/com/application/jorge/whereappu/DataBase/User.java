package com.application.jorge.whereappu.DataBase;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.application.jorge.whereappu.Classes.QueryTable;

/**
 * Created by jgarc on 20/03/2015.
 */
@Table(name = "User")
public class User extends Model {
    @Column(name = "ServerId", unique = true)
    public long serverId = -1;

    @Column(name = "Nick")
    public String nick;

    @Column(name = "Email")
    public String email;

    @Column(name = "PhoneNumber")
    public String phoneNumber;


    @Column(name = "GCMID")
    public String GCMID;

    public User(String nick, String email, String phoneNumber, String GCMID) {
        super();
        this.nick = nick;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.GCMID = GCMID;
    }

    public void update(QueryTable qt) {
        try {
            serverId = (long) qt.getData("ID");
        } catch (QueryTable.QTExcep qtExcep) {
            qtExcep.printStackTrace();
        }
        try {
            nick = (String) qt.getData("nick");
        } catch (QueryTable.QTExcep qtExcep) {
            qtExcep.printStackTrace();
        }
        try {
            email = (String) qt.getData("email");
        } catch (QueryTable.QTExcep qtExcep) {
            qtExcep.printStackTrace();
        }
        try {
            phoneNumber = (String) qt.getData("phoneNumber");
        } catch (QueryTable.QTExcep qtExcep) {
            qtExcep.printStackTrace();
        }
        try {
            GCMID = (String) qt.getData("GCMID");
        } catch (QueryTable.QTExcep qtExcep) {
            qtExcep.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "serverId=" + serverId +
                ", nick='" + nick + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", GCMID='" + GCMID + '\'' +
                '}';
    }

    public User() {
        super();
    }
}
