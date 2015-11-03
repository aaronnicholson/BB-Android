package com.thecodebuilders.babysbrilliant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ShowIntroActivity extends AppCompatActivity {


    ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;
    FrameLayout skipthetour_framelayout;
    String Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_intro);
        Intent returnIntent = getIntent();
        Key = returnIntent.getStringExtra("Key");
        getSupportActionBar().hide();
        viewPager = (ViewPager) findViewById(R.id.myviewpager);
        skipthetour_framelayout = (FrameLayout) findViewById(R.id.skipthetour_layout);
        myPagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(myPagerAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                if (position == 11) {

                }
            }

            @Override
            public void onPageSelected(int position) {

                if (position == 11) {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (state == 11) {

                }
            }
        });

        skipthetour_framelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
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

    private class MyPagerAdapter extends PagerAdapter {

        int NumberOfPages = 12;

        int[] res = {
                R.drawable.intro1,
                R.drawable.intro2, R.drawable.intro3, R.drawable.intro4, R.drawable.intro5, R.drawable.intro6,
                R.drawable.intro6b, R.drawable.intro6c, R.drawable.intro7, R.drawable.intro8, R.drawable.intro9,
        };


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

            layout.addView(imageView);

            final int page = position;
          /*  layout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,
                            "Page " + page + " clicked",
                            Toast.LENGTH_LONG).show();
                }
            });*/


            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

    }
}
