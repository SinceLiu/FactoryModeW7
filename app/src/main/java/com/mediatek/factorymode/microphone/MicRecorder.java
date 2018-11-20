package com.mediatek.factorymode.microphone;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;
import com.mediatek.factorymode.VUMeter;

import android.media.AudioManager;
import android.os.Message;
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Window;
import android.util.Log;

public class MicRecorder extends BaseTestActivity implements OnClickListener {

    private Button mRecord;
    private Button mBtMicOk;
    private Button mBtMicFailed;
    private Button mBtSpkOk;
    private Button mBtSpkFailed;

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;

    boolean mMicClick = false;
    boolean mSpkClick = false;
    boolean mMicTestOk = false;
    boolean mSpkTestOk = false;

    VUMeter mVUMeter;
    SharedPreferences mSp;
    private File mSampleFile;

    private String RECORD_SOURCE_DIR = "mictestDatas";
    private String RECORD_SOURCE_NAME_PREFIX = "mictest";
    /*private String RECORD_SOURCE_NAME_SUFFIX = ".amr";*/
    private String RECORD_SOURCE_NAME_SUFFIX = "";
      
    private static final int HANDLER_MSG_START_RECORD = 1;
    private static final int HANDLER_MSG_STOP_RECORD_AND_PLAY = 2;

    private RecoderControlHandler mRecoderControlHandler;

    Handler VUMeter_hander = new Handler();
    Runnable VUMeter_runnable = new Runnable() {
        @Override
        public void run() {
            mVUMeter.invalidate();
            VUMeter_hander.postDelayed(this, 100);
        }
    };

    AudioManager audioManager;

    private int oldMode;
    private int oldVolume;
    
   class  RecoderControlHandler extends Handler {
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            
            switch (msg.what)  {  
                case HANDLER_MSG_START_RECORD:
                    mRecord.setEnabled(false);
                    mRecoderControlHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecord.setEnabled(true);
                        }
                    },1000);
                   VUMeter_hander.post(VUMeter_runnable);
                    start();
                    break;
                case HANDLER_MSG_STOP_RECORD_AND_PLAY:
                    mRecord.setEnabled(false);
                    mRecord.setText(R.string.Mic_start);
                    mVUMeter.SetCurrentAngle(0);
                    VUMeter_hander.removeCallbacks(VUMeter_runnable);
                    stopRecording();
                    new Thread(new Runnable() {
                        public void run() {
                            stopAndPlay();
                        }
                    }).start();
                    break;
                default:
                    break;
            }
        }  
   }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ActionBar.LayoutParams lp =new  ActionBar.LayoutParams(
        // android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        // android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        // Gravity.CENTER);

        // View mView =  LayoutInflater.from(this).inflate(R.layout.title, new LinearLayout(this), false);
        // TextView mTextView = (TextView) mView.findViewById(R.id.action_bar_title);
        //getActionBar().setCustomView(mView, lp); 
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // mTextView.setText(getTitle());

        //getActionBar().setDisplayShowHomeEnabled(false);
        //getActionBar().setDisplayShowTitleEnabled(false);
        //getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getActionBar().setDisplayShowCustomEnabled(true);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        setContentView(R.layout.micrecorder);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        mRecord = (Button) findViewById(R.id.mic_bt_start);
        mRecord.setOnClickListener(this);
        mBtMicOk = (Button) findViewById(R.id.mic_bt_ok);
        mBtMicOk.setOnClickListener(this);
        mBtMicFailed = (Button) findViewById(R.id.mic_bt_failed);
        mBtMicFailed.setOnClickListener(this);
        mBtSpkOk = (Button) findViewById(R.id.speaker_bt_ok);
        mBtSpkOk.setOnClickListener(this);
        mBtSpkFailed = (Button) findViewById(R.id.speaker_bt_failed);
        mBtSpkFailed.setOnClickListener(this);
        mVUMeter = (VUMeter) findViewById(R.id.uvMeter);

        mRecoderControlHandler = new RecoderControlHandler();
        
        deleteRecordResourceDir();
        
        
        audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        oldMode = audioManager.getRingerMode();
        oldVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_PLAY_SOUND);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager.setMode(AudioManager.MODE_NORMAL);
    }

    protected void onResume() {
        super.onResume();
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_PLAY_SOUND);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();

        audioManager.setRingerMode(oldMode);
    	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 
    			oldVolume,
                AudioManager.FLAG_PLAY_SOUND);
    }

    protected void onDestroy() {
        super.onDestroy();

        stopRecording();
        stopPlaying();
//        deleteRecordResource();
        deleteRecordResourceDir();
    }

    public void isFinish(){
        if(mMicClick == true && mSpkClick == true) {
            if(mMicTestOk && mSpkTestOk) {
                Utils.SetPreferences(this, mSp, R.string.microphone_name, AppDefine.FT_SUCCESS);
            } else {
                Utils.SetPreferences(this, mSp, R.string.microphone_name, AppDefine.FT_FAILED);
            }
//            deleteRecordResource();
            deleteRecordResourceDir();
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mRecord.getId()) {
        	if (mRecord.getTag() == null || !mRecord.getTag().equals("ing")) {
                mRecoderControlHandler.sendEmptyMessage(HANDLER_MSG_START_RECORD);
            } else {
                mRecoderControlHandler.sendEmptyMessage(HANDLER_MSG_STOP_RECORD_AND_PLAY);
            }
        }

        if (v.getId() == mBtMicOk.getId()) {
            mMicClick = true;
            mMicTestOk = true;
            mBtMicFailed.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mBtMicOk.setBackgroundColor(this.getResources().getColor(R.color.Green));
            Utils.SetPreferences(this, mSp, R.string.microphone_name, AppDefine.FT_SUCCESS);
        } else if (v.getId() == mBtMicFailed.getId()) {
            mMicClick = true;
            mMicTestOk = false;
            mBtMicOk.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mBtMicFailed.setBackgroundColor(this.getResources().getColor(R.color.Red));
            Utils.SetPreferences(this, mSp, R.string.microphone_name, AppDefine.FT_FAILED);
        }
        if (v.getId() == mBtSpkOk.getId()) {
            mSpkClick = true;
            mSpkTestOk = true;
            mBtSpkFailed.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mBtSpkOk.setBackgroundColor(this.getResources().getColor(R.color.Green));
            Utils.SetPreferences(this, mSp, R.string.microphone_name, AppDefine.FT_SUCCESS);
        } else if (v.getId() == mBtSpkFailed.getId()) {
            mSpkClick = true;
            mSpkTestOk = false;
            mBtSpkOk.setBackgroundColor(this.getResources().getColor(R.color.gray));
            mBtSpkFailed.setBackgroundColor(this.getResources().getColor(R.color.Red));
            Utils.SetPreferences(this, mSp, R.string.microphone_name, AppDefine.FT_FAILED);
        }
        isFinish();
    }

   private void start() {
        mRecord.setText(R.string.Mic_stop);
        String sDcString = Environment.getExternalStorageState();
        if (!sDcString.equals(Environment.MEDIA_MOUNTED)) {
            mRecord.setText(R.string.sdcard_tips_failed);
            return;
        }
        deleteRecordResource();
        getSampleFile();
        startRecord();
        mVUMeter.setRecorder(mRecorder);
        mRecord.setTag("ing");
    }

    private void startRecord() {
        stopPlaying();
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mRecorder.setOutputFile(mSampleFile.getAbsolutePath());
            mRecorder.setOnErrorListener(new OnErrorListener() {
				
				@Override
				public void onError(MediaRecorder mr, int what, int extra) {
		            Toast.makeText(MicRecorder.this, 
		            		"MediaRecorder Error what="+what+" extra="+extra, Toast.LENGTH_SHORT).show();
					Log.e("FactoryMode", "MediaRecorder Error what="+what+" extra="+extra);
	                mRecoderControlHandler.sendEmptyMessage(HANDLER_MSG_STOP_RECORD_AND_PLAY);
				}
			});

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                mRecoderControlHandler.sendEmptyMessage(HANDLER_MSG_STOP_RECORD_AND_PLAY);
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
                return;
             } 
        } catch (Exception e) {
            e.printStackTrace();
            mRecoderControlHandler.sendEmptyMessage(HANDLER_MSG_STOP_RECORD_AND_PLAY);
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            return;
        }

        mRecorder.start();
    }
    
    private void stopRecording() {
        if(null != mRecorder) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        mVUMeter.setRecorder(null);
    }

    private void startPlay() {
        try {
            mPlayer = new MediaPlayer();
            Log.e("FactoryMode", "MediaRecorder startPlay size:"+mSampleFile.length());
            mPlayer.setDataSource(mSampleFile.getAbsolutePath());
            mPlayer.prepare();
            mPlayer.start();

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                    mRecoderControlHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mRecord.setEnabled(true);
                        }
                    });
                }
            });
            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    stopPlaying();
                    mRecoderControlHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mRecord.setEnabled(true);
                        }
                    });
                    return false;
                }
            });
        } catch (IllegalArgumentException  e) {
                e.printStackTrace();
        } catch (IllegalStateException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
    
    private void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void stopAndPlay() {
        mRecord.setTag("stop");
        startPlay();
    }

    private void deleteRecordResource() {
        if(mSampleFile == null) {
            return;
        }
        if(mSampleFile.exists()) {
            mSampleFile.delete();
        }
    }
    
    private void deleteRecordResourceDir() {
        String sampleDir = Environment.getExternalStorageDirectory() + "/" 
        		+ RECORD_SOURCE_DIR;
        File destDir = new File(sampleDir);
        if(!destDir.exists()) {
            return;
        }
        
		File files[] = destDir.listFiles();
		if (files!=null) {
			int allNum = files.length;
			for (int i = 0; i < allNum; i++) {
				files[i].delete();
			}
		}
		destDir.delete();
    }

    private void getSampleFile() {
    	String sampleDir = Environment.getExternalStorageDirectory() + "/" 
        		+ RECORD_SOURCE_DIR;
        File destDir = new File(sampleDir);
        if(!destDir.exists()) {
			destDir.mkdirs();
        }
        mSampleFile = new File(
        		sampleDir
        		+ "/"
        		+ RECORD_SOURCE_NAME_PREFIX 
        		+ System.currentTimeMillis()
        		+ RECORD_SOURCE_NAME_SUFFIX);
    }
}
