package com.galimimus.phishingmonitor.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class LastMailing extends Mailing{

    private HashMap<String, Calendar> used_ips;

    LastMailing(ArrayList<Employee> recipients, String mailing_name) {
        super(recipients, mailing_name);
    }

    public void add_toUsed_ips(String ip){
        String new_ip = ip.replaceAll(" ", "");

        used_ips.put(ip, Calendar.getInstance());
    }

}
