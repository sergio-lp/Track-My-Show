package com.sergio.trackmyshow.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sergio.trackmyshow.R;
import com.sergio.trackmyshow.activities.MovieActivity;
import com.sergio.trackmyshow.adapter.RecyclerViewItemClick;
import com.sergio.trackmyshow.adapter.SearchResultAdapter;
import com.sergio.trackmyshow.models.tmdb.Movie;
import com.sergio.trackmyshow.models.tmdb.MovieSearch;
import com.sergio.trackmyshow.util.DBUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoviesFragment extends Fragment {
    private ViewGroup mViewGroup;

    public MoviesFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        mViewGroup = (ViewGroup) rootView.getRootView();

        new DatabaseTask().execute();
        return rootView;
    }

    private void setupRecyclerView(final List<MovieSearch> resultList, final List<Movie> movieList) {
        if (resultList.size() >= 1) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            SearchResultAdapter adapter = new SearchResultAdapter(resultList, getActivity());
            RecyclerView rvMovies = new RecyclerView(getActivity());
            rvMovies.setLayoutParams(params);
            rvMovies.setLayoutManager(layoutManager);
            rvMovies.setAdapter(adapter);
            rvMovies.setHasFixedSize(true);
            rvMovies.addOnItemTouchListener(new RecyclerViewItemClick(getActivity(), new RecyclerViewItemClick.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent i = new Intent(getContext(), MovieActivity.class);
                    Bundle extras = new Bundle();
                    extras.putParcelable("MovieSearch", resultList.get(position));
                    extras.putParcelable("Movie", (movieList.get(position)));
                    System.out.println(movieList.get(position).getGenres());
                    i.putExtras(extras);
                    startActivity(i);
                }
            }));
            mViewGroup.addView(rvMovies);
        }
    }

    private void setupEmptyTv() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        TextView tvEmpty = new TextView(getContext());
        tvEmpty.setLayoutParams(params);
        tvEmpty.setText(R.string.empty_movies);
        tvEmpty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvEmpty.setPadding(16, 0, 16, 0);
        mViewGroup.addView(tvEmpty);
    }


    private class DatabaseTask extends AsyncTask<Void, Void, Map<String, List>> {
        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            progressBar = new ProgressBar(getActivity());
            progressBar.setIndeterminate(true);
            progressBar.setLayoutParams(params);
            mViewGroup.addView(progressBar);
        }

        @Override
        protected Map<String, List> doInBackground(Void... voids) {
            Cursor c = new DBUtil(getActivity()).selectAllMovies();

            if (c.getCount() >= 1) {
                Map<String, List> map = new HashMap<>();
                List<MovieSearch> movieSearchList = new ArrayList<>();
                List<Movie> movieList = new ArrayList<>();

                while (c.moveToNext()) {
                    int id = c.getInt(0);
                    String title = c.getString(1);
                    String releaseDate = c.getString(2);
                    String posterPath = c.getString(3);
                    boolean adult = c.getInt(4) == 1;
                    String overview = c.getString(5);
                    String backdropPath = c.getString(6);
                    String imdbId = c.getString(7);
                    int budget = c.getInt(8);
                    int revenue = c.getInt(9);
                    String genres = c.getString(10);
                    String status = c.getString(11);
                    int runtime = c.getInt(12);
                    boolean watched = c.getInt(13) == 1;

                    MovieSearch ms = new MovieSearch(id, title, releaseDate, posterPath, adult);
                    Movie m = new Movie(overview, backdropPath, imdbId, budget, revenue, status, runtime, genres, watched);
                    movieSearchList.add(ms);
                    movieList.add(m);
                }

                c.close();
                map.put("MovieSearch", movieSearchList);
                map.put("Movie", movieList);
                return map;
            } else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(Map<String, List> map) {
            super.onPostExecute(map);
            mViewGroup.removeView(progressBar);
            if (map != null) {
                setupRecyclerView(map.get("MovieSearch"), map.get("Movie"));
            } else {
                setupEmptyTv();
            }
        }
    }
}
