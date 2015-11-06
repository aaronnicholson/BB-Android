package com.thecodebuilders.babysbrilliant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.thecodebuilders.innapppurchase.IabHelper;

/**
 * Created by Android Developer on 10/23/2015.
 */
public class SplashScreen extends AppCompatActivity {

    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    SharedPreferences pref;
    IabHelper mHelper;

    private static final String TAG =
            "com.babybrilliant.inappbilling";
    /*static final String ITEM_SKU = "android.test.purchased";*/
    static final String ITEM_SKU = "com.example.buttonclick";

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = getApplicationContext().getSharedPreferences("BabyBrilliantPref", MODE_PRIVATE);

      /*  Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String base64EncodedPublicKey =
                        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0DxaRSgNLoks1JlnvWKraaRaZLso5uSY8oGOOK4u99U8WKJSELcdAyqURjgv3gLmj+WqNGnnxSxx7ezqimt45kpgDGXS3NAlTh6HT6mhmpjxfv0rkeDNaPw7m0bn7gRG32VHQr0WbV01ylF3PzqhAgPJLQ/TA5jiRVngPzOcW1VQe0Iu1Gfp8XscPBmVEVRPVmadeV5RBKbAX/KcfNtHuKWter4ttpSJdj2EZaeXuQQ/NHPLIhcMhPnCd5pjrOuYyjwb8ok5XpqRg3oGZ7jHqiKBR7Qjyfva8uXnqkXjLYTO936NmDuaTSvrBzSAWrQHNr/kgx4NJM+v1X+4sA+KqwIDAQAB";

                mHelper = new IabHelper(getApplicationContext(), base64EncodedPublicKey);
                mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                    @Override
                    public void onIabSetupFinished(IabResult result) {
                        if (!result.isSuccess())
                            Log.d("", "In-app Billing setup fail" + result);
                        else
                            Log.d("", "In-app Billing is set up OK");
                    }
                });
            }




        });

        Button buy = (Button) findViewById(R.id.buy);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.launchPurchaseFlow(SplashScreen.this, ITEM_SKU, 10001,
                        mPurchaseFinishedListener, "mypurchasetoken");
            }
        });*/




        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent that will start the Menu-Activity.

                if (pref.getString("user_id", "").length() == 0 && pref.getString("user_id", "").equalsIgnoreCase("")) {

                    Intent mainIntent = new Intent(SplashScreen.this, LoginSignUpActivity.class);
                    startActivity(mainIntent);
                    SplashScreen.this.finish();
                } else {
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(mainIntent);
                    SplashScreen.this.finish();

                }

              /*  Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(mainIntent);
                SplashScreen.this.finish();*/

            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
               // buyButton.setEnabled(false);
            }

        }
    };

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
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
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
    }*/

}
