package com.notfound.normalapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ApodImageDao {

    @Query("select * from "+Constants.APOD_IMAGE_TABLE_NAME)
    List<ApodImageEntity> getAll();

    @Insert
    void insert(ApodImageEntity apodImageEntity);

    @Update
    void update(ApodImageEntity apodImageEntity);

    @Delete
    void delete(ApodImageEntity apodImageEntity);

    @Query("select * from "+Constants.APOD_IMAGE_TABLE_NAME+" where date = :date")
    ApodImageEntity findImageByDate(String date);

    @Query("delete from "+Constants.APOD_IMAGE_TABLE_NAME)
    void deleteAll();
}
