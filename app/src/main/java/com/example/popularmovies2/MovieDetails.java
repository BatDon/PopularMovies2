package com.example.popularmovies2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmovies2.RetrofitRequesters.RetrofitRequesterReviews;
import com.example.popularmovies2.RetrofitRequesters.RetrofitRequesterTrailer;
import com.example.popularmovies2.adapters.ReviewAdapter;
import com.example.popularmovies2.adapters.TrailerAdapter;
import com.example.popularmovies2.fetchdata.pojos.Result;
import com.example.popularmovies2.fetchdata.pojos.ReviewPojo;
import com.example.popularmovies2.fetchdata.pojos.TrailerMoviePojo;
import com.example.popularmovies2.moviedata.MovieContract.MovieEntry;
import com.example.popularmovies2.moviedata.MovieProvider;
import com.example.popularmovies2.relatedmovies.RelatedMoviesList;
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

public class MovieDetails extends AppCompatActivity implements RetrofitRequesterTrailer.TrailersOnRetrofitListener,
        RetrofitRequesterReviews.ReviewsOnRetrofitListener, TrailerAdapter.OnTrailerMovieListener {

    public static final String TAG = MovieDetails.class.getSimpleName();

    List<TrailerMoviePojo> trailerResultList;
    List<ReviewPojo> reviewResultList;
    Context context = this;
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

    ProgressBar trailerProgressBar;
    ProgressBar reviewProgressBar;

    RecyclerView trailerRecyclerView;
    RecyclerView reviewRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getMoviePosition();
        setUpUI();
        getMovieDir();
        getTrailers();
        getReviews();

    }

    public void getMoviePosition() {
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        position = intent.getIntExtra(MOVIE_POSITION, DEFAULT_POSITION);

        if (position == DEFAULT_POSITION) {
            closeOnError();
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void setUpUI() {
        movieTitleTV = findViewById(R.id.movieTitle);
        movieImageIV = findViewById(R.id.movieImage);
        ratingTV = findViewById(R.id.rating);
        releaseDateTV = findViewById(R.id.id);
        plotTV = findViewById(R.id.plot);
        favoriteB = findViewById(R.id.favoriteButton);
        relatedB = findViewById(R.id.relatedButton);
        trailerRecyclerView = findViewById(R.id.trailer_recycler_view);
        trailerProgressBar = findViewById(R.id.trailer_progress_bar);
        reviewRecyclerView = findViewById(R.id.review_recycler_view);
        reviewProgressBar = findViewById(R.id.review_progress_bar);

        showTrailersLoading();
        showReviewsLoading();

    }

    private void getMovieDir() {
        ArrayList<Result> movieList;

        try {
            FileInputStream fis = new FileInputStream(new File(getString(R.string.pathToFile)));
            ObjectInputStream ois = new ObjectInputStream(fis);
            movieList = (ArrayList) ois.readObject();

            ois.close();
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println(getString(R.string.class_not_found));
            c.printStackTrace();
            return;
        }

        setValues(movieList);
    }

    private void getTrailers() {
        new RetrofitRequesterTrailer().requestMovies(this, Integer.toString(movieId));
    }

    private void getReviews() {
        new RetrofitRequesterReviews().requestMovies(this, Integer.toString(movieId));
    }


    private void setValues(ArrayList<Result> movieList) {
        movie = movieList.get(position);

        movieId = movie.getId();

        movieTitleString = movie.getOriginalTitle();
        movieTitleTV.setText(movieTitleString);

        movieImageString = movie.getBackdropPath();
        Picasso.get().load(BASE_IMAGE_URL + IMAGE_SIZE + movieImageString).into(movieImageIV);

        String releaseDateString = movie.getReleaseDate();
        releaseDateTV.setText(releaseDateString);

        moviePlotString = movie.getOverview();
        plotTV.setText(moviePlotString);

        movieVoteAverage = movie.getVoteAverage();
        String voteAverageString = String.format(Locale.US, getString(R.string.format_double), movieVoteAverage);

        ratingTV.setText(voteAverageString);

    }


    public void favoriteClick(View view) {
        insertFavoriteMovie(context);
        Toast.makeText(this, movieTitleString + getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();
    }

    private void insertFavoriteMovie(Context context) {
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

        favoriteMovie.put(MovieEntry.COLUMN_ID, movieId);
        favoriteMovie.put(MovieEntry.COLUMN_TITLE, movieTitleString);
        favoriteMovie.put(MovieEntry.COLUMN_POSTER, movieImageString);
        favoriteMovie.put(MovieEntry.COLUMN_PLOT, moviePlotString);
        favoriteMovie.put(MovieEntry.COLUMN_USER_RATING, movieVoteAverage);
        return favoriteMovie;
    }

    public void relatedMovies(View view) {
        Intent intent = new Intent(MovieDetails.this, RelatedMoviesList.class);
        intent.putExtra(RELATED_KEY, Integer.toString(movieId));
        Log.i(TAG, "related movie id" + movieId);
        startActivity(intent);

    }

    @Override
    public void trailersOnRetrofitFinished(List<TrailerMoviePojo> movieList) {

        //keys contain trailers
        trailerResultList = movieList;
//        String movieNumber=movieList.get(0).getIso6391();
//        Toast.makeText(context, movieNumber, Toast.LENGTH_SHORT).show();

        if (trailerResultList.size() > 0) {
            Log.i(TAG, "trailer size= " + trailerResultList.size());
            setUpTrailerAdapter();
        } else {
            Log.i(TAG, "trailer size=0");
        }
    }

    public void setUpTrailerAdapter() {
        trailerRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        trailerRecyclerView.setLayoutManager(linearLayoutManager);

        TrailerMoviePojo[] resultArray = new TrailerMoviePojo[trailerResultList.size()];
        trailerResultList.toArray(resultArray);
        Log.i(TAG, Integer.toString(resultArray.length));


        TrailerAdapter trailerAdapter = new TrailerAdapter(this, resultArray, this);

        trailerRecyclerView.setAdapter(trailerAdapter);

        showTrailerRecyclerView();
//        showTrailersLoading();


        Log.i(TAG, "end of setUpAdapter");
    }

    @Override
    public void reviewsOnRetrofitFinished(List<ReviewPojo> movieList) {

        reviewResultList = movieList;
        if (reviewResultList.size() > 0) {
            Log.i(TAG, "reviews size=" + reviewResultList.size());
            setUpReviewAdapter();
        } else {
            Log.i(TAG, "reviews size=0");
        }

    }

    public void setUpReviewAdapter() {

        reviewRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        reviewRecyclerView.setLayoutManager(linearLayoutManager);

        ReviewPojo[] resultArray = new ReviewPojo[reviewResultList.size()];
        reviewResultList.toArray(resultArray);
        Log.i(TAG, Integer.toString(resultArray.length));

        int reviewLength = resultArray.length;
        int frameLayoutSize = reviewLength * 50;

//        FrameLayout reviewFrameLayout=this.findViewById(R.id.review_frame_layout);
//
//        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) reviewFrameLayout.getLayoutParams();
//        params.height = frameLayoutSize;
//        reviewFrameLayout.setLayoutParams(params);

//        String author=resultArray[0].getAuthor();
//        Log.i(TAG,"author= "+author);


        ReviewAdapter reviewAdapter = new ReviewAdapter(this, resultArray);

        reviewRecyclerView.setAdapter(reviewAdapter);

        Log.i(TAG, "setUpReviewAdapter called");

        showReviewRecyclerView();
    }

    public void showTrailersLoading() {
        trailerProgressBar.setVisibility(View.VISIBLE);
        trailerRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void showTrailerRecyclerView() {
        trailerProgressBar.setVisibility(View.INVISIBLE);
        trailerRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showReviewsLoading() {
        reviewProgressBar.setVisibility(View.VISIBLE);
        reviewRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void showReviewRecyclerView() {
        reviewProgressBar.setVisibility(View.INVISIBLE);
        reviewRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMovieClick(int position) {
        TrailerMoviePojo trailerMoviePojo = trailerResultList.get(position);
        String trailerKey = trailerMoviePojo.getKey();
        Log.i(TAG, "trailerkey= " + trailerKey);
        startSearchIntent(trailerKey);
    }


//https://m.youtube.com/watch?v=key


    //https://www.hellojava.com/a/71879.html

    //https://www.hellojava.com/a/71879.html

    private void startSearchIntent(String query) {

        List<Intent> browserIntents = new ArrayList<>();
        // Find browser intents
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_WEB_SEARCH), 0);

        // Create a search intent for each browser that was found
        for (ResolveInfo info : resInfo) {
            Intent browserIntent = new Intent(Intent.ACTION_WEB_SEARCH);
            browserIntent.putExtra(SearchManager.QUERY, query);
            browserIntent.setPackage(info.activityInfo.packageName.toLowerCase());
            browserIntents.add(browserIntent);
        }

        // Create Youtube Intent
        Intent youtubeIntent = new Intent(Intent.ACTION_SEARCH);
        youtubeIntent.setPackage("com.google.android.youtube");
        youtubeIntent.putExtra(SearchManager.QUERY, query);
        youtubeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent chooserIntent = Intent.createChooser(youtubeIntent, query);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, browserIntents.toArray(new Parcelable[]{}));
        startActivity(chooserIntent);
    }
}























































//    private void startSearchIntent(String query) {
//
//        List<Intent> browserIntents = new ArrayList<>();
//        // Find browser intents
//        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_WEB_SEARCH), 0);
//
//        // Create a search intent for each browser that was found
//        for (ResolveInfo info : resInfo) {
//            Intent browserIntent = new Intent(Intent.ACTION_WEB_SEARCH);
//            browserIntent.putExtra(SearchManager.QUERY, query);
//            browserIntent.setPackage(info.activityInfo.packageName.toLowerCase());
//            browserIntents.add(browserIntent);
//        }
//
//        // Create Youtube Intent
//        Intent youtubeIntent = new Intent(Intent.ACTION_SEARCH);
//        youtubeIntent.setPackage("com.google.android.youtube");
//        youtubeIntent.putExtra(SearchManager.QUERY, query);
//        youtubeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        Intent chooserIntent = Intent.createChooser(youtubeIntent, query);
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, browserIntents.toArray(new Parcelable[]{}));
//        startActivity(chooserIntent);
//
//    private void startSearchIntent(String query) {
//        List<Intent> browserIntents = new ArrayList<>();
//        // Find browser intents
////        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_WEB_SEARCH), 0);
//        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_VIEW), 0);
//
//
////        Intent webIntent = new Intent(Intent.ACTION_VIEW,
////                Uri.parse("http://www.youtube.com/watch?v=" + query));
//
//
//        // Create a search intent for each browser that was found
//        for (ResolveInfo info : resInfo) {
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
//            browserIntent.putExtra(SearchManager.QUERY, query);
//            browserIntent.setPackage(info.activityInfo.packageName.toLowerCase());
//            browserIntents.add(browserIntent);
//        }
//
//        // Create Youtube Intent
//        Intent youtubeIntent = new Intent(Intent.ACTION_SEARCH);
//        youtubeIntent.setPackage("com.google.android.youtube");
//        youtubeIntent.putExtra(SearchManager.QUERY, query);
//        youtubeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        Intent chooserIntent = Intent.createChooser(youtubeIntent, query);
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, browserIntents.toArray(new Parcelable[]{}));
//        startActivity(chooserIntent);

//        Intent youtubeIntent = new Intent(Intent.ACTION_SEARCH);
//        Intent chooserIntent = Intent.createChooser(youtubeIntent, query);

//        if (chooserIntent.resolveActivity(getPackageManager()) != null) {
//            startActivity(chooserIntent);
//        }
//        else{
//            Log.i(TAG,"can't handle this intent");
//        }


//    }
//}