package com.sergio.trackmyshow.models.tmdb;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SeasonInfo implements Parcelable {
    @SerializedName("name")
    private String name;
    @SerializedName("overview")
    private String overview;
    @SerializedName("episodes")
    private List<Episode> episodeList;



    private SeasonInfo(Parcel in) {
        name = in.readString();
        overview = in.readString();
    }

    public static final Creator<SeasonInfo> CREATOR = new Creator<SeasonInfo>() {
        @Override
        public SeasonInfo createFromParcel(Parcel in) {
            return new SeasonInfo(in);
        }

        @Override
        public SeasonInfo[] newArray(int size) {
            return new SeasonInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public List<Episode> getEpisodeList() {
        return episodeList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(overview);
    }
}
