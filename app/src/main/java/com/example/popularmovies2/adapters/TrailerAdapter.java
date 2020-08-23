package com.example.popularmovies2.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies2.R;
import com.example.popularmovies2.fetchdata.pojos.TrailerMoviePojo;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MovieViewHolder> {

    TrailerMoviePojo[] moviesList;
    private LayoutInflater mInflater;
    private OnTrailerMovieListener onTrailerMovieListener;

    private final String TAG = TrailerAdapter.class.getSimpleName();


    Context context;

    public interface OnTrailerMovieListener{
        void onMovieClick(int position);
    }

    public TrailerAdapter(Context context, TrailerMoviePojo[] moviesList, OnTrailerMovieListener onTrailerMovieListener){
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.moviesList=moviesList;
        this.onTrailerMovieListener=onTrailerMovieListener;
    }


    @NonNull
    @Override
    public TrailerAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.trailer_details, parent, false);
        return new MovieViewHolder(view, onTrailerMovieListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, final int position) {
        TrailerMoviePojo movie=moviesList[position];
        int positionIncremented=position+1;
        Log.i(TAG,"position= "+positionIncremented);
        holder.trailerNumber.setText(Integer.toString(positionIncremented));


    }


    public static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView trailerNumber;
        OnTrailerMovieListener onTrailerMovieListener;
        public MovieViewHolder(View itemView, OnTrailerMovieListener onTrailerMovieListener){
            super(itemView);

            trailerNumber=itemView.findViewById(R.id.movie_trailer_number);
            this.onTrailerMovieListener=onTrailerMovieListener;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (onTrailerMovieListener != null){
                onTrailerMovieListener.onMovieClick(getAdapterPosition());
            }
        }
    }


    @Override
    public int getItemCount() {
        if(moviesList!=null) {
            int size= moviesList.length;
            return size;
        }
        return 0;
    }
}
