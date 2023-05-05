package com.example.tharinduranaweera.infinitecharge;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/*
* ReadData class which implements Runnable interface so that it can be run in a different thread itself.
* All the heavy processes and waiting processes related to data retrieval are moved here to increase the
* interactiveness of the application so the application UI never gets stuck due to traffic in data input stream.
* */

public class ReadData implements Runnable {

    //Declaration

    final static double PIEZO_ELECTRIC_CONSTANT = 0.025;
    final static double THICKNESS = 0.001;
    final static double RADIUS = 0.01;
    final static double CURRENT = 0.6;
    static int stepCount = 0;
    static double powerGenerated = 0;
    double force = 0;
    static long startTime;
    static boolean run = true;
    static DataPacket dataPacket = new DataPacket();

    //Overridden run() method
    @Override
    public void run() {

        run = true;

        startTime = System.currentTimeMillis();

        BluetoothSocket socket = HomeActivity.btSocket;
        InputStream inputStream = null;

        byte array[];

        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (run){
            try {

                String fullMessage = "";
                boolean accept = true;

                //Checks whether data is available in the inputStream
                if (inputStream.available() > 0){

                    //Take the byte Stream and putting it to the byte array
                    array = new byte[inputStream.available()];
                    inputStream.read(array);
                    final String message = new String(array, 0, array.length);

                    Log.i("FIRST MESSAGE: ", message);

                    //Validations done to avoid garbage data and take meaningful data and calculating power generated
                    if(message.startsWith("$") && message.endsWith("#")){
                        fullMessage = message.substring(0, message.length() - 1);
                        if(fullMessage.length() == 46 || fullMessage.length() == 45 || fullMessage.length() == 44 || fullMessage.length() == 43) {
                            System.out.println(fullMessage);
                            int piezoReading = Integer.parseInt(fullMessage.split("Analog read:")[1]);
                            force = calculateForce(piezoReading);
                            double tempPower = calculatePower(force);
                            powerGenerated += tempPower;
                            dataPacket.setPowerGenerated(tempPower);

                            double Lat = Double.valueOf(fullMessage.split("LAT: ")[1].substring(0, 8));

                            System.out.println("POWER: " + dataPacket.getPowerGenerated());

                            System.out.println("LAT: " + Lat);
                        }
                    }else{
                        if (accept){
                            if(message.startsWith("$")){
                                fullMessage += message.substring(0, message.length());
                            }else {
                                boolean ok = false;
                                int index = 0;
                                for(int i = 0; i < message.length(); i++){
                                    if(message.charAt(i) == '#'){
                                        index = i;
                                        ok = true;
                                        break;
                                    }
                                }
                                if(index != message.length() - 1){
                                    accept = false;
                                }else {
                                    accept = true;
                                }

                                if(ok) {
                                    fullMessage += message.substring(0, index);
                                    if(fullMessage.length() == 46 || fullMessage.length() == 45 || fullMessage.length() == 44 || fullMessage.length() == 43) {
                                        System.out.println(fullMessage);
                                        int piezoReading = Integer.parseInt(fullMessage.split("Analog read:")[1]);
                                        force = calculateForce(piezoReading);
                                        double tempPower = calculatePower(force);
                                        powerGenerated += tempPower;
                                        //powerGenerated = (Math.round(powerGenerated * 100.0)/ 100.0);
                                        dataPacket.setPowerGenerated(tempPower);

                                        double Lat = Double.valueOf(fullMessage.split("LAT: ")[1].substring(0, 8));

                                        dataPacket.addLatitude(Lat);

                                        double Lng = Double.valueOf(fullMessage.split("LON: ")[1].substring(0, 9));

                                        dataPacket.addLongitude(Lng);

                                        System.out.println("POWER: " + dataPacket.getPowerGenerated());

                                        System.out.println("LAT: " + Lat + ", LON: " + Lng);
                                    }
                                }

                            }
                        }
                    }

                    //Counting number of steps and showing in the relevant TextView
                    if (message.length() >= 20){
                        stepCount++;
                        dataPacket.setStepCount();
                        if(ChargingActivity.position == 1){
                            FitnessFragmentActivity.context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (FitnessFragmentActivity.ready)
                                        FitnessFragmentActivity.setText(String.valueOf(dataPacket.getStepCount()));
                                }
                            });
                        }

                        //Setting generated power into relevant TextView
                        ChargingFragmentActivity.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long currentTime = System.currentTimeMillis();
                                float difference = (float) (currentTime - startTime)/(1000 * 3600);
                                System.out.println("FIRST TIME: " + startTime + " END TIME: " + currentTime);
                                System.out.println("POWERRRRRRR: " + String.valueOf(dataPacket.getPowerGenerated() * difference));
                                if (ChargingFragmentActivity.ready)
                                    ChargingFragmentActivity.setText(String.valueOf((Math.round(dataPacket.getPowerGenerated() * difference * 100.0)/100.0)) + "Wh");
                                dataPacket.setWattAmp((Math.round(dataPacket.getPowerGenerated() * difference * 100.0)/100.0));
                            }
                        });
                    }



                    Thread.sleep(1100);

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //Method which calculates force according to the analog reading by the piezo plate.
    private int calculateForce(int pressureReading){
        return ((50 - pressureReading) * 100)/50;
    }

    //Calculating the power from the retrieved values and Constants
    private double calculatePower(double force){
        double v = (PIEZO_ELECTRIC_CONSTANT * force * THICKNESS) / (Math.PI * Math.pow(RADIUS, 2));
        Log.i("VOLTAGE: " , String.valueOf(v));
        double power = ((v * CURRENT));

        System.out.println("POWER: " + power);
        return power;
    }
}
