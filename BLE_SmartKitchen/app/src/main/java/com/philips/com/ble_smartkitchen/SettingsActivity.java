package com.philips.com.ble_smartkitchen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Set;

/**
 * Created by Gautam Pawar on 10/12/2016.
 */
public class SettingsActivity extends Activity {

    private SharedPreferences prefs;
    private Set<String> deviceSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getActionBar().setTitle("Settings");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_display);

        OpenClose openClose = ((BleApplication)getApplication()).openClose;
        EditText minBattery = (EditText)findViewById(R.id.minBattery);
        EditText maxBattery = (EditText)findViewById(R.id.maxBattery);
        EditText minStrength = (EditText)findViewById(R.id.minStrength);
        EditText maxStrength = (EditText)findViewById(R.id.maxStrength);
        minBattery.setText(String.valueOf(openClose.getMinBattery()));
        maxBattery.setText(String.valueOf(openClose.getMaxBattery()));
        minStrength.setText(String.valueOf(openClose.getMinStrength()));
        maxStrength.setText(String.valueOf(openClose.getMaxStrength()));

        SharedPreferences prefs = getSharedPreferences("NotificationDetails", MODE_PRIVATE);
        String text = prefs.getString("Notify_Type",null);
        String icon = prefs.getString("Notify_Type1",null);
        CheckBox textcheckbox = (CheckBox) findViewById(R.id.text_checkbox);
        CheckBox iconcheckbox = (CheckBox) findViewById(R.id.icon_checkbox);

        if(text!=null) {
            if (text.equalsIgnoreCase("Text")) {
                textcheckbox.setChecked(true);
            }
            else if(text.equalsIgnoreCase("NoText")) {
               textcheckbox.setChecked(false);
            }
        }
        if(icon!=null) {
            if (icon.equalsIgnoreCase("Icon")) {
                iconcheckbox.setChecked(true);
            }
            else if(icon.equalsIgnoreCase("NoIcon")) {
                iconcheckbox.setChecked(false);
            }
        }
    }

    public void startMotionSensorScan(View v) {

        Intent i = new Intent(SettingsActivity.this, ScanDeviceActivity.class);
        i.putExtra("deviceName","iAlert");
        startActivity(i);
    }

    public void startSmartPlugScan(View v) {

        Intent i = new Intent(SettingsActivity.this, ScanDeviceActivity.class);
        i.putExtra("deviceName","Smart Socket");
        startActivity(i);
    }

    public void clearSensors(View v) {

        LayoutInflater li = LayoutInflater.from(SettingsActivity.this);
        View promptsView1 = li.inflate(R.layout.defaultconfirm,null);
        final  AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(SettingsActivity.this);

        alertDialogBuilder1.setView(promptsView1);
        alertDialogBuilder1
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                OpenClose openClose = ((BleApplication)getApplication()).openClose;
                                openClose.setMaxBattery(80);
                                openClose.setMinBattery(20);
                                openClose.setMaxStrength(60);
                                openClose.setMinStrength(38);
                                EditText minBattery = (EditText)findViewById(R.id.minBattery);
                                EditText maxBattery = (EditText)findViewById(R.id.maxBattery);
                                EditText minStrength = (EditText)findViewById(R.id.minStrength);
                                EditText maxStrength = (EditText)findViewById(R.id.maxStrength);
                                minBattery.setText(String.valueOf(openClose.getMinBattery()));
                                maxBattery.setText(String.valueOf(openClose.getMaxBattery()));
                                minStrength.setText(String.valueOf(openClose.getMinStrength()));
                                maxStrength.setText(String.valueOf(openClose.getMaxStrength()));

                                prefs = getApplicationContext().getSharedPreferences("DEVICE_NAME", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.clear();
                                editor.commit();

                                CheckBox iconcheckbox = (CheckBox) findViewById(R.id.icon_checkbox);
                                SharedPreferences settings = getSharedPreferences("NotificationDetails", MODE_PRIVATE);
                                SharedPreferences.Editor iconeditor = settings.edit();
                                iconeditor.putString("Notify_Type1","Icon");
                                iconcheckbox.setChecked(true);

                                CheckBox textheckbox = (CheckBox) findViewById(R.id.text_checkbox);
                                iconeditor.putString("Notify_Type","Text");
                                textheckbox.setChecked(true);
                                iconeditor.commit();

                                Toast.makeText(getApplicationContext(), "Default Settings Applied", Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder1.create();
        alertDialog.show();

    }

    public void startOpenCloseScan(View v) {
        Intent i = new Intent(SettingsActivity.this, ScanDeviceActivity.class);
        i.putExtra("deviceName","ContactSensor");
        startActivity(i);
    }

    public void textNotification(View v){
        CheckBox textcheckbox = (CheckBox) findViewById(R.id.text_checkbox);
        SharedPreferences settings = getSharedPreferences("NotificationDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (!textcheckbox.isChecked()) {            // if user uncheck the checkbox.
            editor.putString("Notify_Type","Notext");
            editor.commit();
            textcheckbox.setChecked(false);
        }
        else {
            editor.putString("Notify_Type", "Text");
            editor.commit();
            textcheckbox.setChecked(true);
        }
    }

    public void iconNotification(View v){
        CheckBox iconcheckbox = (CheckBox) findViewById(R.id.icon_checkbox);
        SharedPreferences settings = getSharedPreferences("NotificationDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (!iconcheckbox.isChecked()) {            // if user uncheck the checkbox.
            editor.putString("Notify_Type1","NoIcon");
            editor.commit();
            iconcheckbox.setChecked(false);
        }
        else
        {
            editor.putString("Notify_Type1", "Icon");
            editor.commit();
            iconcheckbox.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:

                Intent i=new Intent(SettingsActivity.this, homeActivity.class);
                startActivity(i);
                break;

            case R.id.menu_back:
                onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_home).setVisible(true);
        menu.findItem(R.id.menu_stop).setVisible(false);
        menu.findItem(R.id.menu_setting).setVisible(false);
        menu.findItem(R.id.menu_refresh).setActionView(null);
        menu.findItem(R.id.menu_scan).setVisible(false);
        menu.findItem(R.id.menu_help).setVisible(false);
        menu.findItem(R.id.menu_back).setVisible(true);
        return true;
    }

    public  void setProperties(View v){

       OpenClose openclose =  ((BleApplication)getApplication()).openClose;
        EditText e = ((EditText) findViewById(R.id.maxBattery));
        if (e.getText().toString().equals("")) {
            e.setError("Battery field cannot be blank");
            e.requestFocus();
        }
        else {
            String maxBattery = e.getText().toString();
            if (!maxBattery.equalsIgnoreCase("")) {
                int maxBattery_new = Integer.parseInt(maxBattery.trim());
                openclose.setMaxBattery(maxBattery_new);
            }
        }

        EditText e1 = ((EditText) findViewById(R.id.minBattery));
        if (e1.getText().toString().equals("")) {
            e1.setError("Battery field cannot be blank");
            e1.requestFocus();
        }
        else {
            String minBattery = e1.getText().toString();
            if (!minBattery.equalsIgnoreCase("")) {
                int minBattery_new = Integer.parseInt(minBattery.trim());
                openclose.setMinBattery(minBattery_new);
            }
        }

        EditText e2 = ((EditText) findViewById(R.id.minStrength));
        if (e2.getText().toString().equals("")) {
            e2.setError("Strength field cannot be blank");
            e2.requestFocus();
        }
        else {
            String minStrength = e2.getText().toString();
            if (!minStrength.equalsIgnoreCase("")) {
                int minStrenth_new = Integer.parseInt(minStrength.trim());
                openclose.setMinStrength(minStrenth_new);
            }
        }

        EditText e3 = ((EditText) findViewById(R.id.maxStrength));
        if (e3.getText().toString().equals("")) {
            e3.setError("Strength field cannot be blank");
            e3.requestFocus();
        }
        else {
            String maxStrength = e3.getText().toString();
            if (!maxStrength.equalsIgnoreCase("")) {
                int maxStrenth_new = Integer.parseInt(maxStrength.trim());
                openclose.setMaxStrength(maxStrenth_new);
            }
        }


        EditText e4 = ((EditText) findViewById(R.id.stovemaxStrength));
        if (e4.getText().toString().equals("")) {
            e4.setError("Strength field cannot be blank");
            e4.requestFocus();
        }
        else {
            String maxStrength = e4.getText().toString();
            if (!maxStrength.equalsIgnoreCase("")) {
                float maxStrenth_new = Float.parseFloat(maxStrength.trim());
                openclose.setStoveCurrent(maxStrenth_new);
            }
        }
        Toast.makeText(this,"Settings Updated",Toast.LENGTH_SHORT).show();
    }
}
