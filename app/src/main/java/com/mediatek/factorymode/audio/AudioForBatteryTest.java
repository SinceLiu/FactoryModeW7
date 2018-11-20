
package com.mediatek.factorymode.audio;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AudioForBatteryTest extends BaseTestActivity implements OnClickListener {
    private SharedPreferences mSp;

    private MediaPlayer mPlayer;

    private Button mBtOk;

    private Button mBtFailed;

    AudioManager audioManager;

    private int oldMode;
    private int oldVolume;
    
    private int level = -1;
    
    private boolean testEnd = false;
    private int startLevel = -1;
    private int endLevel = -1;
    private int maxPassLevel = 8;
    
    private long testTimeLong = 1*60*60*1000;
    private long start = -1;
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			mHandler.sendEmptyMessage(UPDATE_TIME);
		}
	};
	private final int UPDATE_TIME = 0x0;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_TIME:
				long current = System.currentTimeMillis();
				if (start==-1) {
					start = current;
				}
				long remainingTime = testTimeLong-(current-start);
				if (remainingTime<0) {
					remainingTime=0;
				}
				TextView tv_0 = (TextView)findViewById(R.id.tv_0);
				tv_0.setText(""
						+ ((remainingTime/(60*60*1000))%24) + ":"
						+ ((remainingTime/(60*1000))%60) + ":"
						+ ((remainingTime/1000)%60));
				if (remainingTime==0) {
					if (timer!=null) {
						timer.cancel();
						timer=null;
					}
					stopMediaPlayer();
					
					TextView tv = (TextView)findViewById(R.id.tv_2);
	            	String tag = "结束:";
	            	
	            	testEnd = true;
	            	boolean result = startLevel - endLevel <= maxPassLevel;
	            	Utils.SetPreferences(AudioForBatteryTest.this, 
	            			mSp, R.string.battery_used_name,
	            			result ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
	            	String tail = result ? "成功":"失败";
	            	tv.setText(tag+level+" "+tail);
	            	tv.setTextColor(result?Color.BLUE:Color.RED);
	            	
	                mBtOk.setText("结束");
	                mBtFailed.setEnabled(false);
				}
				break;

			default:
				break;
			}
		};
	};
    
    private IntentFilter mIntentFilter;
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
            	TextView tv;
            	String tag = "";
            	if (level == -1) {
            		tv = (TextView)findViewById(R.id.tv_1);
            		tag = "开始:";
					startLevel = arg1.getIntExtra("level", 0);
				} else {
            		tv = (TextView)findViewById(R.id.tv_3);
            		tag = "现在:";
				}
            	level = arg1.getIntExtra("level", 0);
				endLevel = level;
            	tv.setText(tag+level);
            }

        }
    };

    
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
		
        setContentView(R.layout.audio_and_battery_test);
        mBtOk = (Button) findViewById(R.id.audio_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.audio_bt_failed);
        mBtFailed.setOnClickListener(this);

        audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        oldMode = audioManager.getRingerMode();
        oldVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_PLAY_SOUND);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        initMediaPlayer();
        

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        
        timer.schedule(task, 0, 500);
        registerReceiver(mIntentReceiver, mIntentFilter);
    }

    protected void onResume() {
        super.onResume();
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mBtOk = (Button) findViewById(R.id.audio_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.audio_bt_failed);
        mBtFailed.setOnClickListener(this);

        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_PLAY_SOUND);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();

        audioManager.setRingerMode(oldMode);
    	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 
    			oldVolume,
                AudioManager.FLAG_PLAY_SOUND);
    }

    @Override
    public void finish() {

		if (timer!=null) {
			timer.cancel();
			timer=null;
		}
    	stopMediaPlayer();
        unregisterReceiver(mIntentReceiver);
        
    	super.finish();
    }
    
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initMediaPlayer() {
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tada);
        mPlayer.setLooping(true);
        mPlayer.start();
    }
    
    private void stopMediaPlayer() {
        if (mPlayer!=null) {
        	if (mPlayer.isPlaying()) {
                mPlayer.stop();
			}
        	mPlayer.release();
        	mPlayer = null;
		}
    }

    public void onClick(View v) {
    	if (!testEnd) {
            Utils.SetPreferences(this, mSp, R.string.battery_used_name,
                    (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
		}
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE , AudioManager.FLAG_PLAY_SOUND);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER , AudioManager.FLAG_PLAY_SOUND);
                break;
            default:
                break;  
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return true;
		//return super.onKeyDown(keyCode, event);
    }
}
