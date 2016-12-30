package com.sergio.trackmyshow.models.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TVShowSearch {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("first_air_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private String posterPath;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getReleaseDate() {
        if (releaseDate != null) {
            return releaseDate.replace("-", "/");
        } else {
            return null;
        }
    }

    public String getPosterPath() {
        return posterPath;
    }

}
