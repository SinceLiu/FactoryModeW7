
package com.mediatek.factorymode.earphonevibrator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

public class EarphoneVibrator extends BaseTestActivity implements OnClickListener {
    private SharedPreferences mEarphoneSp;

    private MediaPlayer mPlayer;

    private Button mEarphoneBtOK;

    private Button mEarphoneBtFailed;

    private AudioManager mAudioManager;

    private Vibrator mVibrator;

    private Button mVibratorBtOK;

    private Button mVibratorBtFailed;

    private SharedPreferences mVibratorSp;

    boolean mEarphoneClick = false;

    boolean mVibratorClick = false;

    private boolean mEarphoneTestOk = false;

    private boolean mVibratorTestOk = false;

    private int oldMode;
    private int oldVolume;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earphone_vibrate);
        mEarphoneSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mEarphoneBtOK = (Button) findViewById(R.id.audio_bt_ok);
        mEarphoneBtOK.setOnClickListener(this);
        mEarphoneBtFailed = (Button) findViewById(R.id.audio_bt_failed);
        mEarphoneBtFailed.setOnClickListener(this);
        mAudioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        oldMode = mAudioManager.getRingerMode();
        oldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_PLAY_SOUND);
        mAudioManager.setSpeakerphoneOn(false); //false //bob.chen modify for reiver test too low //bob.chen modify
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        initMediaPlayer();
		
        mVibratorSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mVibratorBtOK = (Button) findViewById(R.id.vibrator_bt_ok);
        mVibratorBtOK.setOnClickListener(this);
        mVibratorBtFailed = (Button) findViewById(R.id.vibrator_bt_failed);
        mVibratorBtFailed.setOnClickListener(this);

        mVibrator = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
        mVibrator.vibrate(10000);
    }

    protected void onResume() {
        super.onResume();
        mEarphoneBtOK = (Button) findViewById(R.id.audio_bt_ok);
        mEarphoneBtOK.setOnClickListener(this);
        mEarphoneBtFailed = (Button) findViewById(R.id.audio_bt_failed);
        mEarphoneBtFailed.setOnClickListener(this);

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
		mVibrator.cancel();
        super.onDestroy();
    }

    private void initMediaPlayer() {
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tada);
        mPlayer.setLooping(true);
        mPlayer.start();
    }

    public void isFinish(){
        if(mEarphoneClick == true && mVibratorClick == true){
            if(mEarphoneTestOk && mVibratorTestOk) {
                Utils.SetPreferences(this, mEarphoneSp, R.string.earphone_vibrator_name, AppDefine.FT_SUCCESS);
            } else {
                Utils.SetPreferences(this, mEarphoneSp, R.string.earphone_vibrator_name, AppDefine.FT_FAILED);
            }
            finish();
        }
    }

    public void onClick(View v) {
        if (v.getId() == mEarphoneBtOK.getId()) {
            mEarphoneClick = true;
            mEarphoneTestOk = true;
            mEarphoneBtFailed.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mEarphoneBtOK.setBackgroundColor(this.getResources().getColor(R.color.Green));
            Utils.SetPreferences(this, mEarphoneSp, R.string.earphone_vibrator_name, AppDefine.FT_SUCCESS);
        } else if (v.getId() == mEarphoneBtFailed.getId()) {
            mEarphoneClick = true;
            mEarphoneTestOk = false;
            mEarphoneBtOK.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mEarphoneBtFailed.setBackgroundColor(this.getResources().getColor(R.color.Red));
            Utils.SetPreferences(this, mEarphoneSp, R.string.earphone_vibrator_name, AppDefine.FT_FAILED);
        }
        if (v.getId() == mVibratorBtOK.getId()) {
            mVibratorClick = true;
            mVibratorTestOk = true;
            mVibratorBtFailed.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mVibratorBtOK.setBackgroundColor(this.getResources().getColor(R.color.Green));
            Utils.SetPreferences(this, mVibratorSp, R.string.earphone_vibrator_name, AppDefine.FT_SUCCESS);
        } else if (v.getId() == mVibratorBtFailed.getId()) {
            mVibratorClick = true;
            mVibratorTestOk =false;
            mVibratorBtOK.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mVibratorBtFailed.setBackgroundColor(this.getResources().getColor(R.color.Red));
            Utils.SetPreferences(this, mVibratorSp, R.string.earphone_vibrator_name, AppDefine.FT_FAILED);
        }
        isFinish();
    }
}
