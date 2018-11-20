
package com.mediatek.factorymode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.AdcCheckNative;

public class AdcCheck extends BaseTestActivity implements OnClickListener {

    private TextView mStatus;
    private Button mBtOK;
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

        setContentView(R.layout.adc_check_info);

        mStatus = (TextView)findViewById(R.id.status);
    }

    @Override
    public void onResume() {
        super.onResume();

        mBtOK = (Button) findViewById(R.id.battery_bt_ok);
        mBtOK.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.battery_bt_failed);
        mBtFailed.setOnClickListener(this);

        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);

        Log.e("", "ellery0706 onResume start");
        AdcCheckNative.openDev();
        Log.e("", "ellery0706 onResume open");
        byte[] buff = new byte[50];
        AdcCheckNative.SetAdcCheck(buff);
        Log.e("", "ellery0706 onResume value: " + new String(buff));
        mStatus.setText(new String(buff));
        AdcCheckNative.closeDev();
        Log.e("", "ellery0706 onResume start");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.adc_check_name,
                (v.getId() == mBtOK.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
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
