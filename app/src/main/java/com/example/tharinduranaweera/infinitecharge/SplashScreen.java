package com.example.tharinduranaweera.infinitecharge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

/*
* Splash screen of the application.
* HCI Fact
* Opens first and display the logo and gives the good look and attraction to the user
* Displays for 3.5 seconds
* */

public class SplashScreen extends AppCompatActivity {

    ImageView logo;
    final int SPLASH_SCREEN_TIMEOUT = 3500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.BLACK);
        setContentView(R.layout.activity_splash_screen);

        logo = (ImageView) findViewById(R.id.logo);

        //Animations
        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(2000);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        logo.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Finishing the splash screen activity and calls the Login page
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_TIMEOUT);

    }
}
