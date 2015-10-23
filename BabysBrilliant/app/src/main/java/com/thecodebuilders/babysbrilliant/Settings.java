package com.thecodebuilders.babysbrilliant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Android Developer on 10/23/2015.
 */
public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().hide();
    }
}
