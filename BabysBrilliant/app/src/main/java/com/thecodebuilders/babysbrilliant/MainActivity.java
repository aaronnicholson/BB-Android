package com.thecodebuilders.babysbrilliant;

import android.app.ProgressDialog;
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
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PlaylistChooser.PlaylistChooserListener{
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
    public ArrayList<JSONArray> jsonSets = new ArrayList<>();

    public static String currentMenu = MOVIES;

    public static String mediaURL = appContext.getString(R.string.media_url);
    private MediaPlayer mediaPlayer;

    public static ArrayList<JSONObject> favoriteItems = new ArrayList<>();
    //TODO: fetch and populated pre-purchased items from user db
    public static JSONArray purchasedItems = new JSONArray();
    public static ArrayList<Playlist> playlists = new ArrayList<>();
    public JSONObject pendingPlaylistItem;

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

    // Used to communicate state changes in the DownloaderThread
    public static final int MESSAGE_DOWNLOAD_STARTED = 1000;
    public static final int MESSAGE_DOWNLOAD_COMPLETE = 1001;
    public static final int MESSAGE_UPDATE_PROGRESS_BAR = 1002;
    public static final int MESSAGE_DOWNLOAD_CANCELED = 1003;
    public static final int MESSAGE_CONNECTING_STARTED = 1004;
    public static final int MESSAGE_ENCOUNTERED_ERROR = 1005;

    // Downloader instance variables
    private static MainActivity thisActivity;
    private static Thread downloaderThread;
    private static ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloaderThread = null;
        progressDialog = null;

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
                configureThumbnailList(purchasedItems, "purchased");
                currentMenu = PURCHASED_ITEMS;
                toggleMenuButton(currentMenu);
            }
        });

        favoritesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(favoriteItems, "favorites");
                currentMenu = FAVORITE_ITEMS;
                toggleMenuButton(currentMenu);
            }
        });

        playListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //create a list of JSONObjects that the list adapter can understand
                ArrayList<JSONObject> playlistItems = new ArrayList<>();

                for (int i = 0; i < playlists.size(); i++) {
                    JSONObject playlistObject = new JSONObject();
                    try {
                        playlistObject.put("name", playlists.get(i).getName());
                        playlistObject.put("isPlaylist", "true");
                        playlistObject.put("thumb", playlists.get(i).getPlaylistItems().get(0).getString("thumb"));
                        playlistObject.put("products", new JSONArray(playlists.get(i).getPlaylistItems()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    playlistItems.add(playlistObject);
                }

                configureThumbnailList(playlistItems, "playlists");

                currentMenu = PLAYLISTS;
                toggleMenuButton(currentMenu);
            }
        });

        moviesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(movies, "section");
                //Log.d(LOGVAR, "movies item:" + movies.toString());
                currentMenu = MOVIES;
                toggleMenuButton(currentMenu);
            }
        });

        musicButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(music, "section");
                currentMenu = MUSIC;
                toggleMenuButton(currentMenu);
            }
        });

        nightLightsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(nightLights, "section");
                currentMenu = NIGHT_LIGHTS;
                toggleMenuButton(currentMenu);
            }
        });

        audioBooksButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(audioBooks, "section");
                currentMenu = AUDIO_BOOKS;
                toggleMenuButton(currentMenu);
            }
        });

        soundBoardsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(soundBoards, "section");
                currentMenu = SOUND_BOARDS;
                toggleMenuButton(currentMenu);
            }
        });

        hearingImpairedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                configureThumbnailList(hearingImpaired, "section");
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
                videoView.pause();
                videoView.stopPlayback();
                videoView.suspend();
//                if(mediaPlayer!=null) {
//                    if(mediaPlayer.isPlaying())
//                        mediaPlayer.stop();
//                    mediaPlayer.reset();
//                    mediaPlayer.release();
//                    mediaPlayer=null;
//                }
                videoToggleButton.setText(getString(R.string.video_pause));
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
                mediaPlayer = mp;
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

        //then set the tapped one to red and set the title in the UI
        switch (clickedItem) {
            case PURCHASED_ITEMS:
                setSectionTitle(getString(R.string.title_purchased));
                return;
            case PLAYLISTS:
                playListButton.setColorFilter(menuBlue);
                //TODO: further refine what is shown here
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
        //TODO: hoist the density code up so that it can be used elsewhere as well
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

    //this is called when tapping on a menu item or subcategory and sets the list to the new content. JSONArray version. Then runs the ArrayList version.
    public void configureThumbnailList(JSONArray jsonData, String adapterType) {
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

        configureThumbnailList(listData, adapterType);
    }

    //overloaded version for passing ArrayLists directly
    public void configureThumbnailList(ArrayList listData, String adapterType) {
        //set the thumbnail list adapter so it will display the items
        //TODO: I could change what kind of adapter is used depending on the type of list we want,
        // such as a list of movies vs. a list of playlists.
        // Right now it is being handled by conditionals within the ThumbnailListAdapter, but this is getting a bit messy.
        // Query what kind of list it is, based on the data, then send a different type of adapter in,
        // such as PlaylistAdapter.
        // We could have: PlaylistsAdapter, PlaylistAdapter, FavoritesAdapter, SoundboardsAdapter, SectionAdapter, PurchasedAdapter
        // These should be based on an interface or a superclass to keep them consistent.

        //TODO: base this on the "cat" setting on the objects?

        Log.d(LOGVAR, "adapterType: "+adapterType);

        if(adapterType=="section") {
            SectionAdapter adapter = new SectionAdapter(listData, this);
            thumbnailList.setAdapter(adapter);
        } else if(adapterType=="videos") {
            VideosAdapter adapter = new VideosAdapter(listData, this);
            thumbnailList.setAdapter(adapter);
        }

        else {
            ThumbnailListAdapter adapter = new ThumbnailListAdapter(listData, this);
            thumbnailList.setAdapter(adapter);
        }

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

    public void removeFromFavorites(JSONObject rawJSON) {

        try {
            //Log.d(LOGVAR, "FAV START: " + favoriteItems.size());
            for (int favoriteIndex = 0; favoriteIndex < favoriteItems.size(); favoriteIndex++) {
                //if it has no SKU, skip it
                if (!rawJSON.isNull("SKU") || favoriteItems.get(favoriteIndex).isNull("SKU")) {
                    if (rawJSON.getString("SKU").equals(favoriteItems.get(favoriteIndex).getString("SKU"))) {
                        favoriteItems.remove(favoriteIndex);
                    }
                }
            }

            //Log.d(LOGVAR, "FAV END: " + favoriteItems.size());

            //TODO: make favorites menu refresh when removing one
            if (currentMenu.equals(FAVORITE_ITEMS)) {
                configureThumbnailList(new JSONArray(favoriteItems), "favorites");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addToPlaylist(JSONObject productJSON) {
        pendingPlaylistItem = productJSON;
        PlaylistChooser playlistChooser = new PlaylistChooser();
        playlistChooser.show(getSupportFragmentManager(), "playlistChooser");
    }

    //TODO replace strings with string resources
    @Override
    public void onPlaylistSelect(int item) {
        playlists.get(item).addPlaylistItem(pendingPlaylistItem);
        Toast.makeText(MainActivity.this, "Your video was added to the " + playlists.get(item).getName() + " playlist." + " We've brought you to the playlists section.", Toast.LENGTH_SHORT).show();
        playListButton.performClick();

    }

    @Override
    public void onPlaylistAdd(String name) {
        if(name.equals("")) {
            Toast.makeText(MainActivity.this, R.string.playlistAddEmptyStringError, Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<JSONObject> newList = new ArrayList<>();
            newList.add(pendingPlaylistItem);
            playlists.add(0, new Playlist(name, newList));
            Toast.makeText(MainActivity.this, "Your video was added to the new " + name + " playlist."  + " We've brought you to the playlists section.", Toast.LENGTH_SHORT).show();
            playListButton.performClick();
        }

    }

    public void playVideo(String videoURL) {
        String url = mediaURL + videoURL;
        //TODO: temporary for testing
//        downloadItem(url);


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
        queue = VolleySingleton.getInstance().getRequestQueue();

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

        jsonSets.add(movies);
        jsonSets.add(audioBooks);
        jsonSets.add(hearingImpaired);
        jsonSets.add(music);
        jsonSets.add(nightLights);
        jsonSets.add(soundBoards);

        initApp();
    }


    private void getLocalJSON() {
        try {
            processJSON(getString(R.string.raw_json));

        } catch (Throwable t) {
            //Log.e(LOGVAR, "Could not parse LOCAL malformed JSON");
        }
    }

    private void initApp() throws JSONException {
        //TODO: add preloader
        configureThumbnailList(jsonData.getJSONArray(currentMenu), "section");
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
