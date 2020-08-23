package com.example.popularmovies2.fakedata;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;


import com.example.popularmovies2.moviedata.MovieContract;
import com.example.popularmovies2.moviedata.MovieContract.MovieEntry;
import com.example.popularmovies2.moviedata.MovieProvider;


public class FakeFavoriteMovies {

    private static ContentValues createTestWeatherContentValues() {
        ContentValues testMovie = new ContentValues();
        //testMovie.put(MovieEntry._ID,0);
        testMovie.put(MovieEntry.COLUMN_ID, 5);
        testMovie.put(MovieEntry.COLUMN_TITLE, "Movie Title");
        testMovie.put(MovieEntry.COLUMN_POSTER, "path to poster");
        testMovie.put(MovieEntry.COLUMN_PLOT, "plot of movie");
        testMovie.put(MovieEntry.COLUMN_USER_RATING, 3.44);
        return testMovie;
    }

    public static final Uri createURI(){
        return MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(Integer.toString(MovieProvider.CODE_SPECIFIC_MOVIE))
                .build();
    }


    public static void insertFakeMovie(Context context){
        context.getContentResolver().insert(
                createURI(),
               createTestWeatherContentValues());
    }
}
