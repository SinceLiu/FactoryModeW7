
package com.mediatek.factorymode.camera;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

public class CameraTest extends BaseTestActivity implements OnClickListener {

    private SurfaceView surfaceView;

    private SurfaceHolder surfaceHolder;

    private Camera mCamera;

    private Button mBtFinish;

    private Button mBtOk;

    private Button mBtFailed;

    SharedPreferences mSp;
    Camera.CameraInfo cameraInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.camera);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        setupViews();
    }

    private void setupViews() {
        surfaceView = (SurfaceView) findViewById(R.id.camera_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceCallback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mBtFinish = (Button) findViewById(R.id.camera_take);
        mBtFinish.setOnClickListener(this);
        mBtOk = (Button) findViewById(R.id.camera_btok);
        mBtOk.setOnClickListener(this);
        mBtFailed = (Button) findViewById(R.id.camera_btfailed);
        mBtFailed.setOnClickListener(this);
        mBtFinish.setEnabled(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_SEARCH) {
            takePic();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void takePic() {
        mBtFinish.setEnabled(false);
        mCamera.takePicture(null, null, pictureCallback);
    }

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            int cameraCount = 0;
            int result = 0;
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 270;
                    break;
                case Surface.ROTATION_90:
                    degrees = 0;
                    break;
                case Surface.ROTATION_180:
                    degrees = 90;
                    break;
                case Surface.ROTATION_270:
                    degrees = 180;
                    break;
                default:
                    break;
            }

            cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    result = (360 - degrees) % 360;
                    try {
                        mCamera = Camera.open(camIdx);
                        mCamera.setDisplayOrientation(result);
                        mCamera.setPreviewDisplay(holder);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mCamera == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Utils.SetPreferences(getApplicationContext(), mSp, R.string.camera_name,
                        AppDefine.FT_FAILED);
                finish();
                return;
            }
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            Size size = parameters.getPreviewSize();

            List<Size> sizes = parameters.getSupportedPreviewSizes();
            Size optimalSize = null;
            if (size != null && size.height != 0) {
                optimalSize = getOptimalPreviewSize(sizes, (double) width / (height));
            }
            if (optimalSize != null) {
                if (true) {
                    parameters.setPreviewSize(optimalSize.width, optimalSize.height);
                    parameters.setPictureSize(optimalSize.width, optimalSize.height);
                } else {
                    parameters.setPreviewSize(optimalSize.height, optimalSize.width);
                    parameters.setPictureSize(optimalSize.height, optimalSize.width);
                }
            }

            mCamera.setParameters(parameters);
            mCamera.startPreview();

            mBtFinish.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mBtFinish.setEnabled(true);
                }
            }, 500);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }
    };

    private Size getOptimalPreviewSize(List<Size> sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.05;
        if (sizes == null){
            return null;
        }
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        Display display = getWindowManager().getDefaultDisplay();
        int targetHeight = Math.min(display.getHeight(), display.getWidth());

        if (targetHeight <= 0) {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            targetHeight = windowManager.getDefaultDisplay().getHeight();
        }

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE){
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBtFinish.getId()) {
            takePic();
        } else if (v.getId() == mBtOk.getId()) {
            Utils.SetPreferences(getApplicationContext(), mSp, R.string.camera_name,
                    AppDefine.FT_SUCCESS);
            finish();
        } else if (v.getId() == mBtFailed.getId()) {
            Utils.SetPreferences(getApplicationContext(), mSp, R.string.camera_name,
                    AppDefine.FT_FAILED);
            finish();
        }
    }
}
