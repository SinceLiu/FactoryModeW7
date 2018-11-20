package com.mediatek.factorymode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class EntranceReceiver extends BroadcastReceiver {
    private static final String TAG = "CIT_EntranceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive Host = " + intent.getData().getHost());
        Intent i = null;
        if (intent.getData().getHost().equals("83789")) {
            i = new Intent(context, FactoryMode.class);
//            i = new Intent(context, TempListActivity.class);
        }

        if (i != null) {
            try {
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } catch (Exception e) {
                // TODO: handle exception
                Log.e(TAG, "startActivity exception "+ e);
            }
        }
    }

}

