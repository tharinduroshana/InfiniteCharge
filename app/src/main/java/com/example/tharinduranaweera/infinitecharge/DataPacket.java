package com.example.tharinduranaweera.infinitecharge;

import java.util.ArrayList;

/*
* The DataPacket class which holds every data of a single session as instances. This makes the program easy and feasible.
* The adding to the database was easily done by just calling the name of the object so that all the attributes inside are saved in a methodical order.
*
* Security: All the data is encapsulated within the application because of the usage of private variables and getter and setters.
* */

public class DataPacket {

    //Declaration

    private double powerGenerated;
    private int stepCount;
    private int time;
    private double wattAmp;

    //Getters and setters

    public double getWattAmp() {
        return wattAmp;
    }

    public void setWattAmp(double wattAmp) {
        this.wattAmp = wattAmp;
    }

    private ArrayList<Double> longitides = new ArrayList<>();
    private ArrayList<Double> latitudes = new ArrayList<>();

    public double getPowerGenerated() {
        return powerGenerated;
    }

    public void setPowerGenerated(double powerGenerated) {
        this.powerGenerated += powerGenerated;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount() {
        this.stepCount++;
    }

    public void addLatitude(double lat){
        latitudes.add(lat);
    }

    public void addLongitude(double lng){
        longitides.add(lng);
    }

    public ArrayList<Double> getLongitides() {
        return longitides;
    }

    public ArrayList<Double> getLatitudes() {
        return latitudes;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
