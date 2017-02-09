package com.philips.com.ble_smartkitchen;

/**
 * Created by 310261380 on 10/24/2016.
 */
public class BatteryValues {

    private int motionBattery;
    private int opencloseBattery;
    private float stoveCurrent;

    public int getMotionBattery() {
        return motionBattery;
    }

    public void setMotionBattery(int motionBattery) {
        this.motionBattery = motionBattery;
    }

    public int getOpencloseBattery() {
        return opencloseBattery;
    }

    public float getStoveCurrent() {
        return stoveCurrent;
    }

    public void setStoveCurrent(float stoveCurrent) {
        this.stoveCurrent = stoveCurrent;
    }

    public void setOpencloseBattery(int opencloseBattery) {
        this.opencloseBattery = opencloseBattery;

    }
}
