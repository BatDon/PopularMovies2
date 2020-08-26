package com.example.popularmovies2.viewmodels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.popularmovies2.R;
import com.example.popularmovies2.RetrofitRequesters.RetrofitRequester;
import com.example.popularmovies2.RetrofitRequesters.RetrofitRequesterRelated;
import com.example.popularmovies2.fetchdata.pojos.Result;
import com.example.popularmovies2.fetchdata.pojos.TrailerMoviePojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RelatedListViewModel extends AndroidViewModel implements RetrofitRequesterRelated.RelatedOnRetrofitListener{

    Context context;
    int movieId;

    List<Result> relatedMovieList;
    public MutableLiveData<List<Result>> liveDataResultList=new MutableLiveData<List<Result>>(){};

    public RelatedListViewModel(@NonNull Application application, int movieId) {
        super(application);
        context=application;
        this.movieId=movieId;

        Result result=new Result("releaseData");
        List<Result> resultList=new ArrayList<Result>();
        resultList.add(result);
        liveDataResultList.setValue(resultList);
    }

    public void requestRelatedMovies(){new RetrofitRequesterRelated().requestMovies(this, Integer.toString(movieId));}

    public MutableLiveData<List<Result>> getAllRelatedMovies(){
        //requestGeneralMovies();
        return this.liveDataResultList;
    }

    public void writeToFile(ArrayList< Result > relatedMovieList) {

        try {
            FileOutputStream fileOut = new FileOutputStream(new File(context.getString(R.string.pathToFile)));
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(relatedMovieList);
            objectOut.close();
            fileOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void relatedOnRetrofitFinished(List<Result> relatedMovieList){
        this.relatedMovieList=relatedMovieList;
        transformToLiveData(relatedMovieList);
    }

    private void transformToLiveData(List<Result> resultList){
        this.liveDataResultList.postValue(resultList);
    }



}
