package com.galimimus.phishingmonitor.logic;

import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.models.Department;
import com.galimimus.phishingmonitor.models.Employee;
import com.galimimus.phishingmonitor.models.LastMailing;
import com.galimimus.phishingmonitor.models.Mailing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Statistic {
    //TODO:
    // 2. вычисление рейтинга компании.
    // 3. вычисление рейтинга прошедшей рассылки.
    // 4.
    public static int countEmployeeRaiting(String ip){

        DB db= new DB();
        db.connect();

        int used = db.getEmpUsedMailings(ip);
        int total = db.getEmpTotalMailings(ip);
        int raiting = 10;
        if (total != 0) {
            raiting = (int) (10 - ((double) used / total) * 10);
        }
        db.updateEmpRaiting(raiting, ip);
        db.close();


        return raiting;
    }

    public static int countCompanyRaiting(){
        int raiting = 0;
        DB db = new DB();
        db.connect();
        raiting = db.getAverageEmpRaiting();
        db.close();
        return raiting;
    }

    public static int countLastMailingRaiting(){
        int raiting = 0;
        DB db = new DB();
        db.connect();
        raiting = db.getLastMailingRaiting();
        db.close();
        return raiting;
    }
    public static HashMap<String, Integer> countMailingsRaiting(){
        HashMap<String, Integer> raiting = new HashMap<>();
        DB db = new DB();
        db.connect();
        raiting = db.getMailingsRaiting();
        db.close();
        return raiting;
    }
    public static HashMap<String, Integer> countDepsRaiting(){
        HashMap<String, Integer> raiting = new HashMap<>();
        DB db = new DB();
        db.connect();
        raiting = db.getDepsRaiting();
        db.close();
        return raiting;
    }
}