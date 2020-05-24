package com.example.exoplayerwithvisualizer;

/*
   ref: https://github.com/gauravk95/audio-visualizer-android/blob/master/audiovisualizer/src/main/java/com/gauravk/audiovisualizer/visualizer/BarVisualizer.java
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.example.exoplayerwithvisualizer.utils.AVConstants;
import com.example.exoplayerwithvisualizer.utils.BaseVisualizer;

import java.util.Random;

public class BarVisualizer extends BaseVisualizer implements AudioDataReceiver.AudioDataListener {

    private static final int BAR_MAX_POINTS = 120;
    private static final int BAR_MIN_POINTS = 3;

    private int mMaxBatchCount;

    private int nPoints;

    private float[] mSrcY, mDestY;

    private float mBarWidth;
    private Rect mClipBounds;

    private int nBatchCount;

    private Random mRandom;

    public BarVisualizer(Context context) {
        super(context);
    }

    public BarVisualizer(Context context,
                         @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BarVisualizer(Context context,
                         @Nullable AttributeSet attrs,
                         int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        nPoints = (int) (BAR_MAX_POINTS * mDensity);
        if (nPoints < BAR_MIN_POINTS)
            nPoints = BAR_MIN_POINTS;

        mBarWidth = -1;
        nBatchCount = 0;

        setAnimationSpeed(mAnimSpeed);

        mRandom = new Random();

        mClipBounds = new Rect();

        mSrcY = new float[nPoints];
        mDestY = new float[nPoints];

    }

    @Override
    public void setAnimationSpeed(AnimSpeed animSpeed) {
        super.setAnimationSpeed(animSpeed);
        mMaxBatchCount = AVConstants.MAX_ANIM_BATCH_COUNT - mAnimSpeed.ordinal();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mBarWidth == -1) {

            canvas.getClipBounds(mClipBounds);

            mBarWidth = canvas.getWidth() / nPoints;

            //initialize points
            for (int i = 0; i < mSrcY.length; i++) {
                float posY;
                if (mPositionGravity == PositionGravity.TOP)
                    posY = mClipBounds.top;
                else
                    posY = mClipBounds.bottom;

                mSrcY[i] = posY;
                mDestY[i] = posY;
            }
        }

        //create the path and draw
        if (isVisualizationEnabled && mRawAudioShort != null) {

            if (mRawAudioShort.length == 0) {
                return;
            }

            //find the destination bezier point for a batch
            if (nBatchCount == 0) {
                float randPosY = mDestY[mRandom.nextInt(nPoints)];
                for (int i = 0; i < mSrcY.length; i++) {

                    int x = (int) Math.ceil((i + 1) * (mRawAudioShort.length / nPoints));
                    int t = 0;
                    if (x < mRawAudioShort.length)
                        t = canvas.getHeight() +
                                ((short) (Math.abs(mRawAudioShort[x]) + 32768)) * canvas.getHeight() / 32768;

                    float posY;
                    if (mPositionGravity == PositionGravity.TOP)
                        posY = mClipBounds.bottom - t;
                    else
                        posY = mClipBounds.top + t;

                    //change the source and destination y
                    mSrcY[i] = mDestY[i];
                    mDestY[i] = posY;
                }

                mDestY[mSrcY.length - 1] = randPosY;
            }

            //increment batch count
            nBatchCount++;

            //calculate bar position and draw
            for (int i = 0; i < mSrcY.length; i++) {
                float barY = mSrcY[i] + (((float) (nBatchCount) / mMaxBatchCount) * (mDestY[i] - mSrcY[i]));
                float barX = (i * mBarWidth) + (mBarWidth / 2);
                canvas.drawLine(barX, canvas.getHeight(), barX, barY, mPaint);
            }

            //reset the batch count
            if (nBatchCount == mMaxBatchCount)
                nBatchCount = 0;

        }
        super.onDraw(canvas);
    }

    @Override
    public void setRawAudioBytes(short[] shorts) {
        this.mRawAudioShort = shorts;
        this.invalidate();
    }

}
