package com.example.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public final class NetworkUtils {

    //                         for specific movie                                       id here
    private static final String MOVIES_RANDOM_URL = "https://api.themoviedb.org/3/movie/157336?api_key=";

    //Correct
    //popular movies
    //http://api.themoviedb.org/3/movie/popular?api_key=c53e06736511ebefbde681d28d6bae9b





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
