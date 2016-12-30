package com.sergio.trackmyshow.models.tmdb;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Season {
    @SerializedName("id")
    private int id;
    @SerializedName("season_number")
    private int number;
    @SerializedName("air_date")
    private String airDate;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("episode_count")
    private int episodeCount;
    private String tempName;

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getAirDate() {
        if (airDate != null) {
            return airDate.replace("-", "/");
        } else {
            return null;
        }
    }

    public String getPosterPath() {
        return posterPath;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public String getTempName() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }
}
