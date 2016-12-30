package com.sergio.trackmyshow.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sergio.trackmyshow.R;
import com.sergio.trackmyshow.activities.MovieActivity;
import com.sergio.trackmyshow.api.tmdb.ImageGetter;
import com.sergio.trackmyshow.database.DatabaseHelper;
import com.sergio.trackmyshow.models.tmdb.MovieSearch;
import com.sergio.trackmyshow.models.tmdb.SearchResult;
import com.sergio.trackmyshow.models.tmdb.TVShowSearch;
import com.sergio.trackmyshow.util.DBUtil;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder> {
    private List mResultList;
    private Context mContext;

    public SearchResultAdapter(List searchResult, Context c) {
        this.mResultList = searchResult;
        this.mContext = c;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_layout, parent, false);

        return new SearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        String title = "";
        String releaseDate = "";
        String posterPath = "";
        String watched = "";

        Object o = mResultList.get(position);
        if (o instanceof MovieSearch) {
            MovieSearch m = (MovieSearch) o;
            title = m.getTitle();
            releaseDate = m.getReleaseDate();
            posterPath = m.getPosterPath();
            watched = new DBUtil(mContext).findMovieStatus(m.getId()) ? "Watched" : "";
        } else if (o instanceof TVShowSearch) {
            TVShowSearch s = (TVShowSearch) o;
            title = s.getName();
            releaseDate = s.getReleaseDate();
            posterPath = s.getPosterPath();
        }

        holder.tvTitle.setText(title);
        holder.tvRelease.setText(releaseDate);
        holder.tvWatched.setText(watched);
        if (watched.equals("")) {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        new ImageGetter(posterPath, "poster", holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return this.mResultList.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgPoster, imgContextMenu;
        private TextView tvTitle, tvRelease, tvWatched;

        SearchViewHolder(View itemView) {
            super(itemView);
            imgPoster = (ImageView) itemView.findViewById(R.id.img_layout_poster);
            imgContextMenu = (ImageView) itemView.findViewById(R.id.img_layout_context_menu);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_layout_title);
            tvRelease = (TextView) itemView.findViewById(R.id.tv_layout_release);
            tvWatched = (TextView) itemView.findViewById(R.id.tv_layout_watched);

            imgContextMenu.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            Menu menu = popupMenu.getMenu();
            popupMenu.getMenuInflater()
                    .inflate(R.menu.menu_actions, menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        default:
                            Toast.makeText(view.getContext(), "Soon", Toast.LENGTH_SHORT)
                                    .show();
                    }
                    return true;
                }
            });

            popupMenu.show();
        }
    }
}

