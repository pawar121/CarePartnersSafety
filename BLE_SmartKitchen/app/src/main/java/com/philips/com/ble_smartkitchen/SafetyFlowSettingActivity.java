package com.philips.com.ble_smartkitchen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by Gautam Pawar on 10/12/2016.
 */
public class SafetyFlowSettingActivity extends FragmentActivity implements SafetyCaseListFragment.OnCompleteListener,SelectNotificationFragment.OnCompleteListener,SoundSettingFragment.OnCompleteListener,SelectNotificationFragmentTwo.OnCompleteListener
       ,SelectNotificationFragmentThree.OnCompleteListener,SoundSettingFragment.OnSetVolumeListener,SoundSettingFragment.OnSetAlarmListener, SelectNotificationFragmentFour.OnCompleteListener {

    public static SafetyFlowSettingActivity curActivity = null;

    private SharedPreferences prefs;
    private Set<String> deviceSet;
    Button dfragbutton;
    FragmentManager fm = getSupportFragmentManager();
    SafetyCaseListFragment dFragment;
    SelectNotificationFragment selectNotificationFragment;
    SelectNotificationFragmentTwo selectNotificationFragmenttwo;
    SelectNotificationFragmentThree selectNotificationFragmentThree;
    SelectNotificationFragmentFour selectNotificationFragmentFour;

    SoundSettingFragment settingfragment;
    AlertSettingFragment alertfragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safetyflowsettings);

        SharedPreferences pref1 = this.getSharedPreferences("case1emailCheck", MODE_PRIVATE);
        final String emailtext = pref1.getString("case1emailID", null);
        TextView Block1 = (TextView) findViewById(R.id.safetystep1);
        TextView emailText55 = (TextView)findViewById(R.id.emailNotificationValue);
        LinearLayout l = (LinearLayout)findViewById(R.id.emailLayout);
        TextView Block2 = (TextView)findViewById(R.id.stepadd1);
        if(emailtext!=null){
            Block1.setVisibility(View.VISIBLE);
              emailText55.setText(emailtext);
                Block2.setVisibility(View.VISIBLE);
                l.setVisibility(View.VISIBLE);
        }

        SharedPreferences pref2 = this.getSharedPreferences("case2emailCheck", MODE_PRIVATE);
        final String emailtext1 = pref2.getString("case2emailID", null);
        LinearLayout l2 = (LinearLayout)findViewById(R.id.emailLayoutone);
        TextView emailText2 = (TextView)findViewById(R.id.emailNotificationValueone);
        TextView Block22 = (TextView)findViewById(R.id.stepaddone);
        TextView Block11 = (TextView) findViewById(R.id.safetystepone);
        if(emailtext1!=null){
            l2.setVisibility(View.VISIBLE);
            emailText2.setText(emailtext1);
            Block11.setVisibility(View.VISIBLE);
            Block22.setVisibility(View.VISIBLE);
        }

        SharedPreferences pref3 = this.getSharedPreferences("case3emailCheck", MODE_PRIVATE);
        final String emailtext3 = pref3.getString("case3emailID", null);
        LinearLayout l3 = (LinearLayout)findViewById(R.id.emailLayoutthree);
        TextView Block33 = (TextView)findViewById(R.id.stepadd33);
        TextView emailText43 = (TextView)findViewById(R.id.emailNotificationValuethree);
        TextView Block21 = (TextView) findViewById(R.id.safetystep3);
        if(emailtext3!=null){
            l3.setVisibility(View.VISIBLE);
            Block33.setVisibility(View.VISIBLE);
            Block21.setVisibility(View.VISIBLE);
            emailText43.setText(emailtext3);
        }

        SharedPreferences pref4 = this.getSharedPreferences("case4emailCheck", MODE_PRIVATE);
        final String emailtext4 = pref4.getString("case4emailID", null);
        LinearLayout l4 = (LinearLayout)findViewById(R.id.emailLayoutfour);
        TextView Block34 = (TextView)findViewById(R.id.stepadd44);
        TextView emailText44 = (TextView)findViewById(R.id.emailNotificationValuefour);
        TextView Block24 = (TextView) findViewById(R.id.safetystep4);
        if(emailtext4!=null){
            l4.setVisibility(View.VISIBLE);
            Block34.setVisibility(View.VISIBLE);
            Block24.setVisibility(View.VISIBLE);
            emailText44.setText(emailtext4);
        }

        SharedPreferences soundpref1 = this.getSharedPreferences("case1soundCheck", MODE_PRIVATE);
        final String soundtext1 = soundpref1.getString("case1sound_alarm", null);
        LinearLayout sl1 = (LinearLayout)findViewById(R.id.soundLayout);
        TextView b1 = (TextView)findViewById(R.id.stepadd2);
        TextView soundval1 = (TextView)findViewById(R.id.soundNotificationValue);
        TextView sblock1 = (TextView) findViewById(R.id.safetystep1);
        TextView bones = (TextView)findViewById(R.id.stepadd1);

        if(soundtext1!=null){
            sl1.setVisibility(View.VISIBLE);
            b1.setVisibility(View.VISIBLE);
            bones.setVisibility(View.VISIBLE);
            sblock1.setVisibility(View.VISIBLE);
            soundval1.setText(soundtext1);
        }

        SharedPreferences soundpref2 = this.getSharedPreferences("case2soundCheck", MODE_PRIVATE);
        final String soundtext2 = soundpref2.getString("case2sound_alarm", null);
        LinearLayout sl2 = (LinearLayout)findViewById(R.id.soundLayoutone);
        TextView b2 = (TextView)findViewById(R.id.stepaddtwo);
        TextView bone = (TextView)findViewById(R.id.stepaddone);
        TextView soundval2 = (TextView)findViewById(R.id.soundNotificationValueone);
        TextView sblock11 = (TextView) findViewById(R.id.safetystepone);
        if(soundtext2!=null){
            sl2.setVisibility(View.VISIBLE);
            b2.setVisibility(View.VISIBLE);
            sblock11.setVisibility(View.VISIBLE);
            soundval2.setText(soundtext2);
            bone.setVisibility(View.VISIBLE);
        }

        SharedPreferences soundpref3 = this.getSharedPreferences("case3soundCheck", MODE_PRIVATE);
        final String soundtext3 = soundpref3.getString("case2sound_alarm", null);
        LinearLayout sl3 = (LinearLayout)findViewById(R.id.soundLayoutthree);
        TextView b3 = (TextView)findViewById(R.id.stepadd31);
        TextView b33 = (TextView)findViewById(R.id.stepadd33);
        TextView soundval3 = (TextView)findViewById(R.id.soundNotificationValuethree);
        TextView sblock12 = (TextView) findViewById(R.id.safetystep3);
        if(soundtext3!=null){
            sl3.setVisibility(View.VISIBLE);
            b3.setVisibility(View.VISIBLE);
            b33.setVisibility(View.VISIBLE);
            sblock12.setVisibility(View.VISIBLE);
            soundval3.setText(soundtext3);
        }

        SharedPreferences soundpref4 = this.getSharedPreferences("case4soundCheck", MODE_PRIVATE);
        final String soundtext4 = soundpref4.getString("case4sound_alarm", null);
        LinearLayout sl4 = (LinearLayout)findViewById(R.id.soundLayoutfour);
        TextView b4 = (TextView)findViewById(R.id.stepadd41);

        TextView soundval4 = (TextView)findViewById(R.id.soundNotificationValuefour);
        TextView sblock14 = (TextView) findViewById(R.id.safetystep4);
        if(soundtext4!=null){
            sl4.setVisibility(View.VISIBLE);
            b4.setVisibility(View.VISIBLE);
            sblock14.setVisibility(View.VISIBLE);
            soundval4.setText(soundtext4);
        }
    }

    @Override
    public void onComplete(int safetycaseid) {
        if(safetycaseid==1){
            Button step1 = (Button)findViewById(R.id.safetystep1);
            Button step1add = (Button)findViewById(R.id.stepadd1);
            step1.setVisibility(View.VISIBLE);
            step1add.setVisibility(View.VISIBLE);
                dFragment.dismiss();
        }
        else if(safetycaseid==2){
            Button stepone = (Button)findViewById(R.id.safetystepone);
            Button step1add = (Button)findViewById(R.id.stepaddone);
            stepone.setVisibility(View.VISIBLE);
            step1add.setVisibility(View.VISIBLE);
            dFragment.dismiss();
        }

        else if(safetycaseid==3){
            Button step3 = (Button)findViewById(R.id.safetystep3);
            Button step3add = (Button)findViewById(R.id.stepadd33);
            step3.setVisibility(View.VISIBLE);
            step3add.setVisibility(View.VISIBLE);
            dFragment.dismiss();
        }

        else if(safetycaseid==4){
            Button step4 = (Button)findViewById(R.id.safetystep4);
            Button step4add = (Button)findViewById(R.id.stepadd44);
            step4.setVisibility(View.VISIBLE);
            step4add.setVisibility(View.VISIBLE);
            dFragment.dismiss();
        }
    }

    // Capturing results of sound volume selected
    @Override
    public void onSetVolume(int volumelevelposition,String safetycase) {
        SharedPreferences pref = getSharedPreferences(safetycase + "_volumeLevel", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if(volumelevelposition==1){
            editor.putString(safetycase + "_volume","20");
        }
        else if(volumelevelposition==2){
            editor.putString(safetycase +"_volume","40");
        }

        else if(volumelevelposition==3){
            editor.putString(safetycase + "_volume","60");
        }
        else if(volumelevelposition==4){
            editor.putString(safetycase + "_volume","80");
        }
        editor.commit();
    }

    // Capturing results of sound volume selected
    @Override
    public void onSelectAlarm(int alarmPosition,String safetycase) {
        SharedPreferences pref = getSharedPreferences(safetycase +"_alarmType", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if(alarmPosition==1){
            editor.putString(safetycase +"_alarmtype","one");
        }
        else if(alarmPosition==2){
            editor.putString(safetycase + "_alarmtype","two");
        }
        editor.commit();
    }

    @Override
    public void onSelect(int notificationType) {
        if(notificationType==1){
            Bundle bundle = new Bundle();
            bundle.putString("origin","safetycaseone");
            EmailFragment emailFragment = new EmailFragment();
            // Show DialogFragment
            emailFragment.show(fm,"");
            emailFragment.setArguments(bundle);
            selectNotificationFragment.dismiss();
        }
        else if(notificationType==2){
            settingfragment = new SoundSettingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("origin","safetycaseone");
            // Show DialogFragment
            settingfragment.show(fm,"SettingFragment");
            settingfragment.setArguments(bundle);
            selectNotificationFragment.dismiss();
        }

        else if(notificationType==3){
            alertfragment = new AlertSettingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("origin","safetycaseone");
            // Show DialogFragment
            alertfragment.show(fm,"SettingFragment");
            alertfragment.setArguments(bundle);
            selectNotificationFragment.dismiss();
        }
    }

    @Override
    public void onSelecttwo(int notificationType) {
        if(notificationType==1){
            Bundle bundle = new Bundle();
            bundle.putString("origin","safetycasetwo");
            EmailFragment emailFragment = new EmailFragment();
            // Show DialogFragment
            emailFragment.show(fm,"");
            emailFragment.setArguments(bundle);
            selectNotificationFragmenttwo.dismiss();

        }
        else if(notificationType==2){
            settingfragment = new SoundSettingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("origin","safetycasetwo");
            // Show DialogFragment
            settingfragment.show(fm,"SettingFragment");
            settingfragment.setArguments(bundle);
            selectNotificationFragmenttwo.dismiss();
        }

        else if(notificationType==3){
            alertfragment = new AlertSettingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("origin","safetycasetwo");
            // Show DialogFragment
            alertfragment.show(fm,"SettingFragment");
            alertfragment.setArguments(bundle);
            selectNotificationFragmenttwo.dismiss();
        }
    }

    @Override
    public void onSelectthree(int notificationType) {
        if(notificationType==1){
            Bundle bundle = new Bundle();
            bundle.putString("origin","safetycasethree");
            EmailFragment emailFragment = new EmailFragment();
            // Show DialogFragment
            emailFragment.show(fm,"");
            emailFragment.setArguments(bundle);
            selectNotificationFragmentThree.dismiss();
        }
        else if(notificationType==2){
            settingfragment = new SoundSettingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("origin","safetycasethree");
            // Show DialogFragment
            settingfragment.show(fm,"SettingFragment");
            settingfragment.setArguments(bundle);
            selectNotificationFragmentThree.dismiss();
        }

        else if(notificationType==3){
            alertfragment = new AlertSettingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("origin","safetycasethree");
            // Show DialogFragment
            alertfragment.show(fm,"SettingFragment");
            alertfragment.setArguments(bundle);
            selectNotificationFragmentThree.dismiss();
        }
    }

    @Override
    public void onSelectfour(int notificationType) {
        if(notificationType==1){
            Bundle bundle = new Bundle();
            bundle.putString("origin","safetycasefour");
            EmailFragment emailFragment = new EmailFragment();
            // Show DialogFragment
            emailFragment.show(fm,"");
            emailFragment.setArguments(bundle);
            selectNotificationFragmentFour.dismiss();
        }
        else if(notificationType==2){
            settingfragment = new SoundSettingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("origin","safetycasefour");
            // Show DialogFragment
            settingfragment.show(fm,"SettingFragment");
            settingfragment.setArguments(bundle);
            selectNotificationFragmentFour.dismiss();
        }

        else if(notificationType==3){
            alertfragment = new AlertSettingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("origin","safetycasefour");
            // Show DialogFragment
            alertfragment.show(fm,"SettingFragment");
            alertfragment.setArguments(bundle);
            selectNotificationFragmentFour.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                Intent i = new Intent(SafetyFlowSettingActivity.this, homeActivity.class);
                startActivity(i);
                break;

            case R.id.menu_back:
                onBackPressed();
        }
        return true;
    }

    // When User clicks on +(Add) Button
    public void addSteps(View v){
        Button viewsetting = (Button)findViewById(R.id.Add);
        viewsetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dFragment = new SafetyCaseListFragment();
                // Show DialogFragment
                dFragment.show(fm, "Dialog Fragment");
            }
        });
    }

    public void showstep3(View v){
        Bundle bundle = new Bundle();
        AlertSettingFragment alertfragment = new AlertSettingFragment();
        alertfragment.show(fm,"SettingFragment");
        bundle.putString("origin","safetycaseone");
        alertfragment.setArguments(bundle);
    }

    public void showstepthree(View v){
        Bundle bundle = new Bundle();
        AlertSettingFragment alertfragment = new AlertSettingFragment();
        alertfragment.show(fm,"SettingFragment");
        bundle.putString("origin","safetycasetwo");
        alertfragment.setArguments(bundle);
    }

    // to be called when safetycase one Add button is clicked
   public  void showEmailSettingOne(View v){
    Bundle bundle = new Bundle();
    bundle.putString("origin","safetycaseone");   // Sending safetycase information along through bundle
       selectNotificationFragment = new SelectNotificationFragment();
       selectNotificationFragment.show(fm,"");
       selectNotificationFragment.setArguments(bundle);
   }

    // to be called when safetycase two Add button is clicked
    public  void showEmailSettingTwo(View v){
        Bundle bundle = new Bundle();
        bundle.putString("origin","safetycasetwo");
        selectNotificationFragmenttwo = new SelectNotificationFragmentTwo();
        selectNotificationFragmenttwo.show(fm,"");
        selectNotificationFragmenttwo.setArguments(bundle);
    }

    // to be called when safetycase one Add button is clicked
    public  void showEmailSettingThree(View v){
        Bundle bundle = new Bundle();
        bundle.putString("origin","safetycasethree");   // Sending safetycase information along through bundle
        selectNotificationFragmentThree = new SelectNotificationFragmentThree();
        selectNotificationFragmentThree.show(fm,"");
        selectNotificationFragmentThree.setArguments(bundle);
    }

    // to be called when safetycase one Add button is clicked
    public  void showEmailSettingFour(View v){
        Bundle bundle = new Bundle();
        bundle.putString("origin","safetycasefour");   // Sending safetycase information along through bundle
        selectNotificationFragmentFour = new SelectNotificationFragmentFour();
        selectNotificationFragmentFour.show(fm,"");
        selectNotificationFragmentFour.setArguments(bundle);
    }



    public  void showsoundSetting(View v){
        settingfragment = new SoundSettingFragment();
        Bundle bundle = new Bundle();
        bundle.putString("origin","safetycaseone");
        // Show DialogFragment
        settingfragment.show(fm,"SettingFragment");
        settingfragment.setArguments(bundle);
    }

    public  void showsoundSettingTwo(View v){
        settingfragment = new SoundSettingFragment();
        Bundle bundle = new Bundle();
        bundle.putString("origin","safetycasetwo");
        // Show DialogFragment
        settingfragment.show(fm,"SettingFragment");
        settingfragment.setArguments(bundle);
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
}
