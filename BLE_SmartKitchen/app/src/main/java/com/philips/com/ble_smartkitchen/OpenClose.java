package com.philips.com.ble_smartkitchen;

import android.app.Application;

/**
 * Created by Gautam on 10/18/2016.
 */



public class OpenClose {
    public int maxBattery=0;
    public int minBattery=0;
    public int minStrength=0;
    public int maxStrength=0;
    public double stoveCurrent;

    public int getMaxBattery() {
        return maxBattery;
    }

    public void setMaxBattery(int maxBattery) {
        this.maxBattery = maxBattery;
    }

    public int getMinBattery() {
        return minBattery;
    }

    public void setMinBattery(int minBattery) {
        this.minBattery = minBattery;
    }

    public int getMinStrength() {
        return minStrength;
    }

    public void setMinStrength(int minStrength) {
        this.minStrength = minStrength;
    }

    public int getMaxStrength() {
        return maxStrength;
    }

    public void setMaxStrength(int maxStrength) {
        this.maxStrength = maxStrength;
    }

    public double getStoveCurrent() {
        return stoveCurrent;
    }

    public void setStoveCurrent(float stoveCurrent) {
        this.stoveCurrent = stoveCurrent;
    }

    public OpenClose(int a, int b, int c, int d, double current){
        this.minStrength =  a;
        this.maxStrength =  b;
        this.minBattery  =  c;
        this.maxBattery  =  d;
        this.stoveCurrent = current;
    }

    public OpenClose(){

    }
}
