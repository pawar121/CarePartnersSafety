package com.philips.com.ble_smartkitchen;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Gautam Pawar on 12/15/2016.
 */
public class SoundSettingFragment extends android.support.v4.app.DialogFragment {
    View rootView;
    CheckBox soundcheck;
    EditText soundText;
    Button okbutton;
    Button cancelbutton;
    public static final int MODE_PRIVATE = 0;
    ArrayList<String> alarmList = new ArrayList<>();
    ArrayList<String> volumeList = new ArrayList<>();
    private SoundSettingFragment.OnCompleteListener mListener;
    private SoundSettingFragment.OnSetVolumeListener sListener;
    private SoundSettingFragment.OnSetAlarmListener aListener;
    ArrayAdapter<String> alarmaddapter;
    ArrayAdapter<String> volumeaddapter;
     String safetycase;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        safetycase = getArguments().getString("origin");
        rootView = inflater.inflate(R.layout.soundfragment, container, false);
        getDialog().setTitle("Select Notification:");
        soundText = (EditText)rootView.findViewById(R.id.emailaddress);
        okbutton = (Button) rootView.findViewById(R.id.okbutton);
        cancelbutton = (Button) rootView.findViewById(R.id.cancelbutton);
        populateAlarmSpinner();
        populateVolumeSpinner();

        // safety case on conditions
        if(safetycase.equalsIgnoreCase("safetycaseone")) {
            okbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences soundpref = getActivity().getSharedPreferences("case1soundCheck", MODE_PRIVATE);
                    SharedPreferences.Editor soundeditor = soundpref.edit();
                    soundeditor.putString("case1sound_alarm", "Yes");
                    soundeditor.commit();
                    if (soundpref != null) {
                        String soundstatus = soundpref.getString("case1sound_alarm", null);
                            TextView soundText = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.soundNotificationValue);
                            LinearLayout l = (LinearLayout) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.soundLayout);
                            TextView Block1 = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.safetystep1);
                            Block1.setVisibility(View.VISIBLE);
                            l.setVisibility(View.VISIBLE);
                            Button b = (Button) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.stepadd3);
                            b.setVisibility(View.VISIBLE);
                            soundText.setText(soundstatus);
                            getDialog().dismiss();
                    }
                }
            });
        }
        // safety case two
            else if(safetycase.equalsIgnoreCase("safetycasetwo")){
            okbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences soundpref = getActivity().getSharedPreferences("case2soundCheck", MODE_PRIVATE);
                    SharedPreferences.Editor soundeditor = soundpref.edit();
                    soundeditor.putString("case2sound_alarm", "Yes");
                    soundeditor.commit();
                    if (soundpref != null) {
                           String soundstatus = soundpref.getString("case2sound_alarm", null);
                           TextView soundText = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.soundNotificationValueone);
                            LinearLayout l = (LinearLayout) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.soundLayoutone);
                            TextView Block1 = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.safetystepone);
                            LinearLayout emailLAyout = (LinearLayout) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.emailLayoutone);
                            Block1.setVisibility(View.VISIBLE);
                            emailLAyout.setVisibility(View.VISIBLE);
                            l.setVisibility(View.VISIBLE);
                            Button b = (Button) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.stepaddthree);
                            b.setVisibility(View.VISIBLE);
                            soundText.setText(soundstatus);
                            getDialog().dismiss();
                        }
                }
            });
            }

        else if(safetycase.equalsIgnoreCase("safetycasethree")){
            okbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences soundpref = getActivity().getSharedPreferences("case3soundCheck", MODE_PRIVATE);
                    SharedPreferences.Editor soundeditor = soundpref.edit();
                    soundeditor.putString("case3sound_alarm", "Yes");
                    soundeditor.commit();
                    if (soundpref != null) {
                        String soundstatus = soundpref.getString("case3sound_alarm", null);
                        TextView soundText = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.soundNotificationValuethree);
                        LinearLayout l = (LinearLayout) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.soundLayoutthree);
                        TextView Block1 = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.safetystep3);
                        Block1.setVisibility(View.VISIBLE);
                        l.setVisibility(View.VISIBLE);
                        Button b = (Button) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.stepadd31);
                        b.setVisibility(View.VISIBLE);
                        soundText.setText(soundstatus);
                        getDialog().dismiss();
                    }
                }
            });
        }

        else if(safetycase.equalsIgnoreCase("safetycasefour")){
            okbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences soundpref = getActivity().getSharedPreferences("case4soundCheck", MODE_PRIVATE);
                    SharedPreferences.Editor soundeditor = soundpref.edit();
                    soundeditor.putString("case4sound_alarm", "Yes");
                    soundeditor.commit();
                    if (soundpref != null) {
                        String soundstatus = soundpref.getString("case4sound_alarm", null);
                        TextView soundText = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.soundNotificationValuefour);
                        LinearLayout l = (LinearLayout) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.soundLayoutfour);
                        TextView Block1 = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.safetystep4);
                        Block1.setVisibility(View.VISIBLE);
                        l.setVisibility(View.VISIBLE);
                        Button b = (Button) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.stepadd42);
                        b.setVisibility(View.VISIBLE);
                        soundText.setText(soundstatus);
                        getDialog().dismiss();
                    }
                }
            });
        }
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                getDialog().dismiss();
            }

        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (SoundSettingFragment.OnCompleteListener)activity;
            this.sListener = (SoundSettingFragment.OnSetVolumeListener)activity;
            this.aListener= (SoundSettingFragment.OnSetAlarmListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    public static interface OnCompleteListener {
        public abstract void onComplete(int safetycaseid);
    }


    public static interface OnSetVolumeListener {
        public abstract void onSetVolume(int safetycaseid,String s);
    }

    public static interface OnSetAlarmListener {
        public abstract void onSelectAlarm(int safetycaseid,String s);
    }

    // populate alarm tunes dropdown menu
    public void populateAlarmSpinner(){
        alarmList.add("Select an Alarm");
        alarmList.add("Alarm 1");
        alarmList.add("Alarm2");

        // Spinner element
        Spinner alarmSpinner = (Spinner)rootView.findViewById(R.id.alarmSpinner);
        // When user selects safetycase from drop down
        alarmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                aListener.onSelectAlarm(position,safetycase); // sending the selected safety case inforamtion to the activity.
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        alarmaddapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, alarmList);
        alarmaddapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alarmSpinner.setAdapter(alarmaddapter);
        alarmaddapter.notifyDataSetChanged();
    }

  // populate volume dropdown
    public void populateVolumeSpinner(){
        volumeList.add("Select an Alarm");
        volumeList.add("20%");
        volumeList.add("40%");
        volumeList.add("60%");
        volumeList.add("80%");

        // Spinner element
        Spinner volumespinner = (Spinner)rootView.findViewById(R.id.volumeSpinner);
        // When user selects safetycase from drop down
        volumespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sListener.onSetVolume(position,safetycase); // sending the selected safety case inforamtion to the activity.
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        volumeaddapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, volumeList);
        volumeaddapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        volumespinner.setAdapter(volumeaddapter);
        volumeaddapter.notifyDataSetChanged();
    }
}

