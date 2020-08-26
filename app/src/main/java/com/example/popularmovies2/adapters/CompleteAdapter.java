package com.example.popularmovies2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies2.R;
import com.example.popularmovies2.fetchdata.pojos.Result;

import com.example.popularmovies2.userfavorites.UserFavorites;
import com.squareup.picasso.Picasso;

import static com.example.popularmovies2.Constants.BASE_IMAGE_URL;
import static com.example.popularmovies2.Constants.GRID_RECYCLER_VIEW;
import static com.example.popularmovies2.Constants.IMAGE_SIZE;
import static com.example.popularmovies2.Constants.LIST_RECYCLER_VIEW;

public class CompleteAdapter extends RecyclerView.Adapter<CompleteAdapter.UserMovieViewHolder> {


    private CompleteAdapter.OnMovieListener onMovieListener;

    public interface OnMovieListener{
        void onMovieClick(int position);
    }

    private CursorAdapter userMovieCursorAdapter;
    private Context mContext;
    private CompleteAdapter.UserMovieViewHolder holder;
    Result[] moviesList;
    private int viewType;

    public static final String TAG= CompleteAdapter.class.getSimpleName();

    int layoutId;


    private Cursor cursor;

    class UserMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        OnMovieListener onMovieListener;
        //GridRecyclerView
        ImageView movieThumbnail;

        //RecyclerViewList
        TextView movieId;
        TextView movieTitle;


        UserMovieViewHolder(View itemView, CompleteAdapter.OnMovieListener onMovieListener) {
            super(itemView);

            Log.i(TAG,itemView.toString());

            this.onMovieListener=onMovieListener;
            itemView.setOnClickListener(this);

            if (layoutId == R.layout.movie_image_item) {
                movieThumbnail = itemView.findViewById(R.id.movie_thumbnail);
                if(movieThumbnail!=null){
                    Log.i(TAG,"movieThumbnail found");
                }
                else{
                    Log.i(TAG,"moveThumbnail equals null");
                }
            } else {
                movieId = (TextView) itemView.findViewById(R.id.movie_id);
                movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            }
        }

        @Override
        public void onClick(View view) {
                if(layoutId==R.layout.movie_image_item) {
                    if (onMovieListener != null){
                        onMovieListener.onMovieClick(getAdapterPosition());
                    }
                }
                else {
//                    mClickHandler.onClick(Integer.toString(getAdapterPosition()));
                    if (onMovieListener != null){
                        onMovieListener.onMovieClick(getAdapterPosition());
                    }

                    int adapterPosition = getAdapterPosition();
                    cursor.moveToPosition(adapterPosition);
                    String movieId = cursor.getString(UserFavorites.INDEX_COLUMN_ID);
                    onMovieListener.onMovieClick(Integer.parseInt(movieId));
                }

        }

    }

    public CompleteAdapter(Context context, OnMovieListener onMovieListener, Result[] moviesList, int viewType) {

        mContext = context;
        this.onMovieListener = onMovieListener;
        this.moviesList=moviesList;
        this.viewType=viewType;
    }

    @Override
    public void onBindViewHolder(CompleteAdapter.UserMovieViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType) {

            case GRID_RECYCLER_VIEW: {
                layoutId = R.layout.movie_image_item;
                Result movie = moviesList[position];
                String imageAsString = movie.getBackdropPath();
                Log.i(TAG,"imageAsString= "+imageAsString);
                Picasso.get().load(BASE_IMAGE_URL + IMAGE_SIZE + imageAsString).into(holder.movieThumbnail);

                break;
            }

            case LIST_RECYCLER_VIEW: {
                Log.i(TAG,"LIST_RECYCLER_VIEW in onBindViewHolder ");
                layoutId = R.layout.activity_user_movie_details;
                cursor.moveToPosition(position);
                String movieId = cursor.getString(UserFavorites.INDEX_COLUMN_ID);
                String movieTitle = cursor.getString(UserFavorites.INDEX_COLUMN_TITLE);
                holder.movieId.setText(movieId);
                holder.movieTitle.setText(movieTitle);
                break;
            }

//        if(moviesList==null && cursor!=null) {
//            cursor.moveToPosition(position);
//            String movieId = cursor.getString(UserFavorites.INDEX_COLUMN_ID);
//            String movieTitle = cursor.getString(UserFavorites.INDEX_COLUMN_TITLE);
//            holder.movieId.setText(movieId);
//            holder.movieTitle.setText(movieTitle);
//        }


        }
    }

    @Override
    public CompleteAdapter.UserMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Passing the inflater job to the cursor-adapter
        Log.i(TAG,"viewType= "+viewType);

        switch (viewType) {

            case GRID_RECYCLER_VIEW: {
                layoutId = R.layout.movie_image_item;
                break;
            }

            case LIST_RECYCLER_VIEW: {
                layoutId = R.layout.activity_user_movie_details;
                break;
            }

            default:
                throw new IllegalArgumentException("ViewType " + viewType + " is not implemented");
        }


        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        holder = new CompleteAdapter.UserMovieViewHolder(view, onMovieListener);
        return holder;
    }

    public Cursor getMovieAtPosition(int position){
        int adapterPosition = position;
        cursor.moveToPosition(adapterPosition);
        return cursor;
    }



    public void swapCursor(Cursor newUserMovieCursor) {
        Log.i(TAG, "swapCursor called in adapter");
        if(cursor==newUserMovieCursor){
            return;
        }


//        cursor = newUserMovieCursor;
//        String[] stAr=newUserMovieCursor.getColumnNames();
//        for (String columnName:stAr) {
//            Log.i(TAG,columnName);
//        }
//        notifyDataSetChanged();

        if(cursor!=null) {
            cursor = newUserMovieCursor;
            String[] stAr=newUserMovieCursor.getColumnNames();
            for (String columnName:stAr) {
                Log.i(TAG,columnName);
            }
            notifyDataSetChanged();
        }
        else{
            cursor = newUserMovieCursor;
            Log.i(TAG,"cursor equals null");
            newUserMovieCursor.moveToFirst();
            int colCount=newUserMovieCursor.getColumnCount();
            Log.i(TAG,"colCount= "+colCount);
            String title=newUserMovieCursor.getString(UserFavorites.INDEX_COLUMN_TITLE);
            Log.i(TAG,"title= "+title);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (viewType==GRID_RECYCLER_VIEW) {
            return GRID_RECYCLER_VIEW;
        } else {
            return LIST_RECYCLER_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        if (cursor == null && moviesList != null){
            return moviesList.length;
        }
        else if (cursor != null && moviesList == null){
            return cursor.getCount();
        }
//        return cursor.getCount();
        return 0;
    }



}




//public class CompleteAdapter extends RecyclerView.Adapter<CompleteAdapter.UserMovieViewHolder> {
//
//    final private UserMovieOnClickHandler mClickHandler;
//
//    public interface UserMovieOnClickHandler {
//        void onClick(String id);
//    }
//
//    private CursorAdapter userMovieCursorAdapter;
//    private Context mContext;
//    private CompleteAdapter.UserMovieViewHolder holder;
//
//    public static final String TAG= CompleteAdapter.class.getSimpleName();
//
// //   private final Context context;
////    private ActivityUserMovieDetailsBinding activityUserMovieDetailsBinding;
//
////    final private UserMovieOnClickHandler userMovieOnClickHandler;
//
//    private Cursor cursor;
//
////    public interface UserMovieOnClickHandler {
////        void onClick(int id);
////    }
//
//
//    public class UserMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//        TextView movieId;
//        TextView movieTitle;
//
//
//        UserMovieViewHolder(View itemView) {
//            super(itemView);
//
//            movieId = (TextView) itemView.findViewById(R.id.movie_id);
//            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
//
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View view) {
//            int adapterPosition = getAdapterPosition();
//            cursor.moveToPosition(adapterPosition);
//            String movieId = cursor.getString(UserFavorites.INDEX_COLUMN_ID);
//            mClickHandler.onClick(movieId);
//        }
//
//
//    }
//
//
//
//    public CompleteAdapter(Context context, UserMovieOnClickHandler userMovieOnClickHandler) {
//
//        mContext = context;
//        mClickHandler = userMovieOnClickHandler;
//    }
//
//
//    @Override
//    public int getItemCount() {
//        if (null == cursor){
//            return 0;
//        }
//        return cursor.getCount();
//    }
//
//    @Override
//    public void onBindViewHolder(UserMovieViewHolder holder, int position) {
//        cursor.moveToPosition(position);
//        String movieId=cursor.getString(UserFavorites.INDEX_COLUMN_ID);
//        String movieTitle=cursor.getString(UserFavorites.INDEX_COLUMN_TITLE);
//        holder.movieId.setText(movieId);
//        holder.movieTitle.setText(movieTitle);
//
//    }
//
//    @Override
//    public UserMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        // Passing the inflater job to the cursor-adapter
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.activity_user_movie_details, parent, false);
//        holder = new UserMovieViewHolder(v);
//        return holder;
//    }
//
//    public Cursor getMovieAtPosition(int position){
//        int adapterPosition = position;
//        cursor.moveToPosition(adapterPosition);
//        return cursor;
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
////    @Override
////    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
////        Movie movie = movieList.get(position);
////        holder.bind(movie);
////    }
//
//
//    public void swapCursor(Cursor newUserMovieCursor) {
//        Log.i(TAG, "swapCursor called in adapter");
//        if(cursor==newUserMovieCursor){
//            return;
//        }
//
//        if(cursor!=null) {
//            cursor = newUserMovieCursor;
//            String[] stAr=newUserMovieCursor.getColumnNames();
//            for (String columnName:stAr) {
//                Log.i(TAG,columnName);
//            }
//            notifyDataSetChanged();
//        }
//        else{
//            cursor = newUserMovieCursor;
//            Log.i(TAG,"cursor equals null");
//            newUserMovieCursor.moveToFirst();
//            int colCount=newUserMovieCursor.getColumnCount();
//            Log.i(TAG,"colCount= "+colCount);
//            String title=newUserMovieCursor.getString(UserFavorites.INDEX_COLUMN_TITLE);
//            Log.i(TAG,"title= "+title);
//            notifyDataSetChanged();
//        }
//    }
//
//
//
//}
