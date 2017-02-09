package com.philips.com.ble_smartkitchen;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Gautam on 9/12/2016.
 */
public class ScanDeviceActivity extends ListActivity {

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private TextView nameView;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothLeService mBluetoothLeService;
    private SharedPreferences pref ;
    private Set<String> deviceSet ;
    private static final long SCAN_PERIOD = 10000; // Stops scanning after 10 seconds
   private static  final UUID SENSE_UUID = UUID.fromString("0000FEE0-494C-4F47-4943-544543480000");
    private  Map<String, List<String>> map = new HashMap<String, List<String>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Select the Device");
        mHandler = new Handler();


        // Use this check to get access to the User's location
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (permissionGranted) {
            // {Some Code}
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    // Retrieving the already paired devices.
        pref = getApplicationContext().getSharedPreferences("DEVICE_NAME", MODE_PRIVATE);
        if(pref!=null) {
            deviceSet = pref.getStringSet("DEVICE_VALUE", null); // to get the paired devices
        }
        mScanning=true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_home).setVisible(true);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
            menu.findItem(R.id.menu_setting).setVisible(false);
            menu.findItem(R.id.menu_help).setVisible(false);

        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_setting).setVisible(false);
            menu.findItem(R.id.menu_help).setVisible(false);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                //    mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
            case R.id.menu_home:
                Intent intent = new Intent(ScanDeviceActivity.this,homeActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_back:
                Intent i=new Intent(ScanDeviceActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {

        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth. Application will be closed.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final  BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        final Intent intent = new Intent(this, ManageDeviceActivity.class);
        intent.putExtra(ManageDeviceActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(ManageDeviceActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());

        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }

    private void scanLeDevice(final boolean enable) {   // enable variable wilell method l twhether to start or stop the scanning.
        if (enable) {
            //Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
//            mBluetoothAdapter.startLeScan(new UUID[]{SENSE_UUID}, mLeScanCallback); // Passing the UUID of the required device to be detected.
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = ScanDeviceActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
        }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
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
            ViewHolder viewHolder;

            // General ListView optimization code.
            if (view == null) {
              view = mInflator.inflate(R.layout.device_list, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.deviceTime = (TextView) view.findViewById(R.id.device_time);
                viewHolder.deviceTime.setText(DateFormat.getDateTimeInstance().format(new Date()));

                view.setTag(viewHolder);

            }
            else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();

            if (deviceName != null && deviceName.length() > 0){
                viewHolder.deviceName.setText(deviceName);
                viewHolder.deviceAddress.setText(device.getAddress());}
            else
            {
                viewHolder.deviceName.setText(R.string.unknown_device);
                viewHolder.deviceAddress.setText(device.getAddress());
            }

            // To check if the device is already connected and changing its color to green.
            if(deviceSet!=null) {
                for (String myVal : deviceSet) {

                    if (device.getAddress().equalsIgnoreCase(myVal)) {

                        viewHolder.deviceName.setTextColor(getResources().getColor(R.color.green));
                        viewHolder.deviceAddress.setTextColor(getResources().getColor(R.color.green));
                        viewHolder.deviceTime.setTextColor(getResources().getColor(R.color.green));
                    }
                }
            }
            return view;
        }
    }

    // Device scan callback to return the scanned devices.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = getIntent();
                            String name = intent.getStringExtra("deviceName");
                            String scandeviceName = device.getName();

                            // check if the device is already connected .
                            if (homeActivity.deviceList.contains(name)) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Dear User,You are already connected to this Sensor", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP | Gravity.CENTER, 80, 100);
                                toast.show();
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);

                            }
                            // check if the device is selected sensor is available nearby.
                            else if(scandeviceName!=null) {
                                if(scandeviceName.contains(name)) {
                                    mLeDeviceListAdapter.addDevice(device);
                                    mLeDeviceListAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            };

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

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceTime;
    }
}


