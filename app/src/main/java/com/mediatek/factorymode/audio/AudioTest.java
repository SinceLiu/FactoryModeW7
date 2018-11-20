
package com.mediatek.factorymode.audio;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;

public class AudioTest extends BaseTestActivity implements OnClickListener {
    private SharedPreferences mSp;

    private MediaPlayer mPlayer;

    private Button mBtOk;

    private Button mBtFailed;

    AudioManager audioManager;

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

        if (mPlayer!=null) {
        	if (mPlayer.isPlaying()) {
                mPlayer.stop();
			}
        	mPlayer.release();
        	mPlayer = null;
		}
        
    	super.finish();
    }
    
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer!=null) {
        	if (mPlayer.isPlaying()) {
                mPlayer.stop();
			}
        	mPlayer.release();
        	mPlayer = null;
		}
    }

    private void initMediaPlayer() {
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tada);
        mPlayer.setLooping(true);
        mPlayer.start();
    }

    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.speaker_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
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
