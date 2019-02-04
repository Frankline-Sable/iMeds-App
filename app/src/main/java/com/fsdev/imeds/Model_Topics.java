package com.fsdev.imeds;

/**
 * Created by Frankline Sable on 28/11/2017.
 */

public class Model_Topics {
    private String title, subInfo;
    int topicDrawable,counts;

    public Model_Topics(String title, String subInfo, int counts, int topicDrawable) {
        this.title = title;
        this.subInfo = subInfo;
        this.counts = counts;
        this.topicDrawable = topicDrawable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubInfo() {
        return subInfo;
    }

    public void setSubInfo(String subInfo) {
        this.subInfo = subInfo;
    }

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public int getTopicDrawable() {
        return topicDrawable;
    }

    public void setTopicDrawable(int topicDrawable) {
        this.topicDrawable = topicDrawable;
    }
}
