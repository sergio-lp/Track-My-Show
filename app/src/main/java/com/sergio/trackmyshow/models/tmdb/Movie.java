package com.sergio.trackmyshow.models.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class Movie implements Parcelable {
    @SerializedName("overview")
    private String overview;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("imdb_id")
    private String imdbID;
    @SerializedName("budget")
    private int budget;
    @SerializedName("revenue")
    private int revenue;
    @SerializedName("genres")
    private List<Genre> genres;
    @SerializedName("status")
    private String status;
    @SerializedName("runtime")
    private int runtime;
    private String genresString;
    private boolean watched;

    public Movie(String overview, String backdropPath, String imdbID, int budget, int revenue, String status, int runtime, String genresString, boolean watched) {
        this.overview = overview;
        this.backdropPath = backdropPath;
        this.imdbID = imdbID;
        this.budget = budget;
        this.revenue = revenue;
        this.status = status;
        this.runtime = runtime;
        this.genresString = genresString;
        this.watched = watched;
    }

    private Movie(Parcel in) {
        overview = in.readString();
        backdropPath = in.readString();
        imdbID = in.readString();
        budget = in.readInt();
        revenue = in.readInt();
        genresString = in.readString();
        status = in.readString();
        runtime = in.readInt();
        watched = in.readInt() == 1;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getOverview() {
        return overview;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getBudget() {
        return " " + NumberFormat.getNumberInstance(Locale.US).format(budget);
    }

    public String getRevenue() {
        return " " + NumberFormat.getNumberInstance(Locale.US).format(revenue);
    }

    public String getStatus() {
        return status;
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

        return genresString;
    }

    public int getRuntime() {
        return runtime;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(overview);
        parcel.writeString(backdropPath);
        parcel.writeString(imdbID);
        parcel.writeInt(budget);
        parcel.writeInt(revenue);
        parcel.writeString(genresString);
        parcel.writeString(status);
        parcel.writeInt(runtime);
        parcel.writeInt(watched ? 1 : 0);
    }
}
