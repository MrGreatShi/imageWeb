package org.example.imageweb.entity;

import java.sql.Timestamp;

public class ImageExif {
    private int width;
    private int height;
    private String type;
    private Timestamp modified;
    public ImageExif() {}
    public ImageExif(int width, int height, String type, Timestamp modified) {
        this.width = width;
        this.height = height;
        this.type = type;
        this.modified = modified;
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Timestamp getModified() {
        return modified;
    }
    public void setModified(Timestamp modified) {
        this.modified = modified;
    }
}
