
package com.mediatek.factorymode;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.mediatek.factorymode.wifi.WiFiTools;

public class AutoTest extends Activity {
    WiFiTools mWifiTools;

    List<ScanResult> mWifiList = null;

    boolean mWifiConReslut = false;

    boolean mWifiResult = false;

    boolean mWifiStatus = false;

    boolean mOtherOk = false;

    boolean mBlueResult = false;

    boolean mBlueFlag = false;

    boolean mBlueStatus = false;

    boolean mSdCardResult = false;

    Message msg = null;

    SharedPreferences mSp;

    private BluetoothAdapter mAdapter = null;

    boolean isregisterReceiver = false;

    HandlerThread mBlueThread = new HandlerThread("blueThread");

    BlueHandler mBlueHandler;

    HandlerThread mWifiThread = new HandlerThread("wifiThread");

    WifiHandler mWifiHandler;

    ProgressDialog pDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alltest);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mWifiTools = new WiFiTools(this);

        mWifiThread.start();
        mWifiHandler = new WifiHandler(mWifiThread.getLooper());
        mWifiHandler.post(wifirunnable);

        mBlueThread.start();
        mBlueHandler = new BlueHandler(mBlueThread.getLooper());
        mBlueHandler.post(bluerunnable);
        SdCardInit();

        Intent intent = new Intent();
        intent.setClassName(this, "com.mediatek.factorymode.BatteryLog");
        this.startActivityForResult(intent, AppDefine.FT_BATTERYID);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.gc();
        Intent intent = new Intent();
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //bob.chen disabled
        int requestid = -1;
        if (requestCode == AppDefine.FT_BATTERYID) {
            if (resultCode == RESULT_FIRST_USER) {
                finish();
                return;
            }
           /* intent.setClassName(this, "com.mediatek.factorymode.KeyCode");
            requestid = AppDefine.FT_KEYCODEID;
        }
        if (requestCode == AppDefine.FT_KEYCODEID) {*/
            intent.setClassName(this, "com.mediatek.factorymode.lcd.LCD");
            requestid = AppDefine.FT_LCDID;
        }
        if (requestCode == AppDefine.FT_LCDID) {
            intent.setClassName(this, "com.mediatek.factorymode.touchscreen.LineTest");
			//intent.setClassName(this, "com.mediatek.factorymode.touchscreen.TouchScreen");
            requestid = AppDefine.FT_TOUCHSCREENID;
        }
        if (requestCode == AppDefine.FT_TOUCHSCREENID) {
            intent.setClassName(this, "com.mediatek.factorymode.backlight.BackLight");
            requestid = AppDefine.FT_BACKLIGHTID;
        }
        /* if (requestCode == AppDefine.FT_BACKLIGHTID) {
            intent.setClassName(this, "com.mediatek.factorymode.sdcard.SDCard");
            requestid = AppDefine.FT_SDCARDID;
        }
        if (requestCode == AppDefine.FT_SDCARDID) {
            intent.setClassName(this, "com.mediatek.factorymode.vibrator.Vibrator");
            requestid = AppDefine.FT_VIBRATORID;
        }*/
        if (requestCode == AppDefine.FT_BACKLIGHTID) {
            intent.setClassName(this, "com.mediatek.factorymode.simcard.SimCard");
            requestid = AppDefine.FT_SIMSDCARDHOOKID;
        }
	/* if(!FactoryModeFeatureOption.CENON_NFC){  //skip Msensor
            if(requestid == AppDefine.FT_SIMSDCARDHOOKID) {
                requestid = AppDefine.FT_SIMSDCARDHOOKID;
            }
        }	
	  if (requestCode == AppDefine.FT_SIMSDCARDHOOKID) {//nfc
            intent.setClassName(this, "com.mediatek.factorymode.sensor.Nfc");
            requestid = AppDefine.FT_SIMSDCARDHOOKID;
        }	*/
        if (requestCode == AppDefine.FT_SIMSDCARDHOOKID) {
            intent.setClassName(this, "com.mediatek.factorymode.vibrator.Vibrator");
            requestid = AppDefine.FT_VIBRATORID;
        }
        if (requestCode == AppDefine.FT_VIBRATORID) {
            intent.setClassName(this, "com.mediatek.factorymode.signal.Signal");
            requestid = AppDefine.FT_SIGNALID;
        }
        if (requestCode == AppDefine.FT_SIGNALID) {
            intent.setClassName(this, "com.mediatek.factorymode.earphone.Earphone");
            requestid = AppDefine.FT_EARPHONEID;
        }
        if (requestCode == AppDefine.FT_EARPHONEID) {
            intent.setClassName(this, "com.mediatek.factorymode.microphone.MicRecorder");
            requestid = AppDefine.FT_FMRADIOID;
        }
       /* if (requestCode == AppDefine.FT_MICROPHONEID) {
            intent.setClassName(this, "com.mediatek.factorymode.headset.HeadSet");
            requestid = AppDefine.FT_HEADSETID;
        }
        if (requestCode == AppDefine.FT_HEADSETID) {
            intent.setClassName(this, "com.mediatek.factorymode.fmradio.FMRadio");
            requestid = AppDefine.FT_FMRADIOID;
        }*/
        if (requestCode == AppDefine.FT_FMRADIOID) {
            intent.setClassName(this, "com.mediatek.factorymode.sensor.GSensor");
            requestid = AppDefine.FT_GSENSORID;
        }
      /* if(!FactoryModeFeatureOption.CENON_MAGNETIC_FEATURE){  //skip Msensor
            if(requestid == AppDefine.FT_GSENSORID) {
                requestid = AppDefine.FT_MSENSORID;
            }
        }*/
	   if (requestCode == AppDefine.FT_GSENSORID) {
            intent.setClassName(this, "com.mediatek.factorymode.sensor.GyroscopeSensor");
            requestid = AppDefine.FT_GYROID;
        }
	   if (requestCode == AppDefine.FT_GYROID) {
            intent.setClassName(this, "com.mediatek.factorymode.sensor.PressureSensor");
            requestid = AppDefine.FT_PSENSORID;
        }
	   if (requestCode == AppDefine.FT_PSENSORID) {
            intent.setClassName(this, "com.mediatek.factorymode.sensor.PulseSensor");
            requestid = AppDefine.FT_PULSEID;
        }
      /*   if (requestCode == AppDefine.FT_GSENSORID) {
            intent.setClassName(this, "com.mediatek.factorymode.sensor.MSensor");
            requestid = AppDefine.FT_MSENSORID;
        }
       if (requestCode == AppDefine.FT_MSENSORID) {
            intent.setClassName(this, "com.mediatek.factorymode.sensor.PSensor");
            requestid = AppDefine.FT_PSENSORID;
        }
        if (requestCode == AppDefine.FT_PSENSORID) {
            intent.setClassName(this, "com.mediatek.factorymode.sensor.LSensor");
            requestid = AppDefine.FT_LSENSORID;
        }
        if (requestCode == AppDefine.FT_LSENSORID) {
            intent.setClassName(this, "com.mediatek.factorymode.camera.SubCamera");
            requestid = AppDefine.FT_SUBCAMERAID;
        }
        if (requestCode == AppDefine.FT_SUBCAMERAID) {
            intent.setClassName(this, "com.mediatek.factorymode.camera.CameraTest");
            requestid = AppDefine.FT_CAMERAID;
        }*/
        if (requestCode == AppDefine.FT_PSENSORID) {
            intent.setClassName(this, "com.mediatek.factorymode.audio.AudioTest");
            requestid = AppDefine.FT_AUDIOID;
        }
       /* if(!FactoryModeFeatureOption.CENON_HALL_FEATURE){ //skip hall
            if(requestid == AppDefine.FT_AUDIOID) {
                requestid = AppDefine.FT_HALLID;
            }
        }*/
        if (requestCode == AppDefine.FT_AUDIOID) {
            intent.setClassName(this, "com.mediatek.factorymode.wifi.WiFiTest");
            requestid = AppDefine.FT_WIFIID;
        }
        if (requestCode == AppDefine.FT_WIFIID) {
            intent.setClassName(this, "com.mediatek.factorymode.bluetooth.Bluetooth");
            requestid = AppDefine.FT_BLUETOOTHID;
        }
        if(false) {  //skip gps
            if(requestid == AppDefine.FT_BLUETOOTHID) {
                requestid = AppDefine.FT_GPSID;
            }
        }
        if (requestCode == AppDefine.FT_BLUETOOTHID) {
            intent.setClassName(this, "com.mediatek.factorymode.gps.GPS");
            requestid = AppDefine.FT_KPDID;
        }
       /* if(requestCode == AppDefine.FT_OTG) {
            intent.setClassName(this, "com.mediatek.factorymode.otg.OtgState");
            requestid = AppDefine.FT_GPSID;
        }
        if (requestCode == AppDefine.FT_GPSID) {
            intent.setClassName(this, "com.mediatek.factorymode.flashlamp.FlashLamp");
            requestid = AppDefine.FT_FLASHLAMP;
        }
        if(true){ //skip rgb
            if(requestid == AppDefine.FT_FLASHLAMP) {
                requestid = AppDefine.FT_RGBLEDID;
            }
        }
        if (requestCode == AppDefine.FT_FLASHLAMP) {
            intent.setClassName(this, "com.mediatek.factorymode.rgb_led.Rgb_led");
            requestid = AppDefine.FT_RGBLEDID;
        }
			 if(!FactoryModeFeatureOption.CENON_FACTORY_BACKTOUCH){ //skip hall
            if(requestid == AppDefine.FT_RGBLEDID) {
                requestid = AppDefine.FT_BACKTOUCH;
            }
        }	
		 if (requestCode == AppDefine.FT_RGBLEDID) {
            intent.setClassName(this, "com.mediatek.factorymode.backtouch.BackTouch");
            requestid = AppDefine.FT_BACKTOUCH;
        }		
		 if(!FactoryModeFeatureOption.CENON_FACTORY_KPDLED){ //skip kpd led
            if(requestid == AppDefine.FT_BACKTOUCH) {
                requestid = AppDefine.FT_KPDID;
            }
        }
		//cenon_kpd_led
         if (requestCode == AppDefine.FT_BACKTOUCH) {
		   intent.setClassName(this, "com.mediatek.factorymode.kpd_led.kpd_led");
            requestid = AppDefine.FT_KPDID;
        }		*/	 
        if (requestCode == AppDefine.FT_KPDID) {
            OnFinish();
            return;
        }
		
        this.startActivityForResult(intent, requestid);
    }

    public void onDestroy() {
        super.onDestroy();
        BackstageDestroy();
    }

    public void BackstageDestroy() {
        mWifiTools.closeWifi();
        mBlueHandler.removeCallbacks(bluerunnable);
        mWifiHandler.removeCallbacks(wifirunnable);
        if (isregisterReceiver == true) {
            unregisterReceiver(mReceiver);
        }
        mAdapter.disable();
    }

    public void SdCardInit() {
        String sDcString = Environment.getExternalStorageState();
        if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
            mSdCardResult = true;
        }
    }

    public boolean WifiInit() {
        mWifiTools.openWifi();
        mWifiList = mWifiTools.scanWifi();
        if (mWifiList == null || mWifiList.size() <= 0) {
            return false;
        } else {
            for (int j = 0; j < mWifiList.size(); j++) {
                ScanResult sr = mWifiList.get(j);
                if (sr.capabilities.equals("[WPS]") || sr.capabilities.equals("")) {
                    mWifiConReslut = mWifiTools.addWifiConfig(mWifiList, sr, "");
                    if (mWifiConReslut == true) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    Runnable wifirunnable = new Runnable() {
        @Override
        public void run() {
            if (mWifiStatus == false) {
                boolean res = WifiInit();
                if (res == false) {
                } else {
                    mWifiStatus = true;
                }
                mWifiHandler.postDelayed(this, 3000);
            } else {
                if (mWifiTools.IsConnection()) {
                    mWifiResult = true;
                    mWifiTools.closeWifi();
                } else {
                    mWifiHandler.postDelayed(this, 3000);
                }
            }
        }
    };

    class WifiHandler extends Handler {
        public WifiHandler() {
        }

        public WifiHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    public void BlueInit() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter.enable();
        if (mAdapter.isEnabled() == true) {
            StartReciver();
            while (mAdapter.startDiscovery() == false) {
                mAdapter.startDiscovery();
            }
        } else {
            mBlueHandler.postDelayed(bluerunnable, 3000);
        }
    }

    public void StartReciver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        isregisterReceiver = true;
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mBlueResult = true;
                    if (isregisterReceiver == true) {
                        unregisterReceiver(mReceiver);
                        isregisterReceiver = false;
                    }
                    mAdapter.disable();
                }
            }
        }
    };

    Runnable bluerunnable = new Runnable() {
        @Override
        public void run() {
            BlueInit();
        }
    };

    class BlueHandler extends Handler {
        public BlueHandler() {
        }

        public BlueHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    public void OnFinish() {
       // Utils.SetPreferences(this, mSp, R.string.backlight_name, AppDefine.FT_SUCCESS);
        // Utils.SetPreferences(this, mSp, R.string.memory_name, AppDefine.FT_SUCCESS);
        Utils.SetPreferences(this, mSp, R.string.wifi_name,
                (mWifiResult == true) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        Utils.SetPreferences(this, mSp, R.string.bluetooth_name,
                (mBlueResult == true) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
       // Utils.SetPreferences(this, mSp, R.string.sdcard_name,
       //         (mSdCardResult == true) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
        finish();
    }
}
