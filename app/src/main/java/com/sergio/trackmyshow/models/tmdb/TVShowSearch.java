package com.sergio.trackmyshow.models.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TVShowSearch implements Parcelable {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("first_air_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private String posterPath;

    public TVShowSearch(int id, String name, String releaseDate, String posterPath) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
    }

    protected TVShowSearch(Parcel in) {
        id = in.readInt();
        name = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
    }

    public static final Creator<TVShowSearch> CREATOR = new Creator<TVShowSearch>() {
        @Override
        public TVShowSearch createFromParcel(Parcel in) {
            return new TVShowSearch(in);
        }

        @Override
        public TVShowSearch[] newArray(int size) {
            return new TVShowSearch[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
    }
}
