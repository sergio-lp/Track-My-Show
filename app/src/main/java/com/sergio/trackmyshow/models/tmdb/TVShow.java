package com.sergio.trackmyshow.models.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShow implements Parcelable {
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

    public TVShow(String overview, String backdropPath, String status, List<Season> seasonList, String genresString) {
        this.overview = overview;
        this.backdropPath = backdropPath;
        this.status = status;
        this.genresString = genresString;
        this.seasonList = seasonList;
    }

    private TVShow(Parcel in) {
        overview = in.readString();
        backdropPath = in.readString();
        status = in.readString();
        seasonList = in.createTypedArrayList(Season.CREATOR);
        genresString = in.readString();
    }

    public static final Creator<TVShow> CREATOR = new Creator<TVShow>() {
        @Override
        public TVShow createFromParcel(Parcel in) {
            return new TVShow(in);
        }

        @Override
        public TVShow[] newArray(int size) {
            return new TVShow[size];
        }
    };

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

    public List<Season> getSeasonList() {
        return seasonList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(overview);
        parcel.writeString(backdropPath);
        parcel.writeString(status);
        parcel.writeTypedList(seasonList);
        parcel.writeString(genresString);
    }
}
