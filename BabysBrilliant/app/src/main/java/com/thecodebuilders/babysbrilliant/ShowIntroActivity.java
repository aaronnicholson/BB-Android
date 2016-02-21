package com.thecodebuilders.babysbrilliant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.thecodebuilders.application.ApplicationContextProvider;
import com.viewpagerindicator.CirclePageIndicator;

public class ShowIntroActivity extends AppCompatActivity {


    ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;
    View skipthetour;
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
        myPagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(myPagerAdapter);

        //TODO: dynamically size intro_menu_scroll_view to fit screen wid


        CirclePageIndicator pageIndicator = (CirclePageIndicator)findViewById(R.id.page_indicator);
        pageIndicator.setViewPager(viewPager);

        pageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

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
                R.layout.intro_5,
                R.layout.intro_6,
                R.layout.intro_7,
                R.layout.intro_8,
                R.layout.intro_9,
                R.layout.intro_10,
                R.layout.intro_11

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
