
package com.mediatek.factorymode.fmradio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

public class FMRadio extends BaseTestActivity implements OnClickListener{
    SharedPreferences mSp;

    static Cursor mCursor = null;
    
    private Button mBtOk;
    
    private Button mBtFailed;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fmradio);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        
        mBtOk = (Button) findViewById(R.id.fmradio_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.fmradio_bt_failed);
        mBtFailed.setOnClickListener(this);
        
        Intent fmActivityIntent = new Intent(Intent.ACTION_MAIN);
        fmActivityIntent.putExtra("fromWhere", "factoryMode");
        fmActivityIntent.setClassName("com.android.fmradio", "com.android.fmradio.FmMainActivity");
        startActivityForResult(fmActivityIntent, AppDefine.FT_FMRADIOSETID);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode != RESULT_OK){
//            finish();
//            return;
//        }
//        boolean fmResult = data.getBooleanExtra("result", false);
//        Utils.SetPreferences(getApplicationContext(), mSp, R.string.fmradio_name,
//                (fmResult) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
//        finish();
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}

	@Override
	public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.fmradio_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
	}
    
}
