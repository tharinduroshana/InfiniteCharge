package com.example.tharinduranaweera.infinitecharge;

//Imports
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

/*
* We've used plenty of Images to make the application user friendly. Every user likes to work with images
* than to work with a bunch of texts. That easily gives the impression to the user what he/she want to do with the relevant
* button. This helps the user to stop getting lost in the application. It also increased the simplicity of the application.
*
* We've handled the exceptions with try-catch blocks to avoid the crashing of the application.
* The user is able to roam around the application without any issue (HCI Fact).
* */

public class HomeActivity extends AppCompatActivity{

    //Declaration of variables
    ViewPager viewPager;
    private FirebaseAuth mAuth;
    int currentPage = 3;
    Timer timer;
    Handler handler;
    Toolbar toolbar;
    RadioGroup radioGroup;
    RadioButton radioButton, radioButton2;
    int[] radioButtonArray = {R.id.radioButton, R.id.radioButton2};
    FloatingTextButton bluetoothbtn, exitbtn;
    Activity activity;

    //Bluetooth config variables

    String address, name;
    static boolean isConnected = false;

    BluetoothAdapter myBluetooth;
    static BluetoothSocket btSocket;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Inflating the options menu created into the activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_layout, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Initializing variables
        activity = this;

        mAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioButton = (RadioButton) findViewById(R.id.radioButton);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);

        exitbtn = (FloatingTextButton) findViewById(R.id.exitbtn);
        bluetoothbtn = (FloatingTextButton) findViewById(R.id.bluetoothbtn);
        bluetoothbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connectDevice();
                    }
                }).start();
            }
        });

        //Setting onClickListeners to exit button
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        radioButton.setClickable(false);
        radioButton2.setClickable(false);

        //Adding the ActionBar to the activity
        setSupportActionBar(toolbar);

        //Initializing the ViewPager which helps the user to slide through the icons to enter different sections
        //of the application
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setCurrentItem(1);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Checking the relevant radio button when the user is in the relevant slide
                radioGroup.check(radioButtonArray[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /*
        * Adds the auto sliding effect at the first time application loads.
        * HCI fact : This feature lets the user know that this area can be navigate through sliding.
        * If the user of the app confuses that's bad for the reputation as well as the user wastes his/her
        * time. By adding this quick animation of auto sliding the user gets an idea about the navigation process
        * and avoid him/her from getting lost in the interface.
        * */
        handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage >= 0)
                    viewPager.setCurrentItem(currentPage--, true);
            }
        };

        timer = new Timer(); // Creating the new Timer
        timer .schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 500, 300);

    }

    //ActionListeners for menu items added.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exit_menu_id){
            finish();
        }else if (item.getItemId() == R.id.logout_menu_id){
           mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.about_us_menu_id){
            //Makes an AlertDialog box pop up and shows the user About Us alert. To reduce the dullness of the
            //AlertDialog we've inserted the Group logo into that too.
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setIcon(R.drawable.logo);
            builder.setTitle("About Us");
            //Description about us
            builder.setMessage("Infinite Charge is an application comes with an IOT device which helps you to charge your mobile phone without any issue while walking. It also motivates you to walk more and have a healthy life.");
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * connectDevice() method which allows the user to connect with the IOT device we've made.
    *
    * Security: It only allows the user to connect with the InfiniteCharge bluetooth device. It makes the user to
    * avoid from misbehaving with the application and having exceptions due to those actions. We've done plenty of
    * validations to make sure the user is connected with the correct device. (Application only allows the user to connect with the
    * unique MAC address of the bluetooth module we've user to integrate into our device)
    * */
    private void connectDevice(){
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        address = myBluetooth.getAddress();
        pairedDevices = myBluetooth.getBondedDevices();

        if (pairedDevices.size() > 0){
            for (BluetoothDevice device : pairedDevices){
                if (device.getAddress().equals("98:D3:32:31:1B:56")) {
                    address = device.getAddress();
                    name = device.getName();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Lets user know that the device is connected with the application.
                            Toast.makeText(activity, "Connected to " + name, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

        myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
        if (address != null) {
            BluetoothDevice infiniteChargeDevice = myBluetooth.getRemoteDevice(address);
            //try and catch which helps the application to end peacefully if something went wrong
            try {
                btSocket = infiniteChargeDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                btSocket.connect();
                isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
