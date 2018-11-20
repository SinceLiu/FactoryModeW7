/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 */
/* MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek Software")
 * have been modified by MediaTek Inc. All revisions are subject to any receiver's
 * applicable license agreements with MediaTek Inc.
 */

package com.mediatek.factorymode;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;

public class CPUTemperatureUtil {

    private static final String TAG = "CPUTemperatureUtil";
    public static final int EVENT_UPDATE = 1;

    
    private static final int UPDATE_INTERVAL = 1000;//250;//500; // 0.5 sec
    public static class FunctionThread extends Thread {

        private String TEMP_PATH = "/sys/devices/virtual/thermal/thermal_zone1/temp";
        
        private Handler mUpdateHandler;
        private boolean mRun = false;
    	
    	public FunctionThread(Handler updateHandler) {
    		mUpdateHandler = updateHandler;
    	}
    	
    	public void setRunTag(boolean run) {
    		mRun = run;
    	}
    	
        @Override
        public void run() {
            while (mRun) {
            	
            	File file = new File(TEMP_PATH);
    			int temperature = 0;
        		if (file != null && file.exists()) {
        			//MyLog.i(TAG, TEMP_PATH + " exist");
        			try {
        				RandomAccessFile raf = new RandomAccessFile(file, "r");
        				String line = raf.readLine();
        				raf.close();
        				temperature = Integer.valueOf(line)/1000;
        				//MyLog.i(TAG, "-------temperature : " + line + ", temp_num:" + temperature);
        			} catch (Exception e) {
        				Log.i(TAG, "read temperature failed cause:" + e);
        			}
        		}
            	
                Bundle b = new Bundle();
                /*b.putString("INFO", ""+(int)currentConsumption+"mA");*/
                b.putString("INFO", ""+temperature+"℃");

                Message msg = new Message();
                msg.what = EVENT_UPDATE;
                msg.setData(b);

                mUpdateHandler.sendMessage(msg);
                try {
                    sleep(UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                	Log.e(TAG, "Catch InterruptedException");
                }
            }
        }
    }

}
