package com.mediatek.factorymode;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class CurrentConsumptionService extends Service {

	public static final String ACTION_STOP_SERVICE = 
			"com.mediatek.factorymode.CurrentConsumptionService.stopService";
	private TextView tv_CurrentConsumption;
	private boolean isStoped = false;
	
	private Intent mBatteryIntent = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private BroadcastReceiver stopReceiver;
	private void registerStopReceiver() {
		unregisterStopReceiver();
		stopReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.d("CurrentConsumptionService", "action = "+action);
				if (ACTION_STOP_SERVICE.equals(action)
						||Intent.ACTION_SHUTDOWN.equals(action)) {
					isStoped = true;
					setTag(false);
					removeAlertView();
					stopSelf();
				} else if (Intent.ACTION_SCREEN_ON.equals(action)) {
					addAlertView();
				} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
					removeAlertView();
				} else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
					mBatteryIntent = intent;
				}
			}
			
		};
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_STOP_SERVICE);
		intentFilter.addAction(Intent.ACTION_SHUTDOWN);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		
		registerReceiver(stopReceiver, intentFilter);
	}
	private void unregisterStopReceiver() {
		if (stopReceiver!=null) {
			unregisterReceiver(stopReceiver);
			stopReceiver = null;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (isStoped) {
			stopSelf();
			return Service.START_NOT_STICKY;
		} else {
			registerStopReceiver();
			setTag(true);
			addAlertView();
			return Service.START_STICKY;
		}
	}
	
	@Override
	public void onDestroy() {
		removeAlertView();
		if (!isStoped) {
			startService(new Intent(this, CurrentConsumptionService.class));
		}
		unregisterStopReceiver();
		super.onDestroy();
	}
	
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		removeAlertView();
		if (!isStoped) {
			startService(new Intent(this, CurrentConsumptionService.class));
		}
		unregisterStopReceiver();
        super.onTaskRemoved(rootIntent);
    }

	private BatteryUtil.FunctionThread mCurrentConsumptionThread;
    private Handler mCurrentConsumptionHandler = new Handler() {
    	@Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case BatteryUtil.EVENT_UPDATE:
                Bundle b = msg.getData();
                String info = b.getString("INFO");
                
                if (mBatteryIntent!=null) {
                	info += "\n" 
                			+ tenthsToFixedString(mBatteryIntent.getIntExtra("temperature", 0))
                            + getString(R.string.battery_info_temperature_units);
				}
                
                if (tv_CurrentConsumption!=null) {
                	tv_CurrentConsumption.setText(info);
				}
                break;
            default:
                break;
            }
        }
    };

    private final String tenthsToFixedString(int x) {
        int tens = x / 10;
        return new String("" + tens + "." + (x - 10 * tens));
    }

	private void addAlertView() {
		Log.d("cwj", "addAlertView");
        if (tv_CurrentConsumption == null) {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams layoutParams = 
            		new WindowManager.LayoutParams();
            layoutParams.format = PixelFormat.TRANSLUCENT;
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|
            		WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
            		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.gravity = Gravity.TOP;
            
            tv_CurrentConsumption = new TextView(this);
            tv_CurrentConsumption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 36);
            
//    		tv_CurrentConsumption.setBackgroundColor(0x88222222);
            tv_CurrentConsumption.setTextColor(0xffff0000);
            android.view.ViewGroup.LayoutParams lp = 
            		new android.view.ViewGroup.LayoutParams(
            				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 
            				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            tv_CurrentConsumption.setLayoutParams(lp);
            
            windowManager.addView(tv_CurrentConsumption, layoutParams);
            
            mCurrentConsumptionThread = new BatteryUtil.FunctionThread(
            		mCurrentConsumptionHandler);
            mCurrentConsumptionThread.setRunTag(true);
            mCurrentConsumptionThread.start();
        }
    }

    private void removeAlertView() {
		Log.d("cwj", "removeAlertView isStoped:"+isStoped);
    	if (mCurrentConsumptionThread!=null) {
        	mCurrentConsumptionThread.setRunTag(false);
		}
        if (tv_CurrentConsumption == null) {
            return;
        }
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(tv_CurrentConsumption);
        tv_CurrentConsumption = null;
    }
    
    private void setTag(boolean tag){
    	SharedPreferences sp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
    	Editor editor = sp.edit();
        editor.putBoolean("CurrentConsumptionMonitored", tag);
        editor.commit();
    }
}
