package com.example.popularmovies2.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RelatedMoviesDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application application;
    private int position;

    public RelatedMoviesDetailViewModelFactory(Application application, int position) {
        this.application=application;
        this.position = position;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RelatedMovieDetailsViewModel(application, position);
    }
}