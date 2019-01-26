
package com.mediatek.factorymode.sensor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.mediatek.factorymode.AllTest;
import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

public class GSensorWithCalibrate extends BaseTestActivity implements OnClickListener {
    private ImageView ivimg;
    private TextView tvSensorX;
    private TextView tvSensorY;
    private TextView tvSensorZ;
    private TextView tvSensorResult;
    private Button btnSensorCalicate;
    private Button btnSensorReTest;

    private Button mBtOk;
    private Button mBtFailed;

    SharedPreferences mSp;
    SensorManager mSm = null;

    Sensor mGravitySensor;

    private final static int OFFSET = 2;

    private static final int MSG_GSENSOR_UPDATING = 0x11;
    private static final int MSG_GSENSOR_RESULT = 0x12;
    private static final int MSG_GSENSOR_RETEST = 0x13;
    private static final int MSG_DO_CALIBRATION_20 = 0x14;
    private static final int MSG_DO_CALIBRATING = 0x15;
    private static final int MSG_CALIBRATION_SUCCESS = 0x16;
    private static final int MSG_CALIBRATION_FAIL = 0x17;
    private int count = 0;
    private boolean result = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_GSENSOR_UPDATING:
                    tvSensorResult.setText(getResources().getString(R.string.GSensor_with_calibrate_result) 
                        + (count%3==0?".":(count%3==1?"..":(count%3==2?"...":""))));

                    if(count <10) {
                        mHandler.sendEmptyMessageDelayed(MSG_GSENSOR_UPDATING, 200);
                        count++;
                    } else {
                        count = 0;
                        mHandler.sendEmptyMessageDelayed(MSG_GSENSOR_RESULT, 200);
                    }
                    break;
                case MSG_GSENSOR_RESULT:
                    if(result) {
                        tvSensorResult.setText(R.string.Success);
                        btnSensorCalicate.setVisibility(View.VISIBLE);
                        mBtOk.setEnabled(true);
                        if(AllTest.begin_auto_test){
                            Utils.SetPreferences(GSensorWithCalibrate.this, mSp, R.string.gsensor_with_calibrate_name, AppDefine.FT_SUCCESS);
                            finish();
                        }
                    } else {
                        tvSensorResult.setText(R.string.Failed);
                        btnSensorCalicate.setVisibility(View.GONE);
                        mBtOk.setEnabled(false);
                    }
                    count = 0;
                    break;
                case MSG_GSENSOR_RETEST:
                    mHandler.sendEmptyMessageDelayed(MSG_GSENSOR_UPDATING, 20);
                    btnSensorReTest.setVisibility(View.GONE);
                    count = 0;
                    break;
                case MSG_DO_CALIBRATION_20:
                    btnSensorCalicate.setEnabled(false);
                    mHandler.sendEmptyMessageDelayed(MSG_DO_CALIBRATING, 20);
                    count = 0;
                    break;
                case MSG_DO_CALIBRATING:
                    tvSensorResult.setText(getResources().getString(R.string.GSensor_with_calibrate_do_calibrate)
                        + (count%3==0?".":(count%3==1?"..":(count%3==2?"...":""))));

                    if(count <50) {
                        mHandler.sendEmptyMessageDelayed(MSG_DO_CALIBRATING, 200);
                        count++;
                    } else {
                        count = 0;
                        mHandler.sendEmptyMessageDelayed(MSG_CALIBRATION_FAIL, 200);
                    }
                    break;
                case MSG_CALIBRATION_SUCCESS:
                    tvSensorResult.setText(R.string.proximity_success);
                    mHandler.removeMessages(MSG_DO_CALIBRATING);
                    count = 0;
                    btnSensorCalicate.setEnabled(true);
                    btnSensorCalicate.setVisibility(View.GONE);
                    btnSensorReTest.setVisibility(View.VISIBLE);
                    break;
                case MSG_CALIBRATION_FAIL:
                    tvSensorResult.setText(R.string.proximity_fail);
                    mHandler.removeMessages(MSG_DO_CALIBRATING);
                    count = 0;
                    btnSensorCalicate.setEnabled(true);
                    btnSensorCalicate.setVisibility(View.VISIBLE);
                    btnSensorReTest.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if("com.mediatek.factorymode.action_calibration_result".equals(action)) {
                boolean result = intent.getBooleanExtra("result", false);
                if(result) {
                    // success
                    mHandler.sendEmptyMessage(MSG_CALIBRATION_SUCCESS);
                } else {
                    // failed
                    mHandler.sendEmptyMessage(MSG_CALIBRATION_FAIL);
                }
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

        setContentView(R.layout.gsensor_with_calibrate);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        ivimg = (ImageView) findViewById(R.id.gsensor_iv_img);
        tvSensorX = (TextView) findViewById(R.id.gsensor_tv_x);
        tvSensorY = (TextView) findViewById(R.id.gsensor_tv_y);
        tvSensorZ = (TextView) findViewById(R.id.gsensor_tv_z);
        tvSensorResult = (TextView) findViewById(R.id.gsensor_tv_result);
        mHandler.sendEmptyMessageDelayed(MSG_GSENSOR_UPDATING, 200);
        btnSensorCalicate = (Button) findViewById(R.id.gsensor_btn_calicate);
        btnSensorCalicate.setOnClickListener(this);
        btnSensorReTest = (Button) findViewById(R.id.gsensor_btn_retest);
        btnSensorReTest.setOnClickListener(this);
        btnSensorReTest.setVisibility(View.GONE);
        mBtOk = (Button) findViewById(R.id.gsensor_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtOk.setEnabled(false);
        mBtFailed = (Button) findViewById(R.id.gsensor_bt_failed);
        mBtFailed.setOnClickListener(this);
        mSm = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGravitySensor = mSm.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER); //TYPE_GRAVITY  //ellery modify
        mSm.registerListener(lsn, mGravitySensor, SensorManager.SENSOR_DELAY_GAME);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.mediatek.factorymode.action_calibration_result");
        registerReceiver(mReceiver, intentFilter);
    }

    protected void onDestroy() {
        mSm.unregisterListener(lsn);
        unregisterReceiver(mReceiver);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    SensorEventListener lsn = new SensorEventListener() {
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent e) {
            float xx = e.values[SensorManager.DATA_X];
            float yy = e.values[SensorManager.DATA_Y];
            float zz = e.values[SensorManager.DATA_Z];
            tvSensorX.setText("X: " + ((xx >= 0.0)?"+":"") + xx);
            tvSensorY.setText("Y: " + ((yy >= 0.0)?"+":"") + yy);
            tvSensorZ.setText("Z: " + ((zz >= 0.0)?"+":"") + zz);

            if (e.sensor == mGravitySensor) {
                float x = (float) e.values[SensorManager.DATA_X];
                float y = (float) e.values[SensorManager.DATA_Y];
                float z = (float) e.values[SensorManager.DATA_Z];

                float absx = Math.abs(x);
                float absy = Math.abs(y);
                float absz = Math.abs(z);

                boolean zzz = (absz >= 7.0 && absz <= 13.0);
                if(SystemProperties.get("ro.cenon_factorymode_feature").equals("1")) {
                    zzz = (absz >= 5.0 && absz <= 15.0);
                }
                if((absx <= 3.0 && absx >= -3.0) && (absy <= 3.0 && absy >= -3.0) && zzz) {
                    result = true;
                } else {
                    result = false;
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.gsensor_bt_ok:
                Utils.SetPreferences(this, mSp, R.string.gsensor_with_calibrate_name, AppDefine.FT_SUCCESS);
                finish();
                break;
            case R.id.gsensor_bt_failed:
                Utils.SetPreferences(this, mSp, R.string.gsensor_with_calibrate_name, AppDefine.FT_FAILED);
                finish();
                break;
            case R.id.gsensor_btn_calicate:
                mHandler.sendEmptyMessage(MSG_DO_CALIBRATION_20);
                Intent intent = new Intent();
                intent.setAction("com.mediatek.engineermode.action_sensor_emsensor");
                sendBroadcast(intent);
                break;
            case R.id.gsensor_btn_retest:
                mHandler.sendEmptyMessageDelayed(MSG_GSENSOR_RETEST, 200);
                break;
        }
    }
}
