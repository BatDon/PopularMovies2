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

//    private void generalMoviesOrPopularMovies(Call call){
//
//        call.enqueue(new Callback<MoviePojo>() {
//            @Override
//            public void onResponse(Call<MoviePojo> call, Response<MoviePojo> response) {
//                List<Result> movieList=generateDataList(response.body());
//                if (onRetrofitListener != null){
//                    onRetrofitListener.onRetrofitFinished((movieList));
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<MoviePojo> call, Throwable t) {
//                Toast.makeText(RetrofitRequester.this, R.string.problem_retrieving_data, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//    }
//
//    public List<Result> generateDataList(MoviePojo moviePojoList) {
//        List<Result> resultList;
//        if (moviePojoList == null) {
//            Toast.makeText(this, R.string.parsing_problem, Toast.LENGTH_SHORT).show();
//            resultList=null;
//        } else {
//            resultList = moviePojoList.getResults();
//        }
//        return resultList;
//    }
//
//
//
//    private void relatedMovies(Call relatedMoviesCall){
//        relatedMoviesCall.enqueue(new Callback<RelatedMovies>() {
//            @Override
//            public void onResponse(Call<RelatedMovies> call, Response<RelatedMovies> response) {
//                List<Result> movieList=generateRelatedMoviesList(response.body());
//                if (onRetrofitListener != null){
//                    onRetrofitListener.onRetrofitFinished((movieList));
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<RelatedMovies> call, Throwable t) {
//                Toast.makeText(RetrofitRequester.this, R.string.problem_retrieving_data, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public List<Result> generateRelatedMoviesList(RelatedMovies relatedMovies) {
//        List<Result> resultList;
//        if (relatedMovies == null) {
//            Toast.makeText(this, R.string.parsing_problem, Toast.LENGTH_SHORT).show();
//            resultList=null;
//        } else {
//            resultList = relatedMovies.getResults();
//        }
//        return resultList;
//    }


