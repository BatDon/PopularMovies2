package com.example.popularmovies2.relatedmovies;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies2.R;
import com.example.popularmovies2.adapters.ReviewAdapter;
import com.example.popularmovies2.adapters.TrailerAdapter;
import com.example.popularmovies2.fetchdata.pojos.Result;
import com.example.popularmovies2.fetchdata.pojos.ReviewPojo;
import com.example.popularmovies2.fetchdata.pojos.TrailerMoviePojo;
import com.example.popularmovies2.viewmodels.RelatedMovieDetailsViewModel;
import com.example.popularmovies2.viewmodels.RelatedMoviesDetailViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.popularmovies2.Constants.BASE_IMAGE_URL;
import static com.example.popularmovies2.Constants.DEFAULT_POSITION;
import static com.example.popularmovies2.Constants.IMAGE_SIZE;
import static com.example.popularmovies2.Constants.MOVIE_POSITION;
import static com.example.popularmovies2.Constants.RELATED_KEY;
import static com.example.popularmovies2.Constants.file_location;


public class RelatedMoviesDetails extends AppCompatActivity implements TrailerAdapter.OnTrailerMovieListener {

    public static final String TAG = RelatedMoviesDetails.class.getSimpleName();

    List<TrailerMoviePojo> trailerList;
    List<ReviewPojo> reviewList;
    Context context = this;
    int position;
    ImageView movieImageIV;
    TextView movieTitleTV;
    TextView ratingTV;
    TextView releaseDateTV;
    TextView plotTV;
    ImageButton favoriteB;
    Button relatedB;

    String trailerKey;

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

    RelatedMovieDetailsViewModel relatedMoviesDetailsViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.related_movie_details);
        //getMoviePosition();
        setUpUI();
        getMoviePosition();

        setUpViewModel();
        setUpTrailerOnChanged();
        setUpReviewOnChanged();

        setValues();

        //send work to viewmodel
//        getMovieDir();
//        getTrailers();
//        getReviews();

    }

    public void getMoviePosition() {
        Intent intent = getIntent();
        if (intent == null) {
            Log.i(TAG,"Error getting position");
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

    private void setUpViewModel(){
        RelatedMoviesDetailViewModelFactory relatedMoviesDetailViewModelFactory =new RelatedMoviesDetailViewModelFactory(getApplication(), position);
        relatedMoviesDetailsViewModel = ViewModelProviders.of(this, relatedMoviesDetailViewModelFactory).get(RelatedMovieDetailsViewModel.class);
//        context= relatedMoviesDetailsViewModel.getApplication();
    }


    public void setUpTrailerOnChanged(){
        Observer<List<TrailerMoviePojo>> observer=new Observer<List<TrailerMoviePojo>>() {
            int i=0;
            @Override
            public void onChanged(@Nullable final List<TrailerMoviePojo> trailers) {
                i=trailers.size();
                // Update the cached copy of the words in the adapter.
                Log.i("MainActivity","onChanged triggered");
                if(i>0){
                    trailerList=trailers;
                    setUpTrailerAdapter();
                }
            }
        };

        relatedMoviesDetailsViewModel.getAllTrailers().observe(this,observer);
    }


    public void setUpReviewOnChanged(){
        Observer<List<ReviewPojo>> observer=new Observer<List<ReviewPojo>>() {
            int i=0;
            @Override
            public void onChanged(@Nullable final List<ReviewPojo> reviews) {
                i=reviews.size();
                // Update the cached copy of the words in the adapter.
                Log.i("MainActivity","onChanged triggered");
                if(i>0){
                    reviewList=reviews;
                    setUpReviewAdapter();
                }
//                i++;
//                //mainViewModel.requestMovies();
//                resultList=movies;
//                if(mainViewModel.getAllMovies().getValue()!=null){
//                    mainViewModel.requestMovies();
//                }
            }
        };

        relatedMoviesDetailsViewModel.getAllReviews().observe(this,observer);
    }



    //    private void setValues(ArrayList<Result> movieList) {
    private void setValues() {

        Result movie= relatedMoviesDetailsViewModel.getMovieDetails();
        //movie = movieList.get(position);

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
        relatedMoviesDetailsViewModel.insertFavoriteMovieVM();
    }

    public void relatedMovies(View view) {
        Intent intent = new Intent(RelatedMoviesDetails.this, RelatedMoviesList.class);
        intent.putExtra(RELATED_KEY, Integer.toString(movieId));
        Log.i(TAG, "related movie id" + movieId);
        startActivity(intent);
    }

    public void setUpTrailerAdapter() {
        trailerRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        trailerRecyclerView.setLayoutManager(linearLayoutManager);

        TrailerMoviePojo[] resultArray = new TrailerMoviePojo[trailerList.size()];
        trailerList.toArray(resultArray);
        Log.i(TAG, Integer.toString(resultArray.length));

        if(trailerList.size()>0){
            trailerKey=trailerList.get(0).getKey();
        }
        else{
            trailerKey=null;
        }

//        Dynamically change Trailer Frame Layout Height
        int reviewLength=0;
        FrameLayout trailerFrameLayout=this.findViewById(R.id.trailer_frame_layout);
        reviewLength = resultArray.length;
        int frameLayoutSize = (reviewLength * 50)+50;
        if(frameLayoutSize>300){
            frameLayoutSize=300;
        }
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) trailerFrameLayout.getLayoutParams();
        params.height = frameLayoutSize;
        trailerFrameLayout.setLayoutParams(params);

        TrailerAdapter trailerAdapter = new TrailerAdapter(this, resultArray, this);

        trailerRecyclerView.setAdapter(trailerAdapter);

        showTrailerRecyclerView();

//        if(resultArray.length>0 && !key.equals("key")) {
//            ReviewAdapter reviewAdapter = new ReviewAdapter(this, resultArray);
//            reviewRecyclerView.setAdapter(reviewAdapter);
//            Log.i(TAG, "setUpReviewAdapter called");
//            showReviewRecyclerView();
//        }
//        else{
//            Log.i(TAG,"reviewArray is equal to zero");
//            FrameLayout reviewFrameLayout=this.findViewById(R.id.review_frame_layout);
//            reviewFrameLayout.setVisibility(View.GONE);
//            this.findViewById(R.id.reviews_title).setVisibility(View.GONE);
//        }


//        showTrailersLoading();


        Log.i(TAG, "end of setUpAdapter");
    }

    public void setUpReviewAdapter() {

        reviewRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        reviewRecyclerView.setLayoutManager(linearLayoutManager);

        ReviewPojo[] resultArray = new ReviewPojo[reviewList.size()];
        reviewList.toArray(resultArray);
        Log.i(TAG, Integer.toString(resultArray.length));

//        int reviewLength = resultArray.length;
//        int frameLayoutSize = reviewLength * 50;

//        FrameLayout reviewFrameLayout=this.findViewById(R.id.review_frame_layout);
//
//        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) reviewFrameLayout.getLayoutParams();
//        params.height = frameLayoutSize;
//        reviewFrameLayout.setLayoutParams(params);

        String author = resultArray[0].getAuthor();
        Log.i(TAG, "author= " + author);

//        ReviewAdapter reviewAdapter = new ReviewAdapter(this, resultArray);
//        reviewRecyclerView.setAdapter(reviewAdapter);
//        Log.i(TAG, "setUpReviewAdapter called");
//        showReviewRecyclerView();
//        Log.i(TAG,"reviews size="+resultArray.length);
        //   }


//        if(resultArray.length>1 && !author.equals("author")) {
        if(resultArray.length>1 || !author.equals("author")) {
            ReviewAdapter reviewAdapter = new ReviewAdapter(this, resultArray);
            reviewRecyclerView.setAdapter(reviewAdapter);
            FrameLayout reviewFrameLayout=this.findViewById(R.id.review_frame_layout);
            reviewFrameLayout.setVisibility(View.VISIBLE);
            this.findViewById(R.id.reviews_title).setVisibility(View.VISIBLE);
            Log.i(TAG, "setUpReviewAdapter called");
            Log.i(TAG,"reviews size="+resultArray.length);
            showReviewRecyclerView();
        }
        else{
            Log.i(TAG,"reviewArray is equal to zero");
            FrameLayout reviewFrameLayout=this.findViewById(R.id.review_frame_layout);
            reviewFrameLayout.setVisibility(View.GONE);
            this.findViewById(R.id.reviews_title).setVisibility(View.GONE);
        }
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
        TrailerMoviePojo trailerMoviePojo = trailerList.get(position);
        trailerKey = trailerMoviePojo.getKey();
        Log.i(TAG, "trailerkey= " + trailerKey);
        startSearchIntent(trailerKey);
    }


//https://m.youtube.com/watch?v=key


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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_share) {
            createShareMovieIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void createShareMovieIntent() {
        if(trailerKey==null){
            Toast.makeText(context, "No trailers to share for this movie", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareYoutubeIntent = new Intent(Intent.ACTION_SEND);
        shareYoutubeIntent.setType("text/plain");
        shareYoutubeIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
        shareYoutubeIntent.putExtra(Intent.EXTRA_TEXT, buildYouTubeUri());
        startActivity(Intent.createChooser(shareYoutubeIntent, "Share YouTube URL"));
    }

    public String buildYouTubeUri(){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", trailerKey);
        String uriString=builder.toString();
        return uriString;
    }

    @Override
    public void onBackPressed() {
        int file_location_int=Integer.parseInt(file_location);
        file_location_int--;
        file_location=Integer.toString(file_location_int);
        Log.i(TAG,"back presssed file_location= "+file_location);
        finish();
    }




}

































//public class RelatedMoviesDetails extends AppCompatActivity {
//
//
//    public static final String TAG= RelatedMoviesDetails.class.getSimpleName();
//
//    List<Result> resultList;
//    Context context=this;
//    int position;
//    ImageView movieImageIV;
//    TextView movieTitleTV;
//    TextView ratingTV;
//    TextView releaseDateTV;
//    TextView plotTV;
//    ImageButton favoriteB;
//    Button relatedB;
//
//    Result movie;
//
//
//    int movieId;
//    String movieTitleString;
//    String movieImageString;
//    String moviePlotString;
//    Double movieVoteAverage;
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.related_movie_details);
//
//        getMoviePosition();
//        setUpUI();
//        getMovieDir();
//    }
//
//
//    public void getMoviePosition(){
//        Intent intent = getIntent();
//        if (intent == null) {
//            closeOnError();
//        }
//
//        position = intent.getIntExtra(MOVIE_POSITION, DEFAULT_POSITION);
//
//        if(position==DEFAULT_POSITION){
//            closeOnError();
//        }
//    }
//
//    private void closeOnError() {
//        finish();
//        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
//    }
//
//    private void setUpUI(){
//        movieTitleTV=findViewById(R.id.movieTitle);
//        movieImageIV=findViewById(R.id.movieImage);
//        ratingTV=findViewById(R.id.rating);
//        releaseDateTV=findViewById(R.id.id);
//        plotTV=findViewById(R.id.plot);
//        favoriteB=findViewById(R.id.favoriteButton);
//        relatedB=findViewById(R.id.relatedButton);
//
//    }
//
//    private void getMovieDir(){
//        ArrayList<Result> movieList;
//
//        try
//        {
//            FileInputStream fis = new FileInputStream(new File(getString(R.string.pathToRelatedMoviesFile)));
//            ObjectInputStream ois = new ObjectInputStream(fis);
//            movieList = (ArrayList) ois.readObject();
//
//            ois.close();
//            fis.close();
//        }
//        catch (IOException ioe)
//        {
//            ioe.printStackTrace();
//            return;
//        }
//        catch (ClassNotFoundException c)
//        {
//            System.out.println(getString(R.string.class_not_found));
//            c.printStackTrace();
//            return;
//        }
//
//        setValues(movieList);
//    }
//
//
//
//    private void setValues(ArrayList<Result> movieList){
//        movie=movieList.get(position);
//
//        movieId=movie.getId();
//
//        movieTitleString =movie.getOriginalTitle();
//        movieTitleTV.setText(movieTitleString);
//
//        movieImageString=movie.getBackdropPath();
//        Picasso.get().load(BASE_IMAGE_URL+IMAGE_SIZE+movieImageString).into(movieImageIV);
//
//        String releaseDateString=movie.getReleaseDate();
//        releaseDateTV.setText(releaseDateString);
//
//        moviePlotString =movie.getOverview();
//        plotTV.setText(moviePlotString);
//
//        movieVoteAverage =movie.getVoteAverage();
//        String voteAverageString=String.format(Locale.US,getString(R.string.format_double), movieVoteAverage);
//
//        ratingTV.setText(voteAverageString);
//
//    }
//
//
//
//    public void favoriteClick(View view) {
//        insertFavoriteMovie(context);
//        Toast.makeText(this, movieTitleString+getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show();
//    }
//
//    private void insertFavoriteMovie(Context context){
//        context.getContentResolver().insert(
//                createURI(),
//                createMoveContentValues());
//    }
//
//    public static final Uri createURI(){
//        return MovieEntry.CONTENT_URI.buildUpon()
//                .appendPath(Integer.toString(MovieProvider.CODE_SPECIFIC_MOVIE))
//                .build();
//    }
//
//    private ContentValues createMoveContentValues() {
//        ContentValues favoriteMovie = new ContentValues();
//
//        favoriteMovie.put(MovieEntry.COLUMN_ID, movieId);
//        favoriteMovie.put(MovieEntry.COLUMN_TITLE, movieTitleString);
//        favoriteMovie.put(MovieEntry.COLUMN_POSTER, movieImageString);
//        favoriteMovie.put(MovieEntry.COLUMN_PLOT, moviePlotString);
//        favoriteMovie.put(MovieEntry.COLUMN_USER_RATING, movieVoteAverage);
//        return favoriteMovie;
//    }
//
//    public void relatedMovies(View view) {
//        Intent intent=new Intent(RelatedMoviesDetails.this, RelatedMoviesList.class);
//        intent.putExtra(RELATED_KEY,Integer.toString(movieId));
//        Log.i(TAG,"related movie id"+movieId);
//        startActivity(intent);
//    }
//
//    public void onRetrofitFinished(List<Result> movieList){
//        resultList=movieList;
//        String movieTitle=movieList.get(0).getOriginalTitle();
//        Toast.makeText(context, movieTitle, Toast.LENGTH_SHORT).show();
//    }
//
//
//}
