package com.thecodebuilders.babysbrilliant;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.thecodebuilders.network.VolleySingleton;
import com.thecodebuilders.utility.Constant;
import com.thecodebuilders.utility.CustomizeDialog;

import org.json.JSONObject;

/**
 * Created by Rahul Sonkhiya on 10/23/2015.
 */
public class LoginSignUpActivity extends AppCompatActivity {

    TextView login, reset_pass, sign_up;

    EditText email, passwd;
    private static String LOGVAR = "LoginSignUpActivity";
    RequestQueue queue;
    ProgressBar progressBar;
    CustomizeDialog customizeDialog;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup_activity);
        customizeDialog = new CustomizeDialog(LoginSignUpActivity.this);
         pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        getSupportActionBar().hide();
        init();

    }

    public void Click(View v) {


        if (v.getId() == R.id.login) {

           /* Intent mainIntent = new Intent(LoginSignUpActivity.this, MainActivity.class);
            startActivity(mainIntent);
            LoginSignUpActivity.this.finish();*/

            if (email.length() == 0) {
                showDialog("Enter Email id");
            } else if (passwd.length() == 0) {

                showDialog("Enter Password");
            } else {
                reset_pass.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getRemoteJSON(Constant.URL + "a=lgn&u=" + email.getText() + "&p=" + passwd.getText());
            }

        } else if (v.getId() == R.id.sign_up) {

            if (email.length() == 0) {
                showDialog("Enter Email id");
            } else if (passwd.length() == 0) {
                showDialog("Enter Password");
            } else {
                Intent mainIntent = new Intent(LoginSignUpActivity.this, ParentalChallengeScreen.class);
                startActivityForResult(mainIntent, 1);


            }
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.RESET_PASSWORD));
            startActivity(browserIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                reset_pass.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getRemoteJSON(Constant.URL + "a=sup&n=" + email.getText() + "&u=" + email.getText() + "&p=" + passwd.getText());
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    public void getRemoteJSON(String assetsURL) {
        queue = VolleySingleton.getInstance().getRequestQueue();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, assetsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    reset_pass.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    JSONObject result = new JSONObject(response);
                    if (result.getString("res").equalsIgnoreCase("successful")) {

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("user_id", result.getString("id"));
                        editor.commit(); // commit changes


                        Intent mainIntent = new Intent(LoginSignUpActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        LoginSignUpActivity.this.finish();
                    }
                    else {
                        showDialog("Something went wrong!");
                    }

                } catch (Throwable t) {
                    Log.e(LOGVAR, "Reverting to LOCAL JSON");

                }

                //TODO: handle for no internet connection
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                reset_pass.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Log.e(LOGVAR, "Reverting to LOCAL JSON. VOLLEY ERROR: " + error.getMessage());

            }
        });

        queue.add(stringRequest);
    }

    public void init() {

        login = (TextView) findViewById(R.id.login);
        reset_pass = (TextView) findViewById(R.id.reset_pass);
        sign_up = (TextView) findViewById(R.id.sign_up);
        email = (EditText) findViewById(R.id.et_email);
        passwd = (EditText) findViewById(R.id.et_passwd);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void showDialog(String msg) {

        customizeDialog.setTitle("Alert");
        customizeDialog.setMessage(msg);
        customizeDialog.show();
    }
}
