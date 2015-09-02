package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    public final static String FAVORITE_ITEMS = "favorite items";
    public static final String MOVIES = "movies";
    public static final String MUSIC = "music";
    public static final String NIGHT_LIGHTS = "night lights";
    public static final String AUDIO_BOOKS = "audiobooks";
    public static final String SOUND_BOARDS = "soundboard";
    public static final String HEARING_IMPAIRED = "hearing impaired";

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

    public static String currentMenu = MOVIES;

    public static String mediaURL = appContext.getString(R.string.media_url);

    public static ArrayList<JSONObject> favoriteItems = new ArrayList<>();
    //TODO: fetch and populated pre-purchased items from user db
    public static JSONArray purchasedItems = new JSONArray();
    public static ArrayList<Playlist> playlists = new ArrayList<>();

    private static RelativeLayout videoLayout;
    private static VideoView videoView;
    private static TextView videoToggleButton;
    private static TextView videoCloseButton;
    private static TextView videoFFButton;
    private static TextView videoRewButton;

    ImageView homeButton;
    ImageView playListButton;
    ImageView favoritesButton;
    ImageView moviesButton;
    ImageView musicButton;
    ImageView nightLightsButton;
    ImageView audioBooksButton;
    ImageView soundBoardsButton;
    ImageView hearingImpairedButton;

    static TextView sectionTitle;

    static Handler controlsHandler = new Handler();
    static Runnable delayedHide = null;
    static Boolean doHideControls = false;

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
        videoCloseButton = (TextView) findViewById(R.id.video_close_button);
        videoFFButton = (TextView) findViewById(R.id.video_ff_button);
        videoRewButton = (TextView) findViewById(R.id.video_rew_button);

        videoView = (VideoView) findViewById(R.id.video_view);
        videoLayout = (RelativeLayout) findViewById(R.id.video_layout);
        videoLayout.setVisibility(View.INVISIBLE);

        sectionTitle = (TextView) findViewById(R.id.section_title);

        videoToggleButton.setTypeface(MainActivity.fontAwesome);
        videoCloseButton.setTypeface(MainActivity.fontAwesome);
        videoFFButton.setTypeface(MainActivity.fontAwesome);
        videoRewButton.setTypeface(MainActivity.fontAwesome);
        sectionTitle.setTypeface(MainActivity.proximaBold);


        //do not show the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setMenuWidth();

        getRemoteJSON();

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
                showControls();
            }
        });

        videoCloseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    videoView.stopPlayback();
                    videoView.suspend();
                    videoToggleButton.setText(getString(R.string.video_pause));
                }
                videoLayout.setVisibility(View.INVISIBLE);
            }
        });

        videoFFButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: Make forward func
                showControls();
            }
        });

        videoRewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: Make backward func
                showControls();
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
                showControls();
            }
        });

        videoLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //nothing, just stop thumbnail list from being clicked
                showControls();
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
                setSectionTitle(getString(R.string.title_purchased));
                return;
            case PLAYLISTS:
                playListButton.setColorFilter(menuBlue);
                videoFFButton.setVisibility(View.VISIBLE);
                videoRewButton.setVisibility(View.VISIBLE);
                setSectionTitle(getString(R.string.title_playlists));
                return;
            case FAVORITE_ITEMS:
                favoritesButton.setColorFilter(menuBlue);
                setSectionTitle(getString(R.string.title_favorites));
                return;
            case MOVIES:
                moviesButton.setColorFilter(menuBlue);
                setSectionTitle(getString(R.string.title_movies));
                return;
            case MUSIC:
                musicButton.setColorFilter(menuBlue);
                setSectionTitle(getString(R.string.title_music));
                return;
            case NIGHT_LIGHTS:
                nightLightsButton.setColorFilter(menuBlue);
                setSectionTitle(getString(R.string.title_night_lights));
                return;
            case AUDIO_BOOKS:
                audioBooksButton.setColorFilter(menuBlue);
                setSectionTitle(getString(R.string.title_audio_books));
                return;
            case SOUND_BOARDS:
                soundBoardsButton.setColorFilter(menuBlue);
                setSectionTitle(getString(R.string.title_sound_boards));
                return;
            case HEARING_IMPAIRED:
                hearingImpairedButton.setColorFilter(menuBlue);
                setSectionTitle(getString(R.string.title_hearing_impaired));
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

    public static void addToPlaylist(String playlistName, JSONObject productJSON) {
        Playlist existingPlaylist = null;

        //search for existing name. If it exists, add to that list. If not, create a new list.
        for (int i = 0; i < playlists.size(); i++) {
            Playlist playlist = playlists.get(i);
            String existingName = playlist.getName();

            if(playlistName.equals(existingName)) {
                existingPlaylist = playlist;

            }
        }

        if(existingPlaylist != null) {
            //don't make a new list. add to existing list
            existingPlaylist.addPlaylistItem(productJSON);
            Log.d(LOGVAR, "EXISTING LIST: " + existingPlaylist.getPlaylistItems());
        } else {
            //prepare a list to be added
            final ArrayList<JSONObject> newList = new ArrayList<JSONObject>();
            newList.add(productJSON);
            //make a new list
            Playlist addedPlayList = new Playlist(playlistName, newList);
            //add the playlist
            playlists.add(addedPlayList);
            Log.d(LOGVAR, "PLAYLIST ADDED: " + addedPlayList.getPlaylistItems());

        }
    }

    public static void playVideo(String videoURL) {
        String url = mediaURL + videoURL;

        videoToggleButton.setText(appContext.getString(R.string.video_pause));
        videoLayout.setVisibility(View.VISIBLE);
        videoView.setVideoPath(url);
        videoView.requestFocus();
        videoView.start();

        videoView.setAlpha(0); //hide prior to media being ready
        //TODO: show preloader

        showControls();
    }

    private static void showControls() {
        videoFFButton.setVisibility(View.VISIBLE);
        videoRewButton.setVisibility(View.VISIBLE);
        videoToggleButton.setVisibility(View.VISIBLE);
        videoCloseButton.setVisibility(View.VISIBLE);

        doHideControls = true;

        //hide again after x sec, after clearing previous hide actions
        delayedHide = new Runnable() {
            public void run() {
                if (doHideControls) {
                    hideControls();
                }
            }
        };

        controlsHandler.removeCallbacks(delayedHide);

        controlsHandler.postDelayed(delayedHide, 10000);
    }

    private static void hideControls() {
        videoFFButton.setVisibility(View.INVISIBLE);
        videoRewButton.setVisibility(View.INVISIBLE);
        videoToggleButton.setVisibility(View.INVISIBLE);
        videoCloseButton.setVisibility(View.INVISIBLE);

        doHideControls = false;
    }

    public static void setSectionTitle(String title) {
        sectionTitle.setText(title);
    }

    //retrieve the data model from the server
    public void getRemoteJSON() {
        queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, assetsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    processJSON(response);

                    //TODO: Save remote JSON to local JSON

                } catch (Throwable t) {
                    Log.e(LOGVAR, "Reverting to LOCAL JSON");
                    getLocalJSON();
                }

                //TODO: handle for no internet connection
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOGVAR, "Reverting to LOCAL JSON. VOLLEY ERROR: " + error.getMessage());
                getLocalJSON();
            }
        });

        queue.add(stringRequest);
    }

    private void processJSON(String response) throws JSONException {
        assetsString = Uri.decode(response);

        jsonData = new JSONObject(assetsString);
        movies = jsonData.getJSONArray(MOVIES);
        audioBooks = jsonData.getJSONArray(AUDIO_BOOKS);
        hearingImpaired = jsonData.getJSONArray(HEARING_IMPAIRED);
        music = jsonData.getJSONArray(MUSIC);
        nightLights = jsonData.getJSONArray(NIGHT_LIGHTS);
        soundBoards = jsonData.getJSONArray(SOUND_BOARDS);

        initApp();
    }

    private void getLocalJSON() {
        try {
            processJSON(getString(R.string.raw_json));

        } catch (Throwable t) {
            Log.e(LOGVAR, "Could not parse LOCAL malformed JSON");
        }
    }


    private void initApp() throws JSONException {
        //TODO: add preloader
        configureThumbnailList(jsonData.getJSONArray(currentMenu));
        toggleMenuButton(MOVIES);
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
