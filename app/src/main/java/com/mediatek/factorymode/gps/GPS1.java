
package com.mediatek.factorymode.gps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

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

public class GPS1 extends BaseTestActivity implements OnClickListener {

    private TextView mStView;

    private TextView mSatelliteNumView;

    private TextView mSignalView;

    private Chronometer mTimeView;

    private TextView mResultView;

    private Button mBtOk;

    private Button mBtFailed;

    private SharedPreferences mSp;

    private GpsUtil mGpsUtil;

    @Override
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

        setContentView(R.layout.gps1);
        mGpsUtil = new GpsUtil(this);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mStView = (TextView) findViewById(R.id.gps_state_id);
        mSatelliteNumView = (TextView) findViewById(R.id.gps_satellite_id);
        mSignalView = (TextView) findViewById(R.id.gps_signal_id);
        mResultView = (TextView) findViewById(R.id.gps_result_id);
        mTimeView = (Chronometer) findViewById(R.id.gps_time_id);
        mBtOk = (Button) findViewById(R.id.gps_bt_ok);
        mBtFailed = (Button) findViewById(R.id.gps_bt_failed);
        mBtOk.setOnClickListener(this);
        mBtOk.setEnabled(false);
        mBtFailed.setOnClickListener(this);
        mTimeView.setFormat(getResources().getString(R.string.GPS_time));
        mStView.setText(R.string.GPS_connect);
        mTimeView.start();
        getSatelliteInfo();
    }

    private int count = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    count++;
                    if(count > 60) { // failed
                        getSatelliteErrorInfo();
                    } else { // continue
                        getSatelliteInfo();
                    }
                    break;
            }
        }
    };

    protected void onDestroy() {
        mGpsUtil.closeLocation();
        super.onDestroy();
    }

    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.gps1_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }

    public void getSatelliteInfo() {
        int num35 = mGpsUtil.getSatelliteSNR35Number();
        int num = num35;
        if (num35 >= 2) {
            mHandler.removeMessages(0);
            mTimeView.stop();
            mResultView.setText("S35: " + getResources().getString(R.string.GPS_Success));
            mBtOk.setEnabled(true);
        } else {
            mHandler.sendEmptyMessageDelayed(0, 2000);
            mBtOk.setEnabled(false);
        }
        mSatelliteNumView.setText(getString(R.string.GPS_satelliteNum) + num);
        mSignalView.setText(getString(R.string.GPS_Signal)
                + mGpsUtil.getSatelliteSignals());
    }

    public void getSatelliteErrorInfo() {
        int num35 = mGpsUtil.getSatelliteSNR35Number();
        int num = num35;
        if (num35 >= 2) {
            mHandler.removeMessages(0);
            mResultView.setText("S35: " + getResources().getString(R.string.GPS_Success));
        } else {
            mResultView.setText("S35: " + getResources().getString(R.string.GPS_Fail));
        }
        mTimeView.stop();
        mSatelliteNumView.setText(getString(R.string.GPS_satelliteNum) + num);
        mSignalView.setText(getString(R.string.GPS_Signal)
                + mGpsUtil.getSatelliteSignals());
        mBtOk.setEnabled(false);
    }
}
