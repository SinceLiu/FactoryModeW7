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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class StepSensor extends BaseTestActivity implements SensorEventListener {
    /** Called when the activity is first created. */
    private TextView mSensorName;
    private TextView mSensorValue;
    private  Button mBtOk;
    private Button mBtFailed;
    private	String motionString="null";
    private	String stepString="null";
    private final static int SENSOR_TYPE_CONTEXT_MOTION = 85;

	// Step Sensor
    private Sensor mMotionSensor;
    private Sensor mStepDetector;
    private Sensor mStepCounter;

    private SensorManager mSensorManager = null;

    private SharedPreferences mSp;
	
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
        
        mSensorName.setText(R.string.step_sensor_name);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        
        // Step Sensor
        /*mMotionSensor = mSensorManager
            .getDefaultSensor(SENSOR_TYPE_CONTEXT_MOTION);
        registerSensor(mMotionSensor);
        mStepDetector = mSensorManager
            .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        registerSensor(mStepDetector);*/
        mStepCounter = mSensorManager
            .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        registerSensor(mStepCounter);
   
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
            Utils.SetPreferences(getApplicationContext(), mSp, R.string.step_sensor_name,
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
		
		case SENSOR_TYPE_CONTEXT_MOTION:
			switch ((int) event.values[0]) {
				case 1:
					motionString = "Unknown";
					break;
				case 2:
					motionString = "Stationary";
					break;
				case 3:
					motionString = "Not on Person";
					break;
				case 4:
					motionString = "Waking";
					break;
				case 5:
					motionString = "Running";
					break;
				case 6:
					motionString = "Jogging";
					break;
			}
			break;
		case Sensor.TYPE_STEP_DETECTOR:
			break;
		case Sensor.TYPE_STEP_COUNTER:
			stepString = event.values[0] + "";
			break;

			default:
				break;
		}
		
		/*mSensorValue.setText(getString(R.string.motion_type) + "\n" + motionString + "\n"
												+getString(R.string.step_number) + "\n" + stepString);*/
            mSensorValue.setText(getString(R.string.step_number) + "\n" + stepString);
	}

}