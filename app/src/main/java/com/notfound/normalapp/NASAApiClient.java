package com.notfound.normalapp;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.notfound.normalapp.pojo.ApodImage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class NASAApiClient {

    private Retrofit retrofit;
    private NASAApiInterface nasaApiInterface;

    public NASAApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(NASAApiInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        nasaApiInterface = retrofit.create(NASAApiInterface.class);
    }

    @NonNull
    public LiveData<ApodImage> getImageOfTheDay(String apiKey){
        final MutableLiveData<ApodImage> apodImageCall = new MutableLiveData<>();
        nasaApiInterface.getImageOfTheDay(apiKey).enqueue(new Callback<ApodImage>() {
            @Override
            public void onResponse(Call<ApodImage> call, Response<ApodImage> response) {
                apodImageCall.setValue(response.body());
                Timber.i(response.toString());
            }

            @Override
            public void onFailure(Call<ApodImage> call, Throwable t) {

            }
        });
        return apodImageCall;
    }

    @NonNull
    public LiveData<ApodImage> getImageOfTheDate(String apiKey,String date){
        final MutableLiveData<ApodImage> apodImageCall = new MutableLiveData<>();
        nasaApiInterface.getImageOfTheDate(apiKey,date,true).enqueue(new Callback<ApodImage>() {
            @Override
            public void onResponse(Call<ApodImage> call, Response<ApodImage> response) {
                apodImageCall.setValue(response.body());
                Timber.i(response.toString());
            }

            @Override
            public void onFailure(Call<ApodImage> call, Throwable t) {

            }
        });
        return apodImageCall;
    }
}
