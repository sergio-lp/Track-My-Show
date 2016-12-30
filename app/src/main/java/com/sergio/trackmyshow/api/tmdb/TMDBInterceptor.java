package com.sergio.trackmyshow.api.tmdb;

import com.sergio.trackmyshow.api.constants.TMDBConstants;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TMDBInterceptor {

    public Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            HttpUrl requestURL = chain.request().url()
                    .newBuilder()
                    .addQueryParameter("api_key", TMDBConstants.API_KEY)
                    .addQueryParameter("language", TMDBConstants.EN_US)
                    .build();

            Request request = chain.request().newBuilder()
                    .url(requestURL).build();

            return chain.proceed(request);
        }
    };
}
