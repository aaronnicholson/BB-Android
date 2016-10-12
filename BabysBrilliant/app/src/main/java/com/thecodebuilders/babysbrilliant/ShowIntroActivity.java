package com.thecodebuilders.babysbrilliant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.thecodebuilders.application.ApplicationContextProvider;
import com.viewpagerindicator.CirclePageIndicator;

public class ShowIntroActivity extends AppCompatActivity {


    ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;
    View skipthetour;
    ImageView intro_playlists,intro_favorites,intro_movies,intro_music,intro_nightlights,intro_audiobooks,intro_soundboards,intro_hearingimpaired, intro_settings;

    String Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_intro);
        Intent returnIntent = getIntent();
        Key = returnIntent.getStringExtra("Key");
        getSupportActionBar().hide();
        viewPager = (ViewPager) findViewById(R.id.myviewpager);
        skipthetour = (View) findViewById(R.id.skipthetour);
        intro_playlists = (ImageView) findViewById(R.id.intro_playlists);
        intro_favorites = (ImageView) findViewById(R.id.intro_favorites);
        intro_movies = (ImageView) findViewById(R.id.intro_movies);
        intro_music = (ImageView) findViewById(R.id.intro_music);
        intro_nightlights = (ImageView) findViewById(R.id.intro_nightlights);
        intro_audiobooks = (ImageView) findViewById(R.id.intro_audiobooks);
        intro_soundboards = (ImageView) findViewById(R.id.intro_soundboards);
        intro_hearingimpaired = (ImageView) findViewById(R.id.intro_hearingimpaired);
        intro_settings = (ImageView) findViewById(R.id.intro_settings);


        myPagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(myPagerAdapter);

        //TODO: dynamically size intro_menu_scroll_view to fit screen width?


        CirclePageIndicator pageIndicator = (CirclePageIndicator)findViewById(R.id.page_indicator);
        pageIndicator.setViewPager(viewPager);

        pageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //default menu items to hidden
                intro_playlists.setVisibility(View.INVISIBLE);
                intro_favorites.setVisibility(View.INVISIBLE);
                intro_movies.setVisibility(View.INVISIBLE);
                intro_music.setVisibility(View.INVISIBLE);
                intro_nightlights.setVisibility(View.INVISIBLE);
                intro_audiobooks.setVisibility(View.INVISIBLE);
                intro_soundboards.setVisibility(View.INVISIBLE);
                intro_hearingimpaired.setVisibility(View.INVISIBLE);
                intro_settings.setVisibility(View.INVISIBLE);

                intro_playlists.clearColorFilter();
                intro_favorites.clearColorFilter();
                intro_movies.clearColorFilter();
                intro_music.clearColorFilter();
                intro_nightlights.clearColorFilter();
                intro_audiobooks.clearColorFilter();
                intro_soundboards.clearColorFilter();
                intro_hearingimpaired.clearColorFilter();
                intro_settings.clearColorFilter();


                if(position==2 ) {
                    intro_playlists.setVisibility(View.VISIBLE);
                    intro_playlists.setColorFilter(Color.RED);
                }

                if(position==3) {
                    intro_playlists.setVisibility(View.VISIBLE);
                    intro_favorites.setVisibility(View.VISIBLE);
                    intro_favorites.setColorFilter(Color.RED);

                }

                if(position==4) {
                    intro_playlists.setVisibility(View.VISIBLE);
                    intro_favorites.setVisibility(View.VISIBLE);
                    intro_music.setVisibility(View.VISIBLE);
                    intro_movies.setVisibility(View.VISIBLE);
                    intro_nightlights.setVisibility(View.VISIBLE);
                    intro_audiobooks.setVisibility(View.VISIBLE);

                    intro_music.setColorFilter(Color.RED);
                    intro_movies.setColorFilter(Color.RED);
                    intro_nightlights.setColorFilter(Color.RED);
                    intro_audiobooks.setColorFilter(Color.RED);

                }
                if(position==5 || position==6) {
                    intro_playlists.setVisibility(View.VISIBLE);
                    intro_favorites.setVisibility(View.VISIBLE);
                    intro_music.setVisibility(View.VISIBLE);
                    intro_movies.setVisibility(View.VISIBLE);
                    intro_nightlights.setVisibility(View.VISIBLE);
                    intro_audiobooks.setVisibility(View.VISIBLE);


                }
                if(position==7) {
                        intro_playlists.setVisibility(View.VISIBLE);
                        intro_favorites.setVisibility(View.VISIBLE);
                        intro_music.setVisibility(View.VISIBLE);
                        intro_movies.setVisibility(View.VISIBLE);
                        intro_nightlights.setVisibility(View.VISIBLE);
                        intro_audiobooks.setVisibility(View.VISIBLE);
                        intro_soundboards.setVisibility(View.VISIBLE);

                        intro_soundboards.setColorFilter(Color.RED);


                }
                if(position==8) {
                        intro_playlists.setVisibility(View.VISIBLE);
                        intro_favorites.setVisibility(View.VISIBLE);
                        intro_music.setVisibility(View.VISIBLE);
                        intro_movies.setVisibility(View.VISIBLE);
                        intro_nightlights.setVisibility(View.VISIBLE);
                        intro_audiobooks.setVisibility(View.VISIBLE);
                        intro_soundboards.setVisibility(View.VISIBLE);
                        intro_hearingimpaired.setVisibility(View.VISIBLE);

                        intro_hearingimpaired.setColorFilter(Color.RED);
                }
                if(position==9) {
                        intro_playlists.setVisibility(View.VISIBLE);
                        intro_favorites.setVisibility(View.VISIBLE);
                        intro_music.setVisibility(View.VISIBLE);
                        intro_movies.setVisibility(View.VISIBLE);
                        intro_nightlights.setVisibility(View.VISIBLE);
                        intro_audiobooks.setVisibility(View.VISIBLE);
                        intro_soundboards.setVisibility(View.VISIBLE);
                        intro_hearingimpaired.setVisibility(View.VISIBLE);
                        intro_settings.setVisibility(View.VISIBLE);

                        intro_settings.setColorFilter(Color.RED);
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        });

        skipthetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }




    private class MyPagerAdapter extends PagerAdapter {

        int[] res = {
                R.layout.intro_0,
                R.layout.intro_1,
                R.layout.intro_2,
                R.layout.intro_3,
                R.layout.intro_4,
                R.layout.intro_6,
                R.layout.intro_7,
                R.layout.intro_8,
                R.layout.intro_9,
                R.layout.intro_10

        };

        int NumberOfPages = res.length;

        @Override
        public int getCount() {
            return NumberOfPages;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(ShowIntroActivity.this);
            imageView.setImageResource(res[position]);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(imageParams);

            LinearLayout layout = new LinearLayout(ShowIntroActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            // layout.setBackgroundColor(backgroundcolor[position]);
            layout.setLayoutParams(layoutParams);

            View currentPage = LayoutInflater.from(ApplicationContextProvider.getContext()).inflate(res[position], null);

            layout.addView(currentPage); //TODO: Change this to add the composed views

            final int page = position;

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

    }

    @Override
    public void onBackPressed() {
        if (Key.equalsIgnoreCase("SignUp")) {
            startActivity(new Intent(ShowIntroActivity.this, MainActivity.class));
            ShowIntroActivity.this.finish();
        } else {
            super.onBackPressed();
        }
    }
}
