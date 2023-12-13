package com.galimimus.phishingmonitor.models;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private Image img;
    private String name;
    private String email;
    private String company;

    public User(String name, String email, String company){
        this.company = company;
        this.email = email;
        this.name = name;
    }

}
