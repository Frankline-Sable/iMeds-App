package com.fsdev.imeds;

/**
 * Created by Frankline Sable on 03/11/2017.
 */

public class Model_Forums {
    private String forImage, forUser, forTitle, forDesc, forTime;
    private int unique;
    private String forEmail;

    public Model_Forums(String forImage, String forUser, String forTitle, String forDesc, String forTime, int unique, String forEmail) {
        this.forImage = forImage;
        this.forUser = forUser;
        this.forTitle = forTitle;
        this.forDesc = forDesc;
        this.forTime = forTime;
        this.unique = unique;
        this.forEmail = forEmail;
    }

    public String getForImage() {
        return forImage;
    }

    public void setForImage(String forImage) {
        this.forImage = forImage;
    }

    public String getForUser() {
        return forUser;
    }

    public void setForUser(String forUser) {
        this.forUser = forUser;
    }

    public String getForTitle() {
        return forTitle;
    }

    public void setForTitle(String forTitle) {
        this.forTitle = forTitle;
    }

    public String getForDesc() {
        return forDesc;
    }

    public void setForDesc(String forDesc) {
        this.forDesc = forDesc;
    }

    public String getForTime() {
        return forTime;
    }

    public void setForTime(String forTime) {
        this.forTime = forTime;
    }

    public int getUnique() {
        return unique;
    }

    public void setUnique(int unique) {
        this.unique = unique;
    }

    public String getForEmail() {
        return forEmail;
    }

    public void setForEmail(String forEmail) {
        this.forEmail = forEmail;
    }
}