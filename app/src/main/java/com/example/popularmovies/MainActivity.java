package com.example.popularmovies;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.popularmovies.Constants.MOVIE_POSITION;
import static com.example.popularmovies.Constants.REQUEST_MOVIE_LIST;
import static com.example.popularmovies.Constants.REQUEST_SORTED_POPULAR_MOVIES;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieListener{

    ProgressBar progressBar;
    GridView gridView;
    List<Result> resultList;
    MovieAdapter movieAdapter;
    RecyclerView recyclerView;
    String api_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api_key="YOUR_API_KEY_GOES_HERE";

        setUpView();
        requestMovies(REQUEST_MOVIE_LIST, api_key);
    }

    public void setUpView(){
        progressBar=findViewById(R.id.loading_progress_bar);
        gridView=findViewById(R.id.movie_grid);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar.setVisibility(View.VISIBLE);
    };



    public void requestMovies(String requestChoice, String api_key){

        MovieApi movieApi = RetrofitClient.getRetrofitInstance().create(MovieApi.class);
        Call<MoviePojo> call;

        switch(requestChoice) {
            case REQUEST_MOVIE_LIST:
                call = movieApi.getAllMovies(api_key);
                break;
            case REQUEST_SORTED_POPULAR_MOVIES:
                call =movieApi.getSortedPopularMovies(api_key);
                break;
            default:
                call=movieApi.getAllMovies(api_key);
        }

        call.enqueue(new Callback<MoviePojo>() {
            @Override
            public void onResponse(Call<MoviePojo> call, Response<MoviePojo> response) {
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<MoviePojo> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.problem_retrieving_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateDataList(MoviePojo moviePojoList) {
        if (moviePojoList == null) {
            Toast.makeText(this, R.string.parsing_problem, Toast.LENGTH_SHORT).show();
        } else {
            resultList = moviePojoList.getResults();
            setUpAdapter();
        }
    }


    public void setUpAdapter(){
        recyclerView.setHasFixedSize(true);
        int numberOfColumns = 2;

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, numberOfColumns, RecyclerView.VERTICAL,false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        Result[] resultArray=new Result[resultList.size()];
        resultList.toArray(resultArray);
        movieAdapter = new MovieAdapter(this, resultArray, this);
        recyclerView.setAdapter(movieAdapter);

        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        ArrayList<Result> movieArrayList = new ArrayList<Result>(Arrays.asList(resultArray));

        writeToFile(movieArrayList,this);
    }

    private void writeToFile(ArrayList<Result> movieList, Context context) {

        try {
            FileOutputStream fileOut = new FileOutputStream(new File(getString(R.string.pathToFile)));
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(movieList);
            objectOut.close();
            fileOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_most_popular: {
                Toast.makeText(this, R.string.popular_sorting, Toast.LENGTH_SHORT).show();
                requestMovies(REQUEST_SORTED_POPULAR_MOVIES,api_key);
                break;
            }
        }
        return true;
    }


    @Override
    public void onMovieClick(int position){
        Intent detailIntent=new Intent(this, MovieDetails.class);
        detailIntent.putExtra(MOVIE_POSITION, position);
        startActivity(detailIntent);
    }

}





















    //load image using background thread
    //Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView);
//    To build an image URL, you will need 3 pieces of data. The base_url, size and file_path. Simply combine them all and you will have a fully qualified URL. Here’s an example URL:
//
//https://image.tmdb.org/t/p/w500/8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg


    //base url                  size    poster path returned by query
    //http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg

//    http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
//You will extract the movie id from this request. You will need this in subsequent requests

//    If you ever upload your code to a public GitHub repo to share with other students or instructors, it’s illegal to include your personal themoviedb.org API key. Please remove it and note in a README where it came from, so someone else trying to run your code can create their own key and will quickly know where to put it. Instructors and code reviewers will expect this behavior for any public GitHub code.

//list of random movies
//https://api.themoviedb.org/3/movie/157336?api_key={api_key}
//c53e06736511ebefbde681d28d6bae9b

// list of popular movies GET
///movie/popular
//                                                                  not necessary for english
//https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US&page=1