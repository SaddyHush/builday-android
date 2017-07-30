package com.learn2crack.model;

/**
 * Created by Saddy on 7/29/2017.
 */

public class Status {
    private String name;
    private String text;

    public Status(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
