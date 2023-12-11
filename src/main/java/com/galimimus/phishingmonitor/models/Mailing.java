package com.galimimus.phishingmonitor.models;

import lombok.Getter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Getter
public class Mailing {
    protected int id;
    protected Timestamp time;

    protected int dep_id;
    protected int total_sent;
    protected int total_used;

    public Mailing(int dep_id, int total_sent){

        this.dep_id = dep_id;
        time = new Timestamp(Calendar.getInstance().getTimeInMillis());
        this.total_sent = total_sent;

    }
    public Mailing(int id, Timestamp time){
        this.id = id;
        this.time = time;
    }


    public Mailing(int id, Timestamp time, int dep_id, int total_sent, int total_used) {
        this.id = id;
        this.time = time;
        this.dep_id = dep_id;
        this.total_sent = total_sent;
        this.total_used = total_used;
    }

    public Mailing(int id) {
        this.id = id;
    }
}
