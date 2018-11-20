
package com.mediatek.factorymode;

import java.util.ArrayList;
import java.util.List;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.widget.LinearLayout;
import android.telephony.TelephonyManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import com.android.internal.telephony.TelephonyIntents;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class MobileData extends Activity implements OnClickListener {
    private static final String TAG = "MobileData";
    SharedPreferences mSp;

    TextView mInfo;

    Button mBtOk;

    Button mBtFailed;

    private boolean SimState = false;

    private static final int UPDATE_ID = 55;
    private static final int UPDATE_DELAY_TIME = 2000;
    
    Handler myHandler = new Handler(){
    public void handleMessage(Message msg){
        switch (msg.what) {
            case UPDATE_ID:
                setMobiledataInfo();
                break;
            }
        }   
    }; 
    Button mBtOpen;
    Button mBtClose;
    LinearLayout mLinearLayout;
    private TelephonyManager mTelephonyManager;
    private IntentFilter mIntentFilter;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            setMobiledataInfo();
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

        setContentView(R.layout.mobiledata);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);

        SimState = TelephonyManager.getDefault().hasIccCard();

        mLinearLayout = (LinearLayout) findViewById(R.id.mobiledata_set);
        mInfo = (TextView) findViewById(R.id.mobiledata_info);
        mBtOk = (Button) findViewById(R.id.md_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.md_bt_failed);
        mBtFailed.setOnClickListener(this);
        mBtOpen = (Button) findViewById(R.id.md_bt_open);
        mBtOpen.setOnClickListener(this);
        mBtClose = (Button) findViewById(R.id.md_bt_close);
        mBtClose.setOnClickListener(this);
        mTelephonyManager = TelephonyManager.from(this);

        if (SimState == true) {
            setMobiledataInfo();
        }else{
            mInfo.setText(R.string.mobiledata_remind);
            mLinearLayout.setEnabled(false);
            mBtOpen.setClickable(false);
            mBtClose.setClickable(false);
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mIntentFilter.addAction(TelephonyIntents.ACTION_ANY_DATA_CONNECTION_STATE_CHANGED);
        
    }

    private void setMobiledataInfo(){
        boolean isEnable = mTelephonyManager.getDataEnabled(1);
        if(isEnable){
            mInfo.setText(R.string.mobiledata_is_open);
        }else{
            mInfo.setText(R.string.mobiledata_is_close);
        }
    }

    @Override
    public void onResume( ) {
        super.onResume();
        if (SimState == true) {
            registerReceiver(mIntentReceiver, mIntentFilter);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.md_bt_open:{
                mTelephonyManager.setDataEnabled(1, true);
                mInfo.setText(R.string.mobiledata_is_opening);
                myHandler.sendEmptyMessageDelayed(UPDATE_ID, UPDATE_DELAY_TIME);
                break;
                }
            case R.id.md_bt_close:{
                mTelephonyManager.setDataEnabled(1, false);
                mInfo.setText(R.string.mobiledata_is_closing);
                myHandler.sendEmptyMessageDelayed(UPDATE_ID, UPDATE_DELAY_TIME);
                break;
                }
            case R.id.md_bt_ok:
            case R.id.md_bt_failed:{
                Utils.SetPreferences(this, mSp, R.string.mobiledata_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
                finish();
                break;
                }
            default :{
                break;
                }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (SimState == true) {
            unregisterReceiver(mIntentReceiver);
        }
    }
}
