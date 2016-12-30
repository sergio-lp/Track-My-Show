package com.sergio.trackmyshow.models.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Season implements Parcelable {
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

    public Season(int id, int number, String airDate, String posterPath, int episodeCount, String tempName) {
        this.id = id;
        this.number = number;
        this.airDate = airDate;
        this.posterPath = posterPath;
        this.episodeCount = episodeCount;
        this.tempName = tempName;
    }

    private Season(Parcel in) {
        id = in.readInt();
        number = in.readInt();
        airDate = in.readString();
        posterPath = in.readString();
        episodeCount = in.readInt();
        tempName = in.readString();
    }

    public static final Creator<Season> CREATOR = new Creator<Season>() {
        @Override
        public Season createFromParcel(Parcel in) {
            return new Season(in);
        }

        @Override
        public Season[] newArray(int size) {
            return new Season[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(number);
        parcel.writeString(airDate);
        parcel.writeString(posterPath);
        parcel.writeInt(episodeCount);
        parcel.writeString(tempName);
    }
}
