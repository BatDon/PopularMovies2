package com.example.popularmovies2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies2.R;
import com.example.popularmovies2.userfavorites.UserFavorites;
//import com.example.popularmovies2.databinding.ActivityUserMovieDetailsBinding;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.UserMovieViewHolder> {

    final private UserMovieOnClickHandler mClickHandler;

    public interface UserMovieOnClickHandler {
        void onClick(String id);
    }

    private CursorAdapter userMovieCursorAdapter;
    private Context mContext;
    private FavoritesAdapter.UserMovieViewHolder holder;

    public static final String TAG= FavoritesAdapter.class.getSimpleName();

 //   private final Context context;
//    private ActivityUserMovieDetailsBinding activityUserMovieDetailsBinding;

//    final private UserMovieOnClickHandler userMovieOnClickHandler;

    private Cursor cursor;

//    public interface UserMovieOnClickHandler {
//        void onClick(int id);
//    }


    public class UserMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView movieId;
        TextView movieTitle;


        UserMovieViewHolder(View itemView) {
            super(itemView);

            movieId = (TextView) itemView.findViewById(R.id.movie_id);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            String movieId = cursor.getString(UserFavorites.INDEX_COLUMN_ID);
            mClickHandler.onClick(movieId);
        }


    }



    public FavoritesAdapter(Context context, UserMovieOnClickHandler userMovieOnClickHandler) {

        mContext = context;
        mClickHandler = userMovieOnClickHandler;
    }


    @Override
    public int getItemCount() {
        if (null == cursor){
            return 0;
        }
        return cursor.getCount();
    }

    @Override
    public void onBindViewHolder(UserMovieViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String movieId=cursor.getString(UserFavorites.INDEX_COLUMN_ID);
        String movieTitle=cursor.getString(UserFavorites.INDEX_COLUMN_TITLE);
        holder.movieId.setText(movieId);
        holder.movieTitle.setText(movieTitle);

    }

    @Override
    public UserMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Passing the inflater job to the cursor-adapter
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_user_movie_details, parent, false);
        holder = new UserMovieViewHolder(v);
        return holder;
    }

    public Cursor getMovieAtPosition(int position){
        int adapterPosition = position;
        cursor.moveToPosition(adapterPosition);
        return cursor;
    }













//    @Override
//    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
//        Movie movie = movieList.get(position);
//        holder.bind(movie);
//    }


    public void swapCursor(Cursor newUserMovieCursor) {
        Log.i(TAG, "swapCursor called in adapter");
        if(cursor==newUserMovieCursor){
            return;
        }

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



}
