package com.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static constants.AppSystem.CONTROL_MODE;
import static constants.Constant.PHONE_CONTROL;
import static constants.Constant.SIMSET_CONTROL;

/**
 * Created by gutte on 4/16/2017.
 */

public class MjpegView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "MjpegView";


    private Context context;
    private MjpegViewThread thread;
    private MjpegInputStream mIn = null;
    private boolean mRun = false;
    private boolean surfaceDone = false;
    private boolean viewing = false;

    private int dispWidth;
    private int dispHeight;
    private ImjpegViewListener listener;
    private String url;

    public class MjpegViewThread extends AsyncTask<Void, Void, Void> {
        private SurfaceHolder mSurfaceHolder;

        private Bitmap bitmap;

        public MjpegViewThread(SurfaceHolder surfaceHolder, Context context) {
            mSurfaceHolder = surfaceHolder;
        }

        private void drawCanvas(Bitmap bitmap, Canvas c) {

            Rect rect ;

            synchronized (mSurfaceHolder) {

                c.drawColor(Color.BLACK);

                // Normal mode
                if (CONTROL_MODE == PHONE_CONTROL ){
                    rect = new Rect(0, 0, dispWidth, dispHeight);
                    c.drawBitmap(bitmap, null, rect, new Paint());

                }

                // VR mode (Simulator set Control)
                else if(CONTROL_MODE == SIMSET_CONTROL){
                    Bitmap tmpBitmap = Bitmap.createScaledBitmap(this.bitmap, dispWidth/2, dispHeight/2, false);

                    c.drawBitmap(tmpBitmap, 0f, dispHeight/4 , new Paint());
                    c.drawBitmap(tmpBitmap, tmpBitmap.getWidth(), dispHeight/4 , new Paint());

                }
            }

        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mSurfaceHolder) {
                dispWidth = width;
                dispHeight = height;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            bitmap = null;
            Canvas c = null;
            boolean sucess = false;

            mIn = MjpegInputStream.read(url);

            if (mIn == null) {
                onError();
                Log.d("MjpegView","Doing Background Error");

            }

            while (mRun) {
                if (surfaceDone) {
                    try {
                        try {
                            this.bitmap = mIn.readMjpegFrame();
                            sucess = true;
                        } catch (IOException e) {
                            sucess = false;
                            break;
                        }
                        c = mSurfaceHolder.lockCanvas();
//                        Log.d(TAG, "Lock");
                        drawCanvas(bitmap, c);

                    } finally {
                        if (c != null) {
                            mSurfaceHolder.unlockCanvasAndPost(c);
//                            Log.d(TAG, "UnLock");
                        }
                    }
                    if (sucess) {
                        if(listener!=null)
                            listener.sucess();
                    } else {
                        onError();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "Cancelled Thread");
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Pre executed Thread");
        }

        private void onError() {
            stopPlayback();
            if(listener!=null)
                listener.error();
            mRun = false;
        }

        public Bitmap getBitmap() {
            return this.bitmap;
        }

        public void setBitmap(Bitmap bitmap){
             this.bitmap = bitmap ;
        }
    }

    private void init(Context context) {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        thread = new MjpegViewThread(holder, context);
        setFocusable(true);
    }

    public MjpegView(Context context, AttributeSet attrs) { super(context, attrs); init(context); }


    public void startPlayback() {
        mRun = true;
        init(context);
        Log.d("MjpegView", "Start Playback");
        thread.execute();
    }

    public void stopPlayback() {
        if (mIn != null) {
            thread.cancel(true);
            mRun = false;
            try {
                mIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MjpegView(Context context, AttributeSet attrs,
                     ImjpegViewListener listener) {
        super(context, attrs);
        this.context = context;
        this.listener = listener;
    }

    public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
        thread.setSurfaceSize(w, h);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceDone = false;
        stopPlayback();
    }

    public MjpegView(Context context, ImjpegViewListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        surfaceDone = true;
    }

//    public void showFps(boolean b) {
//        showFps = b;
//    }

    public void setSource(String URL) {
        if (URL == null) {
            if(listener!=null)
                listener.error();
        } else {
            // mIn = source;
            url = URL;
            startPlayback();
        }
    }

    public void setDispWidth(int dispWidth) {
        this.dispWidth = dispWidth;
    }

    public void setDispHeight(int dispHeight) {
        this.dispHeight = dispHeight;
    }

    public boolean isViewing() {
        return viewing;
    }

    public ImjpegViewListener getListener() {
        return listener;
    }

    public void destroy() {
        stopPlayback();
    }

}
