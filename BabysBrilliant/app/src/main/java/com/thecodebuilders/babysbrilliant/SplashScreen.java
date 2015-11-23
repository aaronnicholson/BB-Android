package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Android Developer on 10/23/2015.
 */
public class SplashScreen extends AppCompatActivity {

    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    SharedPreferences pref;





    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = getApplicationContext().getSharedPreferences("BabyBrilliantPref", MODE_PRIVATE);


        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent that will start the Menu-Activity.

                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(mainIntent);
                SplashScreen.this.finish();

              /*  if (pref.getString("user_id", "").length() == 0 && pref.getString("user_id", "").equalsIgnoreCase("")) {

                    Intent mainIntent = new Intent(SplashScreen.this, LoginSignUpActivity.class);
                    startActivity(mainIntent);
                    SplashScreen.this.finish();
                } else {
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(mainIntent);
                    SplashScreen.this.finish();


                }*/

               /* Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(mainIntent);
                SplashScreen.this.finish();*/

            }
        }, SPLASH_DISPLAY_LENGTH);
    }





}
