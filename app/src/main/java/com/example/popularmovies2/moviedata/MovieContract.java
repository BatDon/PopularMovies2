package com.example.popularmovies2.moviedata;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY="com.example.popularmovies.moviedata";

    public static final Uri BASE_CONTENT_URI= Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_USER_POPULAR_MOVIES="user_popular";


    public static final class MovieEntry implements BaseColumns {


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER_POPULAR_MOVIES)
                .build();


        public static final String TABLE_NAME = "user_popular";

        //Table Columns
//        public static final String _ID="table_id";
        public static final String COLUMN_ID = "movie_id";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_POSTER="poster";
        public static final String COLUMN_PLOT="plot";
        public static final String COLUMN_USER_RATING="user_rating";

    }

}
