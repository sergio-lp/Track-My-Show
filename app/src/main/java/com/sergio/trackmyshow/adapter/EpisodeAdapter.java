package com.sergio.trackmyshow.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sergio.trackmyshow.R;
import com.sergio.trackmyshow.api.tmdb.TMDBService;
import com.sergio.trackmyshow.database.DatabaseHelper;
import com.sergio.trackmyshow.models.tmdb.Episode;
import com.sergio.trackmyshow.models.tmdb.Season;
import com.sergio.trackmyshow.models.tmdb.SeasonInfo;
import com.sergio.trackmyshow.models.tmdb.TVShow;
import com.sergio.trackmyshow.models.tmdb.TVShowSearch;
import com.sergio.trackmyshow.util.DBUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeVH> {
    private TVShowSearch tvShowSearch;
    private TVShow tvShow;
    private List<Episode> episodeList;
    private List<Season> seasonList;
    private Context mContext;
    private int selectedSeasonId;

    public EpisodeAdapter(TVShowSearch tvShowSearch, TVShow tvShow, List<Episode> episodeList, List<Season> seasonList, Context context, int sId) {
        this.tvShowSearch = tvShowSearch;
        this.tvShow = tvShow;
        this.episodeList = episodeList;
        this.seasonList = seasonList;
        this.mContext = context;
        this.selectedSeasonId = sId;
    }

    @Override
    public EpisodeVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_layout, parent, false);
        return new EpisodeVH(v);
    }

    @Override
    public void onBindViewHolder(final EpisodeVH holder, int position) {
        final Episode e = episodeList.get(position);
        final boolean watched = getEpisodeStatus(e);
        if (!watched) {
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.imgBtnAction.setImageDrawable(mContext.getDrawable(R.drawable.ic_check_all_black_24dp));
        } else {
            holder.tvName.setTextColor(mContext.getResources().getColor(android.R.color.secondary_text_light_nodisable));
            holder.imgBtnAction.setImageDrawable(mContext.getDrawable(R.drawable.ic_close_grey600_24dp));
        }

        String name = e.getNumber() + " - " + e.getName();
        holder.tvName.setText(name);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!watched) {
                    e.setWatched(true);
                } else {
                    e.setWatched(false);
                }
                new getSeasonInfoTask(e, holder.imgBtnAction, holder.rootView, holder.getAdapterPosition()).execute();
            }
        });

    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    private boolean getEpisodeStatus(Episode e) {
        return new DatabaseHelper(mContext).getEpisodeStatus(e, selectedSeasonId);
    }

    private String updateEpisode(Episode e) {
        return new DBUtil(mContext).updateEpisode(e, selectedSeasonId);

    }

    class EpisodeVH extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView imgBtnAction;
        private ViewGroup rootView;

        private EpisodeVH(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_episode_name);
            imgBtnAction = (ImageView) itemView.findViewById(R.id.img_episode_action);
            rootView = (ViewGroup) itemView.findViewById(R.id.btn_episode_action);
        }
    }

    private class getSeasonInfoTask extends AsyncTask<Void, Void, List<SeasonInfo>> {
        private Episode episode;
        private ImageView btn;
        private ViewGroup rootView;
        private int index;
        private int position;

        private getSeasonInfoTask(Episode e, ImageView imageView, ViewGroup viewGroup, int pos) {
            this.episode = e;
            this.btn = imageView;
            this.rootView = viewGroup;
            this.position = pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btn.getLayoutParams();
            params.gravity = Gravity.CENTER;
            int index = rootView.indexOfChild(btn);
            ProgressBar progressBar = new ProgressBar(mContext);
            progressBar.setLayoutParams(params);
            progressBar.setIndeterminate(true);
            rootView.removeViewAt(index);
            rootView.addView(progressBar, index);
            this.index = index;
        }

        @Override
        protected List<SeasonInfo> doInBackground(Void... voids) {
            boolean alreadyInDB = new DBUtil(mContext).findTvShow(tvShowSearch.getId());
            if (!alreadyInDB) {
                final List<SeasonInfo> seasonInfoList = new ArrayList<>();
                TMDBService service = new TMDBService.Builder().build();
                for (Season s : seasonList) {
                    Call<SeasonInfo> call = service.getSeasonInfo(tvShowSearch.getId(), s.getNumber());

                    try {
                        Response<SeasonInfo> response = call.execute();
                        seasonInfoList.add(response.body());
                    } catch (IOException e) {
                        Log.e("EpisodeAdapter: ", e.getLocalizedMessage());
                        return null;
                    }
                }

                return seasonInfoList;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<SeasonInfo> seasonInfoList) {
            super.onPostExecute(seasonInfoList);
            if (seasonInfoList != null && seasonInfoList.size() >= 1) {
                new DBUtil(mContext).insertTvShow(tvShowSearch, tvShow, seasonInfoList);
            }
            rootView.removeViewAt(index);
            rootView.addView(btn, index);
            String result = updateEpisode(episode);
            Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
            notifyItemChanged(position);
        }
    }
}
