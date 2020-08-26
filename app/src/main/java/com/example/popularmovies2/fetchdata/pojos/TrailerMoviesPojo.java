package com.example.popularmovies2.fetchdata.pojos;

import java.util.List;

import com.example.popularmovies2.fetchdata.pojos.TrailerMoviePojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


//http://www.jsonschema2pojo.org/

public class TrailerMoviesPojo {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("results")
    @Expose
    private List<TrailerMoviePojo> results = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<TrailerMoviePojo> getResults() {
        return results;
    }

    public void setResults(List<TrailerMoviePojo> results) {
        this.results = results;
    }

}
