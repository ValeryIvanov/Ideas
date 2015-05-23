package com.walts.ideas.db;

import java.io.Serializable;

public class Question extends Entity implements Serializable {

    public String question;

    public Question(String question) {
        this.question = question;
    }
}
