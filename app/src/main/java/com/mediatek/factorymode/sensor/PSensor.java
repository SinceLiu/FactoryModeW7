
package com.mediatek.factorymode.sensor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

import android.util.Log;

import java.io.FileOutputStream;

public class PSensor extends BaseTestActivity implements SensorEventListener, OnClickListener {
    /** Called when the activity is first created. */
    private SensorManager sensorManager;

    private Button mBtOk;

    private Button mFailed;

    private TextView mPsensor;

    public static final String LOG_TAG = "Sensor";

    private int[] mAllPsensor = new int[1000];

    private static int mCount = 0;

    private int mPrePsensor = 0;

    private int mAverage = 0;

    private char[] mWrint = new char[1];

    private int mSumPsensor = 0;

    private Handler myHandler;

    CountDownTimer mCountDownTimer;

    SharedPreferences mSp;
    FileOutputStream out ;
    private static final String DataAlspsPath = "//data//alsps.txt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.psensor);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mBtOk = (Button) findViewById(R.id.psensor_bt_ok);
        mBtOk.setOnClickListener(this);
        mFailed = (Button) findViewById(R.id.psensor_bt_failed);
        mFailed.setOnClickListener(this);
        mPsensor = (TextView) findViewById(R.id.proximity);
		if(false) {
			//System.out.println("Chronlog CENON_A789_PROJECT onCreate");
        	myHandler = new Handler();   //removed by Chron	
        	myHandler.post(myRunnable);   //removed by Chron
		}
    }

    protected void onDestroy() {
        super.onDestroy();
		if(false) {
			//System.out.println("Chronlog CENON_A789_PROJECT onDestroy");
        	myHandler.removeCallbacks(myRunnable);  //removed by Chron
		}
        try{   
            int i = 0;
            out = new FileOutputStream(DataAlspsPath);
            out.write((byte)i);
            out.close();
        }catch(Exception e){} 
    }

    public Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            File file = new File("/sys/bus/platform/drivers/als_ps/ps");
        Log.v(LOG_TAG, "psensor(als_ps): mPrePsensor=" + mPrePsensor );
            if(!file.exists()) {
                Log.v(LOG_TAG, "psensor(als_ps_cm3623): mPrePsensor=" + mPrePsensor );
                file = new File("/sys/bus/platform/drivers/als_ps_cm3623/ps");
            }
            if (file.exists()) {
                String pSensorValues2 = readFile(file);

              if(pSensorValues2.matches("^\\D.*")){  //cgj_test

                        mPrePsensor=0;
                        mAllPsensor[mCount] = mPrePsensor;
                        mCount++;
                        mPsensor.setText(getResources().getString(R.string.proximity) + " "
                        + "Error");
                }
                else{

                mPrePsensor = Integer.parseInt(pSensorValues2.trim(), 16); //bob.chen disabled
				
                mAllPsensor[mCount] = mPrePsensor;
                mCount++;
                mPsensor.setText(getResources().getString(R.string.proximity) + " "
                        + mPrePsensor);

                }
          
       



        Log.v(LOG_TAG, "psensor: mPrePsensor2=" + mPrePsensor );
            }
            for (int i = 0; i < mCount; i++) {
                mSumPsensor = mSumPsensor + mAllPsensor[i];
                mAllPsensor[i] = 0;
            }
            if (mCount > 0) {
                mAverage = mSumPsensor / mCount + 1;
                mWrint[0] = (char) mAverage;
            }
            mCount = 0;
            mSumPsensor = 0;
            myHandler.post(myRunnable);
        }
    };

    protected void onResume() {
        super.onResume();
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensors) {
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
        try{
            int i = 1;
            out = new FileOutputStream(DataAlspsPath);
            out.write((byte)i);
            out.close();
        }catch(Exception e){}
    }

    protected void onStop() {
        super.onStop();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        sensorManager.unregisterListener(this);
        try{           
            int i = 0;
            out = new FileOutputStream(DataAlspsPath);
            out.write((byte)i);
            out.close();
        }catch(Exception e){}
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private static String readFile(File fn) {
        FileReader f;
        int len;

        f = null;
        try {
            f = new FileReader(fn);
            String s = "";
            char[] cbuf = new char[200];
            while ((len = f.read(cbuf, 0, cbuf.length)) >= 0) {
                s += String.valueOf(cbuf, 0, len);
            }
            s = s.substring(2, s.length() - 1);  //ellery add
            return s;
        } catch (IOException ex) {
            return "0";
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (IOException ex) {
                    return "0";
                }
            }
        }
    }

    public void onSensorChanged(SensorEvent event) {
		//System.out.println("Chronlog onSensorChanged");
		//added by Chron for Distance Sensor 20140604 start 
		if(true) {
			//System.out.println("Chronlog CENON_A789_PROJECT onSensorChanged");
			float[] values = event.values;
			if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

				mPsensor.setText(getResources().getString(R.string.proximity) + 
					String.format(" %.1f", values[0]));
			}
		}
		//added by Chron for Distance Sensor 20140604 end
    }

    @Override
    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.psensor_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }
}
