package com.philips.com.ble_smartkitchen;

/**
 * Created by Gautam on 10/24/2016.
 */
public class SignalStrength {

    private int motionSignal=0;
    private int contactSignal=0;
    private int smartPlugSignal=0;

    public int getMotionSignal() {
        return motionSignal;
    }

    public void setMotionSignal(int motionSignal) {
        this.motionSignal = motionSignal;
    }

    public int getContactSignal() {
        return contactSignal;
    }

    public void setContactSignal(int contactSignal) {
        this.contactSignal = contactSignal;
    }

    public int getSmartPlugSignal() {
        return smartPlugSignal;
    }

    public void setSmartPlugSignal(int smartPlugSignal) {
        this.smartPlugSignal = smartPlugSignal;
    }
}


