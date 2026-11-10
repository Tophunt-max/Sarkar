package com.rozgarone.models;

import androidx.annotation.NonNull;

/** A single feed post (govt job, current affairs, daily news or exam result). */
public class Post {

    public String postid = "";
    public String feed = "";
    public String jobTitle = "";
    public String jobContent = "";
    public String postPublishDate = "";
    public String formattedDate = "";
    public String expiredDate = "";
    public boolean isExpired;

    @NonNull
    @Override
    public String toString() {
        return jobTitle;
    }
}
