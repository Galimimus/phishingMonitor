package com.galimimus.phishingmonitor.models;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Mailing {
    protected int id;
    protected Calendar time;

    protected int dep_id;
    protected int total_sent;
    protected int total_used;

    public Mailing(int dep_id, int total_sent){

        this.dep_id = dep_id;
        time = Calendar.getInstance();
        this.total_sent = total_sent;

    }


    public long getTime() {
        return time.getTimeInMillis();
    }


    public int getTotal_used() {
        return total_used;
    }
    public void setTotal_used(int total_used) {
        this.total_used = total_used;
    }


    public int getTotal_sent() {
        return total_sent;
    }

    public int getDep_id() {
        return dep_id;
    }
}
