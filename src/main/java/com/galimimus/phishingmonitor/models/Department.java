package com.galimimus.phishingmonitor.models;

public class Department {
    int id;
    String name;

    Department(int id, String name){
        this.id = id;
        this.name = name;
    }
    public Department(String name){
        this.name = name;
    }
}
