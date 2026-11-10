package com.rozgarone.models;

/** A push-style announcement shown on the Notifications screen. */
public class Notice {

    public String maintitle = "";
    public String secondtitle = "";
    public String publishdate = "";

    @Override
    public String toString() {
        return maintitle;
    }
}
