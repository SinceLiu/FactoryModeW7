
package com.mediatek.factorymode;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.mediatek.factorymode.FactoryModeFeatureOption;

public class Report extends Activity {
    private SharedPreferences mSp;

    private TextView mSuccess;

    private TextView mFailed;

    private TextView mDefault;

    private List<String> mOkList;

    private List<String> mFailedList;

    private List<String> mDefaultList;

    int itemString[] = {
            R.string.touchscreen_name, R.string.lcd_name, R.string.battery_name,
            /*R.string.KeyCode_name,*/ R.string.speaker_name, R.string.headset_name,
            R.string.microphone_name, R.string.earphone_name, R.string.hall_name, R.string.WiFi,
            R.string.bluetooth_name, R.string.vibrator_name, R.string.telephone_name,
            R.string.backlight_name,/* R.string.memory_name, genju.chen disable */ R.string.gsensor_name,
             R.string.msensor_name, //haiming removed
             R.string.pulse_sensor_name, R.string.air_pressure_sensor_name, R.string.gyroscope_sensro_name,R.string.nfc,//haiming nfc
            // R.string.lsensor_name, R.string.psensor_name,
            /* R.string.sdcard_name, */R.string.camera_name, R.string.subcamera_name,
            R.string.fmradio_name,/* R.string.sim_name,R.string.headsethook_name,*/R.string.rgb_led_name,
             R.string.sim_name, R.string.sim_sdcard_name,R.string.flash_lamp,  //haiming __sim_sdcard_name__
            R.string.kpd_light_name,//cenon_kpd_led 
            R.string.mobiledata_name, R.string.weardetection_name,
            R.string.gps_name, R.string.device_info  //bob.chen disabled
    };
	  

    int itemStringB[] = {
            R.string.touchscreen_name, R.string.lcd_name, R.string.battery_name,
            R.string.KeyCode_name, R.string.speaker_name, R.string.headset_name,
            R.string.microphone_name, R.string.earphone_name, R.string.hall_name, R.string.WiFi,
            R.string.bluetooth_name, R.string.vibrator_name, R.string.telephone_name,
            R.string.backlight_name,/* R.string.memory_name, genju.chen disable */ R.string.gsensor_name,
             R.string.msensor_name, //haiming removed
                 R.string.pulse_sensor_name, R.string.air_pressure_sensor_name, R.string.gyroscope_sensro_name, R.string.nfc,//haiming nfc
                R.string.kpd_light_name,//cenon_kpd_led 
            R.string.lsensor_name, R.string.psensor_name,
            /* R.string.sdcard_name, */R.string.camera_name, R.string.subcamera_name,
            R.string.sim_name, R.string.fmradio_name, /*R.string.sim_name, R.string.headsethook_name,*/R.string.rgb_led_name,
            R.string.sim_sdcard_name, R.string.flash_lamp,  //__sim_sdcard_name__
            R.string.mobiledata_name, R.string.weardetection_name,
            R.string.gps_name,R.string.backtouch_name ,R.string.otg_name, R.string.device_info  //bob.chen disabled
         
	};

    int itemString_A731Q[] = {
            R.string.touchscreen_name, R.string.lcd_name, R.string.battery_name,
            R.string.KeyCode_name, R.string.speaker_name, R.string.headset_name,
            R.string.microphone_name, R.string.earphone_name, /*R.string.hall_name,*/ R.string.WiFi,
            R.string.bluetooth_name, R.string.vibrator_name, R.string.telephone_name,
            R.string.backlight_name,/* R.string.memory_name,   genju.chen disable */R.string.gsensor_name,
            R.string.msensor_name, //haiming removed
            /*R.string.nfc,*///haiming nfc
            	R.string.kpd_light_name,//cenon_kpd_led 
            R.string.lsensor_name, R.string.psensor_name,
            /*    R.string.sdcard_name, */R.string.camera_name, R.string.subcamera_name,
            R.string.fmradio_name, /*R.string.sim_name,*/R.string.rgb_led_name,R.string.sim_sdcard_name, //__sim_sdcard_name__
            R.string.gps_name, /*R.string.backtouch_name,*/ R.string.otg_name,  //bob disabled
            R.string.flash_lamp, R.string.calibration_settings_title, R.string.device_info
          
	};

    int itemString_MB4[] = {
            R.string.touchscreen_name, R.string.lcd_name, R.string.battery_name,
            R.string.KeyCode_name, R.string.speaker_name, R.string.headset_name,
            R.string.microphone_name, R.string.earphone_name, /*R.string.hall_name, */R.string.WiFi,
            R.string.bluetooth_name, R.string.vibrator_name, R.string.telephone_name,
            R.string.backlight_name,/* R.string.memory_name,   genju.chen disable */R.string.gsensor_name,
            R.string.lsensor_name, R.string.psensor_name,
               R.string.nfc,//haiming nfc
               	R.string.kpd_light_name,//cenon_kpd_led 
            /*R.string.sdcard_name,*/ R.string.camera_name, R.string.subcamera_name,
            R.string.fmradio_name, /*R.string.sim_name,*/ R.string.rgb_led_name,R.string.sim_sdcard_name, //__sim_sdcard_name__
            R.string.gps_name, R.string.otg_name, 
            R.string.flash_lamp, R.string.device_info
		
    };
    
    int itemString_Mine[] = {
    		R.string.current_consumption_monitored,
            R.string.touchscreen_name,
            R.string.lcd_name,
            R.string.battery_name,
            R.string.adc_check_name,
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
//            R.string.gps1_name,
            R.string.gps2_name,
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
            R.string.device_info
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

            itemString = itemString_Mine;
        
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mSuccess = (TextView) findViewById(R.id.report_success);
        mFailed = (TextView) findViewById(R.id.report_failed);
        mDefault = (TextView) findViewById(R.id.report_default);
        mOkList = new ArrayList<String>();
        mFailedList = new ArrayList<String>();
        mDefaultList = new ArrayList<String>();

        for (int i = 0; i < itemString.length; i++) {
             
             if(true){	 
                      if(getString(R.string.rgb_led_name)  == getString(itemString[i])){	 
                         continue;
                        }
            }
            if(false){	 
                if(getString(R.string.gps_name)  == getString(itemString[i])){	 
                    continue;
                }
            }
			if(!FactoryModeFeatureOption.CENON_HALL_FEATURE){	 
                if(getString(R.string.hall_name)  == getString(itemString[i])){	 
                    continue;
                }
            }	
			if(!FactoryModeFeatureOption.CENON_FACTORY_BACKTOUCH){	 
                    if(getString(R.string.backtouch_name)  == getString(itemString[i])){	 
                        continue;
                    }
                }
				if(!FactoryModeFeatureOption.CENON_FACTORY_KPDLED){	 
                    if(getString(R.string.kpd_light_name)  == getString(itemString[i])){	 
                        continue;
                    }
                }
			 if(!FactoryModeFeatureOption.CENON_MAGNETIC_FEATURE){	 
                    if(getString(R.string.msensor_name)  == getString(itemString[i])){	 
                        continue;
                    }
                }
	      	if(!FactoryModeFeatureOption.CENON_NFC){//!FeatureOption.CENON_MAGNETIC_FEATURE){	 
                    if(getString(R.string.nfc)  == getString(itemString[i])){	 
                        continue;
                    }
                }

       String mValue = mSp.getString(getString(itemString[i]), null);
	 if(mValue != null){
            if (mSp.getString(getString(itemString[i]), null).equals(AppDefine.FT_SUCCESS)) {
                mOkList.add(getString(itemString[i]));
            } else if (mSp.getString(getString(itemString[i]), null).equals(AppDefine.FT_FAILED)) {
                mFailedList.add(getString(itemString[i]));
            } else {
                mDefaultList.add(getString(itemString[i]));
            }
        }
        }
        ShowInfo();
    }

    protected void ShowInfo() {
        String okItem = "\n" + getString(R.string.report_ok) + "\n";
        for (int i = 0; i < mOkList.size(); i++) {
            okItem += mOkList.get(i) + " | ";
        }

        mSuccess.setText(okItem);

        String failedItem = "\n" + getString(R.string.report_failed) + "\n";
        for (int j = 0; j < mFailedList.size(); j++) {
            failedItem += mFailedList.get(j) + " | ";
        }
        mFailed.setText(failedItem);

        String defaultItem = "\n" + getString(R.string.report_notest) + "\n";
        for (int k = 0; k < mDefaultList.size(); k++) {
            defaultItem += mDefaultList.get(k) + " | ";
        }
        mDefault.setText(defaultItem);
    }
}
