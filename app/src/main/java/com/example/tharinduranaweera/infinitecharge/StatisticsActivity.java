package com.example.tharinduranaweera.infinitecharge;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

/*
* StatisticsActivity Class which shows the user the last session statistics and a graphical view (LineChart)
* comparing all the power generations in previous sessions which lets the user to see his/her progress clearly.
* (HCI fact)
*
* The travelled path of the previous session is indicated graphically with Google maps API.
* */

public class StatisticsActivity extends AppCompatActivity {

    //Declarations

    Toolbar toolbar;

    String userId;

    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference myRef;
    double power;
    long stepCount, time;
    static List<Double> lats, lngs;
    FloatingTextButton mapButton;

    List<Double> chargedValues;

    HashMap<String, Double> chargedValuesMap = new HashMap<>();


    TextView timeTextView, powerTextView, stepTextView, distanceTextView;

    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //Initialization

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        timeTextView = (TextView) findViewById(R.id.timeTextView);
        powerTextView = (TextView) findViewById(R.id.powerTextView);
        stepTextView = (TextView) findViewById(R.id.stepTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);

        mapButton = (FloatingTextButton) findViewById(R.id.mapButton);

        graph = (GraphView) findViewById(R.id.graphView);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        myRef = database.getReference().child("Data").child(auth.getCurrentUser().getUid());

        retrieve();


        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    //retrieve() method which retrieves data we stored in firebase earlier.

    private void retrieve(){
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                power = (Double) dataSnapshot.child("wattAmp").getValue();
                stepCount = (Long) dataSnapshot.child("stepCount").getValue();
                time = (Long) dataSnapshot.child("time").getValue();
                lats = (List<Double>) dataSnapshot.child("latitudes").getValue();
                lngs = (List<Double>) dataSnapshot.child("longitides").getValue();

                System.out.println(power + ", " + stepCount);

                timeTextView.setText(String.valueOf(time)+"s");
                powerTextView.setText(String.valueOf(power)+"Wh");
                stepTextView.setText(String.valueOf(stepCount));

                Location first = new Location("");
                first.setLatitude(lats.get(0));
                first.setLongitude(lngs.get(0));

                Location last = new Location("");
                last.setLatitude(lats.get(lats.size() - 1));
                last.setLongitude(lngs.get(lngs.size() - 1));

                //Calculating and setting the distance between first location and last location

                distanceTextView.setText(String.valueOf(first.distanceTo(last)) + "m");

                //Getting data required to plot the graph







            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });

        DatabaseReference ref = database.getReference().child("pastData").child("T6A8nMIVFtRZu90umhjcz2fKcD92");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chargedValuesMap = (HashMap<String, Double>) dataSnapshot.getValue();
                if (chargedValues != null){
                    System.out.println("nullllll: ");
                    chargedValues = new ArrayList<>(chargedValuesMap.values());

                    DataPoint[] data = new DataPoint[chargedValues.size()];

                    for (int i = 0; i < chargedValues.size(); i++){
                        data[i] = new DataPoint(i, chargedValues.get(i));
                    }

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(data);



                    graph.addSeries(series);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
