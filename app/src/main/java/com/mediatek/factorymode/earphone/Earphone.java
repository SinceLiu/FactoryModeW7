
package com.mediatek.factorymode.earphone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
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
import android.widget.LinearLayout;
import android.widget.TextView;

public class Earphone extends BaseTestActivity implements OnClickListener, OnKeyListener {
    private SharedPreferences mSp;

    private MediaPlayer mPlayer;

    private Button mBtOk;

    private Button mBtFailed;

    private AudioManager mAudioManager;

    private int oldMode;
    private int oldVolume;
    
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
		
        setContentView(R.layout.audio_test);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mBtOk = (Button) findViewById(R.id.audio_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.audio_bt_failed);
        mBtFailed.setOnClickListener(this);
        
        mAudioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        oldMode = mAudioManager.getRingerMode();
        oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_PLAY_SOUND);
        mAudioManager.setSpeakerphoneOn(false); //false //bob.chen modify for reiver test too low //bob.chen modify 
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        mAudioManager.setMode(AudioManager.MODE_IN_CALL);

        initMediaPlayer();
    }

    protected void onResume() {
        super.onResume();
        mBtOk = (Button) findViewById(R.id.audio_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.audio_bt_failed);
        mBtFailed.setOnClickListener(this);

        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
        		mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_PLAY_SOUND);
    }

    @Override
    protected void onPause() {
    	super.onPause();

    	mAudioManager.setRingerMode(oldMode);
    	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 
    			oldVolume,
                AudioManager.FLAG_PLAY_SOUND);
    }

    protected void onDestroy() {
        mPlayer.stop();
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        super.onDestroy();
    }

    private void initMediaPlayer() {
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.earphone);   
        mPlayer.setLooping(true);
        mPlayer.start();
    }

    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.earphone_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }
    
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		System.out.println("view="+v+" keyCode="+keyCode+" KeyEvent="+event);
		return false;
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		System.out.println("Chron, keyCode="+keyCode+" KeyEvent="+event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE , AudioManager.FLAG_PLAY_SOUND);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER , AudioManager.FLAG_PLAY_SOUND);
                break;
            default:
                break;  
        }
		return super.onKeyDown(keyCode, event);
	}
}
