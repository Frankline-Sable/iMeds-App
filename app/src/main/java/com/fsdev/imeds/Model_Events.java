package com.fsdev.imeds;

/**
 * Created by Frankline Sable on 04/11/2017.
 */

public class Model_Events {
    private String dayOfMonth, dayOfWeek, title, description, timeView, eventVenue, eventDate, eventImage, fullDateTime;
    private int alarm;

    public Model_Events(String dayOfMonth, String dayOfWeek, String title, String description, String timeView, String eventVenue, String eventDate, String eventImage, int alarm, String fullDateTime) {
        this.dayOfMonth = dayOfMonth;
        this.dayOfWeek = dayOfWeek;
        this.title = title;
        this.description = description;
        this.timeView = timeView;
        this.eventVenue = eventVenue;
        this.eventDate = eventDate;
        this.eventImage = eventImage;
        this.alarm = alarm;
        this.fullDateTime = fullDateTime;
    }

    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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

    public String getTimeView() {
        return timeView;
    }

    public void setTimeView(String timeView) {
        this.timeView = timeView;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public String getFullDateTime() {
        return fullDateTime;
    }

    public void setFullDateTime(String fullDateTime) {
        this.fullDateTime = fullDateTime;
    }
}