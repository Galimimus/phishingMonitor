package com.galimimus.phishingmonitor.logic;

import com.galimimus.phishingmonitor.helpers.DB;

import java.util.HashMap;


public class Statistic {
    //TODO:
    // 2. вычисление рейтинга компании.
    // 3. вычисление рейтинга прошедшей рассылки.
    // 4.
    public static void countEmployeeRaiting(String ip){

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


        //return raiting;
    }

    public static int countCompanyRaiting(){
        int raiting;
        DB db = new DB();
        db.connect();
        raiting = db.getAverageEmpRaiting();
        db.close();
        return raiting;
    }

    public static int countLastMailingRaiting(){
        int raiting;
        DB db = new DB();
        db.connect();
        raiting = db.getLastMailingRaiting();
        db.close();
        return raiting;
    }
    public static HashMap<String, Integer> countMailingsRaiting(){
        HashMap<String, Integer> raiting;
        DB db = new DB();
        db.connect();
        raiting = db.getMailingsRaiting();
        db.close();
        return raiting;
    }
    public static HashMap<String, Integer> countDepsRaiting(){
        HashMap<String, Integer> raiting;
        DB db = new DB();
        db.connect();
        raiting = db.getDepsRaiting();
        db.close();
        return raiting;
    }
}