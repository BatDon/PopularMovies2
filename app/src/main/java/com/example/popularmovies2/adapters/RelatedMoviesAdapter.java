package com.example.popularmovies2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies2.R;

import com.example.popularmovies2.fetchdata.pojos.Result;
import com.squareup.picasso.Picasso;

import static com.example.popularmovies2.Constants.BASE_IMAGE_URL;
import static com.example.popularmovies2.Constants.IMAGE_SIZE;


public class RelatedMoviesAdapter extends RecyclerView.Adapter<RelatedMoviesAdapter.MovieViewHolder> {

    Result[] moviesList;
    private LayoutInflater mInflater;
    private OnRelatedMovieListener onRelatedMovieListener;


    Context context;

    public interface OnRelatedMovieListener{
        void onMovieClick(int position);
    }

    public RelatedMoviesAdapter(Context context, Result[] moviesList, OnRelatedMovieListener onRelatedMovieListener){
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.moviesList=moviesList;
        this.onRelatedMovieListener=onRelatedMovieListener;


    }


    @NonNull
    @Override
    public RelatedMoviesAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.related_movie_details_image, parent, false);
        return new MovieViewHolder(view, onRelatedMovieListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, final int position) {
        Result movie=moviesList[position];
//        holder.movieId.setText(movie.getId());
//        holder.movieThumbnail.setText(movie.getName());
        String imageAsString=movie.getBackdropPath();
//
        Picasso.get().load(BASE_IMAGE_URL+IMAGE_SIZE+imageAsString).into(holder.movieThumbnail);

    }


    public static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        TextView movieId;
        //GridRecyclerView
        ImageView movieThumbnail;
        OnRelatedMovieListener onRelatedMovieListener;
        public MovieViewHolder(View itemView, OnRelatedMovieListener onRelatedMovieListener){
            super(itemView);
//            movieId=itemView.findViewById(R.id.movie_id);
//            movieId.setVisibility(View.INVISIBLE);
//            int maxLength = 8;
//            InputFilter[] filterArray = new InputFilter[1];
//            filterArray[0] = new InputFilter.LengthFilter(maxLength);
//            movieId.setFilters(filterArray);

            movieThumbnail=itemView.findViewById(R.id.movie_thumbnail);
            this.onRelatedMovieListener=onRelatedMovieListener;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (onRelatedMovieListener != null){
                onRelatedMovieListener.onMovieClick(getAdapterPosition());
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
