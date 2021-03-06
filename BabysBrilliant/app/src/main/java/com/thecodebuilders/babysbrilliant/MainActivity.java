package com.thecodebuilders.babysbrilliant;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thecodebuilders.adapter.DownloadPurchaseContentAdapter;
import com.thecodebuilders.adapter.ElementViewHolder;
import com.thecodebuilders.adapter.PlaylistAdapter;
import com.thecodebuilders.adapter.PlaylistItemAdapter;
import com.thecodebuilders.adapter.PurchaseHistoryAdapter;
import com.thecodebuilders.adapter.PurchasedAdapter;
import com.thecodebuilders.adapter.SectionAdapter;
import com.thecodebuilders.adapter.SoundBoardsAdapter;
import com.thecodebuilders.adapter.ThumbnailListAdapter;
import com.thecodebuilders.adapter.VideosAdapter;
import com.thecodebuilders.application.ApplicationContextProvider;
import com.thecodebuilders.innapppurchase.IabHelper;
import com.thecodebuilders.innapppurchase.IabResult;
import com.thecodebuilders.innapppurchase.Inventory;
import com.thecodebuilders.innapppurchase.Purchase;
import com.thecodebuilders.model.Playlist;
import com.thecodebuilders.network.VolleySingleton;
import com.thecodebuilders.utility.Constant;
import com.thecodebuilders.utility.CustomizeDialog;
import com.thecodebuilders.utility.PreferenceStorage;
import com.thecodebuilders.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PlaylistChooser.PlaylistChooserListener, Animation.AnimationListener {
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
    public static final Long DOWNLOAD_ID = -1L;
    String SELECT_FLAG;

    private static ArrayList<String> purchasedItemTitle;
    private static String LOGVAR = "MainActivity";

    private static String assetsURL;
    private static RecyclerView thumbnailList;
    RequestQueue queue;
    String assetsString;
    FrameLayout includedSettingLayout_frame;
    View includedSettingLayout;
    View includedemailPasswordLayout, includedemailPasswordLayout2;
    View includedPrivacyLayout;
    View includedSocialMediaLayout;
    View includedOurStoryLayout;
    View includedContactSupportLayout;
    View includedLoopPlaylistsLayout;
    View includedPurchaseHistoryLayout;
    View includedDownloadPuchaseContentLayout;
    View includedCheckNewContentLayout;

    public static Typeface fontAwesome = Typeface.
            createFromAsset(appContext.getAssets(), appContext.getString(R.string.font_awesome));
    public static Typeface proximaBold = Typeface.createFromAsset(appContext.getAssets(), appContext.getString(R.string.proxima_bold));

    public static JSONObject jsonData;
    public static JSONArray movies, movies1;
    public static JSONArray audioBooks, audioBooks1;
    public static JSONArray hearingImpaired, hearingImpaired1;
    public static JSONArray music, music1;
    public static JSONArray nightLights, nightLights1;
    public static JSONArray soundBoards, soundBoards1;
    public ArrayList<JSONArray> jsonSets = new ArrayList<>();

    public static String currentMenu = PURCHASED_ITEMS;

    public static String mediaURL = appContext.getString(R.string.media_url);
    private MediaPlayer mediaPlayer;

    public static ArrayList<JSONObject> favoriteItems = new ArrayList<JSONObject>();
    //TODO: fetch and populated pre-purchased items from user db
    public static JSONArray purchasedItems = new JSONArray();
    public static JSONArray downloadItems = new JSONArray();
    public static ArrayList<Playlist> playlists = new ArrayList<>();
    public int activePlaylist = 0;
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
    ImageView settings;


    static TextView sectionTitle;

    static Handler controlsHandler = new Handler();
    static Runnable delayedHide = null;
    static Boolean doHideControls = false;
    SharedPreferences pref;
    ImageView close_btn;
    RelativeLayout email_password_update, contact_support, privacy_policy, log_out, loop_playlists,
            show_intro, our_story, social_media, purchase_history, check_new_content, download_purchase_content;

    RelativeLayout fiftin_min_timer_lay, thirty_min_timer_lay, fourty_five_min_timer_lay, sixty_min_timer_lay, ninty_min_timer_lay;
    private ProgressDialog pDialog;
    private RequestQueue rq;
    private StringRequest strReq;
    private EditText et;
    public ToggleButton toggle;
    private CustomizeDialog customizeDialog;
    public CheckedTextView checked1, checked2, checked3, checked4, checked5;
    private static final String TAG = "Android BillingService";
    IabHelper mHelper;
    String ITEM_SKU;
    JSONObject productJSON;
    private TextView back_btn_loopPlaylist, back_btn_ourStory, back_btn_socialMedia, back_btn_downloadPurchasecontent,
            back_btn_privacyPolicy, back_btn_purchaseHistory, back_btn_emilPasswdUpdate, back_btn_emilPasswdUpdate2,
            back_btn_checkNewContent;
    private TextView emptyCategory;


    private int flag = 0;
    private Animation animationBlink;
    private ListItem listItem;
    private ElementViewHolder viewHolder;
    private ThumbnailListAdapter.ElementViewHolder thumbnailViewHolder;

    private VideosAdapter videoAdapter;
    private PurchasedAdapter purchasedAdapter;

    public ArrayList<String> fileArrayList;
    public ArrayList<String> mediaFileNameArray;
    public int indexOfCurrentlyPlayingVideo = 0;
    public ArrayList<String> previewFileNameArray;
    public int indexOfCurrentlyPreviewVideo = 0;
    public int indexOfVideo = 1;
    public boolean isVideoClose = false;
    private int videoStopPosition = 0;
    private boolean isVideoPlaying = true;
    private ArrayList listData;
    private String adapterType;
    private boolean isVideoPreviewStart = false;
    private ProgressBar videoBufferProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        assetsURL = Constant.URL + "a=pDBstandard";
        customizeDialog = new CustomizeDialog(MainActivity.this);
        pref = getApplicationContext().getSharedPreferences("BabyBrilliantPref", MODE_PRIVATE);
        //do not show the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        purchasedItemTitle = new ArrayList<String>();
        mHelper = new IabHelper(getApplicationContext(), Constant.base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess())
                    Log.d(LOGVAR, "In-app Billing setup fail" + result);
                else
                    Log.d(LOGVAR, "In-app Billing is set up OK");
            }
        });
        animationBlink = AnimationUtils.loadAnimation(MainActivity.this, R.anim.blink);
        animationBlink.setAnimationListener(this);
        createRawFile();
        setMenuWidth();
        initializeLayout();
        if (Utils.isNetworkAvailable(MainActivity.this)) {
            getRemoteJSON("Main", assetsURL);
            getRemoteJSON("Purchase", Constant.URL + "a=pH&u=" + pref.getString("user_id", ""));
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
        }
        setUpListeners();
        setUpThumbnailList();
        setUpButtons();
        downloadPurchaseContent();

        //get the locally saved Favourite items and save it to ArrayList
        String favourite = PreferenceStorage.getFavourites(MainActivity.this);
        if (!favourite.isEmpty() && favoriteItems.isEmpty()) {
            try {
                JSONArray array = new JSONArray(favourite);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    favoriteItems.add(jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //get the locally saved playlist and save it to ArrayList
        String playlist = PreferenceStorage.getPlaylist(MainActivity.this);
        if (!playlist.isEmpty() && playlists.isEmpty()) {
            try {
                JSONArray playListArray = new JSONArray(playlist);
                for (int j = 0; j < playListArray.length(); j++) {
                    JSONObject jsonObj = playListArray.getJSONObject(j);
                    ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
                    JSONArray productArray = jsonObj.getJSONArray("products");
                    for (int k = 0; k < productArray.length(); k++) {
                        jsonObjects.add(productArray.getJSONObject(k));
                    }
                    playlists.add(j, new Playlist(jsonObj.getString("name"), jsonObjects));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void createRawFile() {

        //  File file = new File(Environment.getExternalStorageDirectory() + File.separator + "raw.txt");
        try {

            FileOutputStream fOut = openFileOutput("raw.txt", MODE_WORLD_READABLE);

            String s = getResources().getString(R.string.raw_json);

            fOut.write(s.getBytes());
            fOut.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public String readFile() {
        String s = "";
        StringBuffer stringBuffer = new StringBuffer();

        // Attaching BufferedReader to the FileInputStream by the help of
        // InputStreamReader
        try {
            FileInputStream fileIn = openFileInput("raw.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[100000];

            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;

            }
            InputRead.close();
            //Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }


    public void downloadPurchaseContent() {

        back_btn_downloadPurchasecontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedDownloadPuchaseContentLayout.setVisibility(View.INVISIBLE);
            }
        });

        download_purchase_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedDownloadPuchaseContentLayout.setVisibility(View.VISIBLE);
                getRemoteJSON("PurchaseContent", Constant.URL + "a=pV&u=" + pref.getString("user_id", ""));

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == Activity.RESULT_OK) {
                String s = data.getStringExtra("Key");

                if (data.getStringExtra("Key").equalsIgnoreCase("setting")) {

                    viewClickable(false);
                    includedSettingLayout_frame.setVisibility(View.VISIBLE);
                    includedSettingLayout.setVisibility(View.VISIBLE);
                   /* final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    // Include dialog.xml file
                    dialog.setContentView(R.layout.settings);
                    dialog.setCancelable(false);
                    dialog.show();*/

                } else if (data.getStringExtra("Key").equalsIgnoreCase("contact_support")) {

                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{Constant.EMAIL_ADDRESS});
                    i.putExtra(android.content.Intent.EXTRA_SUBJECT, Constant.SUBJECT);
                    // i.putExtra(android.content.Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(i, "Send email"));

                } else if (data.getStringExtra("Key").equalsIgnoreCase("email_password_update")) {

                    includedemailPasswordLayout.setVisibility(View.VISIBLE);
                } else if (data.getStringExtra("Key").equalsIgnoreCase("privacy_policy")) {

                    includedPrivacyLayout.setVisibility(View.VISIBLE);


                    WebView story_data = (WebView) includedPrivacyLayout.findViewById(R.id.privacy_policy_webview);
                    story_data.getSettings().setJavaScriptEnabled(true);
                    story_data.loadData(getString(R.string.privacy_policy), "text/html", "UTF-8");

                } else if (data.getStringExtra("Key").equalsIgnoreCase("log_out")) {

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("user_id", "");
                    editor.commit(); // commit changes
                    PreferenceStorage.clearDefaultFavouritePref(MainActivity.this);
                    PreferenceStorage.clearDefaultPlaylistPref(MainActivity.this);

                    Intent mainIntent = new Intent(MainActivity.this, LoginSignUpActivity.class);
                    startActivity(mainIntent);
                    MainActivity.this.finish();
                } else if (data.getStringExtra("Key").equalsIgnoreCase("fb_social")) {

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/BabysBrilliant"));
                        startActivity(intent);
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/BabysBrilliant")));
                    }
                } else if (data.getStringExtra("Key").equalsIgnoreCase("tw_social")) {


                    Intent intent = null;
                    try {
                        // get the Twitter app if possible
                        this.getPackageManager().getPackageInfo("com.twitter.android", 0);
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=babysbrilliant"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } catch (Exception e) {
                        // no Twitter app, revert to browser
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/babysbrilliant"));
                    }
                    this.startActivity(intent);

                } else if (data.getStringExtra("Key").equalsIgnoreCase("PlayListAdapter")) {

                    removePlaylist(data.getIntExtra("pos", 0));
                } else if (data.getStringExtra("Key").equalsIgnoreCase("PlayListItemAdapter")) {

                    removeItemFromPlaylist(data.getIntExtra("pos", 0));
                } else if (data.getStringExtra("Key").equalsIgnoreCase("Purchase")) {

                    try {
                        ITEM_SKU = productJSON.getString("SKU");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHelper.launchPurchaseFlow(MainActivity.this, ITEM_SKU, 10001,
                            mPurchaseFinishedListener, "mypurchasetoken");
                } else {


                }


            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else {

            if (!mHelper.handleActivityResult(requestCode,
                    resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }

        }
    }

    private void setUpListeners() {


        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (purchasedItems != null) {
                    if (purchasedItems.length() == 0)
                        emptyCategory.setVisibility(View.VISIBLE);
                    else
                        emptyCategory.setVisibility(View.GONE);
                    configureThumbnailList(purchasedItems, "purchased");
                    currentMenu = PURCHASED_ITEMS;
                    toggleMenuButton(currentMenu);
                }
            }
        });

        favoritesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (favoriteItems != null) {
                    if (favoriteItems.size() == 0) {
                        emptyCategory.setVisibility(View.VISIBLE);
                        emptyCategory.setText(getResources().getString(R.string.empty_category_favorite_msg));
                    } else
                        emptyCategory.setVisibility(View.GONE);

                    Log.d(LOGVAR, "FavouriteItems:" + favoriteItems);
                    configureThumbnailList(favoriteItems, "videos");
                    currentMenu = FAVORITE_ITEMS;
                    toggleMenuButton(currentMenu);
                }

            }
        });

        soundBoardsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (soundBoards != null) {
                    if (soundBoards.length() == 0)
                        emptyCategory.setVisibility(View.VISIBLE);
                    else
                        emptyCategory.setVisibility(View.GONE);
                    configureThumbnailList(soundBoards, "soundBoards");
                    currentMenu = SOUND_BOARDS;
                    toggleMenuButton(currentMenu);
                }
            }
        });

        playListButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //create a list of JSONObjects that the list adapter can understand
                if (playlists != null) {
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
                    if (playlistItems.size() == 0) {
                        emptyCategory.setVisibility(View.VISIBLE);
                        emptyCategory.setText(getResources().getString(R.string.empty_category_playlist_msg));
                    } else
                        emptyCategory.setVisibility(View.GONE);
                    configureThumbnailList(playlistItems, "playlists");

                    currentMenu = PLAYLISTS;
                    toggleMenuButton(currentMenu);
                }
            }
        });

        moviesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (movies != null) {
                    if (movies.length() == 0)
                        emptyCategory.setVisibility(View.VISIBLE);
                    else
                        emptyCategory.setVisibility(View.GONE);

                    configureThumbnailList(movies, "section");
                    //Log.d(LOGVAR, "movies item:" + movies.toString());
                    currentMenu = MOVIES;
                    toggleMenuButton(currentMenu);
                }
            }
        });

        musicButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (music != null) {
                    if (music.length() == 0)
                        emptyCategory.setVisibility(View.VISIBLE);
                    else
                        emptyCategory.setVisibility(View.GONE);
                    configureThumbnailList(music, "section");
                    currentMenu = MUSIC;
                    toggleMenuButton(currentMenu);
                }
            }
        });

        nightLightsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (nightLights != null) {
                    if (nightLights.length() == 0)
                        emptyCategory.setVisibility(View.VISIBLE);
                    else
                        emptyCategory.setVisibility(View.GONE);
                    configureThumbnailList(nightLights, "section");
                    currentMenu = NIGHT_LIGHTS;
                    toggleMenuButton(currentMenu);
                }
            }
        });

        audioBooksButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (audioBooks != null) {
                    if (audioBooks.length() == 0)
                        emptyCategory.setVisibility(View.VISIBLE);
                    else
                        emptyCategory.setVisibility(View.GONE);
                    configureThumbnailList(audioBooks, "section");
                    currentMenu = AUDIO_BOOKS;
                    toggleMenuButton(currentMenu);
                }
            }
        });


        hearingImpairedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (hearingImpaired != null) {
                    if (hearingImpaired.length() == 0)
                        emptyCategory.setVisibility(View.VISIBLE);
                    else
                        emptyCategory.setVisibility(View.GONE);
                    configureThumbnailList(hearingImpaired, "section");
                    currentMenu = HEARING_IMPAIRED;
                    toggleMenuButton(currentMenu);
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent setting = new Intent(MainActivity.this, ParentalChallengeScreen.class);
                setting.putExtra("Key", "setting");
                startActivityForResult(setting, 1);
            }
        });

        videoToggleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isVideoClose = false;
                if (videoView.isPlaying()) {
                    videoView.pause();
                    isVideoPlaying = false;
                    videoToggleButton.setText(getString(R.string.video_play));
                } else {
                    videoView.start();
                    isVideoPlaying = true;
                    videoToggleButton.setText(getString(R.string.video_pause));
                }
                showControls();
            }
        });

        videoCloseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(LOGVAR, "VIDEO CLOSE PRESS");
                videoBufferProgressBar.setVisibility(View.INVISIBLE);
                isVideoClose = true;
                videoView.pause();
                videoView.stopPlayback();
                videoView.suspend();
                videoToggleButton.setText(getString(R.string.video_pause));
                videoLayout.setVisibility(View.INVISIBLE);
            }
        });

        videoFFButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: Make forward func
                //showControls();
                try {
                    if (isVideoPreviewStart) {
                        if (previewFileNameArray != null) {
                            String previewFileName = previewFileNameArray.get(indexOfCurrentlyPreviewVideo + 1);
                            if (!previewFileName.equalsIgnoreCase("")) {
                                indexOfCurrentlyPreviewVideo = indexOfCurrentlyPreviewVideo + 1;
                                playingVideos(previewFileName);
                            } else {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.preview_not_available),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        if (mediaFileNameArray != null) {
                            String fileLocation = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/"
                                    + mediaFileNameArray.get((indexOfCurrentlyPlayingVideo + 1));
                            if (Utils.checkFileExist(MainActivity.this, fileLocation,
                                    mediaFileNameArray.get((indexOfCurrentlyPlayingVideo + 1)))) {
                                indexOfCurrentlyPlayingVideo = indexOfCurrentlyPlayingVideo + 1;
                                Uri video = Uri.parse(fileLocation);
                                videoView.setVideoURI(video);
                                videoView.start();
                                videoToggleButton.setText(getString(R.string.video_pause));
                            } else {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.next_video_not_downloaded), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        videoRewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: Make backward func
                //showControls();
                try {
                    if (isVideoPreviewStart) {
                        if (previewFileNameArray != null) {
                            if ((indexOfCurrentlyPreviewVideo - 1) != -1) {
                                String previewFileName = previewFileNameArray.get(indexOfCurrentlyPreviewVideo - 1);
                                if (!previewFileName.equalsIgnoreCase("")) {
                                    indexOfCurrentlyPreviewVideo = indexOfCurrentlyPreviewVideo - 1;
                                    playingVideos(previewFileName);
                                } else {
                                    Toast.makeText(MainActivity.this, getResources().getString(R.string.preview_not_available_next_video),
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                videoView.seekTo(0);
                            }
                        }
                    } else {
                        if (mediaFileNameArray != null) {
                            if ((indexOfCurrentlyPlayingVideo - 1) != -1) {
                                String fileLocation = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/"
                                        + mediaFileNameArray.get((indexOfCurrentlyPlayingVideo - 1));
                                if (Utils.checkFileExist(MainActivity.this, fileLocation,
                                        mediaFileNameArray.get((indexOfCurrentlyPlayingVideo - 1)))) {
                                    indexOfCurrentlyPlayingVideo = indexOfCurrentlyPlayingVideo - 1;
                                    Uri video = Uri.parse(fileLocation);
                                    videoView.setVideoURI(video);
                                    videoView.start();
                                    videoToggleButton.setText(getString(R.string.video_pause));
                                } else {
                                    Toast.makeText(MainActivity.this, getResources().getString(R.string.previous_video_not_downloaded), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                String fileLocationCurrentVideo = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/"
                                        + mediaFileNameArray.get((indexOfCurrentlyPlayingVideo));
                                Uri video = Uri.parse(fileLocationCurrentVideo);
                                videoView.setVideoURI(video);
                                videoView.start();
                                videoToggleButton.setText(getString(R.string.video_pause));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                //videoFFButton.setVisibility(View.VISIBLE);
                //videoRewButton.setVisibility(View.VISIBLE);
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        configureThumbnailList(listData, adapterType);
    }

    //overloaded version for passing ArrayLists directly
    public void configureThumbnailList(ArrayList listData, String adapterType) {

        // set the thumbnail list adapter so it will display the items
        // TODO: I could change what kind of adapter is used depending on the type of list we want,
        // such as a list of movies vs. a list of playlists.
        // Right now it is being handled by conditionals within the ThumbnailListAdapter, but this is getting a bit messy.
        // Query what kind of list it is, based on the data, then send a different type of adapter in,
        // such as PlaylistAdapter.
        // We could have: PlaylistsAdapter, PlaylistAdapter, FavoritesAdapter, SoundboardsAdapter, SectionAdapter, PurchasedAdapter
        // These should be based on an interface or a superclass to keep them consistent.

        //TODO: base this on the "cat" setting on the objects?
        this.listData = listData;
        this.adapterType = adapterType;
        Log.d(LOGVAR, "adapterType: " + adapterType);

        if (adapterType == "section") {
            SectionAdapter sectionAdapter = new SectionAdapter(listData, this);
            thumbnailList.setAdapter(sectionAdapter);
        } else if (adapterType == "videos" || adapterType == "favorites") {
            videoAdapter = new VideosAdapter(listData, this, "");
            thumbnailList.setAdapter(videoAdapter);

        } else if (adapterType == "playlists") {
            PlaylistAdapter playlistAdapter = new PlaylistAdapter(listData, this);
            thumbnailList.setAdapter(playlistAdapter);
        } else if (adapterType == "playlistItems") {
            PlaylistItemAdapter adapter = new PlaylistItemAdapter(listData, this);
            thumbnailList.setAdapter(adapter);
        } else if (adapterType == "purchased") {
            VideosAdapter purchasedVideosAdapter = new VideosAdapter(listData, this, "purchased");
            thumbnailList.setAdapter(purchasedVideosAdapter);
        }
       /* else if (adapterType == "favorites") {
            FavoritesAdapter adapter = new FavoritesAdapter(listData, this);
            thumbnailList.setAdapter(adapter);
        } */
        else if (adapterType == "soundBoards") {
            SoundBoardsAdapter soundBoardsAdapter = new SoundBoardsAdapter(listData, this);
            thumbnailList.setAdapter(soundBoardsAdapter);
        } else {
            ThumbnailListAdapter thumbnailListAdapter = new ThumbnailListAdapter(listData, this);
            thumbnailList.setAdapter(thumbnailListAdapter);
        }

    }

    public void addToPurchased(JSONObject productJSON, ListItem listItem, ElementViewHolder viewHolder) throws JSONException {
        //TODO: run through actual app store purchase routine
        //TODO: check for duplicate purchase
        //TODO: save to user database
        this.productJSON = productJSON;
        this.listItem = listItem;
        this.viewHolder = viewHolder;
        Log.d(LOGVAR, "PuchaseProduct:" + productJSON);
        Intent purchase = new Intent(MainActivity.this, ParentalChallengeScreen.class);
        purchase.putExtra("Key", "Purchase");
        startActivityForResult(purchase, 1);


    }

    public void addToPurchasedSoundBoards(JSONObject productJSON, ListItem listItem, ThumbnailListAdapter.ElementViewHolder viewHolder) throws JSONException {
        //TODO: run through actual app store purchase routine
        //TODO: check for duplicate purchase
        //TODO: save to user database
        this.productJSON = productJSON;
        this.listItem = listItem;
        this.thumbnailViewHolder = viewHolder;
        Log.d(LOGVAR, "PuchaseProduct:" + productJSON);
        Intent purchase = new Intent(MainActivity.this, ParentalChallengeScreen.class);
        purchase.putExtra("Key", "Purchase");
        startActivityForResult(purchase, 1);


    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                Log.d(LOGVAR, "Purcahse Failed");
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {
                Log.d(LOGVAR, "Purchase:" + purchase.getSku() + ":::" + productJSON);
                SELECT_FLAG = "purchase_video";
                purchasedItems.put(productJSON);
                try {
                    AsyncEditPassword(productJSON.getString("SKU"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                purchaseDone();
                //consumeItem();
            }

        }
    };

    private void purchaseDone() {
        listItem.setIsPurchased(true);
        if (viewHolder != null) {
            viewHolder.priceText.setVisibility(View.INVISIBLE);
            viewHolder.favoritesIcon.setVisibility(View.VISIBLE);
            viewHolder.playlistIcon.setVisibility(View.VISIBLE);
            String videoURL = listItem.getMediaFile();
    /*    String fileLocation = Environment.getExternalStorageDirectory()
                + "/" + getResources().getString(R.string.app_name) +
                "/" + videoURL;*/
            String fileLocation = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + videoURL;
            if (Utils.checkFileExist(this, fileLocation, videoURL))
                viewHolder.downloadIcon.setVisibility(View.GONE);
            else
                viewHolder.downloadIcon.setVisibility(View.VISIBLE);
        } else {
            thumbnailViewHolder.priceText.setVisibility(View.GONE);
        }
        Log.d(LOGVAR, "ProductJson:" + productJSON);

    }

    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
            } else {
                // mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                //mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        // clickButton.setEnabled(true);
                    } else {
                        // handle error
                    }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    public static void addToFavorites(JSONObject productJSON) {
        //TODO: save to user database
        Log.d(LOGVAR, "..." + productJSON);
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
                        PreferenceStorage.removeFavourites(appContext, rawJSON.getString("SKU"));
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
        if (name.equals("")) {
            Toast.makeText(MainActivity.this, R.string.playlistAddEmptyStringError, Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<JSONObject> newList = new ArrayList<>();
            newList.add(pendingPlaylistItem);
            playlists.add(0, new Playlist(name, newList));
            Toast.makeText(MainActivity.this, "Your video was added to the new " + name + " playlist." + " We've brought you to the playlists section.", Toast.LENGTH_SHORT).show();
            playListButton.performClick();
        }
    }

    public void removePlaylist(int index) {
        //TODO: insert permission interstitial
        playlists.remove(index);
        playListButton.performClick();
    }

    public void removeItemFromPlaylist(int index) {
        playlists.get(activePlaylist).removePlaylistItemAtIndex(index);

        //if this makes the playlist empty, remove the playlist
        if (playlists.get(activePlaylist).playlistItems.size() < 1) {
            playlists.remove(activePlaylist);
            playListButton.performClick();
        } else {
            playListButton.performClick();
            //TODO: refresh this playlist view instead of going to playlist top level
        }
    }

    public void playingVideos(String videoURL) {
        Log.d(TAG, "playingVideos");
        isVideoPreviewStart = true;
        String url = mediaURL + videoURL;
        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(MainActivity.this, "", "Buffering video...", true);
        progressDialog.setCancelable(false);
        videoBufferProgressBar.setVisibility(View.VISIBLE);
        try {

            // Get the URL from String VideoURL
            Uri video = Uri.parse(url);
            videoView.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            videoBufferProgressBar.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                videoBufferProgressBar.setVisibility(View.INVISIBLE);
                return false;
            }
        });
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                videoLayout.setVisibility(View.VISIBLE);
                videoView.start();
                videoView.setAlpha(1);
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        videoBufferProgressBar.setVisibility(View.GONE);
                    }
                });
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            videoBufferProgressBar.setVisibility(View.VISIBLE);
                        }
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            videoBufferProgressBar.setVisibility(View.INVISIBLE);
                        }
                        return false;
                    }
                });
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.pause();
                videoView.stopPlayback();
                videoView.suspend();
                //videoToggleButton.setText(getString(R.string.video_pause));
                videoLayout.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isVideoClose) {
            videoView.setOnPreparedListener(null);
        } else {
            if (videoStopPosition != 0) {
                videoView.seekTo(videoStopPosition);
                if (isVideoPreviewStart)
                    videoBufferProgressBar.setVisibility(View.VISIBLE);
                if (isVideoPlaying) {
                    videoView.start();
                    videoToggleButton.setText(getString(R.string.video_pause));
                } else {
                    videoView.setOnPreparedListener(null);
                    if(isVideoPreviewStart){
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            // Close the progress bar and play the video
                            public void onPrepared(MediaPlayer mp) {
                                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                                    @Override
                                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                                            videoBufferProgressBar.setVisibility(View.VISIBLE);
                                        }
                                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                                            videoBufferProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                        return false;
                                    }
                                });
                            }
                        });
                    }
                    videoView.pause();
                    videoToggleButton.setText(getString(R.string.video_play));
                }

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoStopPosition = videoView.getCurrentPosition();
        videoView.pause();

    }

    @SuppressLint("NewApi")
    public void playVideo(String videoURL, final boolean isPlayList) {
        isVideoPreviewStart = false;
        // Create a progressbar
        pDialog = new ProgressDialog(MainActivity.this);
        // Set progressbar title
        pDialog.setTitle("Video Streaming");
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        videoToggleButton.setText(appContext.getString(R.string.video_pause));


        try {
            // Get the URL from String VideoURL
            Uri video = Uri.parse(videoURL);
            videoView.setVideoURI(video);
        } catch (Exception e) {
            e.printStackTrace();
        }

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //TODO: come up with more accurate solution to hide previous video image before playback starts
                //show the video after a short delay to allow previous video image to clear out
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            @SuppressLint("NewApi")
                            public void run() {
                                pDialog.dismiss();
                                videoLayout.setVisibility(View.VISIBLE);
                                videoView.start();
                                videoView.setAlpha(1);
                            }
                        },
                        500);

                mediaPlayer = mp;
                showControls();
            }
        });

        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {

                videoLayout.setVisibility(View.VISIBLE);
                pDialog.dismiss();

                videoView.start();
                videoView.setAlpha(1);

                mediaPlayer = mp;
            }

        });


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isPlayList && toggle.isChecked()) {
                    if (fileArrayList.size() == indexOfVideo) {
                        indexOfVideo = 0;
                    }
                }
                if (isPlayList && fileArrayList.size() > indexOfVideo) {
                    String fileLocation = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileArrayList.get(indexOfVideo);

                    Uri video = Uri.parse(fileLocation);
                    videoView.setVideoURI(video);
                    videoView.start();
                    indexOfVideo++;
                } else {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                    videoToggleButton.setText(getString(R.string.video_pause));
                    videoLayout.setVisibility(View.INVISIBLE);
                }


            }
        });

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
    public void getRemoteJSON(final String From, String url) {
        queue = VolleySingleton.getInstance().getRequestQueue();
        if (From.equalsIgnoreCase("Purchase") || From.equalsIgnoreCase("checkForNewContent")
                || From.equalsIgnoreCase("PurchaseContent")) {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(LOGVAR, From + ":Response:" + Uri.decode(response));
                try {
                    if (From.equalsIgnoreCase("Main")) {
                        processJSON(response);
                    } else if (From.equalsIgnoreCase("checkForNewContent")) {

                        assetsString = Uri.decode(response);
                        jsonData = new JSONObject(assetsString);

                        movies1 = jsonData.getJSONArray(MOVIES);
                        audioBooks1 = jsonData.getJSONArray(AUDIO_BOOKS);
                        hearingImpaired1 = jsonData.getJSONArray(HEARING_IMPAIRED);
                        music1 = jsonData.getJSONArray(MUSIC);
                        nightLights1 = jsonData.getJSONArray(NIGHT_LIGHTS);
                        soundBoards1 = jsonData.getJSONArray(SOUND_BOARDS);
                        int i1 = jsonData.length();
                        ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();

                        for (int i = 1; i <= jsonData.length(); i++) {
                            if (movies1.length() == movies.length() && i == 1) {


                            } else {

                                if (audioBooks1.length() == audioBooks.length() && i == 2) {


                                } else {

                                    if (hearingImpaired1.length() == hearingImpaired.length() && i == 3) {


                                    } else {

                                        if (music1.length() == music.length() && i == 4) {


                                        } else {

                                            if (nightLights1.length() == nightLights.length() && i == 5) {


                                            } else {

                                                if (soundBoards1.length() == soundBoards.length() && i == 6) {

                                                    pDialog.hide();


                                                } else {

                                                    pDialog.hide();


                                                    JSONArray arr = new JSONArray(Uri.decode(response));

                                                    for (int i11 = 0; i11 < arr.length(); i11++) {
                                                        HashMap<String, String> hash = new HashMap<String, String>();
                                                        JSONObject json = arr.getJSONObject(i);
                                                        hash.put("title", json.getString("title"));
                                                        hash.put("date", json.getString("date"));
                                                        arraylist.add(hash);
                                                    }

                                                    ListView lv = (ListView) includedPurchaseHistoryLayout.findViewById(R.id.listView);
                                                    PurchaseHistoryAdapter pHA = new PurchaseHistoryAdapter(MainActivity.this, arraylist);
                                                    lv.setAdapter(pHA);


                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        jsonSets.add(movies);
                        jsonSets.add(audioBooks);
                        jsonSets.add(hearingImpaired);
                        jsonSets.add(music);
                        jsonSets.add(nightLights);
                        jsonSets.add(soundBoards);

                    } else if (From.equalsIgnoreCase("Purchase")) {

                        JSONArray downloadItems1 = new JSONArray(Uri.decode(response));
                        JSONArray purchasedItems1 = new JSONArray(Uri.decode(response));
                        downloadItems = downloadItems1;
                        purchasedItems = purchasedItems1;

                        pDialog.hide();
                        initApp();
                        purchaseJSON(response);
                    } else if (From.equalsIgnoreCase("PurchaseContent")) {
                        pDialog.dismiss();
                        JSONObject purchasedJson = null;
                        Log.d(LOGVAR, "PurchaseContent" + Uri.decode(response));
                        JSONArray purchasedItems = new JSONArray(Uri.decode(response));
                        if (/*downloadItems.length() == 0 &&*/ purchasedItems.length() == 0) {

                            Toast.makeText(getApplicationContext(), "No Purchased Item", Toast.LENGTH_SHORT).show();
                        } else {

                            final ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();


                            for (int i = 0; i < purchasedItems.length(); i++) {
                                HashMap<String, String> hash = new HashMap<String, String>();


                                try {

                                    purchasedJson = purchasedItems.getJSONObject(i);
                                    if (purchasedJson.getString("title").equalsIgnoreCase(purchasedJson.getString("title"))) {
                                        hash.put("title", purchasedJson.getString("title"));
                                        hash.put("file", purchasedJson.getString("file"));
                                        arrayList.add(hash);
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            ListView lv = (ListView) includedDownloadPuchaseContentLayout.findViewById(R.id.listView);
                            TextView downloadAll = (TextView) includedDownloadPuchaseContentLayout.findViewById(R.id.download_all);
                            DownloadPurchaseContentAdapter downloadPurchaseContentAdapter = new DownloadPurchaseContentAdapter(MainActivity.this, arrayList, downloadAll);
                            lv.setAdapter(downloadPurchaseContentAdapter);


                        }
                    }


                    //TODO: Save remote JSON to local JSON
                    //  Constant.raw_json= response;


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

    public void purchaseJSON(String response) throws JSONException {

        // JSONObject jsonData = new JSONObject(Uri.decode(response));
        ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();

        JSONArray arr = new JSONArray(Uri.decode(response));
        int lengt = arr.length();
        for (int i = 0; i < arr.length(); i++) {
            HashMap<String, String> hash = new HashMap<String, String>();
            JSONObject json = arr.getJSONObject(i);
            hash.put("title", json.getString("title"));
            hash.put("date", json.getString("date"));
            arraylist.add(hash);
            purchasedItemTitle.add(i, json.getString("title"));
        }

        ListView lv = (ListView) includedPurchaseHistoryLayout.findViewById(R.id.listView);
        PurchaseHistoryAdapter pHA = new PurchaseHistoryAdapter(MainActivity.this, arraylist);
        lv.setAdapter(pHA);


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


    }

    private void getLocalJSON() {
        try {
            //TODO: pull this out of strings and into a flat file.

            Log.d(LOGVAR, "Call ProcessJSON");
            // processJSON(getString(R.string.our_story));
            processJSON(readFile());

        } catch (Throwable t) {
            //Log.e(LOGVAR, "Could not parse LOCAL malformed JSON");
        }
    }

    private void initApp() throws JSONException {
        //TODO: add preloader
        if (purchasedItems != null) {
            if (purchasedItems.length() == 0)
                emptyCategory.setVisibility(View.VISIBLE);
            else
                emptyCategory.setVisibility(View.GONE);
            configureThumbnailList(purchasedItems, "purchased");
            toggleMenuButton(PURCHASED_ITEMS);
        }

        //show_intro.performClick(); //TODO: for testing
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


    public void initializeLayout() {

        homeButton = (ImageView) findViewById(R.id.bblogo);
        playListButton = (ImageView) findViewById(R.id.playlists);
        favoritesButton = (ImageView) findViewById(R.id.favorites);
        moviesButton = (ImageView) findViewById(R.id.movies);
        musicButton = (ImageView) findViewById(R.id.music);
        nightLightsButton = (ImageView) findViewById(R.id.nightlights);
        audioBooksButton = (ImageView) findViewById(R.id.audiobooks);
        soundBoardsButton = (ImageView) findViewById(R.id.soundboards);
        hearingImpairedButton = (ImageView) findViewById(R.id.hearingimpaired);
        settings = (ImageView) findViewById(R.id.settings);

        emptyCategory = (TextView) findViewById(R.id.item_empty_placeholder);

        videoToggleButton = (TextView) findViewById(R.id.video_toggle_button);
        videoCloseButton = (TextView) findViewById(R.id.video_close_button);
        videoFFButton = (TextView) findViewById(R.id.video_ff_button);
        videoRewButton = (TextView) findViewById(R.id.video_rew_button);

        videoView = (VideoView) findViewById(R.id.video_view);
        videoLayout = (RelativeLayout) findViewById(R.id.video_layout);
        videoLayout.setVisibility(View.INVISIBLE);

        sectionTitle = (TextView) findViewById(R.id.section_title);


        includedSettingLayout_frame = (FrameLayout) findViewById(R.id.settings_lay_frame);
        includedemailPasswordLayout = findViewById(R.id.email_pass_update_lay);
        includedemailPasswordLayout2 = findViewById(R.id.email_pass_update_lay2);
        includedPrivacyLayout = findViewById(R.id.privacy_policy_lay);
        includedContactSupportLayout = findViewById(R.id.contact_support);

        includedOurStoryLayout = findViewById(R.id.our_story_lay);
        includedSettingLayout = findViewById(R.id.settings_lay);
        includedSocialMediaLayout = findViewById(R.id.social_media_lay);
        includedLoopPlaylistsLayout = findViewById(R.id.loop_playlist_lay);
        includedPurchaseHistoryLayout = findViewById(R.id.purchase_history_lay);
        includedDownloadPuchaseContentLayout = findViewById(R.id.download_purchase_content_lay);
        includedCheckNewContentLayout = findViewById(R.id.check_new_content_lay);
        close_btn = (ImageView) includedSettingLayout.findViewById(R.id.close_btn);
        email_password_update = (RelativeLayout) includedSettingLayout.findViewById(R.id.email_pass_update);
        contact_support = (RelativeLayout) includedSettingLayout.findViewById(R.id.contact_support);
        privacy_policy = (RelativeLayout) includedSettingLayout.findViewById(R.id.privacy_policy);
        log_out = (RelativeLayout) includedSettingLayout.findViewById(R.id.logOut);
        et = (EditText) includedemailPasswordLayout2.findViewById(R.id.edit_text);

        loop_playlists = (RelativeLayout) includedSettingLayout.findViewById(R.id.loop_playlists);
        show_intro = (RelativeLayout) includedSettingLayout.findViewById(R.id.show_intro);
        our_story = (RelativeLayout) includedSettingLayout.findViewById(R.id.our_story);
        social_media = (RelativeLayout) includedSettingLayout.findViewById(R.id.social_media);
        purchase_history = (RelativeLayout) includedSettingLayout.findViewById(R.id.purchase_history);
        check_new_content = (RelativeLayout) includedSettingLayout.findViewById(R.id.new_content);
        download_purchase_content = (RelativeLayout) includedSettingLayout.findViewById(R.id.download_purchase_content);

        toggle = (ToggleButton) includedLoopPlaylistsLayout.findViewById(R.id.toggleButton1);


        fiftin_min_timer_lay = (RelativeLayout) includedLoopPlaylistsLayout.findViewById(R.id.fiftin_min_timer_lay);
        thirty_min_timer_lay = (RelativeLayout) includedLoopPlaylistsLayout.findViewById(R.id.thirty_min_timer_lay);
        fourty_five_min_timer_lay = (RelativeLayout) includedLoopPlaylistsLayout.findViewById(R.id.fourty_five_min_timer_lay);
        sixty_min_timer_lay = (RelativeLayout) includedLoopPlaylistsLayout.findViewById(R.id.sixty_min_timer_lay);
        ninty_min_timer_lay = (RelativeLayout) includedLoopPlaylistsLayout.findViewById(R.id.ninty_min_timer_lay);
        back_btn_loopPlaylist = (TextView) includedLoopPlaylistsLayout.findViewById(R.id.back_btn);
        back_btn_ourStory = (TextView) includedOurStoryLayout.findViewById(R.id.back_btn);
        back_btn_socialMedia = (TextView) includedSocialMediaLayout.findViewById(R.id.back_btn);
        back_btn_privacyPolicy = (TextView) includedPrivacyLayout.findViewById(R.id.back_btn);
        back_btn_purchaseHistory = (TextView) includedPurchaseHistoryLayout.findViewById(R.id.back_btn);
        back_btn_emilPasswdUpdate = (TextView) includedemailPasswordLayout.findViewById(R.id.back_btn);
        back_btn_emilPasswdUpdate2 = (TextView) includedemailPasswordLayout2.findViewById(R.id.back_btn);
        back_btn_downloadPurchasecontent = (TextView) includedDownloadPuchaseContentLayout.findViewById(R.id.back_btn);
        back_btn_checkNewContent = (TextView) includedCheckNewContentLayout.findViewById(R.id.back_btn);

        fiftin_min_timer_lay = (RelativeLayout) includedLoopPlaylistsLayout.findViewById(R.id.fiftin_min_timer_lay);
        checked1 = (CheckedTextView) findViewById(R.id.checked1);
        checked2 = (CheckedTextView) findViewById(R.id.checked2);
        checked3 = (CheckedTextView) findViewById(R.id.checked3);
        checked4 = (CheckedTextView) findViewById(R.id.checked4);
        checked5 = (CheckedTextView) findViewById(R.id.checked5);

        videoToggleButton.setTypeface(MainActivity.fontAwesome);
        videoCloseButton.setTypeface(MainActivity.fontAwesome);
        videoFFButton.setTypeface(MainActivity.fontAwesome);
        videoRewButton.setTypeface(MainActivity.fontAwesome);
        sectionTitle.setTypeface(MainActivity.proximaBold);
        emptyCategory.setTypeface(MainActivity.proximaBold);
        videoBufferProgressBar = (ProgressBar) findViewById(R.id.video_progress_bar);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewClickable(true);
                includedSettingLayout_frame.setVisibility(View.INVISIBLE);
                includedSettingLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void setUpButtons() {
        //show intro
        show_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, ShowIntroActivity.class).putExtra("Key", "MainActivity"));


            }
        });

        //check for new content
        back_btn_checkNewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedCheckNewContentLayout.setVisibility(View.INVISIBLE);
            }
        });

        check_new_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedCheckNewContentLayout.setVisibility(View.VISIBLE);
                getRemoteJSON("checkForNewContent", assetsURL);

            }
        });

        //purchase history
        back_btn_purchaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedPurchaseHistoryLayout.setVisibility(View.INVISIBLE);
            }
        });

        purchase_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedPurchaseHistoryLayout.setVisibility(View.VISIBLE);
                getRemoteJSON("Purchase", Constant.URL + "a=pH&u=" + pref.getString("user_id", ""));

            }
        });


        //loop playlist
        back_btn_loopPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedLoopPlaylistsLayout.setVisibility(View.INVISIBLE);
            }
        });

        loop_playlists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedLoopPlaylistsLayout.setVisibility(View.VISIBLE);
            }
        });


        fiftin_min_timer_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checked1.isChecked()) {
                    checked1.setChecked(true);
                    checked2.setChecked(false);
                    checked3.setChecked(false);
                    checked4.setChecked(false);
                    checked5.setChecked(false);

                }


            }
        });


        thirty_min_timer_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checked2.isChecked()) {
                    checked2.setChecked(true);
                    checked1.setChecked(false);
                    checked3.setChecked(false);
                    checked4.setChecked(false);
                    checked5.setChecked(false);

                }
            }
        });


        fourty_five_min_timer_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checked3.isChecked()) {
                    checked3.setChecked(true);
                    checked2.setChecked(false);
                    checked1.setChecked(false);
                    checked4.setChecked(false);
                    checked5.setChecked(false);

                }

            }
        });


        sixty_min_timer_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checked4.isChecked()) {
                    checked4.setChecked(true);
                    checked2.setChecked(false);
                    checked3.setChecked(false);
                    checked1.setChecked(false);
                    checked5.setChecked(false);

                }

            }
        });


        ninty_min_timer_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checked5.isChecked()) {
                    checked5.setChecked(true);
                    checked2.setChecked(false);
                    checked3.setChecked(false);
                    checked4.setChecked(false);
                    checked1.setChecked(false);

                }

            }
        });

        //log out
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent privacy_policy = new Intent(MainActivity.this, ParentalChallengeScreen.class);
                privacy_policy.putExtra("Key", "log_out");
                startActivityForResult(privacy_policy, 1);

            }
        });

        back_btn_socialMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedSocialMediaLayout.setVisibility(View.INVISIBLE);
            }
        });

        //social media
        social_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedSocialMediaLayout.setVisibility(View.VISIBLE);

            }
        });

        includedSocialMediaLayout.findViewById(R.id.fb_social).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent privacy_policy = new Intent(MainActivity.this, ParentalChallengeScreen.class);
                privacy_policy.putExtra("Key", "fb_social");
                startActivityForResult(privacy_policy, 1);

            }
        });

        includedSocialMediaLayout.findViewById(R.id.tw_social).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent privacy_policy = new Intent(MainActivity.this, ParentalChallengeScreen.class);
                privacy_policy.putExtra("Key", "tw_social");
                startActivityForResult(privacy_policy, 1);
            }
        });

        //email password update
        back_btn_emilPasswdUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedemailPasswordLayout.setVisibility(View.INVISIBLE);
            }
        });

        back_btn_emilPasswdUpdate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedemailPasswordLayout2.setVisibility(View.INVISIBLE);
            }
        });


        email_password_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent contact_support = new Intent(MainActivity.this, ParentalChallengeScreen.class);
                contact_support.putExtra("Key", "email_password_update");
                startActivityForResult(contact_support, 1);

            }
        });

        includedemailPasswordLayout.findViewById(R.id.confirm_existing_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SELECT_FLAG = "confirm_existing_password";
                includedemailPasswordLayout2.setVisibility(View.VISIBLE);
                et.setHint("Please type in your password...");
            }
        });

        includedemailPasswordLayout.findViewById(R.id.new_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECT_FLAG = "new_password";

                includedemailPasswordLayout2.setVisibility(View.VISIBLE);
                et.setHint("Please type in your new password...");

            }
        });

        includedemailPasswordLayout.findViewById(R.id.new_emailaddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SELECT_FLAG = "new_emailaddress";
                includedemailPasswordLayout2.setVisibility(View.VISIBLE);
                et.setHint("Please type in your new email...");
            }
        });

        includedemailPasswordLayout2.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (et.getText().length() == 0 || et.getText().toString().equalsIgnoreCase("")) {

                } else {
                    AsyncEditPassword(et.getText().toString());
                }
            }
        });

        //contact support
        contact_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent contact_support = new Intent(MainActivity.this, ParentalChallengeScreen.class);
                contact_support.putExtra("Key", "contact_support");
                startActivityForResult(contact_support, 1);

            }
        });

        //privacy policy
        back_btn_privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedPrivacyLayout.setVisibility(View.INVISIBLE);
            }
        });


        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag == 0) {
                    Intent privacy_policy = new Intent(MainActivity.this, ParentalChallengeScreen.class);
                    privacy_policy.putExtra("Key", "privacy_policy");
                    startActivityForResult(privacy_policy, 1);
                    flag = 1;
                } else {
                    includedPrivacyLayout.setVisibility(View.VISIBLE);
                    WebView story_data = (WebView) includedPrivacyLayout.findViewById(R.id.privacy_policy_webview);
                    story_data.getSettings().setJavaScriptEnabled(true);
                    story_data.loadData(getString(R.string.privacy_policy), "text/html", "UTF-8");
                }


            }
        });

        //our story
        back_btn_ourStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedOurStoryLayout.setVisibility(View.INVISIBLE);
            }
        });

        our_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                includedOurStoryLayout.setVisibility(View.VISIBLE);

                WebView story_data = (WebView) includedOurStoryLayout.findViewById(R.id.our_story_webview);
                story_data.getSettings().setJavaScriptEnabled(true);
                story_data.loadData(getString(R.string.our_story), "text/html", "UTF-8");

            }
        });
    }

    public void AsyncEditPassword(final String value) {

        //TODO: put this on the standard "queue" RequestQueue
        rq = Volley.newRequestQueue(MainActivity.this);
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        if (pDialog != null)
            if (!pDialog.isShowing())
                pDialog.show();

        final int a = 1;

        strReq = new StringRequest(Request.Method.POST, "http://new.babysbrilliant.com/app/",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        // TODO Auto-generated method stub
                        pDialog.hide();
                        try {
                            JSONObject result = new JSONObject(arg0);
                            Log.d(LOGVAR, "result:" + result);
                            SharedPreferences.Editor editor = pref.edit();
                            if (result.getString("res").equalsIgnoreCase("successful")) {
                                if (SELECT_FLAG.equalsIgnoreCase("new_emailaddress")) {

                                    editor.putString("user_name", value);
                                    editor.commit();
                                    showDialog("", "Changed Successfully");

                                } else if (SELECT_FLAG.equalsIgnoreCase("new_password")) {
                                    editor.putString("user_password", value);
                                    editor.commit();
                                    showDialog("", "Changed Successfully");

                                } else if (SELECT_FLAG.equalsIgnoreCase("purchase_video")) {
                                    Log.d(TAG, "Purchase:" + arg0);
//                                    Toast.makeText(MainActivity.this, arg0, Toast.LENGTH_SHORT).show();
                                } else {

                                    showDialog("", "Changed Successfully");

                                }
                            } else {
                                if (SELECT_FLAG.equalsIgnoreCase("new_emailaddress")) {


                                } else if (SELECT_FLAG.equalsIgnoreCase("new_password")) {


                                } else if (SELECT_FLAG.equalsIgnoreCase("purchase_video")) {


                                } else {

                                    showDialog("", "Incorrect existing password");
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                System.out.println("Error [" + arg0 + "]");
            }

        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // TODO Auto-generated method stub
                Map<String, String> params = new HashMap<String, String>();
                if (SELECT_FLAG.equalsIgnoreCase("new_emailaddress")) {
                    params.put("a", "nE");
                    params.put("n", pref.getString("user_id", ""));
                    params.put("u", pref.getString("user_name", ""));
                    params.put("p", pref.getString("user_password", ""));
                    params.put("v", value);
                } else if (SELECT_FLAG.equalsIgnoreCase("new_password")) {
                    params.put("a", "nP");
                    params.put("n", pref.getString("user_id", ""));
                    params.put("u", pref.getString("user_name", ""));
                    params.put("p", pref.getString("user_password", ""));
                    params.put("v", value);

                } else if (SELECT_FLAG.equalsIgnoreCase("purchase_video")) {
                    params.put("a", "aV");
                    params.put("u", pref.getString("user_name", ""));
                    params.put("p", pref.getString("user_password", ""));
                    params.put("v", value);

                } else {
                    params.put("a", "lgn");
                    params.put("u", pref.getString("user_name", ""));
                    params.put("p", value);


                }
                Log.d(LOGVAR, "Params:" + value + " :" + params.toString());

                return params;
            }
        };

        rq.add(strReq);
    }

    public void showDialog(String Title, String msg) {


        customizeDialog.setTitle(Title);
        customizeDialog.setMessage(msg);
        customizeDialog.show();

        customizeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                includedemailPasswordLayout2.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void closeVideo() {

        Log.d(LOGVAR, "VIDEO CLOSE PRESS");
        videoView.pause();
        videoView.stopPlayback();
        videoView.suspend();
        videoToggleButton.setText(getString(R.string.video_pause));
        videoLayout.setVisibility(View.INVISIBLE);
    }

    private void viewClickable(boolean isClickable) {
     /*   private SectionAdapter sectionAdapter;
        private VideosAdapter purchasedVideosAdapter;
        private PlaylistAdapter playlistAdapter;
        private SoundBoardsAdapter soundBoardsAdapter;
        private ThumbnailListAdapter thumbnailListAdapter;*/
        homeButton.setClickable(isClickable);
        playListButton.setClickable(isClickable);
        favoritesButton.setClickable(isClickable);
        moviesButton.setClickable(isClickable);
        musicButton.setClickable(isClickable);
        nightLightsButton.setClickable(isClickable);
        audioBooksButton.setClickable(isClickable);
        soundBoardsButton.setClickable(isClickable);
        hearingImpairedButton.setClickable(isClickable);
        settings.setClickable(isClickable);

        if (adapterType == "section") {
            SectionAdapter sectionAdapter = new SectionAdapter(listData, this, isClickable);
            thumbnailList.setAdapter(sectionAdapter);
        } else if (adapterType == "videos" || adapterType == "favorites") {
            videoAdapter = new VideosAdapter(listData, this, "", isClickable);
            thumbnailList.setAdapter(videoAdapter);

        } else if (adapterType == "playlists") {
            PlaylistAdapter playlistAdapter = new PlaylistAdapter(listData, this, isClickable);
            thumbnailList.setAdapter(playlistAdapter);
        } else if (adapterType == "playlistItems") {
            PlaylistItemAdapter adapter = new PlaylistItemAdapter(listData, this, isClickable);
            thumbnailList.setAdapter(adapter);
        } else if (adapterType == "purchased") {
            VideosAdapter purchasedVideosAdapter = new VideosAdapter(listData, this, "purchased", isClickable);
            thumbnailList.setAdapter(purchasedVideosAdapter);
        } else if (adapterType == "soundBoards") {
            SoundBoardsAdapter soundBoardsAdapter = new SoundBoardsAdapter(listData, this, isClickable);
            thumbnailList.setAdapter(soundBoardsAdapter);
        } else {
            ThumbnailListAdapter thumbnailListAdapter = new ThumbnailListAdapter(listData, this, isClickable);
            thumbnailList.setAdapter(thumbnailListAdapter);
        }
    }

}
