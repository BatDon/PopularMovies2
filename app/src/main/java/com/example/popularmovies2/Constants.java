package com.example.popularmovies2;

import android.text.format.Formatter;

import java.util.Locale;

public class Constants {

    public static final String UNIQUE_API_KEY="YOUR_API_KEY_HERE";

    //Movie Details Activity
    public static final String MOVIE_POSITION="MOVIE_POSITION";
    public static final int DEFAULT_POSITION=-1;

    //Picasso to create images
    public static final String BASE_IMAGE_URL="http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE="w185/";

    //MainActivity
    public static final String REQUEST_MOVIE_LIST="MOVIE_LIST";
    public static final String REQUEST_SORTED_POPULAR_MOVIES="SORTED_POPULAR_MOVIES";
    public static final String REQUEST_RELATED_MOVIES="RELATED_MOVIES_LIST";
    public static final String RELATED_KEY="RELATED_KEY";
    public static final String NO_MOVIE_ID="NO_MOVIE_ID";

    //UserFavorite Activity
    public static final String MOVIE_ID="MOVIE_ID";

    //Complete Adapter
    public static final int GRID_RECYCLER_VIEW=0;
    public static final int LIST_RECYCLER_VIEW=1;




}
