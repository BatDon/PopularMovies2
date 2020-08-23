package com.example.popularmovies2.fetchdata;

import com.example.popularmovies2.fetchdata.pojos.MoviePojo;
import com.example.popularmovies2.fetchdata.pojos.ReviewsPojo;
import com.example.popularmovies2.fetchdata.pojos.TrailerMoviesPojo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApi {

    @GET("/3/movie/popular")
    Call<MoviePojo> getAllMovies(@Query("api_key") String api_key);

    @GET("/3/movie/top_rated")
    Call<MoviePojo> getSortedPopularMovies(@Query("api_key") String api_key);

    @GET("/3/movie/{movie_id}/similar")
    Call<MoviePojo> getRelatedMovies(@Path("movie_id") String movie_id, @Query("api_key") String api_key);

    @GET("/3/movie/{id}/videos")
    Call<TrailerMoviesPojo> getMovieTrailers(@Path("id") String id, @Query("api_key") String api_key);

    @GET("3/movie/{id}/reviews")
    Call<ReviewsPojo> getMovieReviews(@Path("id") String id, @Query("api_key") String api_key);



}
