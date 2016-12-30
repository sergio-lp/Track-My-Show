package com.sergio.trackmyshow.models.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieSearch extends SearchResult implements Parcelable {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("adult")
    private boolean adult;

    public MovieSearch(int id, String title, String releaseDate, String posterPath, boolean adult) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.adult = adult;
    }

    private MovieSearch(Parcel in) {
        id = in.readInt();
        title = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        int b = in.readInt();
        adult = b == 1;
    }

    public static final Creator<MovieSearch> CREATOR = new Creator<MovieSearch>() {
        @Override
        public MovieSearch createFromParcel(Parcel in) {
            return new MovieSearch(in);
        }

        @Override
        public MovieSearch[] newArray(int size) {
            return new MovieSearch[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        if (releaseDate != null) {
            return releaseDate.replace("-", "/");
        } else {
            return null;
        }
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        if (posterPath != null) {
            return posterPath;
        } else {
            return null;
        }
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
        parcel.writeInt(adult ? 1 : 0);
    }
}
