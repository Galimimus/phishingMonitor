package com.galimimus.phishingmonitor.models;

import java.util.Calendar;
import java.util.HashMap;

public class LastMailing extends Mailing{

    private HashMap<String, Calendar> used_ips;

    LastMailing(int dep_id, int total_sent) {
        super(dep_id, total_sent);
    }

    public void add_toUsed_ips(String ip){
        String new_ip = ip.replaceAll(" ", "");

        used_ips.put(ip, Calendar.getInstance());
    }

}
