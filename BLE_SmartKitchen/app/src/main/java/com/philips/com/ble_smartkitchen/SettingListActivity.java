package com.philips.com.ble_smartkitchen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Gautam Pawar on 12/7/2016.
 */
public class SettingListActivity extends Activity {

    ArrayList<String> adapt;
    propertyArrayAdapter obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_setting_list);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

      /*  ArrayAdapter<LocationDataObject> availableLocationsArrayAdapter =
                new ArrayAdapter<LocationDataObject>(this, R.layout.location_name);
        // Find and set up the ListView for available locations
        ListView availableLocationsListView = (ListView) findViewById(R.id.available_locations);
        availableLocationsListView.setAdapter(availableLocationsArrayAdapter);
        availableLocationsListView.setOnItemClickListener(mLocationClickListener); */

        adapt = new ArrayList<String>();
        adapt.add("General/Sensor Settings");
        adapt.add("Safety Flow Settings");

        obj = new propertyArrayAdapter(this, R.layout.settingname, adapt);
        ListView availableLocationsListView = (ListView) findViewById(R.id.available_locations);
        availableLocationsListView.setAdapter(obj);
        }

  class propertyArrayAdapter extends ArrayAdapter<String> {

    //constructor, call on creation
    public propertyArrayAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
    }

    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout locationLayout = (LinearLayout) View.inflate(getContext(), R.layout.settingname, null);
        TextView settingView = (TextView) locationLayout.findViewById(R.id.setting_list_name);
        settingView.setText(adapt.get(position));
        final String settingName = (adapt.get(position));

        final Button settingbutton = (Button) locationLayout.findViewById(R.id.button_setting);
        settingbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(settingName.equalsIgnoreCase("General/Sensor Settings")) {
                    startActivity(new Intent(SettingListActivity.this, SettingsActivity.class));
                }

                else{
                    startActivity(new Intent(SettingListActivity.this, SafetyFlowSettingActivity.class));
                    }
            }
        });

        return locationLayout;
    }
}

}

