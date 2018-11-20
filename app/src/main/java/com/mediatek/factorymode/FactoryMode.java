
package com.mediatek.factorymode;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import android.util.Log;
public class FactoryMode extends Activity implements OnItemClickListener {
    /** Called when the activity is first created. */
    public static final String LOG_TAG = "FactoryMode";

    private List<String> mListData;
    private SharedPreferences mSp = null;

    private GridView mGrid;
    private Button mBtAll;
    private Button mBtAuto;

    private MyAdapter mAdapter;

    private static int revertID = 0;
    
    private TextView tv_CCM;

    private TextView tv_CTM;
    
    private int itemString[] = {
    	R.string.current_consumption_monitored,
    	R.string.cpu_temperature,
        R.string.touchscreen_name,
        R.string.lcd_name,
        R.string.battery_name,
//        R.string.adc_check_name,
        R.string.KeyCode_name,
        R.string.speaker_name,
        R.string.microphone_name,
        R.string.WiFi,
        R.string.bluetooth_name,
        R.string.vibrator_name,
        R.string.telephone_name,
        R.string.backlight_name,
        R.string.sim_name,
        R.string.gps_name,
//        R.string.gps1_name,
//        R.string.gps2_name,
        R.string.gsensor_with_calibrate_name,
        R.string.gsensor_name,
        R.string.msensor_name,
        R.string.lsensor_name,
        R.string.psensor_name,
        R.string.pulse_sensor_name,
        // R.string.step_sensor_name,
        R.string.gyroscope_sensro_name,
        R.string.camera_name,
        R.string.air_pressure_sensor_name,
        // R.string.mobiledata_name,
        R.string.weardetection_name,
        R.string.device_info,
        R.string.battery_used_name,
        R.string.lcd_white_name,
        R.string.factory_reset
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        super.onCreate(savedInstanceState);

        // init();
        mBtAuto = (Button) findViewById(R.id.main_bt_autotest);
        mBtAuto.setOnClickListener(cl);
        mBtAll = (Button) findViewById(R.id.main_bt_alltest);
        mBtAll.setOnClickListener(cl);
        mBtAuto.setVisibility(View.GONE);
        /*mBtAll.setVisibility(View.GONE);*/
        mGrid = (GridView) findViewById(R.id.main_grid);
        mListData = getData();
        mAdapter = new MyAdapter(this);
        revertID = 0;
        
        startService(new Intent(this, CurrentConsumptionService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGrid.setAdapter(mAdapter);
        mGrid.setOnItemClickListener(this);

        //back to previous click position
        revertID += 4;
        if(revertID >= itemString.length) {
           revertID = itemString.length;
        }
        mGrid.smoothScrollToPosition(revertID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public View.OnClickListener cl = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            int reqId = -1;
            if (v.getId() == mBtAuto.getId()) {
                intent.setClassName("com.mediatek.factorymode", "com.mediatek.factorymode.AutoTest");
                reqId = AppDefine.FT_AUTOTESTID;
            }
            if (v.getId() == mBtAll.getId()) {
                intent.setClassName("com.mediatek.factorymode", "com.mediatek.factorymode.AllTest");
                reqId = AppDefine.FT_ALLTESTID;
            }
            startActivityForResult(intent, reqId);
        }
    };

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public MyAdapter(FactoryMode factoryMode, int factoryButton) {
        }

        public int getCount() {
            if (mListData == null) {
                return 0;
            }
            return mListData.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            revertID = position;
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.main_grid, null);
            TextView textview = (TextView) convertView.findViewById(R.id.factor_button);
            textview.setText(mListData.get(position));
            SetColor(textview);
            return convertView;
        }
    }

    private void init() {
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        Editor editor = mSp.edit();
        for (int i = 0; i < itemString.length; i++) {
            editor.putString(getString(itemString[i]), AppDefine.FT_DEFAULT);
        }
        editor.putString(getString(R.string.headsethook_name), AppDefine.FT_DEFAULT);
        editor.apply();
    }

    private void SetColor(TextView s) {
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        for (int i = 0; i < itemString.length; i++) {
                if (getResources().getString(itemString[i]).equals(s.getText().toString())) {

                String name = mSp.getString(getString(itemString[i]), AppDefine.FT_DEFAULT);
                
                Log.d("cenontest", "name = " + name);
                Log.d("cenontest", "getString(itemString[i]) = " + getString(itemString[i]));
                if (name.equals(AppDefine.FT_SUCCESS)) {
                    s.setTextColor(getApplicationContext().getResources().getColor(R.color.Blue));
                } else if (name.equals(AppDefine.FT_DEFAULT)) {
                    s.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                } else if (name.equals(AppDefine.FT_FAILED)) {
                    s.setTextColor(getApplicationContext().getResources().getColor(R.color.Red));
                }
                
                if (itemString[i]==R.string.current_consumption_monitored) {
                	tv_CCM = s;
					boolean tag = mSp.getBoolean("CurrentConsumptionMonitored", false);
					if (tag) {
						s.setTextColor(getApplicationContext().getResources().getColor(R.color.Blue));
					} else {
						s.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
					}
				}
                
                if (itemString[i]==R.string.cpu_temperature) {
                	tv_CTM = s;
					boolean tag = mSp.getBoolean("CPUTemperatureMonitored", false);
					if (tag) {
						s.setTextColor(getApplicationContext().getResources().getColor(R.color.Blue));
					} else {
						s.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
					}
				}
            }
        }
    }

    private List<String> getData() {
        List<String> items = new ArrayList<String>();
        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
        for (int i = 0; i < itemString.length; i++) {
            if (pre.getBoolean(getString(itemString[i]), true)) {

                if(!FactoryModeFeatureOption.CENON_KEYCODE_SUPPORT){ //no gps
                    if(getString(R.string.KeyCode_name).equals(getString(itemString[i]))){
                        continue;
                    }
                }

                if(!FactoryModeFeatureOption.MTK_SIM_SUPPORT){  //no sim
                    if(getString(R.string.sim_name).equals(getString(itemString[i]))){
                        continue;
                    }

                    if(getString(R.string.telephone_name).equals(getString(itemString[i]))){ //no tel phone
                        continue;
                    }

                    if(getString(R.string.mobiledata_name).equals(getString(itemString[i]))){ //no mobile data
                        continue;
                    }  
                }

                if(!FactoryModeFeatureOption.CENON_GPS_SUPPORT){ //no gps
                    if(getString(R.string.gps_name).equals(getString(itemString[i]))){
//                                || getString(R.string.gps2_name).equals(getString(itemString[i])
                        continue;
                    }
                } else {
                    if(SystemProperties.get("ro.cenon_factorymode_feature").equals("1")) {
                        if(getString(R.string.gps_name).equals(getString(itemString[i]))) {
                            continue;
                        }
                    }
//                    else {
//                        if(getString(R.string.gps1_name).equals(getString(itemString[i])) {
//                            continue;
//                        }
//                    }
                }

                if(!FactoryModeFeatureOption.CENON_LSENSOR_SUPPORT) {//no lsensor
                    if(getString(R.string.lsensor_name).equals(getString(itemString[i]))){
                        continue;
                    }
                }

                if(!FactoryModeFeatureOption.CENON_PSENSOR_SUPPORT) { //no psensor
                    if(getString(R.string.psensor_name).equals(getString(itemString[i]))){
                        continue;
                    }
                }

                if(!FactoryModeFeatureOption.CENON_GSENSOR_SUPPORT){ //no gsensor
                    if(getString(R.string.gsensor_name).equals(getString(itemString[i]))
                            || getString(R.string.gsensor_with_calibrate_name).equals(getString(itemString[i]))){
                        continue;
                    }
                }

                if(!FactoryModeFeatureOption.CENON_GYROSCOPE_SENSOR_SUPPORT){ //no gyroscope sensor
                    if(getString(R.string.gyroscope_sensro_name).equals(getString(itemString[i]))) {
                        continue;
                    }
                }

                if(!FactoryModeFeatureOption.CENON_MAGNETIC_FEATURE) { //no msensor
                    if(getString(R.string.msensor_name).equals(getString(itemString[i]))){
                        continue;
                    }
                }

                if(!FactoryModeFeatureOption.CENON_PULSE_SENSOR_SUPPORT) { //no PULSE sensor
                    if(getString(R.string.pulse_sensor_name).equals(getString(itemString[i]))){
                        continue;
                    }
                }

                if(!FactoryModeFeatureOption.CENON_AIR_SENSOR_SUPPORT) { //no air sensor
                    if(getString(R.string.air_pressure_sensor_name).equals(getString(itemString[i]))){
                        continue;
                    }
                }

                if(!FactoryModeFeatureOption.CENON_WEAR_SENSOR_SUPPORT) { //no psensor
                    if(getString(R.string.weardetection_name).equals(getString(itemString[i]))){
                        continue;
                    }
                }

//                if(!FactoryModeFeatureOption.CENON_ADC_CHECK_SUPPORT) { // adc check
//                    if(getString(R.string.adc_check_name).equals(getString(itemString[i])){
//                        continue;
//                    }
//                }

                items.add(getString(itemString[i]));
            }
        }
        return items;
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        try {
            revertID = position;
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            String name = mListData.get(position);
            String classname = null;
            if (getString(R.string.speaker_name) .equals(name)) {
                classname = "com.mediatek.factorymode.audio.AudioTest";
            } else if (getString(R.string.battery_name) .equals(name)) {
                classname = "com.mediatek.factorymode.BatteryLog";
            } else if (getString(R.string.battery_used_name) .equals(name)) {
                classname = "com.mediatek.factorymode.audio.AudioForBatteryTest";
            }
//            else if (getString(R.string.adc_check_name) .equals(name)) {
//                classname = "com.mediatek.factorymode.AdcCheck";
//            }
            else if (getString(R.string.touchscreen_name) .equals(name)) {
                classname = "com.mediatek.factorymode.touchscreen.LineTest";
                //classname = "com.mediatek.factorymode.touchscreen.TouchScreen";
            } else if (getString(R.string.camera_name) .equals(name)) {
                classname = "com.mediatek.factorymode.camera.CameraTest";
            } else if (getString(R.string.wifi_name) .equals(name)) {
                classname = "com.mediatek.factorymode.wifi.WiFiTest";
            } else if (getString(R.string.bluetooth_name) .equals(name)) {
                classname = "com.mediatek.factorymode.bluetooth.Bluetooth";
            } else if (getString(R.string.headset_name) .equals(name)) {
                classname = "com.mediatek.factorymode.headset.HeadSet";
            } else if (getString(R.string.earphone_name) .equals(name)) {
                classname = "com.mediatek.factorymode.earphone.Earphone";
            } else if (getString(R.string.vibrator_name) .equals(name)) {
                classname = "com.mediatek.factorymode.vibrator.Vibrator";
            } else if (getString(R.string.telephone_name) .equals(name)) {
                classname = "com.mediatek.factorymode.signal.Signal";
            } else if (getString(R.string.gps_name) .equals(name)) {
                classname = "com.mediatek.factorymode.gps.GPS";
            }
//            else if (getString(R.string.gps1_name) .equals(name)) {
//                classname = "com.mediatek.factorymode.gps.GPS1";
//            } else if (getString(R.string.gps2_name) .equals(name)) {
//                classname = "com.mediatek.factorymode.gps.GPS2";
//            }
            else if (getString(R.string.otg_name) .equals(name)) {
                classname = "com.mediatek.factorymode.otg.OtgState";
            } else if (getString(R.string.backlight_name) .equals(name)) {
                classname = "com.mediatek.factorymode.backlight.BackLight";
            } else if (getString(R.string.memory_name) .equals(name)) {
                classname = "com.mediatek.factorymode.memory.Memory";
            } else if (getString(R.string.microphone_name) .equals(name)) {
                classname = "com.mediatek.factorymode.microphone.MicRecorder";
            } else if (getString(R.string.gsensor_name) .equals(name)) {
                classname = "com.mediatek.factorymode.sensor.GSensor";
            } else if (getString(R.string.gsensor_with_calibrate_name) .equals(name)) {
                classname = "com.mediatek.factorymode.sensor.GSensorWithCalibrate";
            } else if (getString(R.string.msensor_name) .equals(name)) {
                classname = "com.mediatek.factorymode.sensor.MSensor";
            } else if (getString(R.string.lsensor_name) .equals(name)) {
                classname = "com.mediatek.factorymode.sensor.LSensor";
            } else if (getString(R.string.psensor_name) .equals(name)) {
                classname = "com.mediatek.factorymode.sensor.PSensor";
            } else if (getString( R.string.pulse_sensor_name) .equals(name)) {
                classname = "com.mediatek.factorymode.sensor.PulseSensor";
            } else  if (getString(R.string.gyroscope_sensro_name) .equals(name)) {
                classname = "com.mediatek.factorymode.sensor.GyroscopeSensor";
            } else if(getString(R.string.step_sensor_name) .equals(name)) {
                classname = "com.mediatek.factorymode.sensor.StepSensor";
            } else if(getString(R.string.air_pressure_sensor_name) .equals(name)) {
                classname = "com.mediatek.factorymode.sensor.PressureSensor";
            } else if (getString(R.string.sdcard_name) .equals(name)) {
                classname = "com.mediatek.factorymode.sdcard.SDCard";
            } else if (getString(R.string.fmradio_name) .equals(name)) {
                classname = "com.mediatek.factorymode.fmradio.FMRadio";
            } else if (getString(R.string.KeyCode_name) .equals(name)) {
                classname = "com.mediatek.factorymode.KeyCode";
            } else if (getString(R.string.lcd_name) .equals(name)) {
                classname = "com.mediatek.factorymode.lcd.LCD";
            } else if (getString(R.string.lcd_white_name) .equals(name)) {
                classname = "com.mediatek.factorymode.lcd.LCDWhite";
            } else if (getString(R.string.sim_name) .equals(name)) {
                classname = "com.mediatek.factorymode.simcard.SimCard";
            } else if (getString(R.string.sim_sdcard_name) .equals(name)) {//__sim_sdcard_name__
                classname = "com.mediatek.factorymode.simsdcard.SimCardPre";
            } else if (getString(R.string.subcamera_name) .equals(name)) {
                classname = "com.mediatek.factorymode.camera.SubCamera";
            } else if (getString(R.string.rgb_led_name) .equals(name)) {
                classname = "com.mediatek.factorymode.rgb_led.Rgb_led";
            }else if (getString(R.string.kpd_light_name) .equals(name)) {  //cenon_kpd_led
                classname = "com.mediatek.factorymode.kpd_led.kpd_led";
            } else if (getString(R.string.flash_lamp) .equals(name)) {
                classname = "com.mediatek.factorymode.flashlamp.FlashLamp";
            } else if (getString(R.string.hall_name) .equals(name)) {
                classname = "com.mediatek.factorymode.hall.Hall";
            } else if (getString(R.string.factory_data_reset) .equals(name)) {
                classname = "com.mediatek.factorymode.FactoryDataReset";
            } else if (getString(R.string.backtouch_name) .equals(name)) {
                classname = "com.mediatek.factorymode.backtouch.BackTouch";
            } else if (getString(R.string.mobiledata_name) .equals(name)) {
                classname = "com.mediatek.factorymode.MobileData";
            } else if (getString(R.string.weardetection_name) .equals(name)) {
                classname = "com.mediatek.factorymode.WearDetection";
            } else if (getString(R.string.nfc) .equals(name)) {
                classname = "com.mediatek.factorymode.sensor.Nfc";
            } else if (getString(R.string.device_info) .equals(name)) {
                classname = "com.mediatek.factorymode.DeviceInfo";
            } else if (getString(R.string.calibration_settings_title) .equals(name)) {
                intent.setAction("android.intent.action.MAIN");
                intent.setClassName("com.twindroid.sensorcalibration","com.twindroid.sensorcalibration.GSensorCalibration");
                this.startActivity(intent);
                return;
            } else if (getString(R.string.scan_test_apk) .equals(name)) {
                intent.setClassName("com.rongqi.f21.activity","com.rongqi.f21.activity.TestActivity");
                this.startActivity(intent);
                return;
            }

            //恢复出厂设置
            if (getString(R.string.factory_reset) .equals(name)) {
                new AlertDialog.Builder(FactoryMode.this).setTitle(
                        R.string.alert_title).setMessage(
                        R.string.alert_content).setPositiveButton(
                        R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                Intent resetIntent = new Intent("android.intent.action.MASTER_CLEAR");
                                resetIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND); // run with high priority
                                sendBroadcast(resetIntent);
                            }
                        }).setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                            }
                        }).create().show();
                return;
            }
            
            if (getString(R.string.current_consumption_monitored) .equals(name)) {
            	boolean tag = mSp.getBoolean("CurrentConsumptionMonitored", false);
				if (tag) {
			        sendBroadcast(new Intent(CurrentConsumptionService.ACTION_STOP_SERVICE));
			        if (tv_CCM!=null) {
			        	tv_CCM.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
					}
				} else {
					startService(new Intent(this, CurrentConsumptionService.class));
					if (tv_CCM!=null) {
						tv_CCM.setTextColor(getApplicationContext().getResources().getColor(R.color.Blue));
					}
				}
			} else 
            if (getString(R.string.cpu_temperature) .equals(name)) {
            	boolean tag = mSp.getBoolean("CPUTemperatureMonitored", false);
        		Log.d("CPUTemperatureService","tag:"+tag);
				if (tag) {
			        sendBroadcast(new Intent(CPUTemperatureService.ACTION_STOP_SERVICE));
			        if (tv_CTM!=null) {
			        	tv_CTM.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
					}
					
					Editor editor = mSp.edit();
			        editor.putBoolean("CPUTemperatureMonitored", false);
			        editor.apply();
					
				} else {
					startService(new Intent(this, CPUTemperatureService.class));
					if (tv_CTM!=null) {
						tv_CTM.setTextColor(getApplicationContext().getResources().getColor(R.color.Blue));
					}
				}
			} 
            else {
	            intent.setClassName(this, classname);
	            this.startActivity(intent);
			}

        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.PackageIerror);
            builder.setMessage(R.string.Packageerror);
            builder.setPositiveButton("OK", null);
            builder.create().show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.gc();
        Intent intent = new Intent(FactoryMode.this, Report.class);
        startActivity(intent);
    }
}
