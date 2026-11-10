package com.rozgarone.models;

/** One selectable value of a filter group (Category / Profession / Qualification / Location). */
public class FilterItem {

    public String id = "";
    public String category_name = "";

    public FilterItem() {
    }

    public FilterItem(String id, String category_name) {
        this.id = id;
        this.category_name = category_name;
    }

    @Override
    public String toString() {
        return category_name;
    }
}
