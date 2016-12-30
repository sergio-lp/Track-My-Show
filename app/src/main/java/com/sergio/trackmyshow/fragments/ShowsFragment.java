package com.sergio.trackmyshow.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sergio.trackmyshow.R;
import com.sergio.trackmyshow.activities.TvShowActivity;
import com.sergio.trackmyshow.adapter.RecyclerViewItemClick;
import com.sergio.trackmyshow.adapter.SearchResultAdapter;
import com.sergio.trackmyshow.models.tmdb.Season;
import com.sergio.trackmyshow.models.tmdb.TVShow;
import com.sergio.trackmyshow.models.tmdb.TVShowSearch;
import com.sergio.trackmyshow.util.DBUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowsFragment extends Fragment {
    private ViewGroup mViewGrop;

    public ShowsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_shows, container, false);
        mViewGrop = (ViewGroup) rootView.getRootView();
        new DatabaseTask().execute();

        return mViewGrop;
    }

    private void setupRecyclerView(final List<TVShow> tvShowList, final List<TVShowSearch> tvShowSearchList) {
        SearchResultAdapter adapter = new SearchResultAdapter(tvShowSearchList, getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());

        RecyclerView recyclerView = new RecyclerView(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(new RecyclerViewItemClick(getActivity(), new RecyclerViewItemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(getActivity(), TvShowActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable("TVShowSearch", tvShowSearchList.get(position));
                extras.putParcelable("TVShow", tvShowList.get(position));
                i.putExtras(extras);
                startActivity(i);
            }
        }));
        mViewGrop.addView(recyclerView);

    }

    private void setupEmptyTv() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        TextView tvEmpty = new TextView(getActivity());
        tvEmpty.setLayoutParams(params);
        tvEmpty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvEmpty.setText(R.string.empty_shows);

        mViewGrop.addView(tvEmpty);
    }

    private class DatabaseTask extends AsyncTask<Void, Void, SparseArrayCompat<List>> {
        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            progressBar = new ProgressBar(getActivity());
            progressBar.setIndeterminate(true);
            progressBar.setLayoutParams(params);
            mViewGrop.addView(progressBar);
        }

        @Override
        protected SparseArrayCompat<List> doInBackground(Void... voids) {
            SparseArrayCompat<List> sparseArray = new SparseArrayCompat<>();
            Cursor c = new DBUtil(getActivity()).selectAllTvShows();
            if (c.getCount() > 0) {
                List<TVShowSearch> tvShowSearchList = new ArrayList<>();
                List<TVShow> tvShowList = new ArrayList<>();
                while (c.moveToNext()) {
                    int id = c.getInt(0);
                    String name = c.getString(1);
                    String releaseDate = c.getString(2);
                    String posterPath = c.getString(3);
                    String overview = c.getString(4);
                    String backdropPath = c.getString(5);
                    String genres = c.getString(6);
                    String status = c.getString(7);

                    List<Season> seasonList = new DBUtil(getActivity()).selectSeason(id);

                    TVShowSearch ts = new TVShowSearch(id, name, releaseDate, posterPath);
                    TVShow t = new TVShow(overview, backdropPath, status, seasonList, genres);
                    tvShowSearchList.add(ts);
                    tvShowList.add(t);
                }
                sparseArray.put(0, tvShowSearchList);
                sparseArray.put(1, tvShowList);
            }
            return sparseArray;
        }

        @Override
        protected void onPostExecute(SparseArrayCompat<List> sparseArray) {
            if (sparseArray.size() >= 2) {
                List<TVShowSearch> tvShowSearchList = sparseArray.get(0);
                List<TVShow> tvShowList = sparseArray.get(1);
                setupRecyclerView(tvShowList, tvShowSearchList);

            } else {
                setupEmptyTv();
            }
            mViewGrop.removeView(progressBar);
        }
    }

}
