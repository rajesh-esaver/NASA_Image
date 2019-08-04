package com.notfound.normalapp;

import android.content.Context;
import android.os.AsyncTask;

public class ApodImageDBOps {

    public void findApodImageByDate(Context context,String date){

        class GetApodImage extends AsyncTask<Void,Void,ApodImageEntity>{

            @Override
            protected ApodImageEntity doInBackground(Void... voids) {
                return AppDatabaseClient.getAppDatabaseClient(context)
                        .getAppDatabase()
                        .apodImageDao()
                        .findImageByDate(date);
            }

            @Override
            protected void onPostExecute(ApodImageEntity apodImageEntity) {
                super.onPostExecute(apodImageEntity);
            }
        }
        GetApodImage getApodImage = new GetApodImage();
        getApodImage.execute();
        //getApodImage.get();
    }
}
