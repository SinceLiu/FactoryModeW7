package com.mediatek.factorymode.sensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;

import android.content.SharedPreferences;

import com.mediatek.factorymode.R;

import android.view.View;

import com.mediatek.factorymode.Utils;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GyroscopeSensor extends BaseTestActivity implements SensorEventListener {
    /** Called when the activity is first created. */
	private TextView mSensorName;
	private TextView mSensorValue;
	private  Button mBtOk;
  private Button mBtFailed;
	// Gyroscope Sensor
	Sensor mGyroscopeSensor;
	SensorManager mSensorManager = null;
	SharedPreferences mSp;

	
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
		
        setContentView(R.layout.sensor_mould);
        
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        
        mSensorName = (TextView) findViewById(R.id.sensor_name);
        mSensorValue = (TextView) findViewById(R.id.sensor_value);
        
        mBtOk = (Button) findViewById(R.id.bt_ok);
        mBtOk.setOnClickListener(cl);
        mBtFailed = (Button) findViewById(R.id.bt_failed);
        mBtFailed.setOnClickListener(cl);
        
        mSensorName.setText(R.string.gyroscope_sensro_name);
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        
				// Gyroscope Sensor
				mGyroscopeSensor = mSensorManager
						.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
				registerSensor(mGyroscopeSensor);
   
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unRegisterSensor();
		super.onStop();
		
	}
	
	public View.OnClickListener cl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Utils.SetPreferences(getApplicationContext(), mSp, R.string.gyroscope_sensro_name,
                    (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
            finish();
        }
    };
    
	private void registerSensor(Sensor sensor) {
		if (mSensorManager != null) {
			mSensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	private void unRegisterSensor() {
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this);
		}
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		switch (event.sensor.getType()) {
		
		case Sensor.TYPE_GYROSCOPE:
			mSensorValue.setText("x: " + event.values[SensorManager.DATA_X]
					+ "\ny: " + event.values[SensorManager.DATA_Y] + "\nz: "
					+ event.values[SensorManager.DATA_Z]);
			break;

			default:
				break;
		}
	}

}