package com.example.popularmovies2.RetrofitRequesters;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.popularmovies2.R;
import com.example.popularmovies2.fetchdata.MovieApi;
import com.example.popularmovies2.fetchdata.RetrofitClient;
import com.example.popularmovies2.fetchdata.pojos.MoviePojo;
import com.example.popularmovies2.fetchdata.pojos.Result;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.popularmovies2.Constants.UNIQUE_API_KEY;

public class RetrofitRequesterPopular extends AppCompatActivity {

    public interface OnPopularRetrofitListener {
        public void onPopularRetrofitFinished(List<Result> movieList);
    }

    private OnPopularRetrofitListener onPopularRetrofitListener;


    public void requestMovies(OnPopularRetrofitListener onPopularRetrofitListener) {

        this.onPopularRetrofitListener = onPopularRetrofitListener;


        MovieApi movieApi = RetrofitClient.getRetrofitInstance().create(MovieApi.class);
        Call<MoviePojo> call = movieApi.getSortedPopularMovies(UNIQUE_API_KEY);


        call.enqueue(new Callback<MoviePojo>() {
            @Override
            public void onResponse(Call<MoviePojo> call, Response<MoviePojo> response) {
                List<Result> movieList = generateDataList(response.body());
                if (onPopularRetrofitListener != null) {
                    onPopularRetrofitListener.onPopularRetrofitFinished((movieList));
                }
            }

            @Override
            public void onFailure(Call<MoviePojo> call, Throwable t) {
                Toast.makeText(RetrofitRequesterPopular.this, R.string.problem_retrieving_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<Result> generateDataList(MoviePojo moviePojoList) {
        List<Result> resultList;
        if (moviePojoList == null) {
            //           Toast.makeText(this, R.string.parsing_problem, Toast.LENGTH_SHORT).show();
            resultList = null;
        } else {
            resultList = moviePojoList.getResults();
        }
        return resultList;
    }
}

