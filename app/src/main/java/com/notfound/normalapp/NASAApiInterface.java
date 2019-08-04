package com.notfound.normalapp;

import com.notfound.normalapp.pojo.ApodImage;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NASAApiInterface {

    String BASE_URL = "https://api.nasa.gov";

    @GET("/planetary/apod")
    Call<ApodImage> getImageOfTheDay(@Query("api_key") String apiKey);

    @GET("/planetary/apod")
    Call<ApodImage> getImageOfTheDate(@Query("api_key") String apiKey,@Query("date") String date,@Query("hd") Boolean hd);
}
