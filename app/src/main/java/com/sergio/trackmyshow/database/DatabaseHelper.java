package com.sergio.trackmyshow.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sergio.trackmyshow.models.tmdb.Episode;
import com.sergio.trackmyshow.models.tmdb.Movie;
import com.sergio.trackmyshow.models.tmdb.MovieSearch;
import com.sergio.trackmyshow.models.tmdb.Season;
import com.sergio.trackmyshow.models.tmdb.SeasonInfo;
import com.sergio.trackmyshow.models.tmdb.TVShow;
import com.sergio.trackmyshow.models.tmdb.TVShowSearch;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "trackmyshow";
    private static final int DB_VERSION = 4;
    private static final String MOVIES = "movies";
    private static final String TV_SHOWS = "tv_shows";
    private static final String SEASONS = "seasons";
    private static final String EPISODES = "episodes";
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlMovies = "CREATE TABLE " + MOVIES + "(movie_id INT PRIMARY KEY, title TEXT NOT NULL," +
                " release_date DATE, poster_path TEXT, adult BOOLEAN, overview TEXT, backdrop_path TEXT," +
                " imdb_id TEXT, budget INT, revenue INT, genres TEXT, status TEXT, runtime INT, watched BOOLEAN);";

        String sqlShows = "CREATE TABLE " + TV_SHOWS + "(show_id int primary key, name text not null, " +
                "release_date text, poster_path text, overview text, backdrop_path text, genres text, status text);";

        String sqlSeasons = "CREATE TABLE " + SEASONS + "(season_id int primary key, number int not null," +
                " air_date text, poster_path text, episode_count int, name text, overview text, show_id int," +
                " FOREIGN KEY (show_id) REFERENCES " + TV_SHOWS + "(show_id));";

        String sqlEpisodes = "CREATE TABLE " + EPISODES + "(id integer primary key autoincrement, ep_num int not null, " +
                "name text not null, watched boolean, season_id int, show_id int, FOREIGN KEY (season_id) REFERENCES " +
                "" + SEASONS + "(season_id), FOREIGN KEY (show_id) REFERENCES " + TV_SHOWS + "(show_id));";


        sqLiteDatabase.execSQL(sqlMovies);
        sqLiteDatabase.execSQL(sqlShows);
        sqLiteDatabase.execSQL(sqlSeasons);
        sqLiteDatabase.execSQL(sqlEpisodes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sqlMovies = "DROP TABLE IF EXISTS " + MOVIES + ";";
        String sqlShows = "DROP TABLE IF EXISTS " + TV_SHOWS + ";";
        String sqlSeasons = "DROP TABLE IF EXISTS " + SEASONS + ";";
        String sqlEpisodes = "DROP TABLE IF EXISTS " + EPISODES + ";";
        sqLiteDatabase.execSQL(sqlMovies);
        sqLiteDatabase.execSQL(sqlShows);
        sqLiteDatabase.execSQL(sqlSeasons);
        sqLiteDatabase.execSQL(sqlEpisodes);
        onCreate(sqLiteDatabase);
    }

    public long insertMovie(MovieSearch ms, Movie m) {
        Cursor c = selectMovie(ms.getId());

        ContentValues cv = new ContentValues();
        cv.put("movie_id", ms.getId());
        cv.put("title", ms.getTitle());
        cv.put("release_date", ms.getReleaseDate());
        cv.put("poster_path", ms.getPosterPath());
        cv.put("adult", ms.isAdult());
        cv.put("overview", m.getOverview());
        cv.put("backdrop_path", m.getBackdropPath());
        cv.put("imdb_id", m.getImdbID());
        cv.put("budget", m.getBudget());
        cv.put("revenue", m.getRevenue());
        cv.put("genres", m.getGenres());
        cv.put("status", m.getStatus());
        cv.put("runtime", m.getRuntime());
        cv.put("watched", m.isWatched());

        long res = -1;
        if (c.getCount() == 0) {
            try {
                res = database.insertOrThrow(MOVIES, null, cv);
            } catch (SQLException e) {
                Log.e("insertMovie Error:", e.getLocalizedMessage());
            }
        } else {
            res = updateMovie(ms, m);
        }
        return res;
    }

    public Cursor selectAllMovies() {
        return database.query(MOVIES, null, null, null, null, null, null);
    }

    public Cursor selectMovie(int id) {
        String args[] = {Integer.toString(id)};
        return database.query(MOVIES, null, "movie_id = ?", args, null, null, null);
    }

    public int deleteMovie(int id) {
        String args[] = {Integer.toString(id)};
        return database.delete(MOVIES, "movie_id = ?", args);
    }

    private int updateMovie(MovieSearch ms, Movie m) {
        ContentValues cv = new ContentValues();
        cv.put("movie_id", ms.getId());
        cv.put("title", ms.getTitle());
        cv.put("release_date", ms.getReleaseDate());
        cv.put("poster_path", ms.getPosterPath());
        cv.put("adult", ms.isAdult());
        cv.put("overview", m.getOverview());
        cv.put("backdrop_path", m.getBackdropPath());
        cv.put("imdb_id", m.getImdbID());
        cv.put("budget", m.getBudget());
        cv.put("revenue", m.getRevenue());
        cv.put("genres", m.getGenres());
        cv.put("status", m.getStatus());
        cv.put("runtime", m.getRuntime());
        cv.put("watched", m.isWatched());
        String args[] = {Integer.toString(ms.getId())};
        return database.update(MOVIES, cv, "movie_id = ?", args);
    }

    public long insertShow(TVShowSearch ts, TVShow t, List<SeasonInfo> seasonInfoList) {
        List<Season> seasonList = t.getSeasonList();

        ContentValues cv = new ContentValues();
        cv.put("show_id", ts.getId());
        cv.put("name", ts.getName());
        cv.put("release_date", ts.getReleaseDate());
        cv.put("poster_path", ts.getPosterPath());
        cv.put("overview", t.getOverview());
        cv.put("backdrop_path", t.getBackdropPath());
        cv.put("genres", t.getGenres());
        cv.put("status", t.getStatus());

        try {
            database.insertOrThrow(TV_SHOWS, null, cv);
        } catch (SQLException e) {
            Log.e("insertShow Error: ", e.getLocalizedMessage());
            return -1;
        } finally {
            cv.clear();
        }

        for (int i = 0; i < t.getSeasonList().size(); i++) {
            Season s = seasonList.get(i);
            SeasonInfo si = seasonInfoList.get(i);
            cv.put("season_id", s.getId());
            cv.put("number", s.getNumber());
            cv.put("air_date", s.getAirDate());
            cv.put("poster_path", s.getPosterPath());
            cv.put("episode_count", s.getEpisodeCount());
            cv.put("name", si.getName());
            cv.put("overview", si.getOverview());
            cv.put("show_id", ts.getId());

            try {
                database.insertOrThrow(SEASONS, null, cv);
            } catch (SQLException e) {
                Log.e("insertSeason Error: ", e.getLocalizedMessage());
                return -1;
            } finally {
                cv.clear();
            }
        }
        cv.clear();

        for (int i = 0; i < seasonInfoList.size(); i++) {
            Season s = seasonList.get(i);
            SeasonInfo si = seasonInfoList.get(i);
            for (Episode e : si.getEpisodeList()) {
                cv.put("ep_num", e.getNumber());
                cv.put("name", e.getName());
                cv.put("watched", e.isWatched());
                cv.put("season_id", s.getId());
                cv.put("show_id", ts.getId());
                try {
                    database.insertOrThrow(EPISODES, null, cv);
                } catch (SQLException ex) {
                    Log.e("insertEpisode Error: ", ex.getLocalizedMessage());
                    return -1;
                }
            }
        }

        return -1;
    }

    public Cursor selectAllShows() {
        return database.query(false, TV_SHOWS, null, null, null, null, null, null, null, null);
    }

    public Cursor selectShow(int id) {
        String[] args = {Integer.toString(id)};
        return database.query(false, TV_SHOWS, null, "show_id = ?", args, null, null, null, null);
    }

    public List<Season> selectSeasons(int tvId) {
        String[] args = {Integer.toString(tvId)};
        Cursor c = database.query(SEASONS, null, "show_id = ?", args, null, null, null);

        List<Season> seasonList = new ArrayList<>();
        if (c.getCount() >= 1) {
            while (c.moveToNext()) {
                int id = c.getInt(0);
                int number = c.getInt(1);
                String airDate = c.getString(2);
                String posterPath = c.getString(3);
                int episodeCount = c.getInt(4);
                String name = c.getString(5);
                String overview = c.getString(6);

                Season s = new Season(id, number, airDate, posterPath, episodeCount, name);
                seasonList.add(s);
            }
        }
        c.close();
        return seasonList;

    }

    public boolean getEpisodeStatus(Episode e, int seasonId) {
        String[] args = {Integer.toString(seasonId), Integer.toString(e.getNumber())};
        try (Cursor c = database.query(EPISODES, null, "season_id = ? and ep_num = ?", args, null, null, null, null)) {
            return c.moveToNext() && c.getInt(3) == 1;
        }
    }

    public int updateEpisode(Episode ep, int seasonId) {
        ContentValues cv = new ContentValues();
        cv.put("watched", ep.isWatched());
        String[] args = {Integer.toString(ep.getNumber()), Integer.toString(seasonId)};
        return database.update(EPISODES, cv, "ep_num = ? and season_id = ?", args);

    }

    public int deleteTvShow(int id) {
        String args[] = {Integer.toString(id)};
        database.delete(SEASONS, "show_id = ?", args);
        int r = database.delete(EPISODES, "show_id = ?", args);
        return database.delete(TV_SHOWS, "show_id = ?", args);
    }
}
