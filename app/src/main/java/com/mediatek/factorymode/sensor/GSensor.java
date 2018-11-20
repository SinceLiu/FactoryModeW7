
package com.mediatek.factorymode.sensor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

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

public class GSensor extends BaseTestActivity implements OnClickListener {
    private ImageView ivimg;

    private Button mBtOk;

    private Button mBtFailed;

    SharedPreferences mSp;

    SensorManager mSm = null;

    Sensor mGravitySensor;

    private final static int OFFSET = 2;

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

        setContentView(R.layout.gsensor);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        ivimg = (ImageView) findViewById(R.id.gsensor_iv_img);
        mBtOk = (Button) findViewById(R.id.gsensor_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.gsensor_bt_failed);
        mBtFailed.setOnClickListener(this);
        mSm = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGravitySensor = mSm.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER); //TYPE_GRAVITY  //ellery modify
        mSm.registerListener(lsn, mGravitySensor, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onDestroy() {
        mSm.unregisterListener(lsn);
        super.onDestroy();
    }

    SensorEventListener lsn = new SensorEventListener() {
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent e) {
            if (e.sensor == mGravitySensor) {
                int x = (int) e.values[SensorManager.DATA_X];
                int y = (int) e.values[SensorManager.DATA_Y];
                int z = (int) e.values[SensorManager.DATA_Z];

                int absx = Math.abs(x);
                int absy = Math.abs(y);
                int absz = Math.abs(z);

                if(absx > absy && absx + 7 > absz) {
                    if(x > OFFSET) {
                        ivimg.setBackgroundResource(R.drawable.gsensor_x);
                    } else if(x < -OFFSET) {
                        ivimg.setBackgroundResource(R.drawable.gsensor_x_2);
                    }
                } else if(absy > absx && absy + 7 > absz) {
                    if(y > OFFSET) {
                        ivimg.setBackgroundResource(R.drawable.gsensor_y);
                    } else if(y < -OFFSET) {
                        ivimg.setBackgroundResource(R.drawable.gsensor_y_2);
                    }
                } else if(absz > absx && absz > absy) {
                    if(z > OFFSET) {
                        ivimg.setBackgroundResource(R.drawable.gsensor_z);
                    } else if(z < -OFFSET) {
                        ivimg.setBackgroundResource(R.drawable.gsensor_z_2);
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.gsensor_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }
}
