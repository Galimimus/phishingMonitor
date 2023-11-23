package com.galimimus.phishingmonitor.models;

public class Employee {
    int id;
    String name;
    String ip;
    int raiting;
    String email;
    Department department;

    Employee(int id, String name, String ip, int raiting, String email, Department department){
        //TODO: проверить данные перед присвоением, как минимум ip
        this.id = id;
        this.name = name;
        this.department = department;
        this.ip = ip;
        this.raiting = raiting;
        this.email = email;
    }

    public Employee(String name, String ip, int raiting, String email, Department department){
        //TODO: проверить данные перед присвоением, как минимум ip
        this.name = name;
        this.department = department;
        this.ip = ip;
        this.raiting = raiting;
        this.email = email;
    }
    public Employee(String name, String ip, int raiting, String email){
        //TODO: проверить данные перед присвоением, как минимум ip
        this.name = name;
        this.ip = ip;
        this.raiting = raiting;
        this.email = email;
    }

    Employee(int id, String name, String ip, String email, Department department){
        //TODO: проверить данные перед присвоением, как минимум ip
        this.id = id;
        this.name = name;
        this.department = department;
        this.ip = ip;
        this.email = email;
    }

    Employee(int id, String name, String ip, int raiting, String email){
        //TODO: проверить данные перед присвоением, как минимум ip
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.raiting = raiting;
        this.email = email;
    }
    public Employee(int id, String name, String ip, String email){
        //TODO: проверить данные перед присвоением, как минимум ip
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.email = email;
    }
    Employee(String name, String ip, String email){
        //TODO: проверить данные перед присвоением, как минимум ip
        this.name = name;
        this.ip = ip;
        this.email = email;
    }
    public Employee(int id, String name){
        //TODO: проверить данные перед присвоением, как минимум ip
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Department getDepartment() {
        return department;
    }

    public String getIP() {
        return ip;
    }

    public int getRaiting() {
        return raiting;
    }
}
