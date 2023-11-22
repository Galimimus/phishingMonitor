package com.galimimus.phishingmonitor.models;

import javafx.scene.image.Image;

public class User {
    private Image img;
    private String name;
    private String email;
    private String company;
    public User(Image img, String name, String email, String company){
        this.company = company;
        this.img = img;
        this.email = email;
        this.name = name;
    }
    public User(String name, String email, String company){
        this.company = company;
        this.email = email;
        this.name = name;
    }

    public Image getImg() {
        return img;
    }

    public String getCompany() {
        return company;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(Image img) {
        this.img = img;
    }
}
