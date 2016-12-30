package com.sergio.trackmyshow.api.tmdb;

import com.sergio.trackmyshow.api.constants.TMDBConstants;
import com.sergio.trackmyshow.fragments.ShowsFragment;
import com.sergio.trackmyshow.models.tmdb.Episode;
import com.sergio.trackmyshow.models.tmdb.Movie;
import com.sergio.trackmyshow.models.tmdb.MovieSearch;
import com.sergio.trackmyshow.models.tmdb.SearchResult;
import com.sergio.trackmyshow.models.tmdb.SeasonInfo;
import com.sergio.trackmyshow.models.tmdb.TVShow;
import com.sergio.trackmyshow.models.tmdb.TVShowSearch;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDBService {

    @GET(TMDBConstants.SEARCH_URL + TMDBConstants.MOVIE_URL)
    Call<SearchResult<MovieSearch>> searchMovies(@Query("query") String query);

    @GET(TMDBConstants.SEARCH_URL + TMDBConstants.TV_URL)
    Call<SearchResult<TVShowSearch>> searchTvShow(@Query("query") String query);

    @GET(TMDBConstants.MOVIE_URL + "{movie_id}")
    Call<Movie> getMovieInfo(@Path("movie_id") int movieId);

    @GET(TMDBConstants.TV_URL + "{tv_id}")
    Call<TVShow> getShowInfo(@Path("tv_id") int tvId);

    @GET(TMDBConstants.TV_URL + "{tv_id}/" + TMDBConstants.SEASON_URL + "{season_num}")
    Call<SeasonInfo> getSeasonInfo(@Path("tv_id") int tvId, @Path("season_num") int seasonNum);

    class Builder {

        public TMDBService build() {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new TMDBInterceptor().interceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(TMDBConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return retrofit.create(TMDBService.class);
        }
    }
}
