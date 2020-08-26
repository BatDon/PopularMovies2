package com.example.popularmovies2.viewmodels;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.popularmovies2.R;
import com.example.popularmovies2.RetrofitRequesters.RetrofitRequesterReviews;
import com.example.popularmovies2.RetrofitRequesters.RetrofitRequesterTrailer;
import com.example.popularmovies2.fetchdata.pojos.Result;
import com.example.popularmovies2.fetchdata.pojos.ReviewPojo;
import com.example.popularmovies2.fetchdata.pojos.TrailerMoviePojo;
import com.example.popularmovies2.moviedata.MovieContract;
import com.example.popularmovies2.moviedata.MovieContract.MovieEntry;
import com.example.popularmovies2.moviedata.MovieProvider;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RelatedMovieDetailsViewModel extends AndroidViewModel implements
        RetrofitRequesterTrailer.TrailersOnRetrofitListener, RetrofitRequesterReviews.ReviewsOnRetrofitListener{

    Context context;

    Result movie;

    int position;

    int movieId;
    String movieTitleString;
    String movieImageString;
    String releaseDateString;
    String moviePlotString;
    Double movieVoteAverage;
    String voteAverageString;

    public MutableLiveData<List<TrailerMoviePojo>> liveDataTrailerMoviePojoList=new MutableLiveData<List<TrailerMoviePojo>>(){};
    List<TrailerMoviePojo> trailerResultList;

    public MutableLiveData<List<ReviewPojo>> liveDataReviewPojoList=new MutableLiveData<List<ReviewPojo>>(){};
    List<ReviewPojo> reviewResultList;

    //    public MovieDetailsViewModel(@NonNull Application application, @NonNull int position, int movieId) {
    public RelatedMovieDetailsViewModel(@NonNull Application application, @NonNull int position) {
        super(application);
        context=application;
        this.position=position;

        //initialize trailer livedata object
        TrailerMoviePojo trailerMoviePojo=new TrailerMoviePojo(context.getString(R.string.key_inititialize_mutable_live_data));
        List<TrailerMoviePojo> trailerMoviePojoList=new ArrayList<>();
        trailerMoviePojoList.add(trailerMoviePojo);
        liveDataTrailerMoviePojoList.setValue(trailerMoviePojoList);

        //initialize review livedata object
        ReviewPojo reviewPojo=new ReviewPojo(context.getString(R.string.author_inititialize_mutable_live_data));
        List<ReviewPojo> reviewPojoList=new ArrayList<>();
        reviewPojoList.add(reviewPojo);
        liveDataReviewPojoList.setValue(reviewPojoList);

        getMovieDir();
        getTrailers();
        getReviews();
    }


    private void getMovieDir() {
        ArrayList<Result> movieList;

        try {
            FileInputStream fis = new FileInputStream(new File(context.getString(R.string.pathToFile)));
            ObjectInputStream ois = new ObjectInputStream(fis);
            movieList = (ArrayList) ois.readObject();

            ois.close();
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println(context.getString(R.string.class_not_found));
            c.printStackTrace();
            return;
        }

        setValuesForFavorites(movieList);
    }

    private void setValuesForFavorites(ArrayList<Result> movieList){
        movie = movieList.get(position);
        movieId = movie.getId();
        movieTitleString = movie.getOriginalTitle();
        movieImageString = movie.getBackdropPath();
        releaseDateString = movie.getReleaseDate();
        moviePlotString = movie.getOverview();
        movieVoteAverage = movie.getVoteAverage();
        voteAverageString = String.format(Locale.US, context.getString(R.string.format_double), movieVoteAverage);
    }

    public Result getMovieDetails(){
        Result movie=new Result(movieId,movieTitleString,movieImageString,releaseDateString, moviePlotString, movieVoteAverage);
        return movie;
    }

    private void getTrailers() {
        new RetrofitRequesterTrailer().requestMovies(this, Integer.toString(movieId));
    }

    private void getReviews() {
        new RetrofitRequesterReviews().requestMovies(this, Integer.toString(movieId));
    }

    public void insertFavoriteMovieVM() {
        context.getContentResolver().insert(
                createURI(),
                createMoveContentValues());
    }

    public static final Uri createURI() {
        return MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(Integer.toString(MovieProvider.CODE_SPECIFIC_MOVIE))
                .build();
    }

    private ContentValues createMoveContentValues() {
        ContentValues favoriteMovie = new ContentValues();

        favoriteMovie.put(MovieContract.MovieEntry.COLUMN_ID, movieId);
        favoriteMovie.put(MovieEntry.COLUMN_TITLE, movieTitleString);
        favoriteMovie.put(MovieEntry.COLUMN_POSTER, movieImageString);
        favoriteMovie.put(MovieEntry.COLUMN_PLOT, moviePlotString);
        favoriteMovie.put(MovieEntry.COLUMN_USER_RATING, movieVoteAverage);
        return favoriteMovie;
    }

    public MutableLiveData<List<TrailerMoviePojo>> getAllTrailers(){
        //requestGeneralMovies();
        return this.liveDataTrailerMoviePojoList;
    }

    public MutableLiveData<List<ReviewPojo>> getAllReviews(){
        //requestGeneralMovies();
        return this.liveDataReviewPojoList;
    }


    @Override
    public void trailersOnRetrofitFinished(List<TrailerMoviePojo> movieList) {
        //keys contain trailers
        trailerResultList = movieList;
        transformToTrailersLiveData(movieList);
    }

    @Override
    public void reviewsOnRetrofitFinished(List<ReviewPojo> movieList) {
        reviewResultList = movieList;
        transformToReviewsLiveData(movieList);
    }

    private void transformToTrailersLiveData(List<TrailerMoviePojo> trailerMoviePojoList){
        this.liveDataTrailerMoviePojoList.postValue(trailerMoviePojoList);
    }

    private void transformToReviewsLiveData(List<ReviewPojo> reviewPojoList){
        this.liveDataReviewPojoList.postValue(reviewPojoList);
    }



}