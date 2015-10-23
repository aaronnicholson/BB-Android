package com.thecodebuilders.babysbrilliant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Android Developer on 10/23/2015.
 */
public class LoginSignUpActivity extends AppCompatActivity {

    TextView login;
    TextView reset_pass;
    TextView sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup_activity);

        getSupportActionBar().hide();

        login = (TextView) findViewById(R.id.login);
        reset_pass = (TextView) findViewById(R.id.reset_pass);
        sign_up = (TextView) findViewById(R.id.sign_up);
    }

    public void Click(View v){

        if(v.getId()==R.id.login){

            Intent mainIntent = new Intent(LoginSignUpActivity.this, MainActivity.class);
            startActivity(mainIntent);
            LoginSignUpActivity.this.finish();
        }
    }
}
