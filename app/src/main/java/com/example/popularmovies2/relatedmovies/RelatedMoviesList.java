package com.example.popularmovies2.relatedmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies2.R;
import com.example.popularmovies2.RetrofitRequesters.RetrofitRequesterRelated;
import com.example.popularmovies2.adapters.RelatedMoviesAdapter;
import com.example.popularmovies2.fetchdata.pojos.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.popularmovies2.Constants.MOVIE_POSITION;
import static com.example.popularmovies2.Constants.RELATED_KEY;

public class RelatedMoviesList extends AppCompatActivity implements RelatedMoviesAdapter.OnRelatedMovieListener,
        RetrofitRequesterRelated.RelatedOnRetrofitListener{

    public List<Result> resultList;
    private ProgressBar loadingCircle;
    private RecyclerView relatedRecyclerView;
    RelatedMoviesAdapter relatedMoviesAdapter;
    private final String TAG = RelatedMoviesList.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.related_movie_list);
        setUpViews();
        String movieId=checkForIntent();

        new RetrofitRequesterRelated().requestMovies(this, movieId);
    }

    public void setUpViews() {
        loadingCircle = findViewById(R.id.progressBar);
        relatedRecyclerView = findViewById(R.id.recyclerView);

        Log.i(TAG, "setUpViews called");

        showLoading();
    }


    public String checkForIntent(){
        Intent intent = getIntent();
        if (!intent.hasExtra(RELATED_KEY)) {
            return getString(R.string.no_id_set);
        }

        Log.i(TAG,"checkForIntent called");
        String movie_intent_id = intent.getStringExtra(RELATED_KEY);
        return movie_intent_id;
    }


    public void relatedOnRetrofitFinished(List<Result> movieList){
        resultList=movieList;
        setUpAdapter();
    }

        public void setUpAdapter(){
            relatedRecyclerView.setHasFixedSize(true);
            int numberOfColumns = 2;


            GridLayoutManager gridLayoutManager=new GridLayoutManager(this, numberOfColumns, RecyclerView.VERTICAL,false);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return 1;
                }
            });
            relatedRecyclerView.setLayoutManager(gridLayoutManager);
            Result[] resultArray=new Result[resultList.size()];
            resultList.toArray(resultArray);
            Log.i(TAG,Integer.toString(resultArray.length));
//        movieAdapter = new MovieAdapter(this, resultArray, this);
            relatedMoviesAdapter = new RelatedMoviesAdapter(this, resultArray, this);
//        recyclerView.setAdapter(movieAdapter);
            relatedRecyclerView.setAdapter(relatedMoviesAdapter);

            if(resultArray.length>0) {

                showRecyclerView();
            }
            else{
                Toast.makeText(this, "No related movies found", Toast.LENGTH_SHORT).show();
            }

            ArrayList<Result> movieArrayList = new ArrayList<Result>(Arrays.asList(resultArray));
//
            writeToFile(movieArrayList,this);

            Log.i(TAG,"end of setUpAdapter");
        }

    private void writeToFile(ArrayList<Result> movieList, Context context) {

        try {
            FileOutputStream fileOut = new FileOutputStream(new File(getString(R.string.pathToRelatedMoviesFile)));
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(movieList);
            objectOut.close();
            fileOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    public void showLoading(){
        loadingCircle.setVisibility(View.VISIBLE);
        relatedRecyclerView.setVisibility(View.INVISIBLE);
    }
    public void showRecyclerView(){
        loadingCircle.setVisibility(View.INVISIBLE);
        relatedRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMovieClick(int position){
        Intent relatedIntent=new Intent(this, RelatedMoviesDetails.class);
        relatedIntent.putExtra(MOVIE_POSITION, position);
        startActivity(relatedIntent);
    }
}
