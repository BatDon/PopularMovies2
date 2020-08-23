package com.example.popularmovies2.userfavorites;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;


import com.example.popularmovies2.R;
import com.example.popularmovies2.moviedata.MovieContract;

import static com.example.popularmovies2.Constants.BASE_IMAGE_URL;
import static com.example.popularmovies2.Constants.DEFAULT_POSITION;
import static com.example.popularmovies2.Constants.IMAGE_SIZE;
import static com.example.popularmovies2.Constants.MOVIE_ID;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class UserFavorite extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    String movieId;
    public static final int LOADER_ID=16;
    private final String TAG = UserFavorite.class.getSimpleName();
    private ConstraintLayout movieDetails;
    private ProgressBar loadingCircle;

    TextView movieIdTV;
    ImageView movieImageIV;
    TextView movieTitleTV;
    TextView ratingTV;
    TextView plotTV;
    ImageButton unfavoriteIB;
    Button relatedB;



    //Shown when user clicks on recyclerview favorite item
    public static final String[] ALL_USER_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_PLOT,
            MovieContract.MovieEntry.COLUMN_USER_RATING
    };

    public static final int INDEX_COLUMN_ID = 0;
    public static final int INDEX_COLUMN_TITLE = 1;
    public static final int INDEX_COLUMN_POSTER= 2;
    public static final int INDEX_COLUMN_PLOT = 3;
    public static final int INDEX_COLUMN_USER_RATING = 4;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Log.i(TAG,"onCreate called");

        setUpViews();

    }


    public void getMovieId(){
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        movieId = intent.getStringExtra(MOVIE_ID);

//        if(movieId<0){
//            closeOnError();
//        }

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }


    private void setUpViews(){
        //loadingCircle=findViewById(R.id.progressBar);
        movieDetails =findViewById(R.id.constraintLayout);

        movieTitleTV=findViewById(R.id.movieTitle);
        movieImageIV=findViewById(R.id.movieImage);
        ratingTV=findViewById(R.id.rating);
        movieIdTV=findViewById(R.id.id);
        plotTV=findViewById(R.id.plot);
        unfavoriteIB=findViewById(R.id.favoriteButton);
        relatedB=findViewById(R.id.relatedButton);

        unfavoriteIB.setImageResource(R.drawable.blank_star);


        //showLoading();
        getMovieId();

        Bundle bundle=new Bundle();
        bundle.putString(MOVIE_ID, movieId);

        getSupportLoaderManager().initLoader(LOADER_ID, bundle, this);
    }






    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, @Nullable Bundle args) {

        if(loaderID==LOADER_ID){

            String id=args.getString(MOVIE_ID);

            Uri queryUri=MovieContract.MovieEntry.CONTENT_URI.buildUpon()
                    .appendPath(id)
                    .build();

            String sortOrder = null;
            String selection = null;

            Log.i(TAG,"in onCreateLoader");

            return new CursorLoader(this,
                    queryUri,
                    ALL_USER_MOVIE_PROJECTION,
                    selection,
                    null,
                    sortOrder);
        }
        else{
            throw new RuntimeException("Loader Id: " + loaderID+" Not implemented");
        }

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

//        if (data != null && data.moveToFirst()){
        if (data != null && data.getCount()>0){
            Log.i(TAG,""+data.getCount());
            data.moveToFirst();
            movieIdTV.setText(data.getString(INDEX_COLUMN_ID));
            movieTitleTV.setText(data.getString(INDEX_COLUMN_TITLE));
            String poster=data.getString(INDEX_COLUMN_POSTER);

            Picasso.get().load(BASE_IMAGE_URL+IMAGE_SIZE+poster).into(movieImageIV);

            plotTV.setText(data.getString(INDEX_COLUMN_PLOT));
            Double rating=data.getDouble(INDEX_COLUMN_USER_RATING);
            String voteAverageString=String.format(Locale.US,getString(R.string.format_double), rating);
            ratingTV.setText(voteAverageString);
            //showMovieDetails();

            unfavoriteIB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentResolver contentResolver=UserFavorite.this.getContentResolver();
                    contentResolver.delete(createURI(data),null, null);
                    Toast.makeText(UserFavorite.this, data.getString(INDEX_COLUMN_TITLE)+" "+getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            Log.i(TAG,"data equals 0");
            return;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    public static final Uri createURI(Cursor cursor) {
        return MovieContract.MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(cursor.getString(UserFavorites.INDEX_COLUMN_ID))
                .build();
    }

//    public void showLoading(){
//        loadingCircle.setVisibility(View.VISIBLE);
//        movieDetails.setVisibility(View.INVISIBLE);
//    }
//    public void showMovieDetails(){
//        loadingCircle.setVisibility(View.INVISIBLE);
//        movieDetails.setVisibility(View.VISIBLE);
//    }

}
