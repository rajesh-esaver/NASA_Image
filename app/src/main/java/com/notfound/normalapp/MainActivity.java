package com.notfound.normalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.pwittchen.swipe.library.rx2.SimpleSwipeListener;
import com.github.pwittchen.swipe.library.rx2.Swipe;
import com.notfound.normalapp.pojo.ApodImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements
        Toolbar.OnMenuItemClickListener,
        DatePickerDialog.OnDateSetListener,
        View.OnTouchListener {

    androidx.appcompat.widget.Toolbar toolbar;
    ImageView imgOfTheDay;
    TextView txtImgOfDayInfo;
    TextView txtDate;
    TextView txtImgTittle;
    TextView txtImgExplanation;
    TextView txtErrorUrl;
    ProgressBar pgsImgLoading;

    Calendar calendar;
    ApodViewModel apodViewModel;
    String currSelectedDate="", imgUrl = "",imgDesc="";
    Swipe swipe;
    Date todayDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgOfTheDay = findViewById(R.id.imgToday);
        toolbar = findViewById(R.id.toolbar);
        txtImgOfDayInfo = findViewById(R.id.txtImgOfTheDayInfo);
        txtDate = findViewById(R.id.txtImgDate);
        txtImgTittle = findViewById(R.id.txtImgTittle);
        txtImgExplanation = findViewById(R.id.txtImgDesc);
        txtErrorUrl = findViewById(R.id.txtErrorUrl);
        pgsImgLoading = findViewById(R.id.pgsImgLoading);

        //setting the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Constants.APP_BAR_NAME);
        toolbar.setOnMenuItemClickListener(this);

        apodViewModel = ViewModelProviders.of(this).get(ApodViewModel.class);
        loader();

        //get current day
        calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        todayDay = date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMATTER);
        String tmpCurrdate = simpleDateFormat.format(date);
        currSelectedDate = tmpCurrdate;
        requestNewImage(tmpCurrdate);

        swipe = new Swipe();
        listenForSwipes();

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        imgOfTheDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showToast("Clicked on image view");
                openFullImageViewActivity();
            }
        });
    }

    private void listenForSwipes() {
        swipe.setListener(new SimpleSwipeListener() {
            @Override
            public boolean onSwipedLeft(MotionEvent event) {
                showToast("swiped left");
                // need to move right
                requestNextImageOfTheDay();
                return super.onSwipedLeft(event);
            }

            @Override
            public boolean onSwipedRight(MotionEvent event) {
                showToast("swiped right");
                // need to move back
                requestPreviousImageOfTheDay();
                return super.onSwipedRight(event);
            }
        });
    }

    private void requestNextImageOfTheDay() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMATTER);

            //check currSelected equal to today's date
            String todayDate = simpleDateFormat.format(todayDay);
            if(todayDate.equals(currSelectedDate)){
                //current selected date is today's date, can't request next image
                //showToast("Can't move further than this");
                return;
            }

            Date date = simpleDateFormat.parse(currSelectedDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE,1);
            Date newDate = cal.getTime();

            String tmpCurrdate = simpleDateFormat.format(newDate);
            currSelectedDate = tmpCurrdate;
            //showToast(currSelectedDate); //to see the date
            requestNewImage(tmpCurrdate);
        }catch (Exception e){
            Timber.e(e.getMessage());
        }
    }

    private void requestPreviousImageOfTheDay() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMATTER);
            Date date = simpleDateFormat.parse(currSelectedDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE,-1);
            Date newDate = cal.getTime();

            String tmpCurrdate = simpleDateFormat.format(newDate);
            currSelectedDate = tmpCurrdate;
            //showToast(currSelectedDate); //to see the date
            requestNewImage(tmpCurrdate);
        }catch (Exception e){
            Log.e(getLocalClassName(),e.getMessage());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        swipe.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sample_menu,menu);
        return true;
    }

    public void loader(){
        apodViewModel.getApodImageLiveData().observe(this, new Observer<ApodImage>() {
            @Override
            public void onChanged(ApodImage apodImage) {
                showToast("live data");
                /*Glide.with(getApplicationContext())
                        .load(apodImage.getHdurl())
                        .into(imgOfTheDay);*/
                loadImageDetails(apodImage);
                apodViewModel.saveImageDataIntoDB(MainActivity.this,apodImage);
            }
        });

        apodViewModel.getApodImageMutableLiveData().observe(this, new Observer<ApodImage>() {
            @Override
            public void onChanged(ApodImage apodImage) {
                showToast("Mutable live data");
                /*Glide.with(getApplicationContext())
                        .load(apodImage.getHdurl())
                        .into(imgOfTheDay);*/
                loadImageDetails(apodImage);
            }
        });
    }

    public void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if(menuItem.getItemId()==R.id.menu_select_date){
            showDatePicker();
        }else if(menuItem.getItemId()==R.id.menu_clear_cache){
            //removing all records from db
            apodViewModel.deleteAllFromDB(MainActivity.this);
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void showDatePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,MainActivity.this,calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        txtErrorUrl.setText("");
        txtErrorUrl.setVisibility(View.GONE);
        String formattedDate = getFormattedDate(year,month,day);
        requestNewImage(formattedDate);
    }

    public void loadImageDetails(ApodImage apodImage){
        txtDate.setText(apodImage.getDate());
        txtImgTittle.setText(apodImage.getTittle()+":");
        txtImgExplanation.setText(apodImage.getExplanation());
        //making loading true until image appears
        pgsImgLoading.setVisibility(View.VISIBLE);
        currSelectedDate = apodImage.getDate();

        //glide options
        RequestOptions requestOptions = new RequestOptions()
                .error(R.drawable.img_not_found)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(getApplicationContext())
                .applyDefaultRequestOptions(requestOptions)
                .load(apodImage.getHdurl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        pgsImgLoading.setVisibility(View.GONE);
                        txtErrorUrl.setVisibility(View.VISIBLE);
                        txtErrorUrl.setText(apodImage.getHdurl());
                        imgUrl = "";
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        pgsImgLoading.setVisibility(View.GONE);
                        txtErrorUrl.setText("");
                        txtErrorUrl.setVisibility(View.GONE);
                        imgUrl = apodImage.getHdurl();
                        imgDesc = apodImage.getTittle();
                        showToast("Image loaded successfully");
                        return false;
                    }
                })
                .into(imgOfTheDay);
    }

    public void requestNewImage(String date){
        //apodViewModel.setImgDate(date);
        apodViewModel.getImageData(MainActivity.this,date);
    }

    public String getFormattedDate(int year,int month,int day){
        String formattedDate = String.valueOf(year);
        month += 1;

        if(month<10){
            formattedDate += "-" + "0" + String.valueOf(month);
        }else{
            formattedDate += "-" + String.valueOf(month);
        }

        if(day<10){
            formattedDate += "-" + "0" + String.valueOf(day);
        }else{
            formattedDate += "-" + String.valueOf(day);
        }
        showToast(formattedDate);
        return formattedDate;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public void openFullImageViewActivity() {
        if(imgUrl != "") {
            Intent intent = new Intent(MainActivity.this, FullImageView.class);
            intent.putExtra("img_url", imgUrl);
            intent.putExtra("img_desc", imgDesc);
            startActivity(intent);
        }
    }
}
