package org.example.imageweb.entity;

import java.sql.Timestamp;
import java.time.LocalTime;


public class Image {
    private int id;
    private int user_id;
    private String title;
    private String path;
    private Timestamp stored_at;

    public Image() {}
    public Image(int id, int user_id, String title, String path) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.path = path;
        this.stored_at = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Timestamp getStored_at() {
        return stored_at;
    }
    public void setStored_at(Timestamp stored_at) {
        this.stored_at = stored_at;
    }

}
