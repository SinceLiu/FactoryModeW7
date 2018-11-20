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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BatteryUtil {

    private static final String TAG = "BatteryUtil";

    private static String mCmdString = "cat /sys/devices/platform/battery/";
    public static final int EVENT_UPDATE = 1;
    private static final float FORMART_TEN = 10.0f;

    private static final String[] mFiles = { "FG_Battery_CurrentConsumption", "mA" };
    
    private static float getMeanBatteryVal(String filePath, int totalCount, int intervalMs) {
        float meanVal = 0.0f;
        if (totalCount <= 0) {
            return 0.0f;
        }
        for (int i = 0; i < totalCount; i++) {
            try {
                float f = Float.valueOf(getFileContent(filePath));
                meanVal += f / totalCount;
            } catch (NumberFormatException e) {
                Log.e("EM-PMU", "getMeanBatteryVal invalid result from cmd:" + filePath);
            }
            if (intervalMs <= 0) {
                continue;
            }
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
            	Log.e(TAG, "Catch InterruptedException");
            }
        }
        return meanVal;
    }

    private static String getFileContent(String filePath) {
        if (filePath == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            char[] buffer = new char[500];
            int ret = -1;
            while ((ret = reader.read(buffer)) != -1) {
                builder.append(buffer, 0, ret);
            }
        } catch (IOException e) {
        	Log.e(TAG, "IOException:" + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                	Log.e(TAG, "IOException:" + e.getMessage());
                }
            }
        }
        String result = builder.toString();
        if (result != null) {
            result = result.trim();
        }
        return result;
    }

    private static double getCurrentConsumption(){
        String cmd = "";
        cmd = mCmdString + mFiles[0];
        String filePath = cmd;
        if (filePath.startsWith("cat ")) {
            filePath = filePath.substring(3).trim();
        }
        
    	double f = 0.0f;
        if (mFiles[0].equalsIgnoreCase("FG_Battery_CurrentConsumption")) {
            f = getMeanBatteryVal(filePath, 1, 0) / FORMART_TEN;
        } else {
            try {
                f = Float.valueOf(getFileContent(filePath)) / FORMART_TEN;
            } catch (NumberFormatException e) {
            	Log.e("EM-PMU", "read file error " + mFiles[0]);
            }
        }
        return f;
    }
    
    private static final int UPDATE_INTERVAL = 1000;//250;//500; // 0.5 sec
    public static class FunctionThread extends Thread {

        private int count = 0;
        private int max = 600;
        private double[] consumptions = new double[max];
        private boolean full = false;

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
            	double currentConsumption = getCurrentConsumption();
            	
            	consumptions[count%max] = currentConsumption;
            	if (count == max-1) {
            		full = true;
				}
            	count = (count+1)%max;
            	double all = 0;
            	for (int i = 0; i < consumptions.length; i++) {
            		all += consumptions[i];
				}
            	double d = all/(full?max:count);

                Bundle b = new Bundle();
                /*b.putString("INFO", ""+(int)currentConsumption+"mA");*/
                b.putString("INFO", ""+(int)d+"mA");

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
