package com.sergio.trackmyshow.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sergio.trackmyshow.R;
import com.sergio.trackmyshow.adapter.RecyclerViewItemClick;
import com.sergio.trackmyshow.adapter.SearchResultAdapter;
import com.sergio.trackmyshow.api.tmdb.TMDBService;
import com.sergio.trackmyshow.models.tmdb.MovieSearch;
import com.sergio.trackmyshow.models.tmdb.SearchResult;
import com.sergio.trackmyshow.models.tmdb.TVShowSearch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private int type;
    private ViewGroup mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(R.string.search);

        mRootView = (ViewGroup) findViewById(R.id.activity_search);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("SearchActivity: ", "New Intent");
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.item_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchItem.expandActionView();

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return true;
            }
        });

        if (this.getIntent().getAction().equals("search.Movie")) {
            this.type = 1;
            searchView.setQueryHint(getResources().getString(R.string.search_movie));
        } else if (this.getIntent().getAction().equals("search.TvShow")) {
            this.type = 2;
            searchView.setQueryHint(getResources().getString(R.string.search_show));
        }

        return true;
    }

    private void setupRecyclerView(ViewGroup rootView, final SearchResult searchResult) {
        rootView.removeAllViews();
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayoutManager manager = new LinearLayoutManager(rootView.getContext());
        SearchResultAdapter adapter = new SearchResultAdapter(searchResult.getResultList(), this);

        RecyclerView rvResults = new RecyclerView(rootView.getContext());
        rvResults.setHasFixedSize(true);
        rvResults.setLayoutParams(params);
        rvResults.setLayoutManager(manager);
        rvResults.setAdapter(adapter);
        rvResults.addOnItemTouchListener(new RecyclerViewItemClick(this,
                new RecyclerViewItemClick.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent();
                        Bundle extras = new Bundle();
                        if (searchResult.getResultList().get(0) instanceof MovieSearch) {
                            i = new Intent(SearchActivity.this, MovieActivity.class);
                            MovieSearch ms = (MovieSearch) searchResult.getResultList().get(position);
                            extras.putParcelable("MovieSearch", ms);

                        } else if (searchResult.getResultList().get(0) instanceof TVShowSearch) {
                            i = new Intent(SearchActivity.this, TvShowActivity.class);
                            TVShowSearch ts = (TVShowSearch) searchResult.getResultList().get(position);
                            extras.putParcelable("TVShowSearch", ts);
                        }

                        i.putExtras(extras);
                        startActivity(i);
                    }
                }));

        rootView.addView(rvResults);
    }

    private void setupEmptyResult(ViewGroup rootView) {
        rootView.removeAllViews();
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        TextView tvEmpty = new TextView(rootView.getContext());
        tvEmpty.setLayoutParams(params);
        tvEmpty.setText(R.string.no_results);
        rootView.addView(tvEmpty);
    }

    private ProgressBar setupProgressBar(ViewGroup rootView) {
        rootView.removeAllViews();
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        ProgressBar progressBar = new ProgressBar(rootView.getContext());
        progressBar.setIndeterminate(true);
        progressBar.setLayoutParams(params);

        rootView.addView(progressBar);

        return progressBar;
    }

    private void handleIntent(Intent i) {
        String action = i.getAction();

        if (action.equals(Intent.ACTION_SEARCH)) {
            setupProgressBar(mRootView);
            String query = i.getStringExtra(SearchManager.QUERY);
            query = query.replace(" ", "%25");

            TMDBService tmdbService = new TMDBService.Builder().build();

            Callback callback = new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    SearchResult searchResult = (SearchResult) response.body();

                    if (searchResult != null) {
                        if (searchResult.getResultList().size() >= 1) {
                            setupRecyclerView(mRootView, searchResult);
                        } else {
                            setupEmptyResult(mRootView);
                        }
                    }

                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    mRootView.removeAllViews();
                    Toast.makeText(SearchActivity.this, "Error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            };

            if (this.type == 1) {
                Call<SearchResult<MovieSearch>> call = tmdbService.searchMovies(query);
                call.enqueue(callback);
            } else {
                Call<SearchResult<TVShowSearch>> call = tmdbService.searchTvShow(query);
                call.enqueue(callback);
            }
        }
    }
}
