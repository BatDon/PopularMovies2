package com.example.popularmovies2.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies2.R;
import com.example.popularmovies2.RetrofitRequesters.RetrofitRequester;
import com.example.popularmovies2.RetrofitRequesters.RetrofitRequesterPopular;
import com.example.popularmovies2.adapters.CompleteAdapter;
import com.example.popularmovies2.adapters.GridAdapter;
import com.example.popularmovies2.adapters.RelatedMoviesAdapter;
import com.example.popularmovies2.fetchdata.pojos.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel implements
        RetrofitRequester.OnRetrofitListener, RetrofitRequesterPopular.OnPopularRetrofitListener{

    ProgressBar progressBar;
    //    GridView gridView;
    public List<Result> resultList;

    public MutableLiveData<List<Result>> liveDataResultList=new MutableLiveData<List<Result>>(){};
    RelatedMoviesAdapter relatedMoviesAdapter;
    GridAdapter gridAdapter;
    CompleteAdapter completeAdapter;
    RecyclerView gridRecyclerView;

    Context context;


    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        context=getApplication();
        Result result=new Result("releaseData");
        List<Result> resultList=new ArrayList<Result>();
        resultList.add(result);
        liveDataResultList.setValue(resultList);

    }

    public MutableLiveData<List<Result>> getAllMovies(){
        //requestGeneralMovies();
        return this.liveDataResultList;
    }

    public void requestGeneralMovies(){
        new RetrofitRequester().requestMovies(this);
    }

    public void requestPopularMovies(){
        new RetrofitRequesterPopular().requestMovies(this);
    }

    @Override
    public void onRetrofitFinished(List<Result> movieList){
        resultList=movieList;
        transformToLiveData(movieList);
        //liveDataResultList=movieList;
//        for(Result result:resultList){
//            Log.i(TAG," "+result.getOriginalTitle());
//        }
//        if(resultList.size()>0) {
//            setUpGridAdapter();
//        }

    }

    @Override
    public void onPopularRetrofitFinished(List<Result> movieList){
        resultList=movieList;
        transformToLiveData((movieList));
//        if(resultList.size()>0) {
//            setUpGridAdapter();
//        }
    }

    private void transformToLiveData(List<Result> resultList){
//        MutableLiveData<List<Result>> mutableMovieLiveData = new MutableLiveData<List<Result>>();
//        mutableMovieLiveData.postValue(resultList);
////        LiveData<List<Result>> movieList = Transformations.map(resultList, result -> {
////            return result;
////        });
//        Log.i("MainViewModel","transformToLiveData triggered");
        this.liveDataResultList.postValue(resultList);
        //Log.i("MainViewModel","mutableMovieLiveData size= "+mutableMovieLiveData.getValue().size());
        //setAllMovies(mutableMovieLiveData);
    }

//    public void setAllMovies(MutableLiveData<List<Result>> resultList){
//        Log.i("MainViewModel","setAllMovies called");
//        this.liveDataResultList=resultList;
//    }






    public void writeToFile(ArrayList<Result> movieList, Context context) {

        try {
            FileOutputStream fileOut = new FileOutputStream(new File(context.getString(R.string.pathToFile)));
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(movieList);
            objectOut.close();
            fileOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
