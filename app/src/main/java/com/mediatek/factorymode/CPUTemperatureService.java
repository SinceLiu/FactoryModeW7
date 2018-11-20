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

public class CPUTemperatureService extends Service {

	public static final String ACTION_STOP_SERVICE = 
			"com.mediatek.factorymode.CPUTemperatureService.stopService";
	private TextView tv_CPUTemperature;
	private boolean isStoped = false;
	
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
				Log.d("CPUTemperatureService", "action = "+action);
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
				}
			}
			
		};
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_STOP_SERVICE);
		intentFilter.addAction(Intent.ACTION_SHUTDOWN);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
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
			startService(new Intent(this, CPUTemperatureService.class));
		}
		unregisterStopReceiver();
		super.onDestroy();
	}
	
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		removeAlertView();
		if (!isStoped) {
			startService(new Intent(this, CPUTemperatureService.class));
		}
		unregisterStopReceiver();
        super.onTaskRemoved(rootIntent);
    }

	private CPUTemperatureUtil.FunctionThread mCPUTemperatureThread;
    private Handler mCPUTemperatureHandler = new Handler() {
    	@Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case CPUTemperatureUtil.EVENT_UPDATE:
                Bundle b = msg.getData();
                String info = b.getString("INFO");
                if (tv_CPUTemperature!=null) {
					SharedPreferences sp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
					boolean tag = sp.getBoolean("CurrentConsumptionMonitored", false);
					if(tag) {
						info = "\n\n"+info;
					}
                	tv_CPUTemperature.setText(info);
				}
                break;
            default:
                break;
            }
        }
    };
	
	private void addAlertView() {
		Log.d("cwj", "addAlertView");
        if (tv_CPUTemperature == null) {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams layoutParams = 
            		new WindowManager.LayoutParams();
            layoutParams.format = PixelFormat.TRANSLUCENT;
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|
            		WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
            		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.gravity = Gravity.TOP;
            
            tv_CPUTemperature = new TextView(this);
            tv_CPUTemperature.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 36);
            
            tv_CPUTemperature.setTextColor(0xffff0000);
            android.view.ViewGroup.LayoutParams lp = 
            		new android.view.ViewGroup.LayoutParams(
            				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 
            				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            tv_CPUTemperature.setLayoutParams(lp);
            
            windowManager.addView(tv_CPUTemperature, layoutParams);
            
            mCPUTemperatureThread = new CPUTemperatureUtil.FunctionThread(
            		mCPUTemperatureHandler);
            mCPUTemperatureThread.setRunTag(true);
            mCPUTemperatureThread.start();
        }
    }

    private void removeAlertView() {
		Log.d("cwj", "removeAlertView isStoped:"+isStoped);
    	if (mCPUTemperatureThread!=null) {
        	mCPUTemperatureThread.setRunTag(false);
		}
        if (tv_CPUTemperature == null) {
            return;
        }
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(tv_CPUTemperature);
        tv_CPUTemperature = null;
    }
    
    private void setTag(boolean tag){
    	SharedPreferences sp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
    	Editor editor = sp.edit();
        editor.putBoolean("CPUTemperatureMonitored", tag);
        editor.commit();
    }
}
