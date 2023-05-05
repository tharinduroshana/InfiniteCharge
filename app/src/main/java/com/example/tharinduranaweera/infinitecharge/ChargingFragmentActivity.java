package com.example.tharinduranaweera.infinitecharge;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

/*
* Fragment which specifies the power generation section of Charging activity. This is assigned to the Fragment when
* the bottomNavigationView's items are selected.
*
* We've added realtime changing picture depending on the battery percentage which lets the user to
* see the progress faster than reading texts. This feature makes the application easy to use and more attractive in
* it's own way. (HCI)
* */

public class ChargingFragmentActivity extends Fragment {

    //Declaring variables

    static Activity activity;
    static TextView powerGeneratedText, chargingStatusTextView;
    static int counter = 0;
    static boolean ready = true;

    FloatingTextButton stopSessionButton;
    String userId;

    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference ref;

    //Creating a broadcastReceiver which i used to update the Charging status TextView and change the
    //picture according to the battery percentage.

     static BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {

            if (ready){
                //Getting battery eprcentage and charging state
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                String isCharging = "DISCHARGING";


                if (status == BatteryManager.BATTERY_STATUS_CHARGING){
                    isCharging = "CHARGING";
                }

                //Setting the text
                chargingStatusTextView.setText(isCharging + " (" + String.valueOf(level) + "%) ");

                //Setting the relevant image to the ImageView when the battery percentage reacehes relevant level.

                ImageView imageView = (ImageView) activity.findViewById(R.id.imageView2);
                if (level == 100){
                    imageView.setImageResource(R.drawable.battery5);
                }else if (level >= 75){
                    imageView.setImageResource(R.drawable.battery4);
                }else if (level >= 50){
                    imageView.setImageResource(R.drawable.battery3);
                }else if (level >= 25){
                    imageView.setImageResource(R.drawable.battery2);
                }else {
                    imageView.setImageResource(R.drawable.battery1);
                }
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.charging_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

        ready = true;
        activity = getActivity();

        /*
        * -Bluetooth Data Retrieval Part-
        *
        * The retrieval of bluetooth info in a perfect manner is one of the most important fact here in this project.
        * If we retrieved whole bunch of data from UI thread it will be keep freezing because of the traffic and
        * waiting heavy load of data.
        * Retrieving those data in a different thread was the best option to have more interactive application with light weight.
        * So we created a different Class 'ReadData' which implements Runnable interface. So the program never gets stuck and will
        * work fine under high data receiving frequency. It reduces the waiting time of the user from minutes and hours so the user can interact with
        * the application faster (HCI).
        * */

        if (counter++ == 0){
            Thread thread = new Thread(new ReadData());
            thread.start();
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setText(ReadData.dataPacket.getWattAmp()+"Wh");

        stopSessionButton = (FloatingTextButton) getActivity().findViewById(R.id.stopSessionButton);
        chargingStatusTextView = (TextView)getActivity().findViewById(R.id.textView4);
        getActivity().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        stopSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Reduces and cating malfunctions of the application by having exception handling.
                try {
                    getActivity().unregisterReceiver(mBatInfoReceiver);
                }catch (Exception e){

                }
                long endTime = System.currentTimeMillis();

                //Calculating spent time in the session

                int elapsedTime = (int)(endTime - ReadData.startTime)/1000;
                ReadData.run = false;
                ready = false;
                System.out.println("Time: " + elapsedTime);
                counter = 0;
                ReadData.dataPacket.setTime(elapsedTime);

                //Adding data to the realtime database which provides security and easy reach than
                //Saving in the phone's shared preferences.

                addToDatabase();

                //Passing data into next Activity from intents so the Statistics page can directly retrieve them without connecting to the database
                Intent intent = new Intent(getActivity(), SessionOverActivity.class);
                intent.putExtra("power", ReadData.dataPacket.getWattAmp());
                intent.putExtra("steps", ReadData.stepCount);
                intent.putExtra("time", elapsedTime);
                ReadData.dataPacket = new DataPacket();
                startActivity(intent);
                getActivity().finish();
                try {
                    ChargingFragmentActivity.mBatInfoReceiver.abortBroadcast();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            activity.unregisterReceiver(mBatInfoReceiver);
        }catch (Exception e){
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getActivity().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ChargingFragmentActivity newInstance() {

        ChargingFragmentActivity fragment = new ChargingFragmentActivity();
        return fragment;
    }

    public static void setText(String text){
        powerGeneratedText = (TextView) activity.findViewById(R.id.powerGeneratedText);
        powerGeneratedText.setText(text);
    }

    //addToDatabase() method which adds the data to the Firebase.
    private void addToDatabase(){

        //Saving whole bunch of data inside a java object itself.
        ref.child("Data").child(userId).setValue(ReadData.dataPacket);

        ref.child("pastData").child(userId).push().setValue(ReadData.dataPacket.getWattAmp());

    }

}
