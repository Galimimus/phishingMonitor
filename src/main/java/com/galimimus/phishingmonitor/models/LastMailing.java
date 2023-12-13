package com.galimimus.phishingmonitor.models;

import lombok.Getter;

import java.sql.Timestamp;
@Getter
public class LastMailing{

    int id;
    Timestamp timeOfUse;
    String usedIp;
    int mailingId;


    public LastMailing(Timestamp timeOfUse, String usedIp){
        this.timeOfUse = timeOfUse;
        this.usedIp = usedIp;
    }

    public LastMailing(int mailingId) {
        this.mailingId = mailingId;
    }

}
