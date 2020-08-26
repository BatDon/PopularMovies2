package com.example.popularmovies2.RetrofitRequesters;

import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.popularmovies2.R;
import com.example.popularmovies2.fetchdata.MovieApi;
import com.example.popularmovies2.fetchdata.pojos.MoviePojo;
import com.example.popularmovies2.fetchdata.pojos.Result;
import com.example.popularmovies2.fetchdata.RetrofitClient;
import com.example.popularmovies2.relatedmovies.RelatedMoviesList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.popularmovies2.Constants.UNIQUE_API_KEY;

public class RetrofitRequesterRelated extends AppCompatActivity {

    private final String TAG = RetrofitRequesterRelated.class.getSimpleName();

    public interface RelatedOnRetrofitListener {
        public void relatedOnRetrofitFinished(List<Result> movieList);
    }

    private RetrofitRequesterRelated.RelatedOnRetrofitListener relatedOnRetrofitListener;


    public void requestMovies(RetrofitRequesterRelated.RelatedOnRetrofitListener relatedOnRetrofitListener, String id) {

        this.relatedOnRetrofitListener = relatedOnRetrofitListener;


        MovieApi movieApi = RetrofitClient.getRetrofitInstance().create(MovieApi.class);
        Call<MoviePojo> relatedMoviesCall = movieApi.getRelatedMovies(id, UNIQUE_API_KEY);

        relatedMoviesCall.enqueue(new Callback<MoviePojo>() {
            @Override
            public void onResponse(Call<MoviePojo> call, Response<MoviePojo> response) {

//                if(response==null || response.body().getResults().size()==0){
//                    requestMovies(relatedOnRetrofitListener, id);
//                }
                List<Result> movieList = generateDataList(response.body());
                if(movieList!=null){
                    Log.i(TAG,"movieList size= "+movieList.size());
//                    if(movieList.size()==0){
//                        requestMovies(relatedOnRetrofitListener, id);
//                    }
                }
                if (relatedOnRetrofitListener != null) {
                    relatedOnRetrofitListener.relatedOnRetrofitFinished((movieList));
                }

            }

            @Override
            public void onFailure(Call<MoviePojo> call, Throwable t) {
                Toast.makeText(RetrofitRequesterRelated.this, R.string.problem_retrieving_data, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public List<Result> generateDataList(MoviePojo moviePojo) {
        List<Result> resultList;
        if (moviePojo == null) {
            Toast.makeText(this, R.string.parsing_problem, Toast.LENGTH_SHORT).show();
            resultList = null;
        } else {
            resultList = moviePojo.getResults();
            Log.i(TAG,"resultList size= "+resultList.size());
        }
        return resultList;
    }
}
