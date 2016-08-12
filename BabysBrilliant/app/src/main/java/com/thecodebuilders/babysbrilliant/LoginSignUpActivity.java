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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.thecodebuilders.network.VolleySingleton;
import com.thecodebuilders.utility.Constant;
import com.thecodebuilders.utility.CustomizeDialog;
import com.thecodebuilders.utility.Utils;

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

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup_activity);

        pref = getApplicationContext().getSharedPreferences("BabyBrilliantPref", MODE_PRIVATE);

        getSupportActionBar().hide();
        init();

    }

    public void Click(View v) {


        if (v.getId() == R.id.login) {

           /* Intent mainIntent = new Intent(LoginSignUpActivity.this, MainActivity.class);
            startActivity(mainIntent);
            LoginSignUpActivity.this.finish();*/

            if (email.length() == 0) {
                showDialog("Enter Email Address");
            } else if (passwd.length() == 0) {
                showDialog("Enter Password");
            } else {
                reset_pass.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                if(Utils.isNetworkAvailable(LoginSignUpActivity.this))
                     getRemoteJSON(Constant.URL + "a=lgn&u=" + email.getText().toString() + "&p=" + passwd.getText().toString(), email.getText().toString(), passwd.getText().toString(), "SignIn");
                else
                    Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();

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

    public void showDialog(String msg) {
        CustomizeDialog customizeDialog = new CustomizeDialog(LoginSignUpActivity.this);

        customizeDialog.setTitle("Login Failure!");
        customizeDialog.setMessage(msg);
        customizeDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOGVAR, requestCode+"; "+resultCode+"; "+Activity.RESULT_OK+"; "+data.getStringExtra("Key"));
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                if (data.getStringExtra("Key")!=null && data.getStringExtra("Key").equalsIgnoreCase("Cancel")) {
                    //do nothing
                }
                else {
                    reset_pass.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    getRemoteJSON(Constant.URL + "a=sup&n=" + email.getText().toString() + "&u="
                            + email.getText().toString() + "&p=" + passwd.getText(), email.getText().toString(), passwd.getText().toString(), "SignUp");
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //do nothing
            }
       }
    }//onActivityResult

    public void getRemoteJSON(String assetsURL, final String username, final String password, final String type) {
        queue = VolleySingleton.getInstance().getRequestQueue();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, assetsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    reset_pass.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Log.d(LOGVAR, "Response:"+response);
                    JSONObject result = new JSONObject(response);
                    if (result.getString("res").equalsIgnoreCase("successful")) {


                        if (type.equalsIgnoreCase("SignUp")) {

                            Intent mainIntent = new Intent(LoginSignUpActivity.this, ShowIntroActivity.class);
                            mainIntent.putExtra("Key", "SignUp");
                            startActivity(mainIntent);
                            LoginSignUpActivity.this.finish();
                        } else {

                            Intent mainIntent = new Intent(LoginSignUpActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            LoginSignUpActivity.this.finish();
                        }
                    } else if (result.getString("res").equalsIgnoreCase("unsuccessful") ||
                            result.getString("msg").equalsIgnoreCase("This email address is already registered. Please tap Log In.")) {
// The password is incorrect
                        showDialog("The login is incorrect");

                    } else {
                        showDialog("Something went wrong!");
                    }

                    SharedPreferences.Editor editor = pref.edit();


                    editor.putString("user_id", result.getString("id"));
                    editor.putString("user_name", username);
                    editor.putString("user_password", password);
                    editor.commit(); // commit changes

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

        /*login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset_pass.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                getRemoteJSON(Constant.URL + "a=lgn&u=" + email.getText().toString() + "&p=" + passwd.getText().toString(), email.getText().toString(), passwd.getText().toString(), "SignIn");
            }
        });*/

        email.setText("test@tl3.com");
        passwd.setText("21242124");
    }

 /*   public void showDialog(String msg) {
        CustomizeDialog customizeDialog = new CustomizeDialog(LoginSignUpActivity.this);

        customizeDialog.setTitle("Alert");
        customizeDialog.setMessage(msg);
        customizeDialog.show();
    }*/


}
