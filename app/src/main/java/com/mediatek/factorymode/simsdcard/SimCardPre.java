
package com.mediatek.factorymode.simsdcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.telephony.TelephonyManager;
import android.os.ServiceManager;
import android.os.RemoteException;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneFactory;
import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

import android.telephony.SubscriptionManager;

import com.mediatek.factorymode.FactoryModeFeatureOption;
import com.android.internal.telephony.ITelephony;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimCardPre extends BaseTestActivity {


    private boolean Sim1State = false;

    private boolean Sim2State = false;

    private Phone mPhone = null;

    SharedPreferences mSp;

    private String mSimStatus = "";

    private static final String TAG = "SimCardPre";

    private static boolean finishActivity = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	ActionBar.LayoutParams lp =new  ActionBar.LayoutParams(
        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        Gravity.CENTER);

	 View mView =  LayoutInflater.from(this).inflate(R.layout.title, new LinearLayout(this), false);
	 TextView mTextView = (TextView) mView.findViewById(R.id.action_bar_title);
	getActionBar().setCustomView(mView, lp); 
	
	mTextView.setText(getTitle());
	 
	getActionBar().setDisplayShowHomeEnabled(false);
	getActionBar().setDisplayShowTitleEnabled(false);
	getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	getActionBar().setDisplayShowCustomEnabled(true);
	getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        mSimStatus = "";
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        if (FactoryModeFeatureOption.MTK_GEMINI_SUPPORT) {
            mPhone = PhoneFactory.getDefaultPhone();
            Sim1State = isSimInserted(PhoneConstants.SIM_ID_1);
            Sim2State = isSimInserted(PhoneConstants.SIM_ID_2);

            if (Sim1State) {
                mSimStatus += getString(R.string.sim1_info_ok) + "\n";
            } else {
                mSimStatus += getString(R.string.sim1_info_failed) + "\n";
            }
            if (Sim2State) {
                mSimStatus += getString(R.string.sim2_info_ok) + "\n";
            } else {
                mSimStatus += getString(R.string.sim2_info_failed) + "\n";
            }
        } else {
            Sim1State = TelephonyManager.getDefault().hasIccCard();
            if (Sim1State) {
                mSimStatus += getString(R.string.sim_info_ok) + "\n";
            } else {
                mSimStatus += getString(R.string.sim_info_failed) + "\n";
            }
        }

     /*   AlertDialog.Builder builder = new AlertDialog.Builder(SimCardPre.this);
        builder.setTitle(R.string.FMRadio_notice);
        builder.setMessage(mSimStatus);
        builder.setPositiveButton(R.string.Success, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Utils.SetPreferences(SimCardPre.this, mSp, R.string.sim_name, AppDefine.FT_SUCCESS);
            //    isFinish();
            finish();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.Failed),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Utils.SetPreferences(SimCardPre.this, mSp, R.string.sim_name,
                                AppDefine.FT_FAILED);
                     //   isFinish();
                     finish();
                    }
                });
        builder.create().show();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        finishActivity = finishActivity ? false : true;
        if(finishActivity) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void isFinish(){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        String classname = "com.mediatek.factorymode.simsdcard.SimSDCard";
        intent.setClassName(this, classname);
        this.startActivity(intent);
        //finish();
    }

    private boolean isSimInserted(int slotId) { //added by ellery

            final ITelephony iTel = ITelephony.Stub.asInterface(ServiceManager
                .getService(Context.TELEPHONY_SERVICE));

            boolean isSimInsert = false;
            
            try {
                if (iTel != null) {
                    isSimInsert = iTel.hasIccCardUsingSlotId(slotId);
                }
             } catch (RemoteException e) {
                e.printStackTrace();
                isSimInsert = false;
             }

         return isSimInsert;
    }

    

}
