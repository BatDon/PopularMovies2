package com.example.popularmovies2.relatedmovies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies2.R;
import com.example.popularmovies2.adapters.RelatedMoviesAdapter;
import com.example.popularmovies2.fetchdata.pojos.Result;
import com.example.popularmovies2.viewmodels.RelatedListViewModel;
import com.example.popularmovies2.viewmodels.RelatedListViewModelFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.popularmovies2.Constants.MOVIE_POSITION;
import static com.example.popularmovies2.Constants.RELATED_KEY;

public class RelatedMoviesList extends AppCompatActivity implements RelatedMoviesAdapter.OnRelatedMovieListener{
//        RetrofitRequesterRelated.RelatedOnRetrofitListener{

    public List<Result> resultList;
    private ProgressBar loadingCircle;
    private RecyclerView relatedRecyclerView;
    RelatedMoviesAdapter relatedMoviesAdapter;
    String movieId;
    private final String TAG = RelatedMoviesList.class.getSimpleName();

//    public MutableLiveData<List<Result>> liveDataRelatedList=new MutableLiveData<List<Result>>(){};
//    public RelatedMoviesListViewModelFactory relatedMoviesListViewModelFactory;
    public RelatedListViewModel relatedListViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.related_movie_list);
        setUpViews();
        movieId=checkForIntent();

        setUpViewModel();
        relatedListViewModel.requestRelatedMovies();

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

    public void setUpViewModel() {
        RelatedListViewModelFactory relatedListViewModelFactory = new RelatedListViewModelFactory(getApplication(), Integer.parseInt(movieId));
        relatedListViewModel = ViewModelProviders.of(this, relatedListViewModelFactory).get(RelatedListViewModel.class);
        setUpViewModelOnChanged();
    }
        public void setUpViewModelOnChanged(){
            Observer<List<Result>> observer=new Observer<List<Result>>() {
                int i=0;
                @Override
                public void onChanged(@Nullable final List<Result> movies) {
                    i=movies.size();
                    Log.i(TAG, "List<Result> movies size= "+i);
                    // Update the cached copy of the words in the adapter.
                    Log.i("RelatedMoviesList","onChanged triggered");
                    if(i>0){
                        resultList=movies;
                        setUpAdapter();
                    }
//                i++;
//                //mainViewModel.requestMovies();
//                resultList=movies;
//                if(mainViewModel.getAllMovies().getValue()!=null){
//                    mainViewModel.requestMovies();
//                }
                }
            };

            relatedListViewModel.getAllRelatedMovies().observe(this,observer);
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
                Log.i(TAG,"setUpAdapter showRecyclerView");
                showRecyclerView();
            }
            else{
                Toast.makeText(this, "No related movies found", Toast.LENGTH_SHORT).show();
            }

            ArrayList<Result> movieArrayList = new ArrayList<Result>(Arrays.asList(resultArray));
//
            relatedListViewModel.writeToFile(movieArrayList);

            Log.i(TAG,"end of setUpAdapter");
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
        Log.i(TAG,"position= "+position);
        Intent relatedIntent=new Intent(this, RelatedMoviesDetails.class);
        relatedIntent.putExtra(MOVIE_POSITION, position);
        startActivity(relatedIntent);
    }
}
