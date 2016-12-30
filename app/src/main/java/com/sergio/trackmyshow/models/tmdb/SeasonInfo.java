package com.sergio.trackmyshow.models.tmdb;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SeasonInfo {
    @SerializedName("name")
    private String name;
    @SerializedName("overview")
    private String overview;
    @SerializedName("episodes")
    private List<Episode> episodeList;

    public String getName() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public List<Episode> getEpisodeList() {
        return episodeList;
    }
}
