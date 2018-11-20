
package com.mediatek.factorymode.vibrator;

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
import com.mediatek.factorymode.Utils;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Vibrator extends BaseTestActivity implements OnClickListener {
    private android.os.Vibrator mVibrator;

    private Button mBtOk;

    private Button mBtFailed;

    private SharedPreferences mSp;

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
		
        setContentView(R.layout.vibrator);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mBtOk = (Button) findViewById(R.id.vibrator_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.vibrator_bt_failed);
        mBtFailed.setOnClickListener(this);

        mVibrator = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
//        mVibrator.vibrate(10000);
    }

    /*public void onDestroy() {
        super.onDestroy();
        mVibrator.cancel();
    }*/
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
        mVibrator.vibrate(10000);
    }
    
    @Override
	protected void onPause() {
		super.onPause();
        mVibrator.cancel();
	}

    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.vibrator_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }
}
