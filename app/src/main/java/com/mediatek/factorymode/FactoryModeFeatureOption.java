
package com.mediatek.factorymode;

import android.os.SystemProperties;
import android.util.Log;

public class FactoryModeFeatureOption{
 public static final boolean CENON_KEYCODE_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_keycode_support","0"));
 public static final boolean CENON_NFC = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_nfc_support","0"));
 public static final boolean CENON_MAGNETIC_FEATURE = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_magnetic_support","0"));
 public static final boolean CENON_HALL_FEATURE = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_hall_support","0"));
 public static final boolean CENON_FACTORY_BACKTOUCH = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_backtouch_support","0"));
 public static final boolean CENON_FACTORY_KPDLED = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_kpdled_support","0"));
 public static final boolean MTK_GEMINI_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_gemini_support","0"));
 public static final boolean MTK_SIM_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_simcard_support","0"));
 public static final boolean CENON_GPS_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_gps_support","0"));
 public static final boolean CENON_LSENSOR_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_lensor_support","0"));   
 public static final boolean CENON_PSENSOR_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_psensor_support","0"));
 public static final boolean CENON_GSENSOR_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_gsensor_support","0"));
 public static final boolean CENON_PULSE_SENSOR_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_pulse_sensor_support","0"));
 public static final boolean CENON_GYROSCOPE_SENSOR_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_gyroscope_support","0"));
 public static final boolean CENON_AIR_SENSOR_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_air_sensor_support","0"));
 public static final boolean CENON_WEAR_SENSOR_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_wear_sensor_support","0"));
 public static final boolean CENON_ADC_CHECK_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_adc_check","0"));
 public static final boolean CENON_TP_FW_VERSION_SUPPORT = "1".equalsIgnoreCase(SystemProperties.get("ro.cenon_tp_fw","0"));
}
