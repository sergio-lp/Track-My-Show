package com.sergio.trackmyshow.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sergio.trackmyshow.R;
import com.sergio.trackmyshow.api.tmdb.ImageGetter;
import com.sergio.trackmyshow.models.tmdb.Season;

import java.util.List;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.SeasonVH> {
    private List<Season> seasonList;
    private Resources resources;

    public SeasonAdapter(List<Season> list, Resources r) {
        this.seasonList = list;
        this.resources = r;
    }

    @Override
    public SeasonVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.season_layout, parent, false);
        return new SeasonVH(v);
    }

    @Override
    public void onBindViewHolder(SeasonVH holder, int position) {
        Season s = seasonList.get(position);

        String epCount = s.getEpisodeCount() + " " + resources.getString(R.string.episodes);
        String releaseDate = s.getAirDate();
        String posterPath = s.getPosterPath();

        holder.tvTitle.setText(s.getTempName());
        holder.tvRelease.setText(releaseDate);
        holder.tvEpCount.setText(epCount);
        new ImageGetter(posterPath, "poster", holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return seasonList.size();
    }

    class SeasonVH extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvTitle, tvRelease, tvEpCount;

        SeasonVH(View itemView) {
            super(itemView);
            imgPoster = (ImageView) itemView.findViewById(R.id.img_seasonl_poster);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_seasonl_title);
            tvRelease = (TextView) itemView.findViewById(R.id.tv_seasonl_release);
            tvEpCount = (TextView) itemView.findViewById(R.id.tv_seasonl_ep_count);
        }
    }
}
