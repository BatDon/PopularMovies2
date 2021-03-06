package com.example.popularmovies2.fetchdata;

import android.net.Uri;
import android.util.Log;

public final class NetworkUtils {

    //                         for specific movie                                       id here
    private static final String MOVIES_RANDOM_URL = "https://api.themoviedb.org/3/movie/157336?api_key=";

    public static String createMoviesURL(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("");
        builder.build();
        String popularMoviesURL= builder.build().toString();
        Log.i("NetworkUtils","url= "+popularMoviesURL);
        return popularMoviesURL;
    }
}
