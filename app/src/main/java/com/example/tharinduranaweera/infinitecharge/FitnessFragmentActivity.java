package com.example.tharinduranaweera.infinitecharge;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
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

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

/*
* FitnessFragmentActivity Fragment which indicates the fitness information of the charging section.
* This activity monitors whether the user is walking or not and calculates the step count.
* */

public class FitnessFragmentActivity extends Fragment {

    //Declaration
    static TextView stepCountText;
    static Activity context;
    static boolean ready = false;
    FloatingTextButton stopSession;
    String userId;

    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference ref;

    TextView walkingStatus;

    //BroadcastReceiver which connected with this fragment. It constantly checks for a battery state change. and indicated the change
    BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {

            if (ready){
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                String isWalking = "TRY A BIT FASTER";


                if (status == BatteryManager.BATTERY_STATUS_CHARGING){
                    isWalking = "WALKING";
                }

                walkingStatus.setText(isWalking);

            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fitness_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

        ChargingFragmentActivity.ready = false;
        context = getActivity();
        ready = true;



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        stopSession = (FloatingTextButton) getActivity().findViewById(R.id.stopSession);

        walkingStatus = (TextView) getActivity().findViewById(R.id.textView9);

        stopSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long endTime = System.currentTimeMillis();
                int elapsedTime = (int)(endTime - ReadData.startTime)/1000;
                ReadData.run = false;
                ChargingFragmentActivity.counter = 0;
                System.out.println("Time: " + elapsedTime);
                ReadData.dataPacket.setTime(elapsedTime);

                //Adding dara to database if stop session button in that activity is pressed.

                addToDatabase();

                //Passing data via intents
                Intent intent = new Intent(getActivity(), SessionOverActivity.class);
                intent.putExtra("power", ReadData.dataPacket.getWattAmp());
                intent.putExtra("steps", ReadData.stepCount);
                intent.putExtra("time", elapsedTime);
                startActivity(intent);
                getActivity().finish();
                ReadData.dataPacket = new DataPacket();
                ChargingFragmentActivity.ready = false;
                ready = false;
            }
        });

        setText(String.valueOf(ReadData.dataPacket.getStepCount()));

        getActivity().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        ChargingFragmentActivity.ready = false;

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBatInfoReceiver);
    }

    public static FitnessFragmentActivity newInstance() {

        FitnessFragmentActivity fragment = new FitnessFragmentActivity();
        return fragment;
    }

    public static void setText(String text){
        stepCountText = (TextView) context.findViewById(R.id.stepCountText);
        stepCountText.setText(text);
    }

    private void addToDatabase(){

        ref.child("Data").child(userId).setValue(ReadData.dataPacket);

        ref.child("pastData").child(userId).push().setValue(ReadData.powerGenerated);

    }

}
