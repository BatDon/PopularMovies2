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
import com.example.popularmovies2.fetchdata.pojos.ReviewPojo;
import com.example.popularmovies2.fetchdata.pojos.TrailerMoviePojo;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MovieViewHolder> {

    ReviewPojo[] moviesList;
    private LayoutInflater mInflater;
//    private OnReviewMovieListener onReviewMovieListener;

    private final String TAG = ReviewAdapter.class.getSimpleName();


    Context context;

//    public interface OnReviewMovieListener{
//        void onMovieClick(int position);
//    }

    //public ReviewAdapter(Context context, ReviewPojo[] moviesList, OnReviewMovieListener onReviewMovieListener)
    public ReviewAdapter(Context context, ReviewPojo[] moviesList){
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.moviesList=moviesList;
//        this.onReviewMovieListener=onReviewMovieListener;
    }


    @NonNull
    @Override
    public ReviewAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.review_movie_details, parent, false);
//        return new MovieViewHolder(view, onReviewMovieListener);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, final int position) {
        ReviewPojo movie=moviesList[position];
        Log.i(TAG,"author= "+movie.getAuthor());
        holder.author.setText(movie.getAuthor());
        holder.content.setText(movie.getContent());

    }


    //public static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public static class MovieViewHolder extends RecyclerView.ViewHolder{
        TextView author;
        TextView content;
        //OnReviewMovieListener onReviewMovieListener;
//        public MovieViewHolder(View itemView, OnReviewMovieListener onReviewMovieListener){
        public MovieViewHolder(View itemView){
            super(itemView);

            author=itemView.findViewById(R.id.author);
            content=itemView.findViewById(R.id.content);
            //this.onReviewMovieListener=onReviewMovieListener;

            //itemView.setOnClickListener(this);
        }


//        @Override
//        public void onClick(View view) {
//            if (onReviewMovieListener != null){
//                onReviewMovieListener.onMovieClick(getAdapterPosition());
//            }
//        }
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
