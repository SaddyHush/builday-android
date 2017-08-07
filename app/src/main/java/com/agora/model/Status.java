package com.agora.model;

import android.support.annotation.NonNull;

/**
 * Created by Saddy on 7/29/2017.
 */

public class Status implements Comparable<Status> {
    private String name;
    private String text;
    private int position;
    public Status(String name, String text, int position) {
        this.name = name;
        this.text = text;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public int compareTo(@NonNull Status o) {
        return(o.position - position);
    }
}