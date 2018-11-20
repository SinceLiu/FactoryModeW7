
package com.mediatek.factorymode.kpd_led;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

import android.content.Intent;
//cenon_kpd_led
public class kpd_led extends BaseTestActivity implements OnClickListener {
    private SharedPreferences mSp;

    private MediaPlayer mPlayer;

    private Button mBtOk;

    private Button mBtFailed;


    private AudioManager mAudioManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_test);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mBtOk = (Button) findViewById(R.id.audio_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.audio_bt_failed);
        mBtFailed.setOnClickListener(this);
       

		 Intent ledIntent = new Intent("android.intent.action.KPD_LIGHT_EVENT");
        ledIntent.putExtra("value", 1);
		
        getApplicationContext().sendBroadcast(ledIntent);
	
    }

    protected void onResume() {
        super.onResume();
        mBtOk = (Button) findViewById(R.id.audio_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.audio_bt_failed);
        mBtFailed.setOnClickListener(this);
    }

    protected void onDestroy() {
		Intent ledIntent = new Intent("android.intent.action.KPD_LIGHT_EVENT");
        ledIntent.putExtra("value", 0);
		getApplicationContext().sendBroadcast(ledIntent);
                super.onDestroy();
    }

 

    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.kpd_light_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }
}
