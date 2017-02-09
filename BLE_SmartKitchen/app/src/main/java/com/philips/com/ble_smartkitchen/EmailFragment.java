package com.philips.com.ble_smartkitchen;

import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Gautam Pawar on 12/11/2016.
 */
public class EmailFragment extends DialogFragment {
    View rootView;
    EditText emailText;
    Button emailButton;
    Button okbutton;
    Button cancelbutton;
    public static final int MODE_PRIVATE = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final String safetycase = getArguments().getString("origin");
        rootView = inflater.inflate(R.layout.emailfragment, container, false);
        getDialog().setTitle("Email Notification:");

        if(safetycase.equalsIgnoreCase("safetycaseone")) {
            emailText = (EditText) rootView.findViewById(R.id.emailaddress);
            okbutton = (Button) rootView.findViewById(R.id.okbutton);
            cancelbutton = (Button) rootView.findViewById(R.id.cancelbutton);
            SharedPreferences emailpref = getActivity().getSharedPreferences("case1emailCheck", MODE_PRIVATE);

            if (emailpref != null) {
                String emailFlag = emailpref.getString("emailPref", null);
                String emailID = emailpref.getString("case1emailID", null);
                if (emailFlag != null && emailFlag.equalsIgnoreCase("Yes")) {
                    emailText.setVisibility(rootView.VISIBLE);
                    emailText.setText(emailID);
                    emailButton.setVisibility(rootView.VISIBLE);
                    emailButton.setText("UPDATE");
                } else {

                }
            }

            okbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences pref = getContext().getSharedPreferences("case1emailCheck", MODE_PRIVATE);
                    if (pref != null) {
                        SharedPreferences pref1 = getActivity().getSharedPreferences("case1emailCheck", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref1.edit();
                        String emailtext = emailText.getText().toString();
                        editor.putString("case1emailID", emailtext);
                        editor.commit();
                        String emailid = pref.getString("case1emailID", null);
                        TextView emailText = (TextView) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.emailNotificationValue);
                        TextView Block1 = (TextView) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.safetystep1);
                        LinearLayout l = (LinearLayout) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.emailLayout);
                        TextView Block2 = (TextView) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.safetystepone);

                            Block1.setVisibility(View.VISIBLE);
                            l.setVisibility(View.VISIBLE);
                            emailText.setText(emailid);
                            getDialog().dismiss();

                    }
                }
            });

            cancelbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    getDialog().dismiss();
                }
            });
        }

        else if(safetycase.equalsIgnoreCase("safetycasetwo")) {
            emailText = (EditText) rootView.findViewById(R.id.emailaddress);
            okbutton = (Button) rootView.findViewById(R.id.okbutton);
            cancelbutton = (Button) rootView.findViewById(R.id.cancelbutton);

            SharedPreferences emailpref = getActivity().getSharedPreferences("case2emailCheck", MODE_PRIVATE);

            if (emailpref != null) {
                String emailFlag = emailpref.getString("emailPref", null);
                String emailID = emailpref.getString("case2emailID", null);
                if (emailFlag != null && emailFlag.equalsIgnoreCase("Yes")) {
                    emailText.setVisibility(rootView.VISIBLE);
                    emailText.setText(emailID);
                    emailButton.setVisibility(rootView.VISIBLE);
                    emailButton.setText("UPDATE");
                } else {
                }
            }

            okbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences pref = getContext().getSharedPreferences("case2emailCheck", MODE_PRIVATE);
                    if (pref != null) {
                        SharedPreferences pref1 = getActivity().getSharedPreferences("case2emailCheck", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref1.edit();
                        String emailtext = emailText.getText().toString();
                        editor.putString("case2emailID", emailtext);
                        editor.commit();
                        String emailid = pref.getString("case2emailID", null);
                        Toast.makeText(getContext(), "Email Address saved!!", Toast.LENGTH_SHORT).show();
                        TextView emailTextone = (TextView) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.emailNotificationValueone);
                        TextView add2 = (TextView) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.stepaddone);
                        Button b = (Button) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.stepaddtwo);
                        TextView Block2 = (TextView) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.safetystepone);
                        LinearLayout l2 = (LinearLayout) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.emailLayoutone);
                        l2.setVisibility(View.VISIBLE);
                        Block2.setVisibility(View.VISIBLE);
                        b.setVisibility(View.VISIBLE);
                        emailTextone.setText(emailid);
                        add2.setVisibility(View.VISIBLE);
                        getDialog().dismiss();
                    }
                }
            });

            cancelbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    getDialog().dismiss();
                }
            });
        }

        else if(safetycase.equalsIgnoreCase("safetycasethree")) {
            emailText = (EditText) rootView.findViewById(R.id.emailaddress);
            okbutton = (Button) rootView.findViewById(R.id.okbutton);
            cancelbutton = (Button) rootView.findViewById(R.id.cancelbutton);
            SharedPreferences emailpref = getActivity().getSharedPreferences("case3emailCheck", MODE_PRIVATE);
            if (emailpref != null) {
                String emailFlag = emailpref.getString("emailPref", null);
                String emailID = emailpref.getString("case3emailID", null);
                if (emailFlag != null && emailFlag.equalsIgnoreCase("Yes")) {
                    emailText.setVisibility(rootView.VISIBLE);
                    emailText.setText(emailID);
                    emailButton.setVisibility(rootView.VISIBLE);
                    emailButton.setText("UPDATE");
                } else {
                }
            }

            okbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences pref = getContext().getSharedPreferences("case3emailCheck", MODE_PRIVATE);
                    if (pref != null) {
                        SharedPreferences pref1 = getActivity().getSharedPreferences("case3emailCheck", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref1.edit();
                        String emailtext = emailText.getText().toString();
                        editor.putString("case3emailID", emailtext);
                        editor.commit();

                        String emailid = pref.getString("case3emailID", null);
                        Toast.makeText(getContext(), "Email Address saved!!", Toast.LENGTH_SHORT).show();
                        TextView emailTextone = (TextView) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.emailNotificationValuethree);
                        TextView Block1 = (TextView) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.safetystep3);
                        TextView add2 = (TextView) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.stepadd33);
                        Button b = (Button) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.stepadd31);
                        LinearLayout l2 = (LinearLayout) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.emailLayoutthree);
                        l2.setVisibility(View.VISIBLE);
                        Block1.setVisibility(View.VISIBLE);
                        b.setVisibility(View.VISIBLE);
                        emailTextone.setText(emailid);
                        add2.setVisibility(View.VISIBLE);
                        getDialog().dismiss();

                    }
                }
            });

            cancelbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    getDialog().dismiss();
                }
            });

        }
        else if(safetycase.equalsIgnoreCase("safetycasefour")) {
            emailText = (EditText) rootView.findViewById(R.id.emailaddress);
           // emailButton = (Button) rootView.findViewById(R.id.setEmail);
            okbutton = (Button) rootView.findViewById(R.id.okbutton);
            cancelbutton = (Button) rootView.findViewById(R.id.cancelbutton);

            SharedPreferences emailpref = getActivity().getSharedPreferences("case3emailCheck", MODE_PRIVATE);

            if (emailpref != null) {
                String emailFlag = emailpref.getString("emailPref", null);
                String emailID = emailpref.getString("case3emailID", null);
                if (emailFlag != null && emailFlag.equalsIgnoreCase("Yes")) {
                    emailText.setVisibility(rootView.VISIBLE);
                    emailText.setText(emailID);
                    emailButton.setVisibility(rootView.VISIBLE);
                    emailButton.setText("UPDATE");
                } else {
                }
            }

            okbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences pref = getContext().getSharedPreferences("case4emailCheck", MODE_PRIVATE);
                    if (pref != null) {
                        SharedPreferences pref2 = getActivity().getSharedPreferences("case4emailCheck", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref2.edit();
                        String emailtext = emailText.getText().toString();
                        editor.putString("case4emailID", emailtext);
                        editor.commit();
                        Toast.makeText(getContext(), "Email Address saved!!", Toast.LENGTH_SHORT).show();

                        String emailid = pref.getString("case4emailID", null);
                        TextView emailTextone = (TextView) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.emailNotificationValuefour);
                        TextView Block1 = (TextView) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.safetystep4);
                        TextView add2 = (TextView) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.stepadd44);
                        Button b = (Button) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.stepadd41);
                        LinearLayout l2 = (LinearLayout) ((SafetyFlowSettingActivity) getContext()).findViewById(R.id.emailLayoutfour);
                        l2.setVisibility(View.VISIBLE);
                        Block1.setVisibility(View.VISIBLE);
                        b.setVisibility(View.VISIBLE);
                        emailTextone.setText(emailid);
                        add2.setVisibility(View.VISIBLE);
                        getDialog().dismiss();
                    }
                }
            });

            cancelbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    getDialog().dismiss();
                }
            });
        }
        return rootView;
    }
}
