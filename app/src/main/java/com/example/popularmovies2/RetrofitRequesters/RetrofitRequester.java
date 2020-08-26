package com.example.popularmovies2.RetrofitRequesters;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.popularmovies2.R;
import com.example.popularmovies2.fetchdata.MovieApi;
import com.example.popularmovies2.fetchdata.pojos.MoviePojo;
import com.example.popularmovies2.fetchdata.pojos.TrailerMoviesPojo;
import com.example.popularmovies2.fetchdata.pojos.Result;
import com.example.popularmovies2.fetchdata.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.popularmovies2.Constants.REQUEST_MOVIE_LIST;
import static com.example.popularmovies2.Constants.REQUEST_SORTED_POPULAR_MOVIES;
import static com.example.popularmovies2.Constants.UNIQUE_API_KEY;

public class RetrofitRequester extends AppCompatActivity {

    public interface OnRetrofitListener {
        public void onRetrofitFinished(List<Result> movieList);
    }
    private OnRetrofitListener onRetrofitListener;


    public void requestMovies(OnRetrofitListener onRetrofitListener) {

        this.onRetrofitListener = onRetrofitListener;


        MovieApi movieApi = RetrofitClient.getRetrofitInstance().create(MovieApi.class);
        Call<MoviePojo> call=movieApi.getAllMovies(UNIQUE_API_KEY);

        call.enqueue(new Callback<MoviePojo>() {
            @Override
            public void onResponse(Call<MoviePojo> call, Response<MoviePojo> response) {
                List<Result> movieList=generateDataList(response.body());
                if (onRetrofitListener != null){
                    onRetrofitListener.onRetrofitFinished((movieList));
                }
            }

            @Override
            public void onFailure(Call<MoviePojo> call, Throwable t) {
                Toast.makeText(RetrofitRequester.this, R.string.problem_retrieving_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<Result> generateDataList(MoviePojo moviePojoList) {
        List<Result> resultList;
        if (moviePojoList == null) {
 //           Toast.makeText(this, R.string.parsing_problem, Toast.LENGTH_SHORT).show();
            resultList=null;
        } else {
            resultList = moviePojoList.getResults();
        }
        return resultList;
    }

}
