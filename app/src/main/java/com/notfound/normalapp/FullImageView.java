package com.notfound.normalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
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
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;

public class FullImageView extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    PhotoView imgOfTheDay;
    TextView txtFullImageTitle;
    androidx.appcompat.widget.Toolbar toolbar;
    String imgUrl = "", imgDesc="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image_view);

        imgOfTheDay = findViewById(R.id.imgOfTheDay);
        txtFullImageTitle = findViewById(R.id.txtFullImageTitle);
        toolbar = findViewById(R.id.toolbar);

        //setting the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Image From NASA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setOnMenuItemClickListener(FullImageView.this);

        // setting dark action bar color
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black)); //status bar or the time bar at the top
        }

        imgUrl = getIntent().getStringExtra("img_url");
        imgDesc = getIntent().getStringExtra("img_desc");

        if(imgUrl != null && imgUrl !=""){
            loadImage(imgUrl);
            txtFullImageTitle.setText(imgDesc);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_share_action,menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void loadImage(String url) {
        //glide options
        RequestOptions requestOptions = new RequestOptions()
                .error(R.drawable.img_not_found)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(getApplicationContext())
                .applyDefaultRequestOptions(requestOptions)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //showToast("Image loaded successfully");
                        return false;
                    }
                })
                .into(imgOfTheDay);
    }

    public void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId()==R.id.menu_share_image){
            shareImage();
        }
        return true;
    }

    public void shareImage() {

        BitmapDrawable drawable = (BitmapDrawable) imgOfTheDay.getDrawable();
        Bitmap bitImage = drawable.getBitmap();
        try {
            //saving the image
            File cachePath = new File(this.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

            //getting uri
            File imagePath = new File(this.getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", newFile);

            if(contentUri!=null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);

                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));

                shareIntent.putExtra(Intent.EXTRA_STREAM,contentUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT,imgDesc);

                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent,getResources().getText(R.string.share_send_to)));
            }
        }catch (Exception e) {
            Log.e(getLocalClassName(),e.getMessage());
            showToast("some problem, can't share this image");
        }
    }
}
