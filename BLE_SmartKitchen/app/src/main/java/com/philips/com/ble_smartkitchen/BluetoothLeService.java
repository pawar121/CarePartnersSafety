
package com.philips.com.ble_smartkitchen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.content.Context.BATTERY_SERVICE;
import static com.philips.com.ble_smartkitchen.BluetoothLeService.UUID_BLE_Device;

/**
     * Service for managing connection and data communication with a GATT server hosted on a
     * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private Activity mActivity;
    private static final int GATT_AUTH_FAIL = 0x89;
    private static final int ME_AUTH_FAIL = 0x01;
    private static final int ME_AUTH_PASS = 0x01;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private Boolean mDeviceBusy = false;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGatt mService;
    private int mClientIf;
    private Context mContext;
    private BluetoothDevice mDevice;
    private BluetoothGattCharacteristic mcharact;
    private static final boolean DBG = true;
    private static final boolean VDBG = false;
    private int mConnectionState = STATE_DISCONNECTED;
    private boolean mAuthRetry = false;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private List<BluetoothGattService> mServices;
    static final int AUTHENTICATION_NONE = 0;
    static int motionSensorCounter = 0;
    static int contactSensorCounter = 0;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public static final String ACTION_BOND_STATE_CHANGED =
            "android.bluetooth.device.action.BOND_STATE_CHANGED";
    public static final String ACTION_PAIRING_REQUEST =
            "android.bluetooth.device.action.PAIRING_REQUEST";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String MOTION_SENSOR_INFO =
            "com.example.bluetooth.le.MOTION_SENSOR_INFO";
    public final static String BATTERY_INFO =
            "com.example.bluetooth.le.BATTERY_INFO";
    public final static String OPEN_CLOSE_SENSOR_INFO =
            "com.example.bluetooth.le.OPEN_CLOSE_SENSOR_INFO";
    public final static String OPEN_CLOSE_BATTERY_INFO =
            "com.example.bluetooth.le.OPEN_CLOSE_BATTERY_INFO";
    public final static String CONTACTSENSOR_SIGNALINFO =
            "com.example.bluetooth.le.CONTACTSENSOR_SIGNALINFO";
    public final static String MOTIONSENSOR_SIGNALINFO =
            "com.example.bluetooth.le.MOTIONSENSOR_SIGNALINFO";
    public final static String SMARTPLUG_SIGNALINFO =
            "com.example.bluetooth.le.SMARTPLUG_SIGNALINFO";
    public final static String SMARTPLUG_VOLTAGE =
            "com.example.bluetooth.le.SMARTPLUG_VOLTAGE";
    public final static String SMARTPLUG_CURRENT =
            "com.example.bluetooth.le.SMARTPLUG_CURRENT";
    public final static String MOTION_TIME =
            "com.example.bluetooth.le.MOTION_TIME";
    public final static String CONTACTSENSOR_TIME =
            "com.example.bluetooth.le.CONTACTSENSOR_TIME";
    public final static String SMARTPLUG_TIME =
            "com.example.bluetooth.le.SMARTPLUG_TIME";

    public final static UUID UUID_BLE_Device = UUID.fromString(GATTActivity.SENSE_ME);
    private static final UUID SENSOR_SERVICES = UUID.fromString("9DC84838-7619-4f09-A1CE-DDCF63225B50");
    private static final UUID BATTERY_SERVICES = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    private static final UUID BATTERY_CHARACTERISTIC = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    private static final UUID OPENLCOSE_SERVICE = UUID.fromString("887516FD-DDD4-42D2-831E-9FD4A0f97fd0");
    private static final UUID OPENLCOSE_CHARACTERISTIC = UUID.fromString("887516FD-DDD4-42D2-831E-9FD4A0f97fd2");
    private static final UUID SMARTPLUG_SERVICE = UUID.fromString("0000FEE0-494C-4F47-4943-544543480000");
    private static final UUID SMARTPLUG_CHARACTERISTIC = UUID.fromString("0000FEE3-494C-4F47-4943-544543480000");
    private static final UUID VOLTAGE_CHARACTERISTIC = UUID.fromString("0000FEE1-494C-4F47-4943-544543480000");

    public static final byte[] OPEN_SUCCESS = {0x01};
    public static final byte[] OPEN_FAILURE = {0004,0001};


    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                readRemoteRssi();
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

                // Saving the connected devices in a sharedpreference xml file.
                SharedPreferences settings = getSharedPreferences("DEVICE_NAME", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                Set<String> deviceSet = new HashSet<String>(settings.getStringSet("DEVICE_VALUE", new HashSet<String>()));
                deviceSet.add(gatt.getDevice().getAddress());
                editor.putStringSet("DEVICE_VALUE", deviceSet);
                editor.commit();


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    intentAction = ACTION_GATT_DISCONNECTED;
                    mConnectionState = STATE_DISCONNECTED;
                    Log.i(TAG, "Disconnected from GATT server.");
                    broadcastUpdate(intentAction);
                }
            }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            //   broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                if(!homeActivity.deviceList.contains(gatt.getDevice().getAddress())) {
                    homeActivity.deviceList.add(gatt.getDevice().getAddress());
                }// Adding the connected device to the ArrayList
                // check if contact sensor, then read signal strength, battery and set up notification to true.
                if(gatt.getDevice().getName().contains("ContactSensor")){
                      readRemoteRssi(); // reading signal strength
                      if(contactSensorCounter==0){
                      gatt.readCharacteristic(gatt.getService(OPENLCOSE_SERVICE).getCharacteristic(OPENLCOSE_CHARACTERISTIC)); // reading the sensor value for the first time
                    }
                     setCharacteristicNotification(gatt.getService(OPENLCOSE_SERVICE).getCharacteristic(OPENLCOSE_CHARACTERISTIC),true); // setting the notifications true
                }

                // check if motion sensor, then read signal strength, battery and set up notification to true.
                else if(gatt.getDevice().getName().contains("iAlert")){
                       readRemoteRssi();// reading signal strength
                     if(motionSensorCounter==0){
                        gatt.readCharacteristic(gatt.getService(SENSOR_SERVICES).getCharacteristic(UUID_BLE_Device)); // reading the sensor value for the first time
                    }
                     setCharacteristicNotification(gatt.getService(SENSOR_SERVICES).getCharacteristic(UUID_BLE_Device),true); // setting the notifications true
                }

                // for Smartplug read signal strength and set up notification to true.
                else if(gatt.getDevice().getName().contains("Smart Socket")) {
                    readRemoteRssi();// reading signal strength
                  //  gatt.readCharacteristic(gatt.getService(SMARTPLUG_SERVICE).getCharacteristic(VOLTAGE_CHARACTERISTIC)); // reading the sensor value for the first time
                    setCharacteristicNotification(gatt.getService(SMARTPLUG_SERVICE).getCharacteristic(VOLTAGE_CHARACTERISTIC),true);// setting the notifications true
                }

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

//        public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] data,BluetoothGatt gatt) {
//            characteristic.setValue(data);
//
//            boolean status = gatt.writeCharacteristic(characteristic);
//            return status;
//        }

        // For Reading the characteristics like Battery level etc.
        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic,gatt); // calling the function to read the sensor values for the first time.
                batteryBroadcast(ACTION_DATA_AVAILABLE,gatt,gatt.getService(BATTERY_SERVICES).getCharacteristic(BATTERY_CHARACTERISTIC)); //  calling the function to read the battery level.
            }
            else
            {
                gatt.getDevice().createBond();
                if (gatt.getDevice().getBondState() == BluetoothDevice.BOND_NONE) {
                    mDevice.createBond();
                    final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                    mActivity.registerReceiver(mreciever, filter);

                } else {
                    // this situation happens when you try to connect for the second time to already bonded device
                    // it should never happen, in my opinion
                }
            }
        }

        // For handling the notification data.
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic,gatt);
            readRemoteRssi();
        }

        // callback for reading signal strength.
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
               strengthbroadcastUpdate(ACTION_DATA_AVAILABLE, rssi,gatt);
            }
        }
    };


  // function to calculate current and voltage measured through smart plug.
    private String decode_energyData(byte[] paramArrayOfByte, int paramInt)
    {
        if (paramArrayOfByte != null)
        {
            double d = 1000 * (0xF & paramArrayOfByte[(paramInt + 1)] >> 4) + 100 * (0xF & paramArrayOfByte[(paramInt + 1)]) + 10 * (0xF & paramArrayOfByte[(paramInt + 2)] >> 4) + (0xF & paramArrayOfByte[(paramInt + 2)]);
            switch (paramArrayOfByte[paramInt])
            {
                default:
                    return "0.0";
                case 1:
                    Locale localLocale5 = Locale.US;
                    Object[] arrayOfObject5 = new Object[1];
                    arrayOfObject5[0] = Double.valueOf(d / 1000.0D);
                    return String.format(localLocale5, "%4.3f", arrayOfObject5);
                case 2:
                    Locale localLocale4 = Locale.US;
                    Object[] arrayOfObject4 = new Object[1];
                    arrayOfObject4[0] = Double.valueOf(d / 100.0D);
                    return String.format(localLocale4, "%4.2f", arrayOfObject4);
                case 3:
                    Locale localLocale3 = Locale.US;
                    Object[] arrayOfObject3 = new Object[1];
                    arrayOfObject3[0] = Double.valueOf(d / 10.0D);
                    return String.format(localLocale3, "%4.1f", arrayOfObject3);
                case 4:
                    Locale localLocale2 = Locale.US;
                    Object[] arrayOfObject2 = new Object[1];
                    arrayOfObject2[0] = Double.valueOf(d);
                    return String.format(localLocale2, "%4.1f", arrayOfObject2);
            }
        }
        return "0.0";
    }

    private final BroadcastReceiver mreciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(intent.getAction())) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
                Intent pairingIntent = new Intent(BluetoothDevice.ACTION_PAIRING_REQUEST);
                pairingIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
                pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, type);
                if (type == BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION
                        ) {
                    int pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
                            BluetoothDevice.ERROR);
                    pairingIntent.putExtra(BluetoothDevice.EXTRA_PAIRING_KEY, pairingKey);
                }
                pairingIntent.setAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
                pairingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (type == BluetoothDevice.PAIRING_VARIANT_PIN) {
                    abortBroadcast();
                } else {

                }
            }
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    // Handling broadcasting battery level of the sensor devices.
    public void batteryBroadcast(String action,BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){

        final Intent intent = new Intent(action);
        if(gatt.getDevice().getName().contains("ContactSensor")){  // Contact Sensor Battery Level Info.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(byteChar);
                intent.putExtra(OPEN_CLOSE_BATTERY_INFO, stringBuilder.toString() + "%");
                BatteryValues batteryvalue = ((BleApplication) getApplication()).batteryValues;
                batteryvalue.setOpencloseBattery(Integer.parseInt(stringBuilder.toString()));
            }
        }

        else if (gatt.getDevice().getName().contains("iAlert")){  // Motion Sensor Battery Level Info.

            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                stringBuilder.append(byteChar);
                intent.putExtra(BATTERY_INFO, stringBuilder.toString() + "%");
                BatteryValues batteryvalue = ((BleApplication) getApplication()).batteryValues;
                batteryvalue.setMotionBattery(Integer.parseInt(stringBuilder.toString()));
            }
        }
        sendBroadcast(intent);
    }

    // Signal Strength broadcast Handling.
    public void strengthbroadcastUpdate(final String action, int rssi, BluetoothGatt gatt) {

        final Intent intent = new Intent(action);
        if(gatt.getDevice().getName().contains("ContactSensor")) {
            intent.putExtra(CONTACTSENSOR_SIGNALINFO.toString(), String.valueOf(rssi));
            SignalStrength signalStrength = ((BleApplication) getApplication()).signalStrength;
            signalStrength.setContactSignal(rssi);
            sendBroadcast(intent);
        }

        if(gatt.getDevice().getName().contains("iAlert")){
            intent.putExtra(MOTIONSENSOR_SIGNALINFO, String.valueOf(rssi));
            SignalStrength signalStrength  = ((BleApplication)getApplication()).signalStrength;
            signalStrength.setMotionSignal(rssi);
            sendBroadcast(intent);
        }
        if(gatt.getDevice().getName().contains("Smart Socket")){
            intent.putExtra(SMARTPLUG_SIGNALINFO, String.valueOf(rssi));
            SignalStrength signalStrength  = ((BleApplication)getApplication()).signalStrength;
            signalStrength.setSmartPlugSignal(rssi);
            sendBroadcast(intent);
        }
    }

    public void broadcastUpdate( String action,
                                 BluetoothGattCharacteristic characteristic,BluetoothGatt gatt) {
        final  Intent intent = new Intent(action);

       // Reading the notification for Motion Sensor.
        if (UUID_BLE_Device.equals(characteristic.getUuid())) {

            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                stringBuilder.append(byteChar);
                intent.putExtra(MOTION_SENSOR_INFO, stringBuilder.toString());
                java.util.Date date = new java.util.Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
                String formattedDate = sdf.format(date);
                intent.putExtra(MOTION_TIME,formattedDate);
                motionSensorCounter++;    // increasing the counter by one each time a sensor value is notified.
                sendBroadcast(intent);
                readRemoteRssi();
                sendBroadcast(intent);  // sending the sensor notification to be updated.
            }
            // checking the count of sensor notifications and reading battery after each 20th notification.
            if(motionSensorCounter%20==0 || motionSensorCounter==1){
                gatt.readCharacteristic(gatt.getService(BATTERY_SERVICES).getCharacteristic(BATTERY_CHARACTERISTIC));
            }
        }

        // Reading the notification for Contact Sensor.
        if (OPENLCOSE_CHARACTERISTIC.equals(characteristic.getUuid())) {
            int format = -1;
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                format = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 7);
                intent.putExtra(OPEN_CLOSE_SENSOR_INFO, Integer.toString(format));
                java.util.Date date = new java.util.Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
                String formattedDate = sdf.format(date);
                intent.putExtra(CONTACTSENSOR_TIME,formattedDate);
                readRemoteRssi();
                contactSensorCounter++;
                sendBroadcast(intent);  // sending the sensor notification to be updated.
                // increasing the counter by one each time a sensor value is notified.
            }
            // checking the count of sensor notifications and reading battery after each 20th notification.
            if(contactSensorCounter%20==0 || contactSensorCounter==1){
                gatt.readCharacteristic(gatt.getService(BATTERY_SERVICES).getCharacteristic(BATTERY_CHARACTERISTIC));
            }
        }

        // Reading the notification for Contact Sensor.
        if(VOLTAGE_CHARACTERISTIC.equals(characteristic.getUuid())){

            final byte[] data = characteristic.getValue();
            intent.putExtra(SMARTPLUG_CURRENT,decode_energyData(data,4));
            intent.putExtra(SMARTPLUG_VOLTAGE,decode_energyData(data,1));
            java.util.Date date = new java.util.Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
            String formattedDate = sdf.format(date);
            intent.putExtra(SMARTPLUG_TIME,formattedDate);
            readRemoteRssi();
            OpenClose oc = new OpenClose();
            oc.setStoveCurrent(Float.parseFloat(decode_energyData(data,4)));
            sendBroadcast(intent);  // sending the sensor notification to be updated.
        }
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    // function to read signal strength of the sensor.
    public void readRemoteRssi() {
        BluetoothGatt gatt = mBluetoothGatt;
        if (gatt.connect()) {
            gatt.readRemoteRssi();
        }
    }

    /**
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                Log.d(TAG, "Connected.");
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        device.getBondState();
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**connect\\\\\
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
       // mBluetoothGatt.close();
      // mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }
//
//    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic,BluetoothGatt gatt) {
//
//        //check mBluetoothGatt is available
//        if (mBluetoothGatt == null) {
//            Log.e(TAG, "lost connection");
//            return false;
//        }
//            if (characteristic == null) {
//                Log.e(TAG, "char not found!");
//                return false;
//            }
//
//            byte[] value = new byte[1];
//            value[0] = (byte) (0x00);
//            characteristic.getValue();
//            characteristic.setValue(value);
//            boolean status = mBluetoothGatt.writeCharacteristic(characteristic);
//            return status;
//        }

//    public void writeCustomCharacteristic(int value, BluetoothGatt gatt) {
//        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//            Log.w(TAG, "BluetoothAdapter not initialized");
//            return;
//        }
//        /*check if the service is available on the device*/
//        BluetoothGattService mCustomService = gatt.getService(UUID.fromString("0000FEE0-494C-4F47-4943-544543480000"));
//        if(mCustomService == null){
//            Log.w(TAG, "Custom BLE Service not found");
//            return;
//        }
//        /*get the read characteristic from the service*/
//        BluetoothGattCharacteristic mWriteCharacteristic = mCustomService.getCharacteristic(UUID.fromString("0000FEE3-494C-4F47-4943-544543480000"));
//        mWriteCharacteristic.setValue(value,android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT16,0);
//        if(mBluetoothGatt.writeCharacteristic(mWriteCharacteristic) == false){
//            Log.w(TAG, "Failed to write characteristic");
//        }
//    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        //  Enabling notification for Motion Sensor.
        if (UUID_BLE_Device.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GATTActivity.CLIENT_CHARACTERISTIC_CONFIG));
            if(descriptor!=null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }

        //  Enabling notification for Contact Sensor.
        if(OPENLCOSE_CHARACTERISTIC.equals(characteristic.getUuid())){
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GATTActivity.CLIENT_CHARACTERISTIC_CONFIG));
            if(descriptor!=null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }

        //  Enabling notification for Smart Plug.
        if (VOLTAGE_CHARACTERISTIC.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GATTActivity.CLIENT_CHARACTERISTIC_CONFIG));
            if(descriptor!=null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }
    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }
}


