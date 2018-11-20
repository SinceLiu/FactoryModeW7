
package com.mediatek.factorymode.simsdcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

public class SimSDCard extends BaseTestActivity implements OnClickListener {

    private TextView mSIMCardInfo;

    private boolean Sim1State = false;

    private boolean Sim2State = false;

    private String mSimStatus = "";

    private Button mSIMCardBtOk;

    private Button mSIMCardBtFailed;

    private TextView mSDCardInfo;
    private TextView mSDCardInfo1;	//haiming __EMMC_TEST_T_KA__

    private Button mSDCardBtOk;

    private Button mSDCardBtFailed;

    private SharedPreferences mSDCardSp;

    private SharedPreferences mSIMCardSp;

    boolean mSIMCardClick = false;

    boolean mSDCardClick = false;

    private boolean mSIMCardTestOk = false;
    
    private boolean mSDCardTestOk = false;

    private final static String PHONE_STORAGE = "/storage/emulated/0";
    
    private final static String SD_STORAGE = "/storage/sdcard1";
    private final static String INTERNAL_STORAGE =  "/storage/sdcard0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simsdcard);

        //mSIMCardSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mSIMCardInfo = (TextView) findViewById(R.id.simcard_sim_info);
        mSIMCardBtOk = (Button) findViewById(R.id.simcard_bt_ok);
        mSIMCardBtOk.setOnClickListener(this);
        mSIMCardBtFailed = (Button) findViewById(R.id.simcard_bt_failed);
        mSIMCardBtFailed.setOnClickListener(this);

        mSIMCardInfo.setText(mSimStatus);

        mSDCardSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mSDCardInfo = (TextView) findViewById(R.id.sdcard_info);
	    mSDCardInfo1 = (TextView) findViewById(R.id.sdcard_info1);	//haiming __EMMC_TEST_T_KA__
        mSDCardBtOk = (Button) findViewById(R.id.sdcard_bt_ok);
        mSDCardBtOk.setOnClickListener(this);
        mSDCardBtFailed = (Button) findViewById(R.id.sdcard_bt_failed);
        mSDCardBtFailed.setOnClickListener(this);
        SDCardSizeTest();
    }

    public void SDCardSizeTest() {
        
        StorageManager mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] volumes = mStorageManager.getVolumeList();

        if(volumes.length <  1) {
            mSDCardInfo.setText(getString(R.string.sdcard_tips_failed));
            mSDCardInfo1.setText(getString(R.string.sdcard_tips_failed));
        }
        for (int i = 0; i < volumes.length; i++) {
                String path = volumes[i].getPath();
                if(mStorageManager.getVolumeState(path).equals(Environment.MEDIA_MOUNTED))
                getVolumeOfStorage(path);
          }
    }        

    private void getVolumeOfStorage(String path) {
            android.os.StatFs statfs = new android.os.StatFs(path);

            long nTotalBlocks = statfs.getBlockCount();

            long nBlocSize = statfs.getBlockSize();

            long nAvailaBlock = statfs.getAvailableBlocks();

            long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;

            long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;

            if(path.equals(PHONE_STORAGE) || path.equals(INTERNAL_STORAGE)) {
                mSDCardInfo.setText(getString(R.string.sdcard_tips_success) + getString(R.string.storage_phone) + "\n"
                    + getString(R.string.sdcard_totalsize) + nSDTotalSize + "MB" + "\n"
                    + getString(R.string.sdcard_freesize) + nSDFreeSize + "MB" + "\n");
            } else if(path.equals(SD_STORAGE)) {
                 mSDCardInfo1.setText(getString(R.string.sdcard_tips_success) + getString(R.string.storage_sdcard) + "\n"
                    + getString(R.string.sdcard_totalsize) + nSDTotalSize + "MB" + "\n"
                    + getString(R.string.sdcard_freesize) + nSDFreeSize + "MB" + "\n");
            }
            
    }


    public void isFinish(){
        if(mSIMCardClick == true && mSDCardClick == true){
            if(mSIMCardTestOk && mSDCardTestOk) {
                Utils.SetPreferences(this, mSDCardSp, R.string.sim_sdcard_name, AppDefine.FT_SUCCESS);
            } else {
                Utils.SetPreferences(this, mSDCardSp, R.string.sim_sdcard_name, AppDefine.FT_FAILED);
            }
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mSIMCardBtOk.getId()) {
            mSIMCardClick = true;
            mSIMCardTestOk = true;
            mSIMCardBtFailed.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mSIMCardBtOk.setBackgroundColor(this.getResources().getColor(R.color.Green));
            Utils.SetPreferences(this, mSDCardSp, R.string.sim_sdcard_name, AppDefine.FT_SUCCESS);
        } else if (v.getId() == mSIMCardBtFailed.getId()) {
            mSIMCardClick = true;
            mSIMCardTestOk =false;
            mSIMCardBtOk.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mSIMCardBtFailed.setBackgroundColor(this.getResources().getColor(R.color.Red));
            Utils.SetPreferences(this, mSDCardSp, R.string.sim_sdcard_name, AppDefine.FT_FAILED);
        }
        if (v.getId() == mSDCardBtOk.getId()) {
            mSDCardClick = true;
            mSDCardTestOk = true;
            mSDCardBtFailed.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mSDCardBtOk.setBackgroundColor(this.getResources().getColor(R.color.Green));
            Utils.SetPreferences(this, mSDCardSp, R.string.sim_sdcard_name, AppDefine.FT_SUCCESS);
        } else if (v.getId() == mSDCardBtFailed.getId()) {
            mSDCardClick = true;
            mSDCardTestOk = false;
            mSDCardBtOk.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mSDCardBtFailed.setBackgroundColor(this.getResources().getColor(R.color.Red));
            Utils.SetPreferences(this, mSDCardSp, R.string.sim_sdcard_name, AppDefine.FT_FAILED);
        }
        isFinish();
    }
}
