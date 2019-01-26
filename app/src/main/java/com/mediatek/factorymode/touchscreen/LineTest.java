package com.mediatek.factorymode.touchscreen;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mediatek.factorymode.AppDefine;
import com.mediatek.factorymode.BaseTestActivity;
import com.mediatek.factorymode.R;
import com.mediatek.factorymode.Utils;

import android.util.Log;
import android.graphics.RectF;

public class LineTest extends BaseTestActivity {

    public boolean mRun = false;

    private boolean mSuccess = true;

    public double mDiversity = 0;

    public List<List<Point>> mPts1 = new ArrayList<List<Point>>();

    private List<Point> mTemPoints = new ArrayList<Point>();

    public List<List<Point>> mInput = new ArrayList<List<Point>>();

    private List<List<Point>> mSuperPts = new ArrayList<List<Point>>();

    public List<Point> mInputLeft = new ArrayList<Point>();

    public List<Point> mInputTop = new ArrayList<Point>();

    public List<Point> mInputRight = new ArrayList<Point>();

    public List<Point> mInputBottom = new ArrayList<Point>();

    public static final int CALCULATE_ID = Menu.FIRST;

    public static final int NEXTLINE_ID = Menu.FIRST + 1;

    private final static int SFVALUE = 30;

    private int mZoom = 1;

    private int mPadding = 10; //50;//20;

    private int mRectWidth;

    private int mRectHeight;

    private int flags = 0;

    private String mResultString = "";

    private SharedPreferences mSp;

    boolean mDrawRectVertexLeft = false;
    boolean mDrawRectVertexTop = false;
    boolean mDrawRectVertexRight = false;
    boolean mDrawRectVertexBottom = false;
    boolean mDrawRectMidpointLeft = false;
    boolean mDrawRectMidpointTop = false;
    boolean mDrawRectMidpointRight = false;
    boolean mDrawRectMidpointBottom = false;

    boolean mIsDrawRect = false;

    boolean mDrawRectSuccess = false;

    boolean mDrawDiagonalSuccess = false;

    boolean mDrawDiagonalSuccess1 = false;

    boolean mDrawDiagonalSuccess2 = false;

    boolean mDrawVerticalLineSuccess = false;

    boolean mDrawHorizontalLineSuccess = false;

    boolean mDrawCrossSuccess = false;

    private ArrayList<RectF> mDiagonalCorner = new ArrayList<RectF>();

    private ArrayList<RectF> mDiagonalCheck = new ArrayList<RectF>();

    public List<Point> mDiagonalDrawPoint1 = new ArrayList<Point>();

    public List<Point> mDiagonalDrawPoint2 = new ArrayList<Point>();

    public List<Point> mVerticalLinePoint = new ArrayList<Point>();

    public List<Point> mHorizontalLinePoint = new ArrayList<Point>();

    public List<RectF> mVerticalCheckPoint = new ArrayList<RectF>();

    public List<RectF> mHorizontalCheckPoint = new ArrayList<RectF>();

    private ArrayList<RectF> mCrossRect = new ArrayList<RectF>();

    private ArrayList<RectF> mCrossCheck = new ArrayList<RectF>();

    int mDiagonalCornerWidth = 35;
    int mDiagonalCheckWidth = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE);
        DisplayMetrics dm = new DisplayMetrics();
        dm = this.getApplicationContext().getResources().getDisplayMetrics();
        mRectWidth = dm.widthPixels;
        mRectHeight = dm.heightPixels;
        if ((480 == mRectWidth && 800 == mRectHeight)
                || (800 == mRectWidth && 480 == mRectHeight)) {
            mZoom = 2;
        }
        readLine();
        setContentView(new CanvasView(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, CALCULATE_ID, 0, "Calculate");
        menu.add(0, NEXTLINE_ID, 1, "NextLine");
        return true;
    }

    @Override
    protected void onDestroy() {
        releaseList();
        super.onDestroy();
    }

    public void CalculateDiversity() {
        Point cp = new Point(0, 0);
        flags = 0;
        if (mInput.size() == 0) {
            mIsDrawRect = false;
            mDrawRectSuccess = false;
            mTemPoints.clear();
            return;
        }
        distributeAllPoint(mInput);
        double error = 0.0;
        for (int j = 0; j < mInputLeft.size(); j++) {
            cp = mInputLeft.get(j);
            error += Math.abs(cp.x - mPadding);
        }

        for (int j = 0; j < mInputTop.size(); j++) {
            cp = mInputTop.get(j);
            error += Math.abs(cp.y - mPadding);
        }

        for (int j = 0; j < mInputRight.size(); j++) {
            cp = mInputRight.get(j);
            error += Math.abs(cp.x - (mRectWidth - mPadding));
        }

        for (int j = 0; j < mInputBottom.size(); j++) {
            cp = mInputBottom.get(j);
            error += Math.abs(cp.y - (mRectHeight - mPadding));
        }

        mDiversity = error
                / (mInputLeft.size() + mInputTop.size() + mInputRight.size() + mInputBottom
                .size());
        mResultString = String.valueOf(mDiversity);

        int value = (int) Float.parseFloat(mResultString);
        if (value > SFVALUE) {
            flags = 1;
        }

        if (mInputLeft.size() < 5 || mInputTop.size() < 5 || mInputRight.size() < 5 || mInputBottom.size() < 5
                || !mDrawRectVertexLeft || !mDrawRectVertexRight || !mDrawRectVertexTop || !mDrawRectVertexBottom
                || !mDrawRectMidpointLeft || !mDrawRectMidpointRight || !mDrawRectMidpointTop || !mDrawRectMidpointBottom) {
            mIsDrawRect = false;
            mDrawRectSuccess = false;
            mTemPoints.clear();
        }
        Log.e("lxx", mDrawRectVertexLeft + "," + mDrawRectVertexBottom + "," + mDrawRectVertexRight + "," + mDrawRectVertexTop
                + "," + mDrawRectMidpointLeft + "," + mDrawRectMidpointBottom + "," + mDrawRectMidpointRight + "," + mDrawRectMidpointTop);
        mDiversity = 0.0;
        mDrawRectVertexLeft = mDrawRectVertexTop = mDrawRectVertexRight = mDrawRectVertexBottom
                = mDrawRectMidpointLeft = mDrawRectMidpointTop = mDrawRectMidpointRight = mDrawRectMidpointBottom = false;
    }

    //是否经过四个顶点和四条边的中点
    public void checkRect(Point point) {
        if (point.x <= mPadding + 10 && point.y <= mPadding + 10) {
            mDrawRectVertexLeft = true;
        }
        if (point.x <= mPadding + 10 && point.y >= mRectHeight - mPadding - 10) {
            mDrawRectVertexBottom = true;
        }
        if (point.x >= mRectWidth - mPadding - 10 && point.y >= mRectHeight - mPadding - 10) {
            mDrawRectVertexRight = true;
        }
        if (point.x >= mRectWidth - mPadding - 10 && point.y <= mPadding + 10) {
            mDrawRectVertexTop = true;
        }
        if (point.x <= mPadding + 10 &&
                point.y >= mRectHeight / 2 - mPadding - 10 && point.y <= mRectHeight / 2 + mPadding + 10) {
            mDrawRectMidpointLeft = true;
        }
        if (point.x >= mRectWidth / 2 - mPadding - 10 && point.x <= mRectWidth / 2 + mPadding + 10 &&
                point.y >= mRectHeight - mPadding - 10) {
            mDrawRectMidpointBottom = true;
        }
        if (point.x >= mRectWidth - mPadding - 10 &&
                point.y >= mRectHeight / 2 - mPadding - 10 && point.y <= mRectHeight / 2 + mPadding + 10) {
            mDrawRectMidpointRight = true;
        }
        if (point.x >= mRectWidth / 2 - mPadding - 10 && point.x <= mRectWidth / 2 + mPadding + 10 &&
                point.y >= mRectHeight - mPadding - 10) {
            mDrawRectMidpointTop = true;
        }
    }

    public void releaseList() {
        mPts1 = null;
        mTemPoints = null;
        mSuperPts = null;
        mInput = null;
        mInputLeft = null;
        mInputTop = null;
        mInputRight = null;
        mInputBottom = null;
    }

    private boolean isBeginOrEndPoint(int x, int y) {
        Log.d("LineTest", "x=" + x + "y=" + y);
		/*if ((x < mPadding * 4 || x > mRectWidth - mPadding * 4)
                && (y < mPadding * 4 || y > mRectHeight - mPadding * 4)) {
			mIsDrawRect = true;
            return true;
        }*/
        if ((x < mPadding * 4)
                && (y < mPadding * 4)) {
            mIsDrawRect = true;
            return true;
        }
        return false;
    }

    private boolean isBeginOrEndDiagonal(int x, int y) {
        for (int i = 0; i < mDiagonalCorner.size(); i++) {
            if (mDiagonalCorner.get(i).contains(x, y)) {
                mDiagonalCorner.remove(i);
                return true;
            }
        }
        return false;
    }

    private boolean isBeginOrEndCross(int x, int y) {
        for (int i = 0; i < mCrossRect.size(); i++) {
            if (mCrossRect.get(i).contains(x, y)) {
                mCrossRect.remove(i);
                return true;
            }
        }
        return false;
    }

    private void resetCrossRect() {
        mCrossRect.clear();
        mCrossRect.add(new RectF(mRectWidth / 2 - mDiagonalCheckWidth / 2, 0, mRectWidth / 2 + mDiagonalCheckWidth / 2, mDiagonalCheckWidth));
        mCrossRect.add(new RectF(mRectWidth / 2 - mDiagonalCheckWidth / 2, mRectHeight - mDiagonalCheckWidth, mRectWidth / 2 + mDiagonalCheckWidth / 2, mRectHeight));
        mCrossRect.add(new RectF(0, mRectHeight / 2 - mDiagonalCheckWidth / 2, mDiagonalCheckWidth, mRectHeight / 2 + mDiagonalCheckWidth / 2));
        mCrossRect.add(new RectF(mRectWidth - mDiagonalCheckWidth, mRectHeight / 2 - mDiagonalCheckWidth / 2, mRectWidth, mRectHeight / 2 + mDiagonalCheckWidth / 2));
    }

    private void clearDrawCross() {
        Log.d("LineTest", "clearDrawCross");
        mHorizontalLinePoint.clear();
        mDrawHorizontalLineSuccess = false;
        mVerticalLinePoint.clear();
        mDrawVerticalLineSuccess = false;
        mDrawCrossSuccess = false;
        resetCrossRect();
    }

    private void addDrawCrossPoint(Point point) {
        if (!mDrawHorizontalLineSuccess) {
            mHorizontalLinePoint.add(point);
        } else if (!mDrawVerticalLineSuccess) {
            mVerticalLinePoint.add(point);
        }
    }

    private int isDrawInCross(List<Point> drawPoint) {
        Point point;
        int crossCheck = 0;
        for (int i = 0; i < mCrossCheck.size(); i++) {
            Log.d("LineTest", "i=" + i + "left=" + mCrossCheck.get(i).left + "top" + mCrossCheck.get(i).top + "right" + mCrossCheck.get(i).right + "bottom" + mCrossCheck.get(i).bottom);
            for (int j = 0; j < drawPoint.size(); j++) {
                point = drawPoint.get(j);
                if (mCrossCheck.get(i).contains(point.x, point.y)) {
                    crossCheck++;
                    break;
                }
            }
        }
        if (crossCheck == 2) {
            if (!mDrawHorizontalLineSuccess) {
                mDrawHorizontalLineSuccess = true;
                Log.d("LineTest", "mDrawHorizontalLineSuccess=true");
            } else {
                mDrawVerticalLineSuccess = true;
                Log.d("LineTest", "mDrawVerticalLineSuccess=true");
            }
        } else {
            clearDrawDiagonal();
            Log.d("LineTest", "mDrawDiagonalSuccess=false crossCheck=" + crossCheck);
        }
        return crossCheck;
    }

    private void resetDiagonalCorner() {
        mDiagonalCorner.clear();
        mDiagonalCorner.add(new RectF(0f, 0f, mDiagonalCornerWidth, mDiagonalCornerWidth));
        mDiagonalCorner.add(new RectF(0f, mRectHeight - mDiagonalCornerWidth, mDiagonalCornerWidth, mRectHeight));
        mDiagonalCorner.add(new RectF(mRectWidth - mDiagonalCornerWidth, 0f, mRectWidth, mDiagonalCornerWidth));
        mDiagonalCorner.add(new RectF(mRectWidth - mDiagonalCornerWidth, mRectHeight - mDiagonalCornerWidth, mRectWidth, mRectHeight));
    }

    private void clearDrawDiagonal() {
        Log.d("LineTest", "clearDrawDiagonal");
        mDiagonalDrawPoint1.clear();
        mDrawDiagonalSuccess1 = false;
        mDiagonalDrawPoint2.clear();
        mDrawDiagonalSuccess2 = false;
        resetDiagonalCorner();
        mDrawDiagonalSuccess = false;
    }

    private void addDrawDiagonalPoint(Point point) {
        if (!mDrawDiagonalSuccess1) {
            mDiagonalDrawPoint1.add(point);
        } else if (!mDrawDiagonalSuccess2) {
            mDiagonalDrawPoint2.add(point);
        }
    }

    private int isDrawInDiagonal(List<Point> diagonalDrawPoint) {
        Point point;
        int diagonalCheck = 0;
        for (int i = 0; i < mDiagonalCheck.size(); i++) {
            Log.d("LineTest", "i=" + i + "left=" + mDiagonalCheck.get(i).left + "top" + mDiagonalCheck.get(i).top + "right" + mDiagonalCheck.get(i).right + "bottom" + mDiagonalCheck.get(i).bottom);
            for (int j = 0; j < diagonalDrawPoint.size(); j++) {
                point = diagonalDrawPoint.get(j);
                if (mDiagonalCheck.get(i).contains(point.x, point.y)) {
                    diagonalCheck++;
                    break;
                }
            }
        }
        if (diagonalCheck == 3) {
            if (!mDrawDiagonalSuccess1) {
                mDrawDiagonalSuccess1 = true;
                Log.d("LineTest", "mDrawDiagonalSuccess1=true");
            } else {
                mDrawDiagonalSuccess2 = true;
                Log.d("LineTest", "mDrawDiagonalSuccess2=true");
            }
        } else {
            clearDrawDiagonal();
            Log.d("LineTest", "mDrawDiagonalSuccess=false diagonalCorner=" + diagonalCheck);
        }
        return diagonalCheck;
    }

    private void distributeAllPoint(List<List<Point>> lists) {
        Point aPoint;
        List<Point> list;
        for (int j = 0; j < lists.size(); j++) {
            list = lists.get(j);
            for (int i = 0; i < list.size(); i++) {
                aPoint = list.get(i);
                checkRect(aPoint);
                if (aPoint.x < mRectWidth / 2) {
                    if (aPoint.y < mRectHeight / 2) {
                        if (aPoint.x > aPoint.y) {
                            mInputTop.add(aPoint);
                        } else {
                            mInputLeft.add(aPoint);
                        }
                    } else {
                        if (aPoint.x > mRectHeight - aPoint.y) {
                            mInputBottom.add(aPoint);
                        } else {
                            mInputLeft.add(aPoint);
                        }
                    }
                } else {
                    if (aPoint.y < mRectHeight / 2) {
                        if (mRectWidth - aPoint.x > aPoint.y) {
                            mInputTop.add(aPoint);
                        } else {
                            mInputRight.add(aPoint);
                        }
                        if (mRectWidth - aPoint.x > mRectHeight - aPoint.y) {
                            mInputBottom.add(aPoint);
                        } else {
                            mInputRight.add(aPoint);
                        }
                    }
                }
            }
        }
    }

    private void readLine() {
        List<Point> temp = new ArrayList<Point>();
        temp.add(new Point(mPadding, mPadding));
        temp.add(new Point(mPadding, mRectHeight - mPadding));
        mPts1.add(temp);
        temp = new ArrayList<Point>();
        temp.add(new Point(mPadding, mPadding));
        temp.add(new Point(mRectWidth - mPadding, mPadding));
        mPts1.add(temp);
        temp = new ArrayList<Point>();
        temp.add(new Point(mRectWidth - mPadding, mPadding));
        temp.add(new Point(mRectWidth - mPadding, mRectHeight - mPadding));
        mPts1.add(temp);
        temp = new ArrayList<Point>();
        temp.add(new Point(mPadding, mRectHeight - mPadding));
        temp.add(new Point(mRectWidth - mPadding, mRectHeight - mPadding));
        mPts1.add(temp);
        mDiagonalCorner.add(new RectF(0f, 0f, mDiagonalCornerWidth, mDiagonalCornerWidth));
        mDiagonalCorner.add(new RectF(0f, mRectHeight - mDiagonalCornerWidth, mDiagonalCornerWidth, mRectHeight));
        mDiagonalCorner.add(new RectF(mRectWidth - mDiagonalCornerWidth, 0f, mRectWidth, mDiagonalCornerWidth));
        mDiagonalCorner.add(new RectF(mRectWidth - mDiagonalCornerWidth, mRectHeight - mDiagonalCornerWidth, mRectWidth, mRectHeight));

        mDiagonalCheck.add(new RectF(mRectWidth / 2 - mDiagonalCheckWidth / 2, mRectHeight / 2 - mDiagonalCheckWidth / 2, mRectWidth / 2 + mDiagonalCheckWidth / 2, mRectHeight / 2 + mDiagonalCheckWidth / 2));
        mDiagonalCheck.add(new RectF(mRectWidth / 4 - mDiagonalCheckWidth / 2, mRectHeight / 4 - mDiagonalCheckWidth / 2, mRectWidth / 4 + mDiagonalCheckWidth / 2, mRectHeight / 4 + mDiagonalCheckWidth / 2));
        mDiagonalCheck.add(new RectF(mRectWidth / 4 * 3 - mDiagonalCheckWidth / 2, mRectHeight / 4 * 3 - mDiagonalCheckWidth / 2, mRectWidth / 4 * 3 + mDiagonalCheckWidth / 2, mRectHeight / 4 * 3 + mDiagonalCheckWidth / 2));
        mDiagonalCheck.add(new RectF(mRectWidth / 4 - mDiagonalCheckWidth / 2, mRectHeight / 4 * 3 - mDiagonalCheckWidth / 2, mRectWidth / 4 + mDiagonalCheckWidth / 2, mRectHeight / 4 * 3 + mDiagonalCheckWidth / 2));
        mDiagonalCheck.add(new RectF(mRectWidth / 4 * 3 - mDiagonalCheckWidth / 2, mRectHeight / 4 - mDiagonalCheckWidth / 2, mRectWidth / 4 * 3 + mDiagonalCheckWidth / 2, mRectHeight / 4 + mDiagonalCheckWidth / 2));

        mCrossRect.add(new RectF(mRectWidth / 2 - mDiagonalCheckWidth / 2, 0, mRectWidth / 2 + mDiagonalCheckWidth / 2, mDiagonalCheckWidth));
        mCrossRect.add(new RectF(mRectWidth / 2 - mDiagonalCheckWidth / 2, mRectHeight - mDiagonalCheckWidth, mRectWidth / 2 + mDiagonalCheckWidth / 2, mRectHeight));
        mCrossRect.add(new RectF(0, mRectHeight / 2 - mDiagonalCheckWidth / 2, mDiagonalCheckWidth, mRectHeight / 2 + mDiagonalCheckWidth / 2));
        mCrossRect.add(new RectF(mRectWidth - mDiagonalCheckWidth, mRectHeight / 2 - mDiagonalCheckWidth / 2, mRectWidth, mRectHeight / 2 + mDiagonalCheckWidth / 2));

        mCrossCheck.add(new RectF(mRectWidth / 2 - mDiagonalCheckWidth / 2, mRectHeight / 4 - mDiagonalCheckWidth / 2, mRectWidth / 2 + mDiagonalCheckWidth / 2, mRectHeight / 4 + mDiagonalCheckWidth / 2));
        mCrossCheck.add(new RectF(mRectWidth / 2 - mDiagonalCheckWidth / 2, mRectHeight / 4 * 3 - mDiagonalCheckWidth / 2, mRectWidth / 2 + mDiagonalCheckWidth / 2, mRectHeight / 4 * 3 + mDiagonalCheckWidth / 2));

        mCrossCheck.add(new RectF(mRectWidth / 4 - mDiagonalCheckWidth / 2, mRectHeight / 2 - mDiagonalCheckWidth / 2, mRectWidth / 4 + mDiagonalCheckWidth / 2, mRectHeight / 2 + mDiagonalCheckWidth / 2));
        mCrossCheck.add(new RectF(mRectWidth / 4 * 3 - mDiagonalCheckWidth / 2, mRectHeight / 2 - mDiagonalCheckWidth / 2, mRectWidth / 4 * 3 + mDiagonalCheckWidth / 2, mRectHeight / 2 + mDiagonalCheckWidth / 2));
    }

    public void readPoints() {
        int x, y;
        List<Point> v = new ArrayList<Point>();
        Point p;
        for (int j = 0; j < mRectHeight - mPadding * 2; j++) {
            x = mPadding;
            y = j + mPadding;
            p = new Point(x, y);
            v.add(p);
        }
        mSuperPts.add(v);
        v = new ArrayList<Point>();

        for (int j = 0; j < mRectWidth - mPadding * 2; j++) {
            x = j + mPadding;
            y = mPadding;
            p = new Point(x, y);
            v.add(p);
        }
        mSuperPts.add(v);
        v = new ArrayList<Point>();

        for (int j = 0; j < mRectHeight - mPadding * 2; j++) {
            x = mRectWidth - mPadding;
            y = j + mPadding;
            p = new Point(x, y);
            v.add(p);
        }
        mSuperPts.add(v);
        v = new ArrayList<Point>();

        for (int j = 0; j < mRectWidth - mPadding * 2; j++) {
            x = j + mPadding;
            y = mRectHeight - mPadding;
            p = new Point(x, y);
            v.add(p);
        }
        mSuperPts.add(v);
        v = new ArrayList<Point>();

    }

    class CanvasView extends View {
        private Paint mLinePaint = null;

        private Paint mTextPaint = null;

        private Paint mRectPaint = null;

        private Paint mOkPaint = null;

        private Rect mRect = null;

        CanvasView(Context c) {
            super(c);
            mLinePaint = new Paint();
            mLinePaint.setAntiAlias(true);
            mLinePaint.setStrokeCap(Paint.Cap.ROUND);
            mLinePaint.setStrokeWidth(8);
            mTextPaint = new Paint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextSize(12.0f * mZoom);
            mTextPaint.setARGB(255, 0, 0, 0);
            mRect = new Rect(0, 0, mRectWidth, mRectHeight);
            mRectPaint = new Paint();
            mRectPaint.setARGB(255, 255, 255, 255);
            mOkPaint = new Paint();
            mOkPaint.setTextSize(16);
            mOkPaint.setARGB(255, 0, 0, 255);
            mOkPaint.setAntiAlias(true);
            mOkPaint.setStyle(Paint.Style.STROKE);
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFFFFF);

            int i;
            Point p1, p2;
            int height = mRectHeight / 4;
            canvas.drawRect(mRect, mRectPaint);

            mLinePaint.setARGB(255, 0, 0, 255);
            List<Point> temp;
            for (int j = 0; j < mPts1.size(); j++) {
                temp = mPts1.get(j);
                p1 = temp.get(0);
                p2 = temp.get(1);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mLinePaint);
            }

            canvas.drawLine(0, 0, mRectWidth, mRectHeight, mLinePaint);
            canvas.drawLine(0, mRectHeight, mRectWidth, 0, mLinePaint);

            canvas.drawLine(mRectWidth / 2, 0, mRectWidth / 2, mRectHeight, mLinePaint);
            canvas.drawLine(0, mRectHeight / 2, mRectWidth, mRectHeight / 2, mLinePaint);

            mLinePaint.setARGB(255, 255, 0, 0);
            for (i = 0; i < mTemPoints.size() - 2; i++) {
                p1 = mTemPoints.get(i);
                p2 = mTemPoints.get(i + 1);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mLinePaint);
            }

            for (int j = 0; j < mInput.size(); j++) {
                List<Point> lscp = mInput.get(j);
                for (int k = 0; k < lscp.size() - 1; k++) {
                    p1 = lscp.get(k);
                    p2 = lscp.get(k + 1);
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mLinePaint);
                }
            }
            for (i = 0; i < mDiagonalDrawPoint1.size() - 1; i++) {
                p1 = mDiagonalDrawPoint1.get(i);
                p2 = mDiagonalDrawPoint1.get(i + 1);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mLinePaint);
            }
            for (i = 0; i < mDiagonalDrawPoint2.size() - 1; i++) {
                p1 = mDiagonalDrawPoint2.get(i);
                p2 = mDiagonalDrawPoint2.get(i + 1);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mLinePaint);
            }

            for (i = 0; i < mHorizontalLinePoint.size() - 1; i++) {
                p1 = mHorizontalLinePoint.get(i);
                p2 = mHorizontalLinePoint.get(i + 1);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mLinePaint);
            }
            for (i = 0; i < mVerticalLinePoint.size() - 1; i++) {
                p1 = mVerticalLinePoint.get(i);
                p2 = mVerticalLinePoint.get(i + 1);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mLinePaint);
            }
            if (!mResultString.equals("")) {
                canvas.drawText(
                        getResources().getString(R.string.Offset) + mResultString,
                        20 * mZoom, height, mTextPaint);
            }
            canvas.drawCircle(mRectWidth / 2, mRectHeight / 2, 50, mOkPaint);
            canvas.drawText("先画框,再画叉,最后画十", mRectWidth / 5, mRectHeight / 2 - 55, mOkPaint);

        }

        @Override
        protected void onSizeChanged(int i, int j, int k, int l) {
            super.onSizeChanged(i, j, k, l);
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            int x = (int) e.getX();
            int y = (int) e.getY();
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!mIsDrawRect
                            && !(x < mRectWidth / 2 + 50 && x > mRectWidth / 2 - 50
                            && y < mRectHeight / 2 + 50 && y > mRectHeight / 2 - 50)) {
                        mIsDrawRect = isBeginOrEndPoint(x, y);
                    } else if (!mDrawDiagonalSuccess) {
                        Log.d("LineTest", "mDrawRectSuccess1=" + mDrawRectSuccess + "x=" + x + "y=" + y);
                        if (!isBeginOrEndDiagonal(x, y)) {
                            clearDrawDiagonal();
                        } else {
                            addDrawDiagonalPoint(new Point(x, y));
                        }
                    } else {
                        Log.d("LineTest", "mDrawDiagonalSuccess=" + mDrawDiagonalSuccess + "x=" + x + "y=" + y);
                        if (!isBeginOrEndCross(x, y)) {
                            clearDrawCross();
                        } else {
                            addDrawCrossPoint(new Point(x, y));
                        }
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (!mDrawRectSuccess) {
                        mTemPoints.add(new Point(x, y));
                    } else if (!mDrawDiagonalSuccess) {
                        addDrawDiagonalPoint(new Point(x, y));
                    } else if (!mDrawCrossSuccess) {
                        addDrawCrossPoint(new Point(x, y));
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    Log.d("LineTest", "x=" + x + "y=" + y + "a=" + (mRectWidth / 2 + 50) + "b=" + (mRectHeight / 2 + 50));
                    if (!mDrawRectSuccess && x < mRectWidth / 2 + 50 && x > mRectWidth / 2 - 50
                            && y < mRectHeight / 2 + 50 && y > mRectHeight / 2 - 50) {
                        CalculateDiversity();
                        break;
                    } else if (mDrawRectSuccess && !mDrawDiagonalSuccess) {
                        addDrawDiagonalPoint(new Point(x, y));
                        if (!isBeginOrEndDiagonal(x, y)) {
                            showDialog(getString(R.string.Error),
                                    getString(R.string.DrawError), true, false);
                            /*clearDrawDiagonal();*/
                            break;
                        } else if (!mDrawDiagonalSuccess1) {
                            isDrawInDiagonal(mDiagonalDrawPoint1);
                        } else if (!mDrawDiagonalSuccess2) {
                            isDrawInDiagonal(mDiagonalDrawPoint2);
                        }
                        if (mDrawDiagonalSuccess1 && mDrawDiagonalSuccess2) {
                            mDrawDiagonalSuccess = true;
                        }
                        break;
                    } else if (mIsDrawRect && !mDrawRectSuccess) {
                        mIsDrawRect = isBeginOrEndPoint(x, y);
                        mDrawRectSuccess = mIsDrawRect;
                        Log.d("LineTest", "mDrawRectSuccess=" + mDrawRectSuccess);
                        mInput.add(mTemPoints);
                        mTemPoints = new ArrayList<Point>();
                        CalculateDiversity();
                        Log.d("LineTest", "mDrawRectSuccess=" + mDrawRectSuccess);
                    } else if (mDrawRectSuccess && mDrawDiagonalSuccess) {
                        addDrawCrossPoint(new Point(x, y));
                        if (!isBeginOrEndCross(x, y)) {
                            showDialog(getString(R.string.Error),
                                    getString(R.string.DrawError), true, false);
                            /*clearDrawCross();*/
                            break;
                        } else if (!mDrawHorizontalLineSuccess) {
                            isDrawInCross(mHorizontalLinePoint);
                        } else if (!mDrawVerticalLineSuccess) {
                            isDrawInCross(mVerticalLinePoint);
                        }
                        if (mDrawHorizontalLineSuccess && mDrawVerticalLineSuccess) {
                            mDrawCrossSuccess = true;
                            Utils.SetPreferences(getApplicationContext(), mSp, R.string.touchscreen_name, AppDefine.FT_SUCCESS);
                            finish();
                        }
                        break;
                    }
                    if (mDrawRectSuccess && mDrawDiagonalSuccess1 && mDrawDiagonalSuccess2 && mDrawHorizontalLineSuccess && mDrawVerticalLineSuccess) {
                        mDrawCrossSuccess = true;
                        Utils.SetPreferences(getApplicationContext(), mSp, R.string.touchscreen_name, AppDefine.FT_SUCCESS);
                        finish();
                        break;
                    }
                    if (!mDrawRectSuccess) {
                        /*showDialog(getString(R.string.Error),
                                getString(R.string.DrawError), true, true);*/
                        showDialog(getString(R.string.Error),
                                getString(R.string.DrawError), true, false);
                    }
                    break;
                default:
                    break;
            }
            invalidate();
            return true;
        }

        private void showDialog(String title, String msg, boolean posi, boolean negati) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(LineTest.this);
            dialog.setTitle(title).setMessage(msg);
            dialog.setCancelable(false);
            if (posi) {
                dialog.setPositiveButton(getString(R.string.Again),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /*if(!mDrawRectSuccess){
									mInput.clear();
                                    mTemPoints.clear();
								}
								clearDrawDiagonal();
								clearDrawCross();
                                invalidate();*/
                                if (!mDrawRectSuccess) {
                                    mInput.clear();
                                    mTemPoints.clear();
                                    mInputLeft.clear();
                                    mInputTop.clear();
                                    mInputRight.clear();
                                    mInputBottom.clear();
                                } else if (mDrawRectSuccess && !mDrawDiagonalSuccess) {
                                    clearDrawDiagonal();
                                } else if (mDrawRectSuccess && mDrawDiagonalSuccess) {
                                    clearDrawCross();
                                }
                                invalidate();
                            }
                        });
            }
            if (negati) {
                dialog.setNegativeButton(getString(R.string.GoOn),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!mDrawRectSuccess) {
                                    mInput.clear();
                                    mTemPoints.clear();
                                    mInputLeft.clear();
                                    mInputTop.clear();
                                    mInputRight.clear();
                                    mInputBottom.clear();
                                } else if (mDrawRectSuccess && !mDrawDiagonalSuccess) {
                                    clearDrawDiagonal();
                                } else if (mDrawRectSuccess && mDrawDiagonalSuccess) {
                                    clearDrawCross();
                                }
                                invalidate();
                            }
                        });
            }
            dialog.show();
        }
    }
}
