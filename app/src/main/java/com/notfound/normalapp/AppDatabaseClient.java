package com.notfound.normalapp;

import android.content.Context;

import androidx.room.Room;

public class AppDatabaseClient {
    private Context context;
    private static AppDatabaseClient appDatabaseClient;

    //App Database object
    private AppDatabase appDatabase;

    private AppDatabaseClient(Context context){
        this.context = context;

        //creating App Database object with Room database builder
        appDatabase = Room.databaseBuilder(context,AppDatabase.class,Constants.APP_DB_NAME).build();
    }

    public static synchronized AppDatabaseClient getAppDatabaseClient(Context context){
        if(appDatabaseClient==null){
            appDatabaseClient = new AppDatabaseClient(context);
        }
        return appDatabaseClient;
    }

    public AppDatabase getAppDatabase(){
        return appDatabase;
    }
}
