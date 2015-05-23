package com.walts.ideas.db;

import java.io.Serializable;

public class Idea extends Entity implements Serializable {

    public String title;
    public String desc;
    public String password; //SHA1
    public double latitude;
    public double longitude;
    public String address;

    public Idea(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Idea{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", password='" + password + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                '}';
    }

}
