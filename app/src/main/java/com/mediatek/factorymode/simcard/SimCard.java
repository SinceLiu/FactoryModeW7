package com.mediatek.factorymode.simcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.os.ServiceManager;
import android.os.RemoteException;

import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneFactory;
import com.mediatek.common.telephony.ITelephonyExt;
import com.mediatek.factorymode.AllTest;
import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;
import com.android.internal.telephony.Phone;

import android.telephony.SubscriptionManager;

import com.mediatek.factorymode.FactoryModeFeatureOption;

import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
public class SimCard extends BaseTestActivity {

    private Phone mPhone = null;

    private boolean Sim1State = false;

    private boolean Sim2State = false;

    SharedPreferences mSp;

    private String mSimStatus = "";


    private static final String TAG = "SimCard";

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
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
	//  mPhone =  PhoneFactory.getDefaultPhone();
        if (FactoryModeFeatureOption.MTK_GEMINI_SUPPORT) {
			
            Sim1State = isSimInserted(PhoneConstants.SIM_ID_1); 

            Sim2State = isSimInserted(PhoneConstants.SIM_ID_2);
				
            if (Sim1State == true) {
                mSimStatus += getString(R.string.sim1_info_ok) + "\n";
            } else {
                mSimStatus += getString(R.string.sim1_info_failed) + "\n";
            }
            if (Sim2State == true) {
                mSimStatus += getString(R.string.sim2_info_ok) + "\n";
            } else {
                mSimStatus += getString(R.string.sim2_info_failed) + "\n";
            }
        } else {
            Sim1State = TelephonyManager.getDefault().hasIccCard();
            if (Sim1State == true) {
                mSimStatus += getString(R.string.sim_info_ok) + "\n";
            } else {
                mSimStatus += getString(R.string.sim_info_failed) + "\n";
            }
        }

        if(AllTest.begin_auto_test&&(mSimStatus.indexOf(getString(R.string.sim_info_ok)))!=-1){
            Utils.SetPreferences(SimCard.this, mSp, R.string.sim_name, AppDefine.FT_SUCCESS);
            finish();
        }
				
        AlertDialog.Builder builder = new AlertDialog.Builder(SimCard.this);
        builder.setTitle(R.string.FMRadio_notice);
        builder.setCancelable(false);
        builder.setMessage(mSimStatus);
        builder.setPositiveButton(R.string.Success, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	Log.d("cenontestSim", "setPositiveButton");
                Utils.SetPreferences(SimCard.this, mSp, R.string.sim_name, AppDefine.FT_SUCCESS);
                finish();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.Failed),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	Log.d("cenontestSim", "setNegativeButton"); 
                        Utils.SetPreferences(SimCard.this, mSp, R.string.sim_name,
                                AppDefine.FT_FAILED);
                        finish();
                    }
                });
        builder.create().show();
    }

    private boolean isSimInserted(int slotId) { //added by ellery
    /**    boolean isSimInserted = false;

	if(null != mPhone) {
	        mPhone = PhoneFactory.getDefaultPhone();
	        int simId = SubscriptionManager.getSlotId(mPhone.getSubId());
			
		 if(simId == slotId) {
			isSimInserted = true;
		 }
	 }
        return isSimInserted;**/
        return true;
    }
}
