package com.philips.com.ble_smartkitchen;

import java.util.HashMap;

/**
 * Created by Gautam on 9/20/2016.
 */
public class GATTActivity {

    private static HashMap<String, String> attributes = new HashMap();
    public static String SENSE_ME = "9dc84838-7619-4f09-a1ce-ddcf63225b52";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String MANUFACT_DEV = "00002a29-0000-1000-8000-00805f9b34fb";
    public static String BATTERY_STATUS = "00002a19-0000-1000-8000-00805f9b34fb";
    public static String OPENCLOSE_CHARA = "887516FD-DDD4-42D2-831E-9FD4A0f97fd2";
    public static String OPENCLOSE_SERVICE = "887516FD-DDD4-42D2-831E-9FD4A0f97fd0";
    public static String SWITCH_UUID = "0000FEE3-494C-4F47-4943-54454380000";
    public static String SWITCH_SERVICE_UUID = "0000FEE0-494C-4F47-4943-54454380000";



    static
    {
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("00002a19-0000-1000-8000-00805f9b34fb","Battery Level");
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("9DC84838-7619-4f09-A1CE-DDCF63225B50", "Sensor Services");
    }

    public static String lookup(String uuid, String defaultName)
    {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
