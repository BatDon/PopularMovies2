package com.example.popularmovies2.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.popularmovies2.RetrofitRequesters.RetrofitRequester;
import com.example.popularmovies2.RetrofitRequesters.RetrofitRequesterPopular;
import com.example.popularmovies2.adapters.GridAdapter;

public class MovieDetailsViewModel extends AndroidViewModel implements GridAdapter.OnMovieListener,
        RetrofitRequester.OnRetrofitListener, RetrofitRequesterPopular.OnPopularRetrofitListener{


    public MovieDetailsViewModel(@NonNull Application application) {
        super(application);
    }
}
