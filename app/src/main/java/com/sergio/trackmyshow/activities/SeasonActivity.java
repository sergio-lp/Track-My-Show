package com.sergio.trackmyshow.activities;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sergio.trackmyshow.R;
import com.sergio.trackmyshow.adapter.EpisodeAdapter;
import com.sergio.trackmyshow.adapter.SeasonAdapter;
import com.sergio.trackmyshow.api.tmdb.ImageGetter;
import com.sergio.trackmyshow.api.tmdb.TMDBService;
import com.sergio.trackmyshow.models.tmdb.Episode;
import com.sergio.trackmyshow.models.tmdb.Season;
import com.sergio.trackmyshow.models.tmdb.SeasonInfo;
import com.sergio.trackmyshow.models.tmdb.TVShow;
import com.sergio.trackmyshow.models.tmdb.TVShowSearch;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeasonActivity extends AppCompatActivity {
    private ViewGroup mRootView, mContentView;
    private ImageView imgPoster;
    private TextView tvName, tvReleaseDate, tvEpCount, tvOverview;
    private RecyclerView rvEpisodes;
    private ProgressBar mProgressBar;
    private int selectedSeasonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);

        mRootView = (ViewGroup) findViewById(R.id.activity_season);
        mContentView = (ViewGroup) findViewById(R.id.scroll_view_season);
        imgPoster = (ImageView) findViewById(R.id.img_season_poster);
        tvName = (TextView) findViewById(R.id.tv_season_title);
        tvReleaseDate = (TextView) findViewById(R.id.tv_season_release);
        tvEpCount = (TextView) findViewById(R.id.tv_season_episode_count);
        tvOverview = (TextView) findViewById(R.id.tv_season_overview);
        imgPoster = (ImageView) findViewById(R.id.img_season_poster);
        rvEpisodes = (RecyclerView) findViewById(R.id.rv_season_episodes);

        Bundle extras = getIntent().getExtras();
        List<Season> seasons = extras.getParcelableArrayList("paramSeasonList");
        TVShowSearch ts = extras.getParcelable("paramTVShowSearch");
        TVShow t = extras.getParcelable("paramTVShow");
        Season ps = extras.getParcelable("paramSeason");
        getSeasonInformation(t, seasons, ts, ps);
        setTitle(ps.getTempName());
        selectedSeasonId = ps.getId();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        addProgressBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void getSeasonInformation(final TVShow tvShow, final List<Season> seasonList, final TVShowSearch ts, final Season paramSeason) {
        TMDBService tmdbService = new TMDBService.Builder().build();

        Call<SeasonInfo> call = tmdbService.getSeasonInfo(ts.getId(), paramSeason.getNumber());
        call.enqueue(new Callback<SeasonInfo>() {
            @Override
            public void onResponse(Call<SeasonInfo> call, Response<SeasonInfo> response) {
                if (response.isSuccessful()) {
                    SeasonInfo s = response.body();
                    tvName.setText(s.getName());
                    tvOverview.setText(s.getOverview());
                    tvReleaseDate.setText(ts.getReleaseDate());
                    String epCount = paramSeason.getEpisodeCount() + " " + getResources().getString(R.string.episodes);
                    tvEpCount.setText(epCount);

                    setupRecyclerView(ts, tvShow, seasonList, s.getEpisodeList());

                    removeProgressBar();
                    new ImageGetter(paramSeason.getPosterPath(), "poster", imgPoster);
                } else {
                    Log.e("Error SeasonActivity:", call.request().url() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SeasonInfo> call, Throwable t) {
                Log.e("Error SeasonActivity:", call.request().url() + " " + t.getLocalizedMessage());
            }
        });
    }

    public void addProgressBar() {
        mContentView.setVisibility(View.INVISIBLE);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mProgressBar = new ProgressBar(this);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setLayoutParams(params);
    }

    public void removeProgressBar() {
        mRootView.removeView(mProgressBar);
        mContentView.setVisibility(View.VISIBLE);
    }

    public void setupRecyclerView(TVShowSearch tvShowSearch, TVShow tvShow, List<Season> seasonList, List<Episode> episodeList) {
        System.out.println(tvShow.getSeasonList() + " <<<-----------");
        EpisodeAdapter adapter = new EpisodeAdapter(tvShowSearch, tvShow, episodeList, seasonList, this, selectedSeasonId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvEpisodes.setAdapter(adapter);
        rvEpisodes.setLayoutManager(layoutManager);
        rvEpisodes.setHasFixedSize(true);
        rvEpisodes.setNestedScrollingEnabled(false);
        rvEpisodes.setFocusable(false);
        rvEpisodes.setVisibility(View.VISIBLE);
    }
}
