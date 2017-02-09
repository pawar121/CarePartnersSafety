package com.philips.com.ble_smartkitchen;

import android.Manifest;
import android.app.Activity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.opencsv.CSVWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

/**
 * Created by Gautam Pawar on 9/29/2016.
 */
public class homeActivity extends Activity {
    private Intent emailIntent;
    private String feedback;
    private EditText feedbackBox;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private ListView lv;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private boolean mScanning;
    private BluetoothGatt gatt;
    private SharedPreferences pref ;
    private SharedPreferences notify_pref ;
    private boolean mConnected = false;
    static  ArrayList<String> deviceList = new ArrayList<>();
    private ServiceConnection mServiceConnection = null;
    private Context mContext;
    private int code2flag =0;
    private int code1flag = 0;
    private int code3flag = 0;
    private int code4flag = 0;
    long date1 =0;
    long code1date = 0;
    long code2date = 0;
    private Set<String> deviceSet;
    private Set<String> greetSet;
    List<String[]> motiondata = new ArrayList<String[]>();
    List<String[]> contactdata = new ArrayList<String[]>();
    private static final int REQUEST_WRITE_STORAGE = 112;
    String notify_type;
    String notify_type1;
    private TextToSpeech myTTS;
    MediaPlayer mMediaPlayer;
Double current;
    String current1;

    @Override
    public void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        mHandler = new Handler();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;  // location permission approval
        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);   // write external storage access
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
        if (permissionGranted) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        if (hasPermission) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // To check if the device has been previously connected. In that case it will automatically connect to the device and update User Interface.
        pref = getApplicationContext().getSharedPreferences("DEVICE_NAME", MODE_PRIVATE);
        if(pref!=null) {
            deviceSet = pref.getStringSet("DEVICE_VALUE", null);
        }
        ((TextView) findViewById(R.id.Help_Value)).setText("Hint: No Sensor Paired. Click on the Setting Icon on the top to get all the nearby sensors.");
        if(deviceSet!=null) {
            ((TextView) findViewById(R.id.Help_Value)).setText("Hint: You have paired devices, but they are not available nearby.");
            ((TextView) findViewById(R.id.HelpIcon)).setText("   ");
            for (final String myVal : deviceSet) {
                 mServiceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder service) {
                        mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
                        //Initializes a reference to the local Bluetooth adapter. Destroy activity if returned false.
                        if (!mBluetoothLeService.initialize()) {
                            finish();
                        }
                        // As connection to sensors is not made in a single attempt so Initializing a timer to make sure devices gets connected.
                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(!deviceList.contains(myVal)) {  // check if device is already connected
                                    mBluetoothLeService.connect(myVal);// Try to connect to BLE device
                                }
                                // Breaking the timer
                               if(deviceList.size()==deviceSet.size()){
                                   timer.cancel();
                                   timer.purge();
                                   return;
                               }
                            }
                        },0,20000);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        mBluetoothLeService = null;
                    }
                };

                Intent gattServiceIntent = new Intent(getApplicationContext(), BluetoothLeService.class);
                getApplicationContext().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                getApplicationContext().startService(gattServiceIntent);
            }
        }
        notify_pref = getApplicationContext().getSharedPreferences("NotificationDetails", MODE_PRIVATE);
        if(notify_pref!=null){
            notify_type  = notify_pref.getString("Notify_Type",null);
            notify_type1  = notify_pref.getString("Notify_Type1",null);
        }

        if(notify_type1!=null || notify_type!=null) {
            if (notify_type1!=null && notify_type1.equalsIgnoreCase("Icon")) {
                ((TextView) findViewById(R.id.Battery_Value_OpenClose)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.SignalStrengthOpenClose)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.Battery_Value)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.SignalStrength)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.SmartPlugSignalStrength_Value)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.Battery_Value_OpenClose)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.SignalStrengthOpenClose)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.Battery_Value)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.SignalStrength)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.SmartPlugSignalStrength_Value)).setVisibility(View.INVISIBLE);
            }
            if (notify_type!=null && notify_type.equalsIgnoreCase("Text")) {
                ((TextView) findViewById(R.id.SignalStrengthOpenClose_Image)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.Battery_Value_OpenClose_Image)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.SignalStrength_Image)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.Battery_Value_Image)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.SmartPlugSignalStrength_Image)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.SignalStrengthOpenClose_Image)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.Battery_Value_OpenClose_Image)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.SignalStrength_Image)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.Battery_Value_Image)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.SmartPlugSignalStrength_Image)).setVisibility(View.INVISIBLE);
            }
            if(notify_type!=null && notify_type1!=null && notify_type.equalsIgnoreCase("Text") && notify_type1.equalsIgnoreCase("Icon")){
                ((TextView) findViewById(R.id.Battery_Value_OpenClose)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.SignalStrengthOpenClose)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.Battery_Value)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.SignalStrength)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.SmartPlugSignalStrength_Value)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.SignalStrengthOpenClose_Image)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.Battery_Value_OpenClose_Image)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.SignalStrength_Image)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.Battery_Value_Image)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.SmartPlugSignalStrength_Image)).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:

                Intent i=new Intent(homeActivity.this, SettingListActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_home).setVisible(false);
        menu.findItem(R.id.menu_stop).setVisible(false);
        menu.findItem(R.id.menu_setting).setVisible(true);
        menu.findItem(R.id.menu_refresh).setActionView(null);
        menu.findItem(R.id.menu_scan).setVisible(false);
        menu.findItem(R.id.menu_help).setVisible(false);
        menu.findItem(R.id.menu_back).setVisible(false);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(this.mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
        }
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = homeActivity.this.getLayoutInflater();
            mInflator = homeActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final BluetoothDevice device = mLeDevices.get(i);
            return view;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     // getApplicationContext().unbindService(mServiceConnection);
       // mBluetoothLeService = null;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()  {
        @Override
        public void onReceive(Context context,Intent intent) {

            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                invalidateOptionsMenu();
                // clearUI();
            } else if (BluetoothLeService.
                    ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

            }  else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {    // Data received as a result of Characteristic read or changed

                // Refrigerator Open Close Sensors Broadcasted values
                findViewById(R.id.Help_TextBox).setVisibility(View.INVISIBLE);
                OpenClose openClose = ((BleApplication)getApplication()).openClose;
                int minStrength = openClose.getMinStrength();
                int maxStrength = openClose.getMaxStrength();
                int maxbattery = openClose.getMaxBattery();
                int minbatery = openClose.getMinBattery();

                SignalStrength signalStrength  = ((BleApplication)getApplication()).signalStrength;
                BatteryValues batteryvalue = ((BleApplication)getApplication()).batteryValues;
                java.util.Date date = new java.util.Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
                String formattedDate = sdf.format(date);

                // If signal strength, battery value or Sensor information is available, Display the Contact Sensor Block.
                if(intent.getStringExtra(BluetoothLeService.OPEN_CLOSE_BATTERY_INFO)!=null || intent.getStringExtra(BluetoothLeService.OPEN_CLOSE_SENSOR_INFO)!=null) {
                    findViewById(R.id.RefrigeratorBlock).setVisibility(View.VISIBLE);

                    ((TextView) findViewById(R.id.Help_Value)).setText("");
                    ((TextView) findViewById(R.id.HelpIcon)).setText("   ");

                    if (intent.getStringExtra(BluetoothLeService.CONTACTSENSOR_TIME)!= null) {       // set Contact Sensor Time
                        String contactSensorDate = intent.getStringExtra(BluetoothLeService.CONTACTSENSOR_TIME).toString();
                        if (contactSensorDate != null) {
                            ((TextView) findViewById(R.id.timecatchOpenClose)).setText(contactSensorDate);
                        }
                    }
                    // Defining the csv file path.
                    File path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File file1 = new File(path1, "ContactSensorDetails.csv");

                try{
                    CSVWriter writer = new CSVWriter(new FileWriter(file1));
                    if (intent.getStringExtra(BluetoothLeService.OPEN_CLOSE_SENSOR_INFO) != null) {
                        String contactsensorvalue = intent.getStringExtra(BluetoothLeService.OPEN_CLOSE_SENSOR_INFO).toString();// Setting the Open/Close information according to the signal recieved.
                        contactdata.add(new String[]{"ContactSensor" + "," + contactsensorvalue + "," + "Type1" + "," + date.toString()});

                        if (intent.getStringExtra(BluetoothLeService.OPEN_CLOSE_SENSOR_INFO).toString().equalsIgnoreCase("0")) {
                            ((TextView) findViewById(R.id.OpenClose_Value)).setText("CLOSE");
                            ((TextView) findViewById(R.id.OpenClose_Value)).setTextColor(Color.GREEN);
                        } else {
                            ((TextView) findViewById(R.id.OpenClose_Value)).setText("OPEN");
                            ((TextView) findViewById(R.id.OpenClose_Value)).setTextColor(Color.RED);
                        }
                    }

                    // Setting the Contact Sensor battery.
                    int contactsensorbattery = batteryvalue.getOpencloseBattery();

                    contactdata.add(new String[]{"Contact Sensor" + "," + contactsensorbattery + "," + "Type2" + "," + date.toString()});
                    if(contactsensorbattery==0){
                        ((TextView) findViewById(R.id.Battery_Value_OpenClose)).setText("Fetching..");
                    }
                  else  if (contactsensorbattery < minbatery) {
                        ((TextView) findViewById(R.id.Battery_Value_OpenClose_Image)).setBackgroundResource(R.drawable.low_battery);
                        ((TextView) findViewById(R.id.Battery_Value_OpenClose)).setText(String.valueOf(contactsensorbattery) + "%");
                    }
                    else if(contactsensorbattery>minbatery && contactsensorbattery<maxbattery){
                        ((TextView) findViewById(R.id.Battery_Value_OpenClose_Image)).setBackgroundResource(R.drawable.moderate_battery);
                        ((TextView) findViewById(R.id.Battery_Value_OpenClose)).setText(String.valueOf(contactsensorbattery)+ "%");

                    }
                    else {
                        ((TextView) findViewById(R.id.Battery_Value_OpenClose_Image)).setBackgroundResource(R.drawable.high_battery);
                        ((TextView) findViewById(R.id.Battery_Value_OpenClose)).setText(String.valueOf(contactsensorbattery)+ "%");
                    }

                    // Setting the contact Sensor Strength.
                    int rssi = signalStrength.getContactSignal();
                        //int rssi = Integer.parseInt(intent.getStringExtra(BluetoothLeService.CONTACTSENSOR_SIGNALINFO).toString());
                        contactdata.add(new String[]{"Contact Sensor" + "," + rssi + "," + "Type3" + "," + date.toString()});

                    if(rssi==0){
                        ((TextView) findViewById(R.id.SignalStrengthOpenClose)).setText("Fetching..");
                    }
                       else if (rssi > minStrength) {
                            ((TextView) findViewById(R.id.SignalStrengthOpenClose_Image)).setBackgroundResource(R.drawable.high_strength);
                            ((TextView) findViewById(R.id.SignalStrengthOpenClose)).setText(String.valueOf(rssi));

                        } else if (rssi < minStrength && rssi > maxStrength) {
                            ((TextView) findViewById(R.id.SignalStrengthOpenClose_Image)).setBackgroundResource(R.drawable.moderate_strength);
                            ((TextView) findViewById(R.id.SignalStrengthOpenClose)).setText(String.valueOf(rssi));

                        } else if (rssi < maxStrength) {
                            ((TextView) findViewById(R.id.SignalStrengthOpenClose_Image)).setBackgroundResource(R.drawable.low_strength);
                            ((TextView) findViewById(R.id.SignalStrengthOpenClose)).setText(String.valueOf(rssi));
                        }
                    writer.writeAll(contactdata);
                    writer.close();
                }
                     catch (IOException e) {
                        System.out.println("File not found");
                    }
                }

                // Motion Sensor Broadcast Values
                // If signal strength, battery value or Sensor information is available, display the Motion Sensor Block.
                if(intent.getStringExtra(BluetoothLeService.BATTERY_INFO)!=null || intent.getStringExtra(BluetoothLeService.MOTION_SENSOR_INFO)!=null){
                    findViewById(R.id.MotionSensorBlock).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.Help_Value)).setText("");
                    ((TextView) findViewById(R.id.HelpIcon)).setText("   ");

                    if(intent.getStringExtra(BluetoothLeService.MOTION_TIME)!=null) {
                        String motionSensorDate = intent.getStringExtra(BluetoothLeService.MOTION_TIME).toString();// Setting the timestamp for the Motion Sensors received.
                        ((TextView) findViewById(R.id.timecatch)).setText(motionSensorDate);
                    }

                    // Setting the signal Strength of the Motion Sensor.
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File file = new File(path, "MotionSensorDetails.csv");
                    try {
                        CSVWriter writer = new CSVWriter(new FileWriter(file));
                        if (intent.getStringExtra(BluetoothLeService.MOTION_SENSOR_INFO) != null) {
                            String sensorvalue = intent.getStringExtra(BluetoothLeService.MOTION_SENSOR_INFO).toString();// Setting PRESENCE/ABSENCE according to the 0's and 1's received from the sensor.
                            motiondata.add(new String[]{"Motion Sensor" + "," + sensorvalue + "," + "Type1" + "," + date.toString()});
                            if (intent.getStringExtra(BluetoothLeService.MOTION_SENSOR_INFO).toString().equalsIgnoreCase("0")) {
                                ((TextView) findViewById(R.id.MotionSensor_Value)).setText("ABSENCE");
                                ((TextView) findViewById(R.id.MotionSensor_Value)).setTextColor(Color.RED);

                            } else {
                                ((TextView) findViewById(R.id.MotionSensor_Value)).setText("PRESENCE");
                                ((TextView) findViewById(R.id.MotionSensor_Value)).setTextColor(Color.GREEN);
                            }
                        }

                        int sensorBattery = batteryvalue.getMotionBattery();
                        motiondata.add(new String[]{"Motion Sensor" + "," + sensorBattery + "," + "Type2" + "," + date.toString()});
                        //    String sensorBattery = intent.getStringExtra(BluetoothLeService.BATTERY_INFO);  // set the battery information of the Motion Sensor.

                        if(sensorBattery==0){
                            ((TextView) findViewById(R.id.Battery_Value)).setText("Fetching Signal Strength");
                        }
                      else if (sensorBattery<minbatery) {
                            motiondata.add(new String[]{"Motion Sensor" + "," + sensorBattery + "," + "Type2" + "," + date.toString()});
                            ((TextView) findViewById(R.id.Battery_Value_Image)).setBackgroundResource(R.drawable.low_battery);
                            ((TextView) findViewById(R.id.Battery_Value)).setText(String.valueOf(sensorBattery)+"%");
                        }
                        else if(sensorBattery>minbatery && sensorBattery<maxbattery){
                            ((TextView) findViewById(R.id.Battery_Value_Image)).setBackgroundResource(R.drawable.moderate_battery);
                            ((TextView) findViewById(R.id.Battery_Value)).setText(String.valueOf(sensorBattery)+ "%");
                        }
                        else {
                            ((TextView) findViewById(R.id.Battery_Value_Image)).setBackgroundResource(R.drawable.high_battery);
                            ((TextView) findViewById(R.id.Battery_Value)).setText(String.valueOf(sensorBattery)+ "%");
                        }

                        //    int sensorStrength = Integer.parseInt(intent.getStringExtra(BluetoothLeService.MOTIONSENSOR_SIGNALINFO).toString());
                            int sensorStrength = signalStrength.getMotionSignal();
                                motiondata.add(new String[]{"Motion Sensor" + "," + sensorStrength + "," + "Type3" + "," + date.toString()});
                              if(sensorStrength==0){
                                  ((TextView) findViewById(R.id.SignalStrength)).setText("Fetching Sensor Strength");
                              }
                               else  if (sensorStrength > minStrength) {
                                    ((TextView) findViewById(R.id.SignalStrength_Image)).setBackgroundResource(R.drawable.high_strength);
                                    ((TextView) findViewById(R.id.SignalStrength)).setText(String.valueOf(sensorStrength));
                                } else if (sensorStrength < minStrength && sensorStrength > maxStrength) {
                                    ((TextView) findViewById(R.id.SignalStrength_Image)).setBackgroundResource(R.drawable.moderate_strength);
                                    ((TextView) findViewById(R.id.SignalStrength)).setText(String.valueOf(sensorStrength));
                                } else if (sensorStrength < maxStrength) {
                                    ((TextView) findViewById(R.id.SignalStrength_Image)).setBackgroundResource(R.drawable.low_strength);
                                    ((TextView) findViewById(R.id.SignalStrength)).setText(String.valueOf(sensorStrength));
                                }

                        writer.writeAll(motiondata);
                        writer.close();
                        }
                    catch (IOException e) {
                            System.out.println("File not found");
                        }
                }

                // SmartPlug Broadcast Receive Values
                // If current/voltage strength, signal  strength information is available, Display the Smart Plug  Block.
                if(intent.getStringExtra(BluetoothLeService.SMARTPLUG_SIGNALINFO)!=null || intent.getStringExtra(BluetoothLeService.SMARTPLUG_TIME)!=null) {
                    findViewById(R.id.SmartPlugBlock).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.Help_Value)).setText("");
                    ((TextView) findViewById(R.id.HelpIcon)).setText("   ");

                    if (intent.getStringExtra(BluetoothLeService.SMARTPLUG_TIME) != null) {
                        String motionSensorDate1 = intent.getStringExtra(BluetoothLeService.SMARTPLUG_TIME).toString(); // set timestamp for Smart Plug
                        ((TextView) findViewById(R.id.smartplugtime)).setText(motionSensorDate1);
                    }

                    if(intent.getStringExtra(BluetoothLeService.SMARTPLUG_SIGNALINFO)!=null) {
                    int smartPlugstrength = Integer.parseInt(intent.getStringExtra(BluetoothLeService.SMARTPLUG_SIGNALINFO));
                        if (smartPlugstrength > minStrength) {
                            ((TextView) findViewById(R.id.SmartPlugSignalStrength_Image)).setBackgroundResource(R.drawable.high_strength);
                            ((TextView) findViewById(R.id.SmartPlugSignalStrength_Value)).setText(intent.getStringExtra(BluetoothLeService.SMARTPLUG_SIGNALINFO).toString());
                        } else if (smartPlugstrength < minStrength && smartPlugstrength > maxStrength) {
                            ((TextView) findViewById(R.id.SmartPlugSignalStrength_Image)).setBackgroundResource(R.drawable.moderate_strength);
                            ((TextView) findViewById(R.id.SmartPlugSignalStrength_Value)).setText(intent.getStringExtra(BluetoothLeService.SMARTPLUG_SIGNALINFO).toString());
                        } else if (smartPlugstrength < maxStrength) {
                            ((TextView) findViewById(R.id.SmartPlugSignalStrength_Image)).setBackgroundResource(R.drawable.low_strength);
                            ((TextView) findViewById(R.id.SmartPlugSignalStrength_Value)).setText(intent.getStringExtra(BluetoothLeService.SMARTPLUG_SIGNALINFO).toString());
                        }
                    }
                    if (intent.getStringExtra(BluetoothLeService.SMARTPLUG_CURRENT) != null) {
                        String current = (intent.getStringExtra(BluetoothLeService.SMARTPLUG_CURRENT).toString() + "A" + ""); // Setting the Current information
                         current1 = (intent.getStringExtra(BluetoothLeService.SMARTPLUG_CURRENT).toString());
                        ((TextView) findViewById(R.id.Current_Strength)).setText(current);
                    }
                    if (intent.getStringExtra(BluetoothLeService.SMARTPLUG_VOLTAGE) != null) {
                        String voltage = (intent.getStringExtra(BluetoothLeService.SMARTPLUG_VOLTAGE).toString() + "" + "V");// Setting the voltage information.
                        ((TextView) findViewById(R.id.Voltage_Strength)).setText(voltage);
                        ((TextView) findViewById(R.id.Voltage_Strength)).setTextColor(Color.GREEN);
                    }
                }
                checkSafetyCondition1();// Safety Condition One - check
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        deviceList.clear();
        if(mBluetoothLeService!=null) {
        mBluetoothLeService.disconnect();
        }
        unregisterReceiver(this.mGattUpdateReceiver);
    }

    // Safety Condition Check 1 Implementation.
    public void checkSafetyCondition1(){
        String contactsensorField =   ((TextView) findViewById(R.id.OpenClose_Value)).getText().toString();
        String motionField =          ((TextView) findViewById(R.id.MotionSensor_Value)).getText().toString();

        try {
            if(contactsensorField!=null && motionField!=null) {
                // SafetyFlow Check one Refrigerator door is open and User Is Present
                if (motionField.equalsIgnoreCase("PRESENCE") && contactsensorField.equalsIgnoreCase("OPEN")) {
                    // codeflag = 0 suggest // Refrigerator door is open and User Is Present.
                    // Check for user's preferred notification.

                    SharedPreferences pref = this.getSharedPreferences("case3emailCheck", MODE_PRIVATE);
                    final String emailtext = pref.getString("case3emailID", null);
                    SharedPreferences pref1 = this.getSharedPreferences("case3soundCheck", MODE_PRIVATE);
                    final String soundtext = pref1.getString("case3sound_alarm", null);
                    SharedPreferences pref3 = this.getSharedPreferences("case3alertCheck", MODE_PRIVATE);
                    final String alerttext = pref3.getString("case3alert_notify", null);
                    SharedPreferences volumepref = this.getSharedPreferences("safetycasethree_volumeLevel", MODE_PRIVATE);
                    final String volumelevel = volumepref.getString("safetycasethree_volume", null);

                    if (code1flag == 0) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (emailtext != null) {
                                    sendMail();             // sending mail..
                                }

                                if (soundtext != null && soundtext.equalsIgnoreCase("Yes")) {
                                    mMediaPlayer = new MediaPlayer();
                                    if (volumelevel != null) {
                                        mMediaPlayer.setVolume(Float.parseFloat(volumelevel), Float.parseFloat(volumelevel));   // Setting user-defined volume for the alarm tone.
                                    }
                                    SharedPreferences pref = getSharedPreferences("safetycasethree_alarmType", MODE_PRIVATE);
                                    String alarmType = pref.getString("safetycasethree_alarmtype", null);
                                    if (alarmType != null && alarmType.equalsIgnoreCase("one")) {                               // Checking the type of alarm user has selected.
                                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm1);
                                    } else if (alarmType != null && alarmType.equalsIgnoreCase("two")) {
                                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm2);
                                    }
                                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    mMediaPlayer.start();
                                }
                                if (alerttext != null && alerttext.equalsIgnoreCase("Yes")) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(homeActivity.this).create();  // Setting up an  alert notification
                                    alertDialog.setTitle("Alert...");
                                    alertDialog.setMessage("Refrigerator door is open. Please Close the Door");
                                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    alertDialog.setIcon(R.drawable.setting_icon);
                                    alertDialog.show();
                                }
                            }
                        }, 20000);

                        java.util.Date date1 = new java.util.Date();
                        code1date = date1.getTime();
                        code1flag = 1;
                    } else if (code1flag == 1) {
                        java.util.Date date2 = new java.util.Date();
                        code2date = date2.getTime();
                        long difference = (code2date - code1date) / 1000;
                        if (difference > 30) {         // check if the difference between both the notification is greater than 30 seconds , then send another notification.
                            code1flag = 0;
                        }
                    }
                }
                else if (motionField.equalsIgnoreCase("ABSENCE") && contactsensorField.equalsIgnoreCase("OPEN")) {    // SafetyCase  4
                    // codeflag2 = 0 suggests Refrigerator door is open and User Is Absent

                    SharedPreferences pref = this.getSharedPreferences("case4emailCheck", MODE_PRIVATE);
                    final String emailtext = pref.getString("case4emailID", null);
                    SharedPreferences pref1 = this.getSharedPreferences("case4soundCheck", MODE_PRIVATE);
                    final String soundtext = pref1.getString("case4sound_alarm", null);
                    SharedPreferences pref3 = this.getSharedPreferences("case4alertCheck", MODE_PRIVATE);
                    final String alerttext = pref3.getString("case4alert_notify", null);
                    SharedPreferences volumepref = this.getSharedPreferences("safetycasefour_volumeLevel", MODE_PRIVATE);
                    final String volumelevel = volumepref.getString("safetycasefour_volume", null);

                    if (code2flag == 0) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (emailtext != null) {
                                    sendMail();
                                }
                                if (soundtext != null && soundtext.equalsIgnoreCase("Yes")) {
                                    mMediaPlayer = new MediaPlayer();
                                    if (volumelevel != null) {
                                        mMediaPlayer.setVolume(Float.parseFloat(volumelevel), Float.parseFloat(volumelevel));   // Setting user-defined volume for the alarm tone.
                                    }
                                    SharedPreferences pref = getSharedPreferences("safetycasefour_alarmType", MODE_PRIVATE);
                                    String alarmType = pref.getString("safetycasefour_alarmtype", null);
                                    if (alarmType != null && alarmType.equalsIgnoreCase("one")) {                               // Checking the type of alarm user has selected.
                                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm1);
                                    } else if (alarmType != null && alarmType.equalsIgnoreCase("two")) {
                                        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm2);
                                    }
                                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    mMediaPlayer.start();
                                }

                                if (alerttext != null && alerttext.equalsIgnoreCase("Yes")) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(homeActivity.this).create();  // Setting up an  alert notification
                                    alertDialog.setTitle("Alert...");
                                    alertDialog.setMessage("Refrigerator door is open. Please Close the Door");
                                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    alertDialog.setIcon(R.drawable.setting_icon);
                                    alertDialog.show();

                                }
                            }
                        }, 10000);
                        java.util.Date firstdate = new java.util.Date();
                        date1 = firstdate.getTime();
                        code2flag = 1;
                    } else if (code2flag == 1) {
                        java.util.Date seconddate = new java.util.Date();
                        long date2 = seconddate.getTime();
                        long difference1 = (date2 - date1) / 1000;
                        if (difference1 > 30) {       // check if the difference between both the notification is greater than 30 seconds , then send another notification.
                            code2flag = 0;
                        }
                    }
                }

                String curr = ((TextView) findViewById(R.id.Current_Strength)).getText().toString();

                if (!curr.equalsIgnoreCase("No Reading Available")) {
                    OpenClose oc = ((BleApplication) getApplication()).openClose;
                    double stovecurrent = oc.getStoveCurrent();
                    current = Double.parseDouble(current1);

                    if (motionField.equalsIgnoreCase("ABSENCE") && current > stovecurrent) {    // SafetyCase  2
                        // codeflag3 = 0 suggests Refrigerator door is open and User Is Absent

                        SharedPreferences pref = this.getSharedPreferences("case2emailCheck", MODE_PRIVATE);
                        final String emailtext = pref.getString("case2emailID", null);
                        SharedPreferences pref1 = this.getSharedPreferences("case2soundCheck", MODE_PRIVATE);
                        final String soundtext = pref1.getString("case2sound_alarm", null);
                        SharedPreferences pref3 = this.getSharedPreferences("case2alertCheck", MODE_PRIVATE);
                        final String alerttext = pref3.getString("case2alert_notify", null);
                        SharedPreferences volumepref = this.getSharedPreferences("safetycasetwo_volumeLevel", MODE_PRIVATE);
                        final String volumelevel = volumepref.getString("safetycasetwo_volume", null);

                        if (code3flag == 0) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (emailtext != null) {
                                        sendMail();
                                    }
                                    if (soundtext != null && soundtext.equalsIgnoreCase("Yes")) {
                                        mMediaPlayer = new MediaPlayer();
                                        if (volumelevel != null) {
                                            mMediaPlayer.setVolume(Float.parseFloat(volumelevel), Float.parseFloat(volumelevel));   // Setting user-defined volume for the alarm tone.
                                        }
                                        SharedPreferences pref = getSharedPreferences("safetycasetwo_alarmType", MODE_PRIVATE);
                                        String alarmType = pref.getString("safetycasetwo_alarmtype", null);
                                        if (alarmType != null && alarmType.equalsIgnoreCase("one")) {                               // Checking the type of alarm user has selected.
                                            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm1);
                                        } else if (alarmType != null && alarmType.equalsIgnoreCase("two")) {
                                            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm2);
                                        }
                                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                        mMediaPlayer.start();
                                    }

                                    if (alerttext != null && alerttext.equalsIgnoreCase("Yes")) {
                                        AlertDialog alertDialog = new AlertDialog.Builder(homeActivity.this).create();  // Setting up an  alert notification
                                        alertDialog.setTitle("Alert...");
                                        alertDialog.setMessage("Refrigerator door is open. Please Close the Door");
                                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        alertDialog.setIcon(R.drawable.setting_icon);
                                        alertDialog.show();
                                    }
                                }
                            }, 10000);
                            java.util.Date firstdate = new java.util.Date();
                            date1 = firstdate.getTime();
                            code3flag = 1;
                        } else if (code3flag == 1) {
                            java.util.Date seconddate = new java.util.Date();
                            long date2 = seconddate.getTime();
                            long difference1 = (date2 - date1) / 1000;
                            if (difference1 > 5) {       // check if the difference between both the notification is greater than 30 seconds , then send another notification.
                                code3flag = 0;
                            }
                        }
                    }
                    if (motionField.equalsIgnoreCase("PRESENCE") && current > stovecurrent) {    // SafetyCase  1
                        // codeflag3 = 0 suggests Refrigerator door is open and User Is Absent

                        SharedPreferences pref = this.getSharedPreferences("case1emailCheck", MODE_PRIVATE);
                        final String emailtext = pref.getString("case1emailID", null);
                        SharedPreferences pref1 = this.getSharedPreferences("case1soundCheck", MODE_PRIVATE);
                        final String soundtext = pref1.getString("case1sound_alarm", null);
                        SharedPreferences pref3 = this.getSharedPreferences("case1alertCheck", MODE_PRIVATE);
                        final String alerttext = pref3.getString("case1alert_notify", null);
                        SharedPreferences volumepref = this.getSharedPreferences("safetycaseone_volumeLevel", MODE_PRIVATE);
                        final String volumelevel = volumepref.getString("safetycaseone_volume", null);

                        if (code4flag == 0) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (emailtext != null) {
                                        sendMail();
                                    }
                                    if (soundtext != null && soundtext.equalsIgnoreCase("Yes")) {
                                        mMediaPlayer = new MediaPlayer();
                                        if (volumelevel != null) {
                                            mMediaPlayer.setVolume(Float.parseFloat(volumelevel), Float.parseFloat(volumelevel));   // Setting user-defined volume for the alarm tone.
                                        }
                                        SharedPreferences pref = getSharedPreferences("safetycaseone_alarmType", MODE_PRIVATE);
                                        String alarmType = pref.getString("safetycaseone_alarmtype", null);
                                        if (alarmType != null && alarmType.equalsIgnoreCase("one")) {                               // Checking the type of alarm user has selected.
                                            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm1);
                                        } else if (alarmType != null && alarmType.equalsIgnoreCase("two")) {
                                            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm2);
                                        }
                                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                        mMediaPlayer.start();
                                    }

                                    if (alerttext != null && alerttext.equalsIgnoreCase("Yes")) {
                                        AlertDialog alertDialog = new AlertDialog.Builder(homeActivity.this).create();  // Setting up an  alert notification
                                        alertDialog.setTitle("Alert...");
                                        alertDialog.setMessage("Refrigerator door is open. Please Close the Door");
                                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        alertDialog.setIcon(R.drawable.setting_icon);
                                        alertDialog.show();
                                    }
                                }
                            }, 10000);
                            java.util.Date firstdate = new java.util.Date();
                            date1 = firstdate.getTime();
                            code4flag = 1;
                        } else if (code4flag == 1) {
                            java.util.Date seconddate = new java.util.Date();
                            long date2 = seconddate.getTime();
                            long difference1 = (date2 - date1) / 1000;
                            if (difference1 > 5) {       // check if the difference between both the notification is greater than 30 seconds , then send another notification.
                                code4flag = 0;
                            }
                        }
                    }

                }
            }
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public  void sendMail(){    // functionality to send mail
        Mail m = new Mail("spawargautam@gmail.com", "370113000");
        String[] toArr = {"spawargautam@gmail.com"};
        m.setTo(toArr);
        m.setFrom("spawargautam@gmail.com");
        m.setSubject("Alert..Please close the Refrigerator door");
        m.setBody("The refrigerator door has been opened for more than 20 Seconds. Please close it to avoid any food spoilage.");
        try {
            if(m.send()) {
                Toast.makeText(homeActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(homeActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
            }
        } catch(Exception e) {
            //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
            Log.e("MailApp", "Could not send email", e);
        }
    }

    private void speakWords(String speech) {
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }
    private static IntentFilter makeGattUpdateIntentFilter() {

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothLeService.ACTION_PAIRING_REQUEST);
        return intentFilter;
    }
}