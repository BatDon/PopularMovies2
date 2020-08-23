package com.example.popularmovies2.RetrofitRequesters;

import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.popularmovies2.MovieDetails;
import com.example.popularmovies2.R;
import com.example.popularmovies2.fetchdata.MovieApi;
import com.example.popularmovies2.fetchdata.RetrofitClient;
import com.example.popularmovies2.fetchdata.pojos.ReviewPojo;
import com.example.popularmovies2.fetchdata.pojos.ReviewsPojo;
import com.example.popularmovies2.fetchdata.pojos.TrailerMoviePojo;
import com.example.popularmovies2.fetchdata.pojos.TrailerMoviesPojo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.example.popularmovies2.Constants.UNIQUE_API_KEY;


public class RetrofitRequesterReviews extends AppCompatActivity {

    public static final String TAG= RetrofitRequesterReviews.class.getSimpleName();

    public interface ReviewsOnRetrofitListener {
        public void reviewsOnRetrofitFinished(List<ReviewPojo> movieList);
    }

    private RetrofitRequesterReviews.ReviewsOnRetrofitListener reviewsOnRetrofitListener;


    public void requestMovies(RetrofitRequesterReviews.ReviewsOnRetrofitListener reviewsOnRetrofitListener, String id) {

        this.reviewsOnRetrofitListener = reviewsOnRetrofitListener;


        MovieApi movieApi = RetrofitClient.getRetrofitInstance().create(MovieApi.class);
        Call<ReviewsPojo> relatedMoviesCall = movieApi.getMovieReviews(id, UNIQUE_API_KEY);

        relatedMoviesCall.enqueue(new Callback<ReviewsPojo>() {
            @Override
            public void onResponse(Call<ReviewsPojo> call, Response<ReviewsPojo> response) {
                ReviewsPojo reviewsPojo=response.body();
                int id=reviewsPojo.getId();
                Log.i(TAG,"id= "+id);
                List<ReviewPojo> reviewPojoList=reviewsPojo.getResults();
                Log.i(TAG,reviewPojoList.size()+"");
                List<ReviewPojo> movieList = generateDataList(response.body());

                if (reviewsOnRetrofitListener != null) {
                    reviewsOnRetrofitListener.reviewsOnRetrofitFinished((movieList));
                }

            }

            @Override
            public void onFailure(Call<ReviewsPojo> call, Throwable t) {
                Toast.makeText(RetrofitRequesterReviews.this, R.string.problem_retrieving_data, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public List<ReviewPojo> generateDataList(ReviewsPojo relatedMovies) {
        List<ReviewPojo> resultList;
        if (relatedMovies == null) {
            Toast.makeText(this, R.string.parsing_problem, Toast.LENGTH_SHORT).show();
            resultList = null;
        } else {
            resultList = relatedMovies.getResults();
        }
        return resultList;
    }
}
