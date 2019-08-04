package com.notfound.normalapp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.notfound.normalapp.pojo.ApodImage;

public class ApodViewModel extends ViewModel {

    private MutableLiveData<ApodImage> apodImageMutableLiveData = new MutableLiveData<>();
    private NASAApiClient nasaApiClient = new NASAApiClient();
    private MutableLiveData<String> imgDate = new MutableLiveData<>();
    private LiveData<ApodImage> apodImageLiveData = Transformations.switchMap(imgDate,(new_date)-> nasaApiClient.getImageOfTheDate(Constants.API_KEY,new_date));

    public void ApodViewModel(){
        nasaApiClient = new NASAApiClient();

    }

    public LiveData<ApodImage> getApodImageLiveData() {
        return apodImageLiveData;
    }

    public MutableLiveData<ApodImage> getApodImageMutableLiveData() {
        return apodImageMutableLiveData;
    }

    public void getImageData(Context context, String date){

        class GetApodImage extends AsyncTask<Void,Void,ApodImageEntity> {

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
                if(apodImageEntity==null) {
                    //image not exists for this day, need to make api call
                    setImgDate(date);
                }else {
                    ApodImage apodImage = new ApodImage(apodImageEntity.getCopyright(),
                            apodImageEntity.getDate(),
                            apodImageEntity.getExplanation(),
                            apodImageEntity.getHdurl(),
                            apodImageEntity.getMediaType(),
                            apodImageEntity.getServiceVersion(),
                            apodImageEntity.getTittle(),
                            apodImageEntity.getUrl());

                    apodImageMutableLiveData.postValue(apodImage);
                }
            }
        }
        GetApodImage getApodImage = new GetApodImage();
        getApodImage.execute();
    }

    public void saveImageDataIntoDB(Context context,ApodImage apodImage){

        class SaveApodImage extends AsyncTask<Void,Void,Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                if(apodImage.getTittle()==null){
                    return null;
                }
                if(apodImage.getHdurl()==null && apodImage.getUrl()==null){
                    return null;
                }
                if(apodImage.getHdurl()==null && apodImage.getUrl()!=null){
                    apodImage.setHdurl(apodImage.getUrl());
                }
                ApodImageEntity apodImageEntity = new ApodImageEntity();
                apodImageEntity.setCopyright(apodImage.getCopyright());
                apodImageEntity.setDate(apodImage.getDate());
                apodImageEntity.setExplanation(apodImage.getExplanation());
                apodImageEntity.setHdurl(apodImage.getHdurl());
                apodImageEntity.setMediaType(apodImage.getMediaType());
                apodImageEntity.setServiceVersion(apodImage.getServiceVersion());
                apodImageEntity.setTittle(apodImage.getTittle());
                apodImageEntity.setUrl(apodImage.getUrl());

                AppDatabaseClient.getAppDatabaseClient(context)
                        .getAppDatabase()
                        .apodImageDao()
                        .insert(apodImageEntity);
                return null;
            }
        }

        SaveApodImage saveApodImage = new SaveApodImage();
        saveApodImage.execute();
    }

    public void deleteAllFromDB(Context context){
        class DeleteAllApodImages extends AsyncTask<Void,Void,Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabaseClient.getAppDatabaseClient(context)
                        .getAppDatabase()
                        .apodImageDao()
                        .deleteAll();
                return null;
            }
        }
        DeleteAllApodImages deleteAllApodImages=new DeleteAllApodImages();
        deleteAllApodImages.execute();
    }

    public void setImgDate(String date) {
        imgDate.setValue(date);
    }
}
