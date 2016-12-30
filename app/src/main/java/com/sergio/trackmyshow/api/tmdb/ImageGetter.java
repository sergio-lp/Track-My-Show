package com.sergio.trackmyshow.api.tmdb;

import android.widget.ImageView;
import com.sergio.trackmyshow.R;
import com.sergio.trackmyshow.api.constants.TMDBConstants;
import com.squareup.picasso.Picasso;

public class ImageGetter {

    public ImageGetter(String path, String res, ImageView target) {
        if (res.equals("poster")) {
            res = TMDBConstants.RES_LOW;
        } else {
            res = TMDBConstants.RES_HIGH;
        }

        String url = TMDBConstants.IMAGES_URL + res + path;
        Picasso.with(target.getContext())
                .load(url)
                .error(R.drawable.not_avaliable)
                .into(target);
    }
}
