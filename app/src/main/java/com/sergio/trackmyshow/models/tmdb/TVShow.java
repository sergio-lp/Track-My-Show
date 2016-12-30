package com.sergio.trackmyshow.models.tmdb;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShow {
    @SerializedName("overview")
    private String overview;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("genres")
    private List<Genre> genres;
    @SerializedName("status")
    private String status;
    @SerializedName("seasons")
    private List<Season> seasonList;
    private String genresString;

    public String getOverview() {
        return overview;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getGenres() {
        if (genres != null && genres.size() >= 1) {
            StringBuilder sb = new StringBuilder();
            for (Genre g : genres) {
                if (genres.lastIndexOf(g) == genres.size() - 1) {
                    sb.append(g.getName());
                } else {
                    sb.append(g.getName()).append(", ");
                }
            }

            return sb.toString();
        }

        return null;
    }

    public String getStatus() {
        return status;
    }

    public String getGenresString() {
        return genresString;
    }
}
