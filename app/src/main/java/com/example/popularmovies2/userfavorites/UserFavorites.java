package com.example.popularmovies2.userfavorites;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies2.adapters.CompleteAdapter;
import com.example.popularmovies2.R;
import com.example.popularmovies2.moviedata.MovieContract;

import static com.example.popularmovies2.Constants.LIST_RECYCLER_VIEW;
import static com.example.popularmovies2.Constants.MOVIE_ID;

//public class UserFavorites extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
//        FavoritesAdapter.UserMovieOnClickHandler {
public class UserFavorites extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        CompleteAdapter.OnMovieListener {

    private final String TAG = UserFavorites.class.getSimpleName();

    public static final int LOADER_ID=13;

    private RecyclerView userRecyclerView;
//    private FavoritesAdapter userFavoritesAdapter;
    private CompleteAdapter completeAdapter;
    private int recyclerViewPosition = RecyclerView.NO_POSITION;

    private ProgressBar loadingCircle;

    //These are shown in RecyclerView of user favorites
    public static final String[] BASIC_USER_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
    };

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
        setContentView(R.layout.activity_user_favorite_list);

        Log.i(TAG,"onCreate called");

        //Remove after testing
        //FakeFavoriteMovies.insertFakeMovie(this);

        setUpViews();
        setUpItemSwiper();

    }

    private void setUpViews(){
        loadingCircle=findViewById(R.id.progressBar);
        userRecyclerView=findViewById(R.id.recyclerView);

        Log.i(TAG,"setUpViews called");

        showLoading();


        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        userRecyclerView.setLayoutManager(linearLayoutManager);
        userRecyclerView.setHasFixedSize(true);


//        userFavoritesAdapter = new FavoritesAdapter(this, this);
        completeAdapter=new CompleteAdapter(this,this,null, LIST_RECYCLER_VIEW);


//        userRecyclerView.setAdapter(userFavoritesAdapter);
        userRecyclerView.setAdapter(completeAdapter);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }


    public void setUpItemSwiper(){
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //going to delete here
                //Cursor cursor=userFavoritesAdapter.getMovieAtPosition(viewHolder.getAdapterPosition());
                Cursor cursor=completeAdapter.getMovieAtPosition(viewHolder.getAdapterPosition());
                Toast.makeText(UserFavorites.this, cursor.getString(UserFavorites.INDEX_COLUMN_TITLE)+" removed from favorites", Toast.LENGTH_SHORT).show();

                ContentResolver contentResolver=UserFavorites.this.getContentResolver();
                contentResolver.delete(createURI(cursor),null, null);

            }
        }).attachToRecyclerView(userRecyclerView);
    }

    public static final Uri createURI(Cursor cursor) {
        return MovieContract.MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(cursor.getString(UserFavorites.INDEX_COLUMN_ID))
                .build();
    }



    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, @Nullable Bundle args) {

        if(loaderID==LOADER_ID){

            Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;

            String sortOrder = null;
            String selection = null;

            Log.i(TAG,"in onCreateLoader");

            return new CursorLoader(this,
                    movieQueryUri,
                    BASIC_USER_MOVIE_PROJECTION,
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
//        userFavoritesAdapter.swapCursor(data);

        if (recyclerViewPosition == RecyclerView.NO_POSITION){
            recyclerViewPosition = 0;
        }
//        if (data != null && data.moveToFirst()){
        if (data != null && data.moveToFirst()){
            Log.i(TAG,""+data.getCount());
//            userFavoritesAdapter.swapCursor(data);
            completeAdapter.swapCursor(data);
            Log.i(TAG,"swapCursor called");
//            showRecyclerView();
        }
        else{
            Toast.makeText(this, R.string.user_message_create_movie_list, Toast.LENGTH_SHORT).show();
            Log.i(TAG,"data equals 0");
//            completeAdapter.swapCursor(data);
//            showRecyclerView();
        }
        showRecyclerView();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
//        userFavoritesAdapter.swapCursor(null);
        completeAdapter.swapCursor(null);
    }

    public void showLoading(){
        loadingCircle.setVisibility(View.VISIBLE);
        userRecyclerView.setVisibility(View.INVISIBLE);
    }
    public void showRecyclerView(){
        loadingCircle.setVisibility(View.INVISIBLE);
        userRecyclerView.setVisibility(View.VISIBLE);
    }

//    @Override
//    public void onClick(String movieId) {
//        Intent detailIntent=new Intent(this, UserFavorite.class);
//        detailIntent.putExtra(MOVIE_ID, movieId);
//        startActivity(detailIntent);
//        Log.i(TAG, "movieId= " + movieId);
//
//    }


    @Override
    public void onMovieClick(int movieIdInt) {
        String movieId=Integer.toString(movieIdInt);
        Intent detailIntent=new Intent(this, UserFavorite.class);
        detailIntent.putExtra(MOVIE_ID, movieId);
        detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(detailIntent);
        Log.i(TAG, "movieId= " + movieId);

    }
}