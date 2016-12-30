package com.sergio.trackmyshow.models.tmdb;

import com.google.gson.annotations.SerializedName;

class Genre {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
