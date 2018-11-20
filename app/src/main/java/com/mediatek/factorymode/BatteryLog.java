
package com.mediatek.factorymode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.AdcCheckNative;

public class BatteryLog extends BaseTestActivity implements OnClickListener {

    private TextView mStatus;

    private TextView mLevel;

    private TextView mScale;

    private TextView mHealth;

    private TextView mVoltage;

    private TextView mTemperature;
    private TextView mCharging;  // ellery add for charging
    private TextView mTechnology;

    private TextView mUptime;
    private TextView adc;

    private Button mBtOK;

    private Button mBtFailed;

    private SharedPreferences mSp;

    private static final int EVENT_TICK = 1;

    String flag = "";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_TICK:
                    updateBatteryStats();
                    sendEmptyMessageDelayed(EVENT_TICK, 1000);
                    break;
            }
        }

        private void updateBatteryStats() {
            long uptime = SystemClock.elapsedRealtime();
            mUptime.setText(DateUtils.formatElapsedTime(uptime / 1000));
        }
    };

    private final String tenthsToFixedString(int x) {
        int tens = x / 10;
        return new String("" + tens + "." + (x - 10 * tens));
    }

    private IntentFilter mIntentFilter;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int plugType = arg1.getIntExtra("plugged", 0);

                mLevel.setText("" + arg1.getIntExtra("level", 0));
                mScale.setText("" + arg1.getIntExtra("scale", 0));
                mVoltage.setText("" + arg1.getIntExtra("voltage", 0) + " "
                        + getString(R.string.battery_info_voltage_units));
                mTemperature.setText(tenthsToFixedString(arg1.getIntExtra("temperature", 0))
                        + getString(R.string.battery_info_temperature_units));
                if(plugType == BatteryManager.BATTERY_PLUGGED_USB) {   // ellery add charging
                    mCharging.setText("500 mA");
                } else if(plugType == BatteryManager.BATTERY_PLUGGED_AC) {
                    mCharging.setText("750 mA");
                } else {
                    mCharging.setText("0 mA");
                    //mCharging.setText("" + tenthsToFixedString(20 * arg1.getIntExtra("temperature", 0))
                    //        + " mA");  // ellery add charging
                }
                mTechnology.setText("" + arg1.getStringExtra("technology"));

                int status = arg1.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                if(status == BatteryManager.BATTERY_STATUS_FULL) {   // ellery add charging
                    mCharging.setText("0 mA");
                }
                String statusString;
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        statusString = getString(R.string.battery_info_status_charging);
                        if (plugType > 0) {
                            statusString = statusString
                                    + " "
                                    + getString((plugType == BatteryManager.BATTERY_PLUGGED_AC) ? R.string.battery_info_status_charging_ac
                                            : R.string.battery_info_status_charging_usb);
                        }
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        statusString = getString(R.string.battery_info_status_discharging);
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        statusString = getString(R.string.battery_info_status_not_charging);
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        statusString = getString(R.string.battery_info_status_full);
                        break;
                    default:
                        statusString = getString(R.string.battery_info_status_unknown);
                        break;
                }
                mStatus.setText(statusString);

                int health = arg1.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN);
                String healthString;
                switch (health) {
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        healthString = getString(R.string.battery_info_health_good);
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        healthString = getString(R.string.battery_info_health_overheat);
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        healthString = getString(R.string.battery_info_health_dead);
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        healthString = getString(R.string.battery_info_health_over_voltage);
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        healthString = getString(R.string.battery_info_health_unspecified_failure);
                        break;
                    default:
                        healthString = getString(R.string.battery_info_health_unknown);
                        break;
                }
                mHealth.setText(healthString);
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	ActionBar.LayoutParams lp =new  ActionBar.LayoutParams(
        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        Gravity.CENTER);

	 View mView =  LayoutInflater.from(this).inflate(R.layout.title, new LinearLayout(this), false);
	 TextView mTextView = (TextView) mView.findViewById(R.id.action_bar_title);
	getActionBar().setCustomView(mView, lp); 
	
	mTextView.setText(getTitle());
	 
	getActionBar().setDisplayShowHomeEnabled(false);
	getActionBar().setDisplayShowTitleEnabled(false);
	getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	getActionBar().setDisplayShowCustomEnabled(true);
	getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
		
        setContentView(R.layout.battery_info);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
    }

    @Override
    public void onResume() {
        super.onResume();

        mStatus = (TextView) findViewById(R.id.status);
        mLevel = (TextView) findViewById(R.id.level);
        mScale = (TextView) findViewById(R.id.scale);
        mHealth = (TextView) findViewById(R.id.health);
        mTechnology = (TextView) findViewById(R.id.technology);
        mVoltage = (TextView) findViewById(R.id.voltage);
        mTemperature = (TextView) findViewById(R.id.temperature);
        mCharging = (TextView)findViewById(R.id.charging);  // ellery add for charging
        mUptime = (TextView) findViewById(R.id.uptime);
        adc = (TextView) findViewById(R.id.adc);
        mBtOK = (Button) findViewById(R.id.battery_bt_ok);
        mBtOK.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.battery_bt_failed);
        mBtFailed.setOnClickListener(this);

        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mHandler.sendEmptyMessageDelayed(EVENT_TICK, 1000);

        registerReceiver(mIntentReceiver, mIntentFilter);



        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);

        Log.e("", "ellery0706 onResume start");
        AdcCheckNative.openDev();
        Log.e("", "ellery0706 onResume open");
        byte[] buff = new byte[50];
        AdcCheckNative.SetAdcCheck(buff);
        Log.e("", "ellery0706 onResume value: " + new String(buff));
        adc.setText(new String(buff));
        AdcCheckNative.closeDev();
        Log.e("", "ellery0706 onResume start");
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(EVENT_TICK);
        // we are no longer on the screen stop the observers
        unregisterReceiver(mIntentReceiver);
    }

    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.battery_name,
                (v.getId() == mBtOK.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }

	/*
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, R.string.menu_exit);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(Activity.RESULT_FIRST_USER);
        finish();
        return true;
    }
	*/
}
