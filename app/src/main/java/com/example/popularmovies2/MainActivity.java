package com.example.popularmovies2;


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

import com.example.popularmovies2.adapters.CompleteAdapter;
//import com.example.popularmovies2.adapters.RelatedMoviesAdapter;
import com.example.popularmovies2.fetchdata.pojos.Result;
import com.example.popularmovies2.RetrofitRequesters.RetrofitRequester;
import com.example.popularmovies2.userfavorites.UserFavorites;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.popularmovies2.Constants.GRID_RECYCLER_VIEW;
import static com.example.popularmovies2.Constants.MOVIE_POSITION;
import static com.example.popularmovies2.Constants.RELATED_KEY;
import static com.example.popularmovies2.Constants.REQUEST_MOVIE_LIST;
import static com.example.popularmovies2.Constants.REQUEST_SORTED_POPULAR_MOVIES;

//public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieListener, RetrofitRequester.OnRetrofitListener{
public class MainActivity extends AppCompatActivity implements CompleteAdapter.OnMovieListener, RetrofitRequester.OnRetrofitListener{

    public static final String TAG= MainActivity.class.getSimpleName();


    ProgressBar progressBar;
    GridView gridView;
    public List<Result> resultList;
    //RelatedMoviesAdapter movieAdapter;
    CompleteAdapter completeAdapter;
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("MainActivity",getFilesDir().toString());


        setUpView();
            Log.i(TAG,"regular movies requested");
            new RetrofitRequester().requestMovies(REQUEST_MOVIE_LIST,this, null);

    }

    public void setUpView(){
        progressBar=findViewById(R.id.loading_progress_bar);
        gridView=findViewById(R.id.movie_grid);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar.setVisibility(View.VISIBLE);
    };




    public void onRetrofitFinished(List<Result> movieList){
        resultList=movieList;
        for(Result result:resultList){
            Log.i(TAG," "+result.getOriginalTitle());
        }
        setUpAdapter();

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
        Log.i(TAG,Integer.toString(resultArray.length));
//        movieAdapter = new MovieAdapter(this, resultArray, this);
        completeAdapter = new CompleteAdapter(this, this, resultArray, GRID_RECYCLER_VIEW);
//        recyclerView.setAdapter(movieAdapter);
        recyclerView.setAdapter(completeAdapter);

        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        ArrayList<Result> movieArrayList = new ArrayList<Result>(Arrays.asList(resultArray));

        writeToFile(movieArrayList,this);

        Log.i(TAG,"end of setUpAdapter");
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
                new RetrofitRequester().requestMovies(REQUEST_SORTED_POPULAR_MOVIES,this, null);
                break;
            }
            case R.id.action_favorite_movies:{
                Intent intent=new Intent(MainActivity.this, UserFavorites.class);
                startActivity(intent);
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
