package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static Context appContext = ApplicationContextProvider.getContext();

    public final static String PURCHASED_ITEMS = "purchasedItems";
    public final static String PLAYLISTS = "playlists";
    public final static String FAVORITE_ITEMS = "favoriteItems";
    public static final String MOVIES = "movies";
    public static final String MUSIC = "music";
    public static final String NIGHT_LIGHTS = "nightLights";
    public static final String AUDIO_BOOKS = "audioBooks";
    public static final String SOUND_BOARDS = "soundBoards";
    public static final String HEARING_IMPAIRED = "hearingImpaired";

    private static String LOGVAR = "MainActivity";
    private static String assetsURL;
    private static RecyclerView thumbnailList;
    RequestQueue queue;
    String assetsString;

    public static Typeface fontAwesome = Typeface.createFromAsset(appContext.getAssets(), appContext.getString(R.string.font_awesome));
    public static Typeface proximaBold = Typeface.createFromAsset(appContext.getAssets(), appContext.getString(R.string.proxima_bold));

    public static JSONObject jsonData;
    public static JSONArray movies;
    public static JSONArray audioBooks;
    public static JSONArray hearingImpaired;
    public static JSONArray music;
    public static JSONArray nightLights;
    public static JSONArray soundBoards;

    public static String currentMenu = "";

    public static String mediaURL = appContext.getString(R.string.media_url);

    public static ArrayList<JSONObject> favoriteItems = new ArrayList<>();
    //TODO: fetch and populated pre-purchased items from user db
    public static JSONArray purchasedItems = new JSONArray();

    private static RelativeLayout videoLayout;
    private static VideoView videoView;
    private static TextView videoToggleButton;
    private static Button videoCloseButton;

    ImageView homeButton;
    ImageView playListButton;
    ImageView favoritesButton;
    ImageView moviesButton;
    ImageView musicButton;
    ImageView nightLightsButton;
    ImageView audioBooksButton;
    ImageView soundBoardsButton;
    ImageView hearingImpairedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assetsURL = getString(R.string.assets_url);

        homeButton = (ImageView) findViewById(R.id.bblogo);
        playListButton = (ImageView) findViewById(R.id.playlists);
        favoritesButton = (ImageView) findViewById(R.id.favorites);
        moviesButton = (ImageView) findViewById(R.id.movies);
        musicButton = (ImageView) findViewById(R.id.music);
        nightLightsButton = (ImageView) findViewById(R.id.nightlights);
        audioBooksButton = (ImageView) findViewById(R.id.audiobooks);
        soundBoardsButton = (ImageView) findViewById(R.id.soundboards);
        hearingImpairedButton = (ImageView) findViewById(R.id.hearingimpaired);

        videoToggleButton = (TextView) findViewById(R.id.video_toggle_button);
        videoCloseButton = (Button) findViewById(R.id.video_close_button);

        videoView = (VideoView) findViewById(R.id.video_view);
        videoLayout = (RelativeLayout) findViewById(R.id.video_layout);
        videoLayout.setVisibility(View.INVISIBLE);

        videoToggleButton.setTypeface(MainActivity.fontAwesome);

        //do not show the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setMenuWidth();

        getJSON();

        setUpListeners();

        setUpThumbnailList();

    }

    private void setUpListeners() {

        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(purchasedItems);
                currentMenu = PURCHASED_ITEMS;
                toggleMenuButton(currentMenu);
            }
        });

        favoritesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(favoriteItems);
                currentMenu = FAVORITE_ITEMS;
                toggleMenuButton(currentMenu);
            }
        });

        playListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                configureThumbnailList(new JSONArray(favoriteItems));
                currentMenu = PLAYLISTS;
                toggleMenuButton(currentMenu);
            }
        });

        moviesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(movies);
                currentMenu = MOVIES;
                toggleMenuButton(currentMenu);
            }
        });

        musicButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(music);
                currentMenu = MUSIC;
                toggleMenuButton(currentMenu);
            }
        });

        nightLightsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(nightLights);
                currentMenu = NIGHT_LIGHTS;
                toggleMenuButton(currentMenu);
            }
        });

        audioBooksButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(audioBooks);
                currentMenu = AUDIO_BOOKS;
                toggleMenuButton(currentMenu);
            }
        });

        soundBoardsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(soundBoards);
                currentMenu = SOUND_BOARDS;
                toggleMenuButton(currentMenu);
            }
        });

        hearingImpairedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(hearingImpaired);
                currentMenu = HEARING_IMPAIRED;
                toggleMenuButton(currentMenu);
            }
        });

        videoToggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    videoToggleButton.setText(getString(R.string.video_play));
                } else {
                    videoView.start();
                    videoToggleButton.setText(getString(R.string.video_pause));
                }
            }
        });

        videoCloseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.stopPlayback();
                    videoToggleButton.setText(getString(R.string.video_pause));
                }
                videoLayout.setVisibility(View.INVISIBLE);
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                //TODO: come up with more accurate solution to hide previous video image before playback starts
                //show the video after a short delay to allow previous video image to clear out
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                videoView.setAlpha(1);
                            }
                        },
                        500);
            }
        });

        videoLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //nothing, just stop thumbnail list from being clicked
            }
        });
    }

    private void toggleMenuButton(String clickedItem) {
        //default all to dark grey
        final int menuDarkGrey = Color.parseColor(getString(R.string.menu_dark_grey));
        final int menuBlue = Color.parseColor(getString(R.string.menu_blue));
        favoritesButton.setColorFilter(menuDarkGrey);
        playListButton.setColorFilter(menuDarkGrey);
        moviesButton.setColorFilter(menuDarkGrey);
        musicButton.setColorFilter(menuDarkGrey);
        nightLightsButton.setColorFilter(menuDarkGrey);
        audioBooksButton.setColorFilter(menuDarkGrey);
        soundBoardsButton.setColorFilter(menuDarkGrey);
        hearingImpairedButton.setColorFilter(menuDarkGrey);

        //then set the tapped one to red
        switch (clickedItem) {
            case PURCHASED_ITEMS:

                return;
            case PLAYLISTS:
                playListButton.setColorFilter(menuBlue);
                return;
            case FAVORITE_ITEMS:
                favoritesButton.setColorFilter(menuBlue);
                return;
            case MOVIES:
                moviesButton.setColorFilter(menuBlue);
                return;
            case MUSIC:
                musicButton.setColorFilter(menuBlue);
                return;
            case NIGHT_LIGHTS:
                nightLightsButton.setColorFilter(menuBlue);
                return;
            case AUDIO_BOOKS:
                audioBooksButton.setColorFilter(menuBlue);
                return;
            case SOUND_BOARDS:
                soundBoardsButton.setColorFilter(menuBlue);
                return;
            case HEARING_IMPAIRED:
                hearingImpairedButton.setColorFilter(menuBlue);
                return;
            default:
        }

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

    //initial configuration of the list
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

    //this is called when tapping on a menu item or subcategory and sets the list to the new content. JSONArray version.
    public static void configureThumbnailList(JSONArray jsonData) {
        //convert JSONArray to ArrayList
        ArrayList<JSONObject> listData = new ArrayList<>();
        try {
            for (int i = 0; i < jsonData.length(); i++) {
                JSONObject itemToAdd = jsonData.getJSONObject(i);
                listData.add(itemToAdd);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //set the thumbnail list adapter so it will display the items
        ThumbnailListAdapter adapter = new ThumbnailListAdapter(listData);
        thumbnailList.setAdapter(adapter);


    }

    //overloaded version for passing ArrayLists directly
    public static void configureThumbnailList(ArrayList listData) {
        //set the thumbnail list adapter so it will display the items
        ThumbnailListAdapter adapter = new ThumbnailListAdapter(listData);
        thumbnailList.setAdapter(adapter);


    }


    public static void addToPurchased(JSONObject productJSON) {
        //TODO: run through actual app store purchase routine
        //TODO: check for duplicate purchase
        //TODO: save to user database
        purchasedItems.put(productJSON);
    }

    public static void addToFavorites(JSONObject productJSON) {
        //TODO: save to user database
        favoriteItems.add(productJSON);
    }

    public static void removeFromFavorites(JSONObject rawJSON) {

        try {
            Log.d(LOGVAR, "FAV START: " + favoriteItems.size());
            for (int favoriteIndex = 0; favoriteIndex < favoriteItems.size(); favoriteIndex++) {
                //if it has no SKU, skip it
                if (!rawJSON.isNull("SKU") || favoriteItems.get(favoriteIndex).isNull("SKU")) {
                    if (rawJSON.getString("SKU").equals(favoriteItems.get(favoriteIndex).getString("SKU"))) {
                        favoriteItems.remove(favoriteIndex);
                    }
                }
            }

            Log.d(LOGVAR, "FAV END: " + favoriteItems.size());

            //TODO: make favorites menu refresh when removing one
            if (currentMenu.equals(FAVORITE_ITEMS)) {
                configureThumbnailList(new JSONArray(favoriteItems));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void playVideo(String videoURL) {
        String url = mediaURL + videoURL; // your URL here


//        videoToggleButton.setText(appContext.getString(R.string.video_pause));
        videoLayout.setVisibility(View.VISIBLE);
        videoView.setVideoPath(url);
        videoView.requestFocus();
        videoView.start();

        videoView.setAlpha(0); //hide prior to media being ready
        //TODO: show preloader
    }

    //retrieve the data model from the server
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
                    music = jsonData.getJSONArray(MUSIC);
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
