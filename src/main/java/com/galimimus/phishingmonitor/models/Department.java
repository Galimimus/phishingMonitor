package com.galimimus.phishingmonitor.models;

public class Department {
    int id;
    String name;

    public Department(int id, String name){
        this.id = id;
        this.name = name;
    }
    public Department(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }
}
