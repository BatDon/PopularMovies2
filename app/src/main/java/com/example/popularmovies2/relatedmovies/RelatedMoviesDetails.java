package com.example.popularmovies2.relatedmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.popularmovies2.MovieDetails;
import com.example.popularmovies2.R;
import com.example.popularmovies2.fetchdata.pojos.Result;
import com.example.popularmovies2.moviedata.MovieContract.MovieEntry;
import com.example.popularmovies2.moviedata.MovieProvider;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.popularmovies2.Constants.BASE_IMAGE_URL;
import static com.example.popularmovies2.Constants.DEFAULT_POSITION;
import static com.example.popularmovies2.Constants.IMAGE_SIZE;
import static com.example.popularmovies2.Constants.MOVIE_POSITION;
import static com.example.popularmovies2.Constants.RELATED_KEY;

public class RelatedMoviesDetails extends AppCompatActivity {


    public static final String TAG= MovieDetails.class.getSimpleName();

    List<Result> resultList;
    Context context=this;
    int position;
    ImageView movieImageIV;
    TextView movieTitleTV;
    TextView ratingTV;
    TextView releaseDateTV;
    TextView plotTV;
    ImageButton favoriteB;
    Button relatedB;

    Result movie;


    int movieId;
    String movieTitleString;
    String movieImageString;
    String moviePlotString;
    Double movieVoteAverage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.related_movie_details);

        getMoviePosition();
        setUpUI();
        getMovieDir();
    }


    public void getMoviePosition(){
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        position = intent.getIntExtra(MOVIE_POSITION, DEFAULT_POSITION);

        if(position==DEFAULT_POSITION){
            closeOnError();
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void setUpUI(){
        movieTitleTV=findViewById(R.id.movieTitle);
        movieImageIV=findViewById(R.id.movieImage);
        ratingTV=findViewById(R.id.rating);
        releaseDateTV=findViewById(R.id.id);
        plotTV=findViewById(R.id.plot);
        favoriteB=findViewById(R.id.favoriteButton);
        relatedB=findViewById(R.id.relatedButton);

    }

    private void getMovieDir(){
        ArrayList<Result> movieList;

        try
        {
            FileInputStream fis = new FileInputStream(new File(getString(R.string.pathToRelatedMoviesFile)));
            ObjectInputStream ois = new ObjectInputStream(fis);
            movieList = (ArrayList) ois.readObject();

            ois.close();
            fis.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            return;
        }
        catch (ClassNotFoundException c)
        {
            System.out.println(getString(R.string.class_not_found));
            c.printStackTrace();
            return;
        }

        setValues(movieList);
    }



    private void setValues(ArrayList<Result> movieList){
        movie=movieList.get(position);

        movieId=movie.getId();

        movieTitleString =movie.getOriginalTitle();
        movieTitleTV.setText(movieTitleString);

        movieImageString=movie.getBackdropPath();
        Picasso.get().load(BASE_IMAGE_URL+IMAGE_SIZE+movieImageString).into(movieImageIV);

        String releaseDateString=movie.getReleaseDate();
        releaseDateTV.setText(releaseDateString);

        moviePlotString =movie.getOverview();
        plotTV.setText(moviePlotString);

        movieVoteAverage =movie.getVoteAverage();
        String voteAverageString=String.format(Locale.US,getString(R.string.format_double), movieVoteAverage);

        ratingTV.setText(voteAverageString);

    }



    public void favoriteClick(View view) {
        insertFavoriteMovie(context);
        Toast.makeText(this, movieTitleString+getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();
    }

    private void insertFavoriteMovie(Context context){
        context.getContentResolver().insert(
                createURI(),
                createMoveContentValues());
    }

    public static final Uri createURI(){
        return MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(Integer.toString(MovieProvider.CODE_SPECIFIC_MOVIE))
                .build();
    }

    private ContentValues createMoveContentValues() {
        ContentValues favoriteMovie = new ContentValues();

        favoriteMovie.put(MovieEntry.COLUMN_ID, movieId);
        favoriteMovie.put(MovieEntry.COLUMN_TITLE, movieTitleString);
        favoriteMovie.put(MovieEntry.COLUMN_POSTER, movieImageString);
        favoriteMovie.put(MovieEntry.COLUMN_PLOT, moviePlotString);
        favoriteMovie.put(MovieEntry.COLUMN_USER_RATING, movieVoteAverage);
        return favoriteMovie;
    }

    public void relatedMovies(View view) {
        Intent intent=new Intent(RelatedMoviesDetails.this, RelatedMoviesList.class);
        intent.putExtra(RELATED_KEY,Integer.toString(movieId));
        Log.i(TAG,"related movie id"+movieId);
        startActivity(intent);
    }

    public void onRetrofitFinished(List<Result> movieList){
        resultList=movieList;
        String movieTitle=movieList.get(0).getOriginalTitle();
        Toast.makeText(context, movieTitle, Toast.LENGTH_SHORT).show();
    }


}
