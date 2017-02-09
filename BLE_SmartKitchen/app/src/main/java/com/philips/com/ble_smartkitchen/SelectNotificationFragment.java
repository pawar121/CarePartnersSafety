package com.philips.com.ble_smartkitchen;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;

/**
 * Created by Gautam Pawar on 12/27/2016.
 */
public class SelectNotificationFragment extends DialogFragment {

    ArrayList<String> rangeTimeList = new ArrayList<>();
    ArrayAdapter<String> safeycasedataadapter;
    View rootView;
    private OnCompleteListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.selectfragment, container, false);
        getDialog().setTitle("Select Type of Notification");
        populateSpinner();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    public static interface OnCompleteListener {
        public abstract void onSelect(int safetycaseid);
    }

    public void populateSpinner(){
        rangeTimeList.add("Select type of Notification");
        rangeTimeList.add("Email Notification");
        rangeTimeList.add("Sound Notification");
        rangeTimeList.add("Alert Notification");

        // Spinner element
        Spinner safetycasespinner = (Spinner)rootView.findViewById(R.id.safetycase_list);
        // When user selects safetycase from drop down
        safetycasespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mListener.onSelect(position); // sending the selected safety case inforamtion to the activity.
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        safeycasedataadapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, rangeTimeList);
        safeycasedataadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        safetycasespinner.setAdapter(safeycasedataadapter);
        safeycasedataadapter.notifyDataSetChanged();
    }
}
