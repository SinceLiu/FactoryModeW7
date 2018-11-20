
package com.mediatek.factorymode.sdcard;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

public class SDCard extends BaseTestActivity implements OnClickListener {
    private TextView mInfo;

    private Button mBtOk;

    private Button mBtFailed;

    private SharedPreferences mSp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdcard);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mInfo = (TextView) findViewById(R.id.sdcard_info);
        mBtOk = (Button) findViewById(R.id.sdcard_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.sdcard_bt_failed);
        mBtFailed.setOnClickListener(this);
        SDCardSizeTest();
    }

    public void SDCardSizeTest() {
        String sDcString = Environment.getExternalStorageState();
        if (sDcString.equals(Environment.MEDIA_MOUNTED)) {

            java.io.File pathFile = Environment.getExternalStorageDirectory();

            android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());

            long nTotalBlocks = statfs.getBlockCount();

            long nBlocSize = statfs.getBlockSize();

            long nAvailaBlock = statfs.getAvailableBlocks();

            long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;

            long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;

            mInfo.setText(getString(R.string.sdcard_tips_success) + "\n\n"
                    + getString(R.string.sdcard_totalsize) + nSDTotalSize + "MB" + "\n\n"
                    + getString(R.string.sdcard_freesize) + nSDFreeSize + "MB");
        } else {
            mInfo.setText(getString(R.string.sdcard_tips_failed));
        }
    }

    @Override
    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.sdcard_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }
}
