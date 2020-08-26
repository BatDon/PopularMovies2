package com.example.popularmovies2;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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

import com.example.popularmovies2.RetrofitRequesters.RetrofitRequesterPopular;
import com.example.popularmovies2.adapters.CompleteAdapter;
//import com.example.popularmovies2.adapters.RelatedMoviesAdapter;
import com.example.popularmovies2.adapters.GridAdapter;
import com.example.popularmovies2.adapters.RelatedMoviesAdapter;
import com.example.popularmovies2.fetchdata.pojos.Result;
import com.example.popularmovies2.RetrofitRequesters.RetrofitRequester;
import com.example.popularmovies2.userfavorites.UserFavorites;
import com.example.popularmovies2.viewmodels.MainActivityViewModel;

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

public class MainActivity extends AppCompatActivity implements GridAdapter.OnMovieListener{
       // RetrofitRequester.OnRetrofitListener, RetrofitRequesterPopular.OnPopularRetrofitListener{
//public class MainActivity extends AppCompatActivity implements CompleteAdapter.OnMovieListener, RetrofitRequester.OnRetrofitListener{

    public static final String TAG= MainActivity.class.getSimpleName();


    ProgressBar progressBar;
//    GridView gridView;
    public List<Result> resultList;
    RelatedMoviesAdapter relatedMoviesAdapter;
    GridAdapter gridAdapter;
    CompleteAdapter completeAdapter;
    RecyclerView gridRecyclerView;

    MainActivityViewModel mainActivityViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("MainActivity",getFilesDir().toString());


        setUpView();
//        new RetrofitRequester().requestMovies(this);

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        setUpViewModelOnChanged();
        mainActivityViewModel.requestGeneralMovies();
            Log.i(TAG,"regular movies requested");

    }

    public void setUpView(){
        progressBar=findViewById(R.id.loading_progress_bar);
        //gridView=findViewById(R.id.movie_grid);
        gridRecyclerView = findViewById(R.id.gridRecyclerView);
        progressBar.setVisibility(View.VISIBLE);

        //setUpViewModelOnChanged();
    };

    public void setUpViewModelOnChanged(){
        Observer<List<Result>> observer=new Observer<List<Result>>() {
            int i=0;
            @Override
            public void onChanged(@Nullable final List<Result> movies) {
                i=movies.size();
                // Update the cached copy of the words in the adapter.
                Log.i("MainActivity","onChanged triggered");
                if(i>0){
                    resultList=movies;
                    setUpGridAdapter();
                }
//                i++;
//                //mainViewModel.requestMovies();
//                resultList=movies;
//                if(mainViewModel.getAllMovies().getValue()!=null){
//                    mainViewModel.requestMovies();
//                }
            }
        };

        mainActivityViewModel.getAllMovies().observe(this,observer);
    }

//    public void onRetrofitFinished(List<Result> movieList){
//        resultList=movieList;
////        for(Result result:resultList){
////            Log.i(TAG," "+result.getOriginalTitle());
////        }
//        if(resultList.size()>0) {
//            setUpGridAdapter();
//        }
//
//    }
//
//    @Override
//    public void onPopularRetrofitFinished(List<Result> movieList){
//        resultList=movieList;
//        if(resultList.size()>0) {
//            setUpGridAdapter();
//        }
//    }



    public void setUpGridAdapter(){
        gridRecyclerView.setHasFixedSize(true);
        int numberOfColumns = 2;

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, numberOfColumns, RecyclerView.VERTICAL,false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        gridRecyclerView.setLayoutManager(gridLayoutManager);
        Result[] resultArray=new Result[resultList.size()];
        resultList.toArray(resultArray);
        if(resultArray.length==0){
            Log.i(TAG,"no results found");
        }
        else{
            Log.i(TAG,"resultArray length= "+resultArray.length+"");
        }
        Log.i(TAG,Integer.toString(resultArray.length));
        gridAdapter = new GridAdapter(this, resultArray, this);
        //completeAdapter = new CompleteAdapter(this, this, resultArray, GRID_RECYCLER_VIEW);
        gridRecyclerView.setAdapter(gridAdapter);
//        gridRecyclerView.setAdapter(completeAdapter);

        progressBar.setVisibility(View.INVISIBLE);
        gridRecyclerView.setVisibility(View.VISIBLE);

        ArrayList<Result> movieArrayList = new ArrayList<Result>(Arrays.asList(resultArray));

        mainActivityViewModel.writeToFile(movieArrayList,this);

        Log.i(TAG,"end of setUpAdapter");
    }

//    private void writeToFile(ArrayList<Result> movieList, Context context) {
//
//        try {
//            FileOutputStream fileOut = new FileOutputStream(new File(getString(R.string.pathToFile)));
//            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
//            objectOut.writeObject(movieList);
//            objectOut.close();
//            fileOut.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

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
                mainActivityViewModel.requestPopularMovies();
                //new RetrofitRequester().requestMovies(this);
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
    public void onMoviePosterClick(int position){
        Intent detailIntent=new Intent(this, MovieDetails.class);
        detailIntent.putExtra(MOVIE_POSITION, position);
        startActivity(detailIntent);
    }



}
