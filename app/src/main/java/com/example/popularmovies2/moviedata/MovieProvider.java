package com.example.popularmovies2.moviedata;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    public static final int CODE_MOVIES=10;
    public static final int CODE_SPECIFIC_MOVIE=11;

    public static final UriMatcher sUriMovieMatcher=buildUriMovieMatcher();
    private MovieDbHelper movieOpenHelper;



    public static UriMatcher buildUriMovieMatcher() {

        final UriMatcher baseMovieMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        baseMovieMatcher.addURI(authority, MovieContract.PATH_USER_POPULAR_MOVIES, CODE_MOVIES);

        baseMovieMatcher.addURI(authority, MovieContract.PATH_USER_POPULAR_MOVIES + "/#", CODE_SPECIFIC_MOVIE);
        return baseMovieMatcher;
    }

    @Override
    public boolean onCreate() {
        movieOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor movieCursor;
        switch (sUriMovieMatcher.match(uri)) {
            case CODE_MOVIES: {
                movieCursor = movieOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            case CODE_SPECIFIC_MOVIE:{

                String select=MovieContract.MovieEntry.COLUMN_ID +" = ? ";
                String movieId=uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movieId};


                movieCursor= movieOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        select,
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Following Uri is not supported: " + uri);
        }

        movieCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return movieCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValue) {
        final SQLiteDatabase movieDb = movieOpenHelper.getWritableDatabase();

        long _id=0;
        switch (sUriMovieMatcher.match(uri)) {

            case CODE_SPECIFIC_MOVIE:
                movieDb.beginTransaction();
                int rowsInserted = 0;
                try {
                    _id = movieDb.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValue);
                    if (_id != -1) {
                        rowsInserted++;
                    }
                    movieDb.setTransactionSuccessful();
                } finally {
                    movieDb.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return Uri.parse(MovieContract.MovieEntry.TABLE_NAME + "/" + _id);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String select, @Nullable String[] selectionArguments) {
//    @Override
//    public int delete(@NonNull Uri uri) {

        final SQLiteDatabase movieDb = movieOpenHelper.getWritableDatabase();
        switch (sUriMovieMatcher.match(uri)) {

            case CODE_SPECIFIC_MOVIE:

                movieDb.beginTransaction();
                int rowsDeleted=0;

                String selection=MovieContract.MovieEntry.COLUMN_ID +" = ? ";
                String movieId=uri.getLastPathSegment();
                String[] selectionArgs = new String[]{movieId};

                try {
                    long _id = movieDb.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                    if (_id != -1) {
                        rowsDeleted++;
                    }
                    movieDb.setTransactionSuccessful();
                } finally {
                    movieDb.endTransaction();
                }

                if (rowsDeleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            default:
                return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }


}

