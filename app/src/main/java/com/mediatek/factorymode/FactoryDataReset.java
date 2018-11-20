package com.mediatek.factorymode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
//import com.android.internal.os.storage.ExternalStorageFormatter;

public class FactoryDataReset extends Activity {

	private static final String MASTER_CLEAR = "android.intent.action.MASTER_CLEAR";
	private boolean mEraseSdCard = false;

	private Button ResetPhone;
	private CheckBox EraseSdCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.factory_data_reset);
		ResetPhone = (Button) findViewById(R.id.rest_phone);
		ResetPhone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(FactoryDataReset.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.reset_dialog)
					.setMessage(R.string.reset_dialog_label)
					.setPositiveButton(R.string.reset_dialog_comfirm,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									onResetPhone(FactoryDataReset.this, 0, mEraseSdCard);
								}
							})
					.setNegativeButton(R.string.reset_dialog_cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									return;
								}
							})
					.create().show();
			}
		});
		EraseSdCard = (CheckBox) findViewById(R.id.erase_sdcard);
		EraseSdCard.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean  isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					mEraseSdCard = true;
				}else {
					mEraseSdCard = false;
				}
			}
		});;
	}

	public void onResetPhone(Activity activity, int eraseInternalData,
			boolean eraseSdCard) {
		System.out.println("eraseSdCard="+eraseSdCard);
		if (eraseSdCard) {
			/*Intent intent = new Intent(
					ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
			intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
			activity.startService(intent);*/
			activity.sendBroadcast(new Intent(MASTER_CLEAR));
		} else {
			activity.sendBroadcast(new Intent(MASTER_CLEAR));
		}
	}

}
