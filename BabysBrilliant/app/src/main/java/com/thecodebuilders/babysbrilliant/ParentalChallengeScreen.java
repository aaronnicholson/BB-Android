package com.thecodebuilders.babysbrilliant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ParentalChallengeScreen extends AppCompatActivity {

    TextView digit_first;
    TextView digit_second;
    TextView digit_third;
    TextView digit_fourth;

    TextView one_digit;
    TextView two_digit;
    TextView three_digit;
    TextView four_digit;
    TextView five_digit;
    TextView six_digit;
    TextView seven_digit;
    TextView eight_digit;
    TextView nine_digit;
    TextView zero_digit;
    TextView erase_digit;

    TextView num_digit_first;
    TextView num_digit_second;
    TextView num_digit_third;
    TextView num_digit_fourth;

    TextView cancel_btn;

    String randomNumber;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parental_challenge_screen);

        getSupportActionBar().hide();

        one_digit = (TextView) findViewById(R.id.one_digit);
        two_digit = (TextView) findViewById(R.id.two_digit);
        three_digit = (TextView) findViewById(R.id.three_digit);
        four_digit = (TextView) findViewById(R.id.four_digit);
        five_digit = (TextView) findViewById(R.id.five_digit);
        six_digit = (TextView) findViewById(R.id.six_digit);
        seven_digit = (TextView) findViewById(R.id.seven_digit);
        eight_digit = (TextView) findViewById(R.id.eight_digit);
        nine_digit = (TextView) findViewById(R.id.nine_digit);
        zero_digit = (TextView) findViewById(R.id.zero_digit);
        erase_digit = (TextView) findViewById(R.id.erase_digit);

        digit_first = (TextView) findViewById(R.id.digit_first);
        digit_second = (TextView) findViewById(R.id.digit_second);
        digit_third = (TextView) findViewById(R.id.digit_third);
        digit_fourth = (TextView) findViewById(R.id.digit_fourth);

        num_digit_first = (TextView) findViewById(R.id.num_digit_first);
        num_digit_second = (TextView) findViewById(R.id.num_digit_second);
        num_digit_third = (TextView) findViewById(R.id.num_digit_third);
        num_digit_fourth = (TextView) findViewById(R.id.num_digit_fourth);

        cancel_btn = (TextView) findViewById(R.id.cancel_btn);

        generateRandomDigit();
        // setListener();

//        successCompletion(); //TODO: for testing only

    }

    public void generateRandomDigit() {

        int randomPIN = (int) (Math.random() * 9000) + 1000;

        randomNumber = String.valueOf(randomPIN);

        digitTotext(randomNumber);
    }

    public void digitTotext(String randomNumber) {


        String[] text = {"ZERO", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE"};

        for (int i = 0; i < randomNumber.length(); i++) {


            char s = randomNumber.charAt(i);


            int a = Character.getNumericValue(s);


            String TEXT = text[a];

            setToTEXT(i, TEXT);
        }

    }

    public void setToTEXT(int i, String TEXT) {

        if (i == 0)
            digit_first.setText(TEXT + " ,");
        else if (i == 1)
            digit_second.setText(TEXT + " ,");
        else if (i == 2)
            digit_third.setText(TEXT + " ,");
        else
            digit_fourth.setText(TEXT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent returnIntent = getIntent();
        returnIntent.putExtra("Key", "Cancel");

        setResult(Activity.RESULT_OK, returnIntent);
        finish();


    }

    public void Click(View v) {

        TextView t = (TextView) v;

        if (v.getId() == R.id.cancel_btn) {

            Intent returnIntent = getIntent();
            returnIntent.putExtra("Key", "Cancel");

            setResult(Activity.RESULT_OK, returnIntent);
            finish();

            onBackPressed();

        } else if (v.getId() == R.id.erase_digit) {


            if (count == 1) {
                num_digit_first.setText("");
                count--;
            } else if (count == 2) {

                num_digit_second.setText("");
                count--;
            } else if (count == 3) {

                num_digit_third.setText("");
                count--;
            } else if (count == 4) {

                num_digit_fourth.setText("");
                count--;
            } else {

            }
        } else {


            if (count == 0) {

                num_digit_first.setText(t.getText());
                count++;
            } else if (count == 1) {

                num_digit_second.setText(t.getText());
                count++;
            } else if (count == 2) {

                num_digit_third.setText(t.getText());
                count++;
            } else if (count == 3) {

                num_digit_fourth.setText(t.getText());
                count++;


                if (randomNumber.equalsIgnoreCase(num_digit_first.getText() + "" + num_digit_second.getText() + num_digit_third.getText() + num_digit_fourth.getText())) {


                    successCompletion();
                }

            } else {

            }

        }

    }

    private void successCompletion() {
        Intent returnIntent = getIntent();
        returnIntent.putExtra("Key", returnIntent.getStringExtra("Key"));

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


}
