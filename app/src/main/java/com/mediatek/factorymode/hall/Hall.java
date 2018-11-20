package com.mediatek.factorymode.hall;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
//import android.util.PhoneKpdNative;  //there is no function, 2015.05.28


public class Hall extends BaseTestActivity implements OnClickListener {

    private TextView mStatus;
    private Button mBtOK;
    private Button mBtFailed;
    private SharedPreferences mSp;
    private static final int EVENT_TICK = 1;
    String flag = "";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case EVENT_TICK:
                updateHallStats();
                sendEmptyMessageDelayed(EVENT_TICK, 1000);
                break;
            }
        }

        private void updateHallStats() {
            boolean mIsHallOn = false;//PhoneKpdNative.kpd_is_on();//there is no function, 2015.05.28
            mStatus.setText(mIsHallOn ? R.string.hall_on : R.string.hall_off);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hall_info);
        mStatus = (TextView) findViewById(R.id.status);
        mBtOK = (Button) findViewById(R.id.hall_ok);
        mBtOK.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.hall_failed);
        mBtFailed.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mHandler.sendEmptyMessageDelayed(EVENT_TICK, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(EVENT_TICK);
    }

    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.hall_name, (v.getId() == mBtOK
                .getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
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
