package com.example.popularmovies2.moviedata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.popularmovies2.moviedata.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "user_popular_movie.db";
    public static final int DATABASE_VERSION=1;
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_USER_POPULAR_MOVIE_TABLE =

                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                        MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_POSTER   + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_PLOT   + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_USER_RATING   + " DOUBLE NOT NULL, " +
                        " UNIQUE (" + MovieEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

                //Title, poster, plot string Before real not null
                //UserRating double
        sqLiteDatabase.execSQL(SQL_CREATE_USER_POPULAR_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
