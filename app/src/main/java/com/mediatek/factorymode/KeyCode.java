
package com.mediatek.factorymode;

import java.util.ArrayList;
import java.util.List;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

public class KeyCode extends Activity implements OnClickListener {
    private static final String TAG = "keycode";
    SharedPreferences mSp;

    TextView mInfo;

    Button mBtOk;

    Button mBtFailed;

    String mKeycode = "";

    private GridView mGrid;

    private List<Integer> mListData;

    final int imgString[] = {
            R.drawable.home, R.drawable.menu, R.drawable.vldown, R.drawable.vlup, R.drawable.back,
            R.drawable.search, R.drawable.camera, R.drawable.unknown
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keycode);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        onAttachedToWindow();
        mInfo = (TextView) findViewById(R.id.keycode_info);
        mBtOk = (Button) findViewById(R.id.keycode_bt_ok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.keycode_bt_failed);
        mBtFailed.setOnClickListener(this);
        mListData = new ArrayList<Integer>();
        mGrid = (GridView) findViewById(R.id.keycode_grid);
    }


    @Override
    public void onResume( ) {
    	 super.onResume();
	  //Settings.System.putInt(this.getContentResolver(),  Settings.System.CENON_IGNORE_HOME_POWER, 1);
	  //Log.d(TAG, "put Settings.System.CENON_IGNORE_HOME_POWER 1");
	  
    	}
	  

   @Override
       public boolean dispatchKeyEvent(KeyEvent event) {

	    //Log.d(TAG, "dispatchKeyEvent: keyCode is : " + event.getKeyCode());

		if(KeyEvent.KEYCODE_HOME == event.getKeyCode()) {
			switch(event.getAction()) {
				case  KeyEvent.ACTION_DOWN:
					if (mKeycode.indexOf("HOME") >= 0) {
						return false;
					  }
					mKeycode += "HOME\n";
					mListData.add(imgString[0]);
					break;
				case KeyEvent.ACTION_UP:
					break;
				default:
					break;
			}
			mGrid.setAdapter(new MyAdapter(this));
			return true;
		}

        return super.dispatchKeyEvent(event);
       }
   
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v(TAG, "onKeyDown: keyCode=" + keyCode + " event=" + event);
		
        switch (keyCode) {
            case KeyEvent.KEYCODE_CAMERA:
                if (mKeycode.indexOf("CAMERA") >= 0) {
                    return false;
                }
                mKeycode += "CAMERA\n";
                mListData.add(imgString[6]);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (mKeycode.indexOf("VLDOWN") >= 0) {
                    return false;
                }
                mKeycode += "VLDOWN\n";
                mListData.add(imgString[2]);
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (mKeycode.indexOf("VLUP") >= 0) {
                    return false;
                }
                mKeycode += "VLUP\n";
                mListData.add(imgString[3]);
                break;
            case KeyEvent.KEYCODE_BACK:
                if (mKeycode.indexOf("BACK") >= 0) {
                    return false;
                }
                mKeycode += "BACK\n";
                mListData.add(imgString[4]);
                break;
            case KeyEvent.KEYCODE_SEARCH:
                if (mKeycode.indexOf("SEARCH") >= 0) {
                    return false;
                }
                mKeycode += "SEARCH\n";
                mListData.add(imgString[5]);
                break;
            case KeyEvent.KEYCODE_MENU:
                if (mKeycode.indexOf("MENU") >= 0) {
                    return false;
                }
                mKeycode += "MENU\n";
                mListData.add(imgString[1]);
                break;
            /*case KeyEvent.KEYCODE_PHONE_MUTE: //bob.chen add mute, 20120413
                if (mKeycode.indexOf("MUTE") >= 0) {
                    return false;
                }
                mKeycode += "MUTE\n";
                mListData.add(imgString[1]); //Bob.chen use this icon temp
                break;*/
            default:
                mListData.add(imgString[7]);
                break;

        }
        mGrid.setAdapter(new MyAdapter(this));
        return true;
    }


    @Override 
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    		boolean is_keycode_invalid = true;
    		switch (keyCode) {
	            case KeyEvent.KEYCODE_CAMERA:
	            case KeyEvent.KEYCODE_VOLUME_DOWN:
	            case KeyEvent.KEYCODE_VOLUME_UP:
	            case KeyEvent.KEYCODE_BACK:
	            case KeyEvent.KEYCODE_SEARCH:
	            case KeyEvent.KEYCODE_MENU:
	                break;
    		}
		return is_keycode_invalid;
    }	
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
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.keycode_grid, null);
            ImageView imgview = (ImageView) convertView.findViewById(R.id.imgview);
            imgview.setBackgroundResource(mListData.get(position));
            return convertView;
        }
    }

    @Override
    public void onClick(View v) {
        Utils.SetPreferences(this, mSp, R.string.KeyCode_name,
                (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
       // Settings.System.putInt(this.getContentResolver(),  Settings.System.CENON_IGNORE_HOME_POWER, 0);
        finish();
    }

    @Override
    public void onPause() {
	        super.onPause();
	    //    Settings.System.putInt(this.getContentResolver(),  Settings.System.CENON_IGNORE_HOME_POWER, 0);
    	}
}
