package com.galimimus.phishingmonitor.models;

import lombok.Getter;

@Getter
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

    public Department(int id) {
        this.id = id;
    }
}
