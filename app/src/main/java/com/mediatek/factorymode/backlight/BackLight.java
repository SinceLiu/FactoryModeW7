
package com.mediatek.factorymode.backlight;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.ShellExe;
import com.mediatek.factorymode.Utils;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

public class BackLight extends BaseTestActivity implements OnClickListener {

    private Button mBtnLcdON;

    private Button mBtnLcdOFF;

    private Button mBtOk;

    private Button mBtFailed;

    private SharedPreferences mSp;

    private String lcdCmdON = "echo 255 >  /sys/class/leds/lcd-backlight/brightness";

    private String lcdCmdOFF = "echo 30 >  /sys/class/leds/lcd-backlight/brightness";


    private int BRIGHTNESSLEVEL_MIN_VALUE =0;
    private int BRIGHTNESSLEVEL_MAX_VALUE =5;
    private PowerManager mPowerManager;
    private int mMinBrightness;
    private int mMaxBrightness;
    private int mInternalValue;

    private final int ERR_OK = 0;

    private final int ERR_ERR = 1;
    private int  mOriginBrightness = -1;
    private int mNowBrightness = mOriginBrightness;

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

        mPowerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mMinBrightness = mPowerManager.getMinimumScreenBrightnessSetting();
        mMaxBrightness = mPowerManager.getMaximumScreenBrightnessSetting();
        mInternalValue = (mMaxBrightness -mMinBrightness) / BRIGHTNESSLEVEL_MAX_VALUE;

        setContentView(R.layout.backlight);

        mBtnLcdON = (Button) findViewById(R.id.Display_lcd_on);
        mBtnLcdOFF = (Button) findViewById(R.id.Display_lcd_off);
        mBtOk = (Button) findViewById(R.id.display_bt_ok);
        mBtFailed = (Button) findViewById(R.id.display_bt_failed);

        mBtnLcdON.setOnClickListener(this);
        mBtnLcdOFF.setOnClickListener(this);
        mBtOk.setOnClickListener(this);
        mBtFailed.setOnClickListener(this);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mOriginBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 20);
        mNowBrightness = mOriginBrightness;
    }

    private void setLastError(int err) {
        System.out.print(err);
    }

   private void SetBrightnessValue(int level){

        int setbrightness = mMinBrightness + level * mInternalValue;
        mNowBrightness = setbrightness;
        
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, setbrightness);
    }

    public void onClick(View arg0) {
     //   try {

            if (arg0.getId() == mBtnLcdON.getId()) {
               /** String[] cmd = {
                        "/system/bin/sh", "-c", lcdCmdON
                };
                int ret = ShellExe.execCommandOnServer(cmd);
                if (0 == ret) {
                    setLastError(ERR_OK);
                } else {
                    setLastError(ERR_ERR);
                }**/
                SetBrightnessValue(5);

            } else if (arg0.getId() == mBtnLcdOFF.getId()) {
               /** String[] cmd = {
                        "/system/bin/sh", "-c", lcdCmdOFF
                };
                int ret = ShellExe.execCommandOnServer(cmd);
                if (0 == ret) {
                    setLastError(ERR_OK);
                } else {
                    setLastError(ERR_ERR);
                }**/
                SetBrightnessValue(0);

            } else if (arg0.getId() == mBtOk.getId()) {
                Utils.SetPreferences(this, mSp, R.string.backlight_name, AppDefine.FT_SUCCESS);
                finish();
            } else if (arg0.getId() == mBtFailed.getId()) {
                Utils.SetPreferences(this, mSp, R.string.backlight_name, AppDefine.FT_FAILED);
                finish();
            }
      /**  } catch (IOException e) {
            setLastError(ERR_ERR);
        }**/
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        if(mOriginBrightness != -1) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, mOriginBrightness);
        }
    }*/
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	if (mNowBrightness!=mOriginBrightness&&mNowBrightness!=-1) {
    		Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, mNowBrightness);
		}
    }

	@Override
	protected void onPause() {
		super.onPause();
		if(mOriginBrightness != -1) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, mOriginBrightness);
        }
	}
}
