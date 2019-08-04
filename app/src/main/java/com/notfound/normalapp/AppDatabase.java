package com.notfound.normalapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ApodImageEntity.class},
    version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ApodImageDao apodImageDao();
}
