package com.example.tharinduranaweera.infinitecharge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;


/*
* The activity which provides the user with a summery of the session he/she been through. It indicates all
* the collected info like time, stepcount, generated power. This Activity show every single bit of data which the user must know
* about his/her session so the user doesnt need to navigate to 2 different pages.
*
* So this keep improving simplicity and helps user to get the maximum out of the session.
* */

public class SessionOverActivity extends AppCompatActivity {

    //Declaration

    TextView timeTextView, powerTextView, stepTextView, caloriesTextView;
    FloatingTextButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_over);

        //Initialization

        timeTextView = (TextView) findViewById(R.id.timeTextView);
        powerTextView = (TextView) findViewById(R.id.powerTextView);
        stepTextView = (TextView) findViewById(R.id.stepTextView);

        back = (FloatingTextButton) findViewById(R.id.back);

        //Getting data and saving in variables. (Using intents)

        int time = getIntent().getIntExtra("time", 0);
        double powerGenerated = getIntent().getDoubleExtra("power", 0);
        int stepCount = getIntent().getIntExtra("steps", 0);

        //Setting acquired data

        timeTextView.setText(String.valueOf(time) + "s");
        powerTextView.setText(String.valueOf(powerGenerated) + "Wh");
        stepTextView.setText(String.valueOf(stepCount));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
