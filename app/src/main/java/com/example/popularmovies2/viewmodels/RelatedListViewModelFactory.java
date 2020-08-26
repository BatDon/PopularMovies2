package com.example.popularmovies2.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RelatedListViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application application;
    private int id;

    public RelatedListViewModelFactory(Application application, int id) {
        this.application=application;
        this.id = id;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RelatedListViewModel(application, id);
    }
}