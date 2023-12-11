package com.galimimus.phishingmonitor.models;

import lombok.Getter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
@Getter
public class LastMailing{

    int id;
    Timestamp timeOfUse;
    String usedIp;
    int mailingId;

    public LastMailing(int id, Timestamp timeOfUse, String usedIp, int mailingId){
        this.id = id;
        this.timeOfUse = timeOfUse;
        this.usedIp = usedIp;
        this.mailingId = mailingId;
    }

    public LastMailing(Timestamp timeOfUse, String usedIp){
        this.timeOfUse = timeOfUse;
        this.usedIp = usedIp;
    }

    public LastMailing(String ip) {
        usedIp = ip;
    }
    public LastMailing(int mailingId) {
        this.mailingId = mailingId;
    }

}
