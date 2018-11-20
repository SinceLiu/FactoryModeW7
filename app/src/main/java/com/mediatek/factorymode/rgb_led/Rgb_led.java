
package com.mediatek.factorymode.rgb_led;

import android.app.Activity;
import android.content.Intent;
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
//import com.android.server.BatteryService;  //bob.chen



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Rgb_led extends BaseTestActivity implements OnClickListener {

    private Button mBtOk;

    private Button mBtFailed;

    private SharedPreferences mSp;
    private static final String brownOutFilePath = "//data//brownout.txt";  //add by ellery for brown out

    @Override
    public void onCreate(Bundle savedInstanceState) {
         int i = 1; // 1: blink; 0: breathing mode
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rgb_led);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mBtOk = (Button) findViewById(R.id.rgb_led_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.rgb_led_bt_failed);
        mBtFailed.setOnClickListener(this);

//        mVibrator = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
 //       mVibrator.vibrate(10000);

					//updateLedStatus();
                    // Solid green when full or charging and nearly full
//                    mBatteryLight.setColor(mBatteryFullARGB);
 //                   BatteryService.mBatteryLight.setColor(0xFF00FF00);
	if (false) {
	      try {

			FileOutputStream outStream=new FileOutputStream(brownOutFilePath);
			outStream.write((byte)i);
			outStream.close();
			} catch (FileNotFoundException e) {
			e.printStackTrace();
			} catch (IOException e) {
			e.printStackTrace();
			} 
	 }
        Intent ledIntent = new Intent("android.intent.action.LEDON_SHUTDOWN_EVENT");
        ledIntent.putExtra("value", 1);
		
        getApplicationContext().sendBroadcast(ledIntent);
	try {
		Thread.sleep(1000);
            } catch (InterruptedException unused) {
            }


        ledIntent.putExtra("value", 2);
        getApplicationContext().sendBroadcast(ledIntent);
	try {
		Thread.sleep(1000);
            } catch (InterruptedException unused) {
            }
         ledIntent.putExtra("value", 3);
        getApplicationContext().sendBroadcast(ledIntent);
	try {
		Thread.sleep(1000);
            } catch (InterruptedException unused) {
            }
     if (false) {

	 i = 0; // 1: blink; 0: breathing mode
          try {
	
		FileOutputStream outStream=new FileOutputStream(brownOutFilePath);
		outStream.write((byte)i);
		outStream.close();
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		} 	
     	}
        ledIntent.putExtra("value", 0);
        getApplicationContext().sendBroadcast(ledIntent);
					
    }

    public void onDestroy() {
        Intent ledIntent = new Intent("android.intent.action.LEDON_SHUTDOWN_EVENT");
        ledIntent.putExtra("value", 0);
        getApplicationContext().sendBroadcast(ledIntent);

        super.onDestroy();
//        mVibrator.cancel();
    }

    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.rgb_led_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }
}
