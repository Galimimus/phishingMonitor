package com.galimimus.phishingmonitor.models;

import lombok.Getter;

@Getter
public class Employee {
    int id;
    String name;
    String ip;
    int raiting;
    String email;
    Department department;


    public Employee(String name, String ip, int raiting, String email, Department department){
        this.name = name;
        this.department = department;
        this.ip = ip;
        this.raiting = raiting;
        this.email = email;
    }



    public Employee(int id, String name){
        this.id = id;
        this.name = name;
    }



    public Employee(String ip, String email, Department department) {
        this.email = email;
        this.ip = ip;
        this.department = department;
    }

    public Employee(int id, String name, Department department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }
}
