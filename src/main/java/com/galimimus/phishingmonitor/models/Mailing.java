package com.galimimus.phishingmonitor.models;

import java.util.ArrayList;
import java.util.Calendar;

public class Mailing {
    protected int id;
    protected Calendar time;
    protected final ArrayList<Employee> recipients;
    protected int total_sent;
    protected int total_used;
    protected final String mailing_name;

    Mailing(ArrayList<Employee> recipients, String mailing_name){

        this.recipients = recipients;
        this.mailing_name = mailing_name;
        time = Calendar.getInstance();

    }

    public String getMailing_name() {
        return mailing_name;
    }

    public Calendar getTime() {
        return time;
    }

    public ArrayList<Employee> getRecipients() {
        return recipients;
    }

    public int getTotal_used() {
        return total_used;
    }

    public int getTotal_sent() {
        return total_sent;
    }
}
