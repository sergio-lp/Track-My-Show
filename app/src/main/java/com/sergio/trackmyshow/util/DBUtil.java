package com.sergio.trackmyshow.util;

import android.content.Context;
import android.database.Cursor;

import com.sergio.trackmyshow.R;
import com.sergio.trackmyshow.database.DatabaseHelper;
import com.sergio.trackmyshow.models.tmdb.Episode;
import com.sergio.trackmyshow.models.tmdb.Movie;
import com.sergio.trackmyshow.models.tmdb.MovieSearch;
import com.sergio.trackmyshow.models.tmdb.Season;
import com.sergio.trackmyshow.models.tmdb.SeasonInfo;
import com.sergio.trackmyshow.models.tmdb.TVShow;
import com.sergio.trackmyshow.models.tmdb.TVShowSearch;

import java.util.List;

public class DBUtil {
    private DatabaseHelper dbHelper;
    private String success;
    private String fail;
    private String episodeWatched;
    private String episodeUnwatched;

    public DBUtil(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.success = context.getString(R.string.success);
        this.fail = context.getString(R.string.error);
        this.episodeWatched = context.getString(R.string.episode_watched);
        this.episodeUnwatched = context.getString(R.string.episode_unwatched);
    }

    public String actionInsert(MovieSearch ms, Movie m) {
        try {
            long result = dbHelper.insertMovie(ms, m);

            if (result == -1) {
                return fail;
            } else {
                return success;
            }
        } finally {
            dbHelper.close();
        }
    }

    public String actionDelete(int id) {
        try {
            int result = dbHelper.deleteMovie(id);

            if (result == 1) {
                return success;
            } else {
                return fail;
            }
        } finally {
            dbHelper.close();
        }
    }

    public Cursor selectAllMovies() {
        return dbHelper.selectAllMovies();
    }

    public boolean findMovie(int id) {
        Cursor c = dbHelper.selectMovie(id);
        try {
            return c.getCount() >= 1;
        } finally {
            c.close();
            dbHelper.close();
        }
    }

    public boolean findMovieStatus(int id) {
        if (findMovie(id)) {
            try (Cursor c = dbHelper.selectMovie(id)) {
                c.moveToNext();
                return c.getInt(13) == 1;
            } finally {
                dbHelper.close();
            }
        } else {
            try {
                return false;
            } finally {
                dbHelper.close();
            }
        }
    }

    public String insertTvShow(TVShowSearch ts, TVShow t, List<SeasonInfo> seasonInfoList) {
        try {
            long result = dbHelper.insertShow(ts, t, seasonInfoList);
            if (result != -1) {
                return success;
            } else {
                return fail;
            }
        } finally {
            dbHelper.close();
        }
    }

    public boolean findTvShow(int id) {
        Cursor c = dbHelper.selectShow(id);
        try {
            return c.getCount() >= 1;
        } finally {
            c.close();
            dbHelper.close();
        }
    }

    public String updateEpisode(Episode e, int seasonId) {
        int result = dbHelper.updateEpisode(e, seasonId);
        try {
            if (result == 1) {
                if (e.isWatched()) {
                    return episodeWatched;
                } else {
                    return episodeUnwatched;
                }
            } else {
                return fail;
            }
        } finally {
            dbHelper.close();
        }
    }

    public Cursor selectAllTvShows() {
        return dbHelper.selectAllShows();

    }
}
