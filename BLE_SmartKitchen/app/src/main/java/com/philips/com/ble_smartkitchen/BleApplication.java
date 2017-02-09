package com.philips.com.ble_smartkitchen;

import android.app.Application;
import android.renderscript.ScriptGroup;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 310261380 on 10/18/2016.
 */
public class BleApplication extends Application {

    public OpenClose openClose = new OpenClose(-38,-60,20,80,3);
    public BatteryValues batteryValues = new BatteryValues();
    public SignalStrength signalStrength = new SignalStrength();

    }


