package com.mediatek.factorymode.backtouch;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.ShellExe;
import com.mediatek.factorymode.Utils;

public class BackTouch extends BaseTestActivity implements OnClickListener {

    private TextView mStatus;
	private TextView mrawdataStatus;
	private TextView mdeltaStatus;
    private Button mBtOK;
    private Button mBtFailed;
    private SharedPreferences mSp;
    private static final int EVENT_TICK = 1;
    private static final String TAG = "OtgState";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case EVENT_TICK:
                updateBackTouchStates();
                sendEmptyMessageDelayed(EVENT_TICK, 1000);
                break;
            }
        }
    };

    private String getBackTouchRawdataState() {
        String[] cmdx = { "/system/bin/sh", "-c",
                "cat sys/bus/i2c/drivers/AW9163_ts/0-002c/rawdata" }; // file
        int ret = 0;
        try {
            ret = ShellExe.execCommand(cmdx);
            if (0 == ret) {
                //Toast.makeText(this, "ok", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "failed!", Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return ShellExe.getOutput();
    }

	private String getBackTouchDeltaState() {
        String[] cmdx = { "/system/bin/sh", "-c",
                "cat sys/bus/i2c/drivers/AW9163_ts/0-002c/delta" }; // file
        int ret = 0;
        try {
            ret = ShellExe.execCommand(cmdx);
            if (0 == ret) {
                //Toast.makeText(this, "ok", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "failed!", Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
        return ShellExe.getOutput();
    }	
    private int[]  getdata(String string) 
		{
		     int data[] =new int [6];
		     String ss[]=new String [6];
				 int i;
		      ss=string.split(",");     
			for(i=0;i<6;i++){
				data[i]=Integer.parseInt(ss[i].trim());
				}

			return data;
			
    	}
    private void updateBackTouchStates() {
        int temp[]=new  int [6] ;
	    boolean  bakctouchstateerror=false;
		int i;
        if((getBackTouchRawdataState() == null) || (getBackTouchRawdataState() == null)){
            mStatus.setText(R.string.back_touch_status_failed);
            mrawdataStatus.setText("base:00,00,00,00,00,00");
            mdeltaStatus.setText("delta:00,00,00,00,00,00");
            return;
        }
        String  string = getBackTouchRawdataState().replace("base:","");

        temp=getdata(string);
        for(i=0;i<6;i++){
            if((temp[i]<500)||(temp[i]>3500)){
                bakctouchstateerror=true;
                break;
            }
        }

        int temp1[]=new  int [6] ;
        boolean  bakctouchstateerror1=false;			
        String  string1 =	getBackTouchDeltaState().replace("delta:","");

        temp1=getdata(string1);
        for(i=0;i<6;i++){
            if((temp1[i]<30)){
                bakctouchstateerror1=true;
                break;
            }
        }
        if(bakctouchstateerror||bakctouchstateerror1) {
            mStatus.setText(R.string.back_touch_status_failed);
        } else {
            mStatus.setText(R.string.back_touch_status_ok);
        }
		mrawdataStatus.setText(getBackTouchRawdataState());		
		mdeltaStatus.setText(	getBackTouchDeltaState());	
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.back_touch_info);
    }

    @Override
    public void onResume() {
        super.onResume();

        mStatus = (TextView) findViewById(R.id.status);
		mrawdataStatus=(TextView) findViewById(R.id.rawdatastatus);;
	    mdeltaStatus=(TextView) findViewById(R.id.deltastatus);;

        mBtOK = (Button) findViewById(R.id.otg_state_ok);
        mBtOK.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.otg_state_failed);
        mBtFailed.setOnClickListener(this);

        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mHandler.sendEmptyMessageDelayed(EVENT_TICK, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(EVENT_TICK);
    }

    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.backtouch_name,
                (v.getId() == mBtOK.getId()) ? AppDefine.FT_SUCCESS
                        : AppDefine.FT_FAILED);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, R.string.menu_exit);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        setResult(Activity.RESULT_FIRST_USER);
        finish();
        return true;
    }
}
