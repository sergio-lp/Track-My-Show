package com.sergio.trackmyshow.models.tmdb;

import com.google.gson.annotations.SerializedName;

public class Episode {
    @SerializedName("episode_number")
    private int number;
    @SerializedName("name")
    private String name;
    @SerializedName("overview")
    private String overview;
    private boolean watched;

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }
}
