package com.walts.ideas.db;

import java.io.Serializable;

public class Idea implements Serializable {

    public long id;
    public String title;
    public String desc;

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
                '}';
    }
}
