package com.example.popularmovies2.RetrofitRequesters;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.popularmovies2.R;
import com.example.popularmovies2.fetchdata.MovieApi;
import com.example.popularmovies2.fetchdata.pojos.TrailerMoviePojo;
import com.example.popularmovies2.fetchdata.pojos.TrailerMoviesPojo;
import com.example.popularmovies2.fetchdata.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.popularmovies2.Constants.UNIQUE_API_KEY;

public class RetrofitRequesterTrailer extends AppCompatActivity {

    public interface TrailersOnRetrofitListener {
        public void trailersOnRetrofitFinished(List<TrailerMoviePojo> movieList);
    }

    private RetrofitRequesterTrailer.TrailersOnRetrofitListener trailersOnRetrofitListener;


    public void requestMovies(RetrofitRequesterTrailer.TrailersOnRetrofitListener trailersOnRetrofitListener, String id) {

        this.trailersOnRetrofitListener = trailersOnRetrofitListener;


        MovieApi movieApi = RetrofitClient.getRetrofitInstance().create(MovieApi.class);
        Call<TrailerMoviesPojo> relatedMoviesCall = movieApi.getMovieTrailers(id, UNIQUE_API_KEY);

        relatedMoviesCall.enqueue(new Callback<TrailerMoviesPojo>() {
            @Override
            public void onResponse(Call<TrailerMoviesPojo> call, Response<TrailerMoviesPojo> response) {
                List<TrailerMoviePojo> movieList = generateDataList(response.body());
                if (trailersOnRetrofitListener != null) {
                    trailersOnRetrofitListener.trailersOnRetrofitFinished((movieList));
                }

            }

            @Override
            public void onFailure(Call<TrailerMoviesPojo> call, Throwable t) {
                Toast.makeText(RetrofitRequesterTrailer.this, R.string.problem_retrieving_data, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public List<TrailerMoviePojo> generateDataList(TrailerMoviesPojo relatedMovies) {
        List<TrailerMoviePojo> resultList;
        if (relatedMovies == null) {
            Toast.makeText(this, R.string.parsing_problem, Toast.LENGTH_SHORT).show();
            resultList = null;
        } else {
            resultList = relatedMovies.getResults();
        }
        return resultList;
    }
}




