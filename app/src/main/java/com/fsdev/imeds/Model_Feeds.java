package com.fsdev.imeds;

/**
 * Created by Frankline Sable on 03/11/2017.
 */

public class Model_Feeds {
    private String title, description, dateTime, url, imageUrl;

    public Model_Feeds(String title, String description, String dateTime, String url, String imageUrl) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}