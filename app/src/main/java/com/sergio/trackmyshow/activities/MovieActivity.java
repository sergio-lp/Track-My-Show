package com.sergio.trackmyshow.activities;

import android.database.Cursor;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sergio.trackmyshow.R;
import com.sergio.trackmyshow.api.tmdb.ImageGetter;
import com.sergio.trackmyshow.api.tmdb.TMDBService;
import com.sergio.trackmyshow.database.DatabaseHelper;
import com.sergio.trackmyshow.models.tmdb.Movie;
import com.sergio.trackmyshow.models.tmdb.MovieSearch;
import com.sergio.trackmyshow.util.DBUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieActivity extends AppCompatActivity {
    private ViewGroup mRootView, mContentView;
    private ImageView imgPoster, imgBackdrop;
    private TextView tvOverview, tvRelease, tvBudget, tvRevenue, tvGenres, tvStatus, tvRuntime;
    private ProgressBar mProgressBar;
    private MovieSearch mMovieSearch;
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Toolbar toolbar = (Toolbar) findViewById(R.id.movie_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRootView = (ViewGroup) findViewById(R.id.activity_movie);
        mContentView = (ViewGroup) findViewById(R.id.scroll_view_movie);
        imgPoster = (ImageView) findViewById(R.id.img_movie_poster);
        imgBackdrop = (ImageView) findViewById(R.id.img_movie_backdrop);
        tvOverview = (TextView) findViewById(R.id.tv_movie_overview);
        tvRelease = (TextView) findViewById(R.id.tv_movie_release);
        tvBudget = (TextView) findViewById(R.id.tv_movie_budget);
        tvRevenue = (TextView) findViewById(R.id.tv_movie_revenue);
        tvGenres = (TextView) findViewById(R.id.tv_movie_genres);
        tvStatus = (TextView) findViewById(R.id.tv_movie_status);
        tvRuntime = (TextView) findViewById(R.id.tv_movie_runtime);
        mProgressBar = addProgressBar();

        MovieSearch ms = getIntent().getExtras().getParcelable("MovieSearch");
        mMovie = getIntent().getExtras().getParcelable("Movie");
        mMovieSearch = ms;
        setTitle(mMovieSearch.getTitle());

        if (mMovie == null) {
            getMovieInformation();
        } else {
            setMovieInformation(mMovie);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast feedbackToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                mMovie.setWatched(false);
                String result = new DBUtil(this).actionInsert(mMovieSearch, mMovie);
                feedbackToast.setText(result);
                feedbackToast.show();
                break;
            case R.id.action_delete:
                result = new DBUtil(this).actionDelete(mMovieSearch.getId());
                feedbackToast.setText(result);
                feedbackToast.show();
                break;
            case R.id.action_watch:
                mMovie.setWatched(true);
                result = new DBUtil(this).actionInsert(mMovieSearch, mMovie);
                feedbackToast.setText(result);
                feedbackToast.show();
                break;
            case R.id.action_unwatch:
                mMovie.setWatched(false);
                result = new DBUtil(this).actionInsert(mMovieSearch, mMovie);
                feedbackToast.setText(result);
                feedbackToast.show();
                break;
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        DBUtil dbUtil = new DBUtil(this);
        MenuItem save = menu.findItem(R.id.action_save);
        MenuItem saveWatched = menu.findItem(R.id.action_watch);
        MenuItem delete = menu.findItem(R.id.action_delete);
        MenuItem deleteUnWatched = menu.findItem(R.id.action_unwatch);

        boolean movieRegistered = dbUtil.findMovie(mMovieSearch.getId());

        if (movieRegistered) {
            delete.setVisible(true);
            save.setVisible(false);
            boolean watched = dbUtil.findMovieStatus(mMovieSearch.getId());

            if (watched) {
                deleteUnWatched.setVisible(true);
                saveWatched.setVisible(false);
            } else {
                deleteUnWatched.setVisible(false);
                saveWatched.setVisible(true);
            }
        } else {
            menu.setGroupVisible(R.id.action_group_save, true);
            menu.setGroupVisible(R.id.action_group_delete, false);
        }

        return true;
    }

    private void getMovieInformation() {
        TMDBService tmdbService = new TMDBService.Builder().build();

        Call<Movie> call = tmdbService.getMovieInfo(mMovieSearch.getId());

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MovieActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT)
                            .show();
                    Log.e("URL: ", call.request().url().toString());
                } else {
                    mMovie = response.body();
                    setMovieInformation(mMovie);

                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(MovieActivity.this, "Error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT)
                        .show();
                Log.e("Error: MovieActivity", t.getMessage());
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void setMovieInformation(Movie m) {
        String releaseDate = "<b>Release date: </b>" + mMovieSearch.getReleaseDate();
        String budget = "<b>Budget: </b> US$" + m.getBudget();
        String revenue = "<b>Revenue: </b> US$" + m.getRevenue();
        String status = "<b>Status: </b>" + m.getStatus();
        String runtime = "<b>Runtime: </b>" + m.getRuntime() + " " + getResources().getString(R.string.minutes);
        String genres = "<b>Genres: </b>" + m.getGenres();

        tvOverview.setText(m.getOverview());
        tvGenres.setText(Html.fromHtml(genres));
        tvRelease.setText(Html.fromHtml(releaseDate));
        tvBudget.setText(Html.fromHtml(budget));
        tvRevenue.setText(Html.fromHtml(revenue));
        tvStatus.setText(Html.fromHtml(status));
        tvRuntime.setText(Html.fromHtml(runtime));

        removeProgressBar(mProgressBar);
        new ImageGetter(m.getBackdropPath(), "backdrop", imgBackdrop);
        new ImageGetter(mMovieSearch.getPosterPath(), "poster", imgPoster);
    }

    private ProgressBar addProgressBar() {
        mContentView.setVisibility(View.INVISIBLE);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(params);
        progressBar.setIndeterminate(true);
        mRootView.addView(progressBar);

        return progressBar;
    }

    private void removeProgressBar(ProgressBar progressBar) {
        mRootView.removeView(progressBar);
        mContentView.setVisibility(View.VISIBLE);
    }
}
