package com.anyasoft.es.surveyapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.anyasoft.es.surveyapp.location.GPSTracker;
import com.anyasoft.es.surveyapp.logger.L;

public class SplashScreen extends AppCompatActivity {

    //private static final int REQUEST_LOCATION_SETTING = 101;
    ImageView imageView;
    //DetectInternetConnection connectionDetector;
    private static final int CONNECTED_TO_INTERNET = 100;
    private GPSTracker gps;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        imageView = (ImageView) findViewById(R.id.splash_icon);

        Animation zoom	=	AnimationUtils.loadAnimation(SplashScreen.this, R.anim.splash_screen);

        AnimationSet s = new AnimationSet(false);

        s.addAnimation(zoom);
        imageView.startAnimation(s);

        s.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
               //checkAndMove();
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

}
