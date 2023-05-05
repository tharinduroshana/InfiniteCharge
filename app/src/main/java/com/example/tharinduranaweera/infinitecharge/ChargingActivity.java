package com.example.tharinduranaweera.infinitecharge;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/*
* The main activity which the charging process is shoed to the user. This Activity contains with a
* BottomNavigationView which makes the user to switch between the power section and fitness section of the applocation
* which categorizes the specific facts like power generated into power section and steps walked into
* fitness section. This categorization makes the application more user-friendly and lets the user to reach the
* needed information fast without getting lost. (HCI fact)
* */

public class ChargingActivity extends AppCompatActivity {

    //Declaration of variables

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    static int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging);

        //Initializing

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        loadChargingFragment();

        //Setting onItemSelectedListeners for the BottomNavigationView which assigns relevant activity to the given fragment
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.charging_menu_id:
                        selectedFragment = ChargingFragmentActivity.newInstance();
                        position = 0;
                        break;
                    case R.id.fitness_menu_id:
                        selectedFragment = FitnessFragmentActivity.newInstance();
                        position = 1;
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });
    }

    //Setting the transition animations for the BottomNavigationView which makes the application more attractive (HCI fact)

    public void loadChargingFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ChargingFragmentActivity.newInstance());
        transaction.commit();
    }
}
