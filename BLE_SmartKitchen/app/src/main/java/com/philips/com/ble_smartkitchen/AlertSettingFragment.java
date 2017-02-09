package com.philips.com.ble_smartkitchen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Gautam Pawar on 12/15/2016.
 */
public class AlertSettingFragment extends DialogFragment {

    View rootView;
    CheckBox alertcheck;
    Button okbutton;
    Button cancelbutton;
    public static final int MODE_PRIVATE = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final String safetycase = getArguments().getString("origin");
        rootView = inflater.inflate(R.layout.alertfragment, container, false);
        getDialog().setTitle("Select Notification:");
        okbutton = (Button) rootView.findViewById(R.id.okbutton);
        cancelbutton = (Button) rootView.findViewById(R.id.cancelbutton);
        alertcheck = (CheckBox)rootView.findViewById(R.id.alertcheck);

        if(safetycase.equalsIgnoreCase("safetycaseone")) {

            okbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences pref = getActivity().getSharedPreferences("case1alertCheck", MODE_PRIVATE);
                    SharedPreferences.Editor alerteditor = pref.edit();
                    alerteditor.putString("case1alert_notify","Yes");
                    alerteditor.commit();
                    if (pref != null) {
                            String alertstatus = pref.getString("case1alert_notify", null);
                            TextView alertText = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.alertNotificationValue);
                            LinearLayout l = (LinearLayout) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.alertLayout);
                            LinearLayout l1 = (LinearLayout) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.soundLayout);
                            LinearLayout l2 = (LinearLayout) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.emailLayout);
                            TextView block1 = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.safetystep1);
                            l1.setVisibility(View.VISIBLE);
                            block1.setVisibility(View.VISIBLE);
                            l2.setVisibility(View.VISIBLE);
                            l.setVisibility(View.VISIBLE);
                            Button b = (Button) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.stepadd3);
                            b.setVisibility(View.VISIBLE);
                            alertText.setText(alertstatus);
                            getDialog().dismiss();
                    }
                }
            });
        }

        else if (safetycase.equalsIgnoreCase("safetycasetwo")) {
            okbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences pref = getActivity().getSharedPreferences("case2alertCheck", MODE_PRIVATE);
                    SharedPreferences.Editor alerteditor = pref.edit();
                    alerteditor.putString("case2alert_notify","Yes");
                    alerteditor.commit();
                    if (pref != null) {
                        String alertstatus = pref.getString("case2alert_notify", null);
                        TextView alertText = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.alertNotificationValueone);
                        LinearLayout l = (LinearLayout) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.alertLayoutone);
                        TextView block1 = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.safetystepone);
                        block1.setVisibility(View.VISIBLE);
                        l.setVisibility(View.VISIBLE);
                        Button b = (Button) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.stepaddthree);
                        b.setVisibility(View.VISIBLE);
                        alertText.setText(alertstatus);
                        getDialog().dismiss();
                    }
                }
            });
        }


        else if (safetycase.equalsIgnoreCase("safetycasethree")) {
            okbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences pref = getActivity().getSharedPreferences("case3alertCheck", MODE_PRIVATE);
                    SharedPreferences.Editor alerteditor = pref.edit();
                    alerteditor.putString("case3alert_notify","Yes");
                    alerteditor.commit();
                    if (pref != null) {
                        String alertstatus = pref.getString("case3alert_notify", null);
                        TextView alertText = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.alertNotificationValuethree);
                        LinearLayout l = (LinearLayout) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.alertLayoutthree);
                        TextView block1 = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.safetystep3);
                        block1.setVisibility(View.VISIBLE);
                        l.setVisibility(View.VISIBLE);
                        Button b = (Button) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.stepadd32);
                        b.setVisibility(View.VISIBLE);
                        alertText.setText(alertstatus);
                        getDialog().dismiss();
                    }
                }
            });
        }

        else if (safetycase.equalsIgnoreCase("safetycasefour")) {
            okbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences pref = getActivity().getSharedPreferences("case4alertCheck", MODE_PRIVATE);
                    SharedPreferences.Editor alerteditor = pref.edit();
                    alerteditor.putString("case4alert_notify","Yes");
                    alerteditor.commit();
                    if (pref != null) {
                        String alertstatus = pref.getString("case4alert_notify", null);
                        TextView alertText = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.alertNotificationValuefour);
                        LinearLayout l = (LinearLayout) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.alertLayoutfour);
                        TextView block1 = (TextView) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.safetystep4);
                        block1.setVisibility(View.VISIBLE);
                        l.setVisibility(View.VISIBLE);
                        Button b = (Button) ((SafetyFlowSettingActivity) getActivity()).findViewById(R.id.stepadd42);
                        b.setVisibility(View.VISIBLE);
                        alertText.setText(alertstatus);
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
}


