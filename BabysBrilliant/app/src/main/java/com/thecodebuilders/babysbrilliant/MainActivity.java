package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static String LOGVAR = "MainActivity";
    private static String assetsURL;
    private RecyclerView thumbnailList;
    RequestQueue queue;
    String assetsString;
    JSONArray assetsJSON;

    public static JSONObject jsonData;
    public static JSONArray movies;
    public static JSONArray audioBooks;
    public static JSONArray hearingImpaired;
    public static JSONArray music;
    public static JSONArray nightLights;
    public static JSONArray soundBoards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setMenuWidth();

        //do not show the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        assetsURL = getString(R.string.assets_url);

        getJSON();

        setUpListeners();

        setUpThumbnailList();


    }

    private void setUpListeners() {
        ImageView moviesButton = (ImageView) findViewById(R.id.movies);
        ImageView musicButton = (ImageView) findViewById(R.id.music);
        ImageView nightLightsButton = (ImageView) findViewById(R.id.nightlights);
        ImageView audioBooksButton = (ImageView) findViewById(R.id.audiobooks);
        ImageView soundBoardsButton = (ImageView) findViewById(R.id.soundboards);
        ImageView hearingImpairedButton = (ImageView) findViewById(R.id.hearingimpaired);

        moviesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(movies);
            }
        });

        musicButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(music);
            }
        });

        nightLightsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(nightLights);
            }
        });

        audioBooksButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(audioBooks);
            }
        });

        soundBoardsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(soundBoards);
            }
        });

        hearingImpairedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(hearingImpaired);
            }
        });


    }

    private void setMenuWidth() {
        HorizontalScrollView menuScrollView = (HorizontalScrollView) findViewById(R.id.menu_scroll_view);
        ImageView logoView = (ImageView) findViewById(R.id.bblogo);
        ImageView settingsView = (ImageView) findViewById(R.id.settings);

        //determine screen size in dp
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int density = (int) displayMetrics.density;
        int margin = (int) getResources().getDimension(R.dimen.small_margin) / density;//to give scroll area its own margin
        int margins = margin * 4; //to account for left and right margins of logo, scroll view and settings icon
        int menuHorizontalRoom = (int) (dpWidth - logoView.getMaxWidth() / displayMetrics.density - settingsView.getMaxWidth() / displayMetrics.density - margins);
        int convertedWidth = (int) Utils.convertDpToPixel(menuHorizontalRoom, this);

        //set menu width
        menuScrollView.getLayoutParams().width = convertedWidth;

        //TODO: create indicator when there are items to scroll to
    }

    private void setUpThumbnailList() {
        //setup recycler view
        thumbnailList = (RecyclerView) findViewById(R.id.thumbnail_list);
        thumbnailList.setHasFixedSize(true);//TODO: should this be set?

        //set the layout manager
        final LinearLayoutManager thumbnailListLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        thumbnailListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        thumbnailListLayoutManager.scrollToPosition(0); //the index of the list item to start at
        thumbnailList.setLayoutManager(thumbnailListLayoutManager);


    }

    public void getJSON() {
        queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, assetsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                assetsString = Uri.decode(response);
//                Log.d(LOGVAR, "RESPONSE:" + assetsString);

                try {

                    jsonData = new JSONObject(assetsString);
                    movies = jsonData.getJSONArray("movies");
                    audioBooks = jsonData.getJSONArray("audiobooks");
                    hearingImpaired = jsonData.getJSONArray("hearing impaired");
                    music = jsonData.getJSONArray("music");
                    nightLights = jsonData.getJSONArray("night lights");
                    soundBoards = jsonData.getJSONArray("soundboard");

                    configureThumbnailList(music);


                } catch (Throwable t) {
                    Log.e(LOGVAR, "Could not parse malformed JSON");
                }

                //TODO: handle for no internet connection
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOGVAR, "VOLLEY ERROR: " + error.getMessage());
            }
        });

        queue.add(stringRequest);
    }

    private void configureThumbnailList(JSONArray jsonData) {
        //set the thumbnail list adapter so it will display the items
        ThumbnailListAdapter adapter = new ThumbnailListAdapter(getApplicationContext(), jsonData);
        thumbnailList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
