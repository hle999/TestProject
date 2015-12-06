package com.sen.test.ui.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class WaterWave1 extends View {

    /**
     * ���ε�List
     */
    private List<Wave> waveList;

    /**
     * ���Ĳ�͸���ȣ���ȫ��͸��
     */
    private static final int MAX_ALPHA = 255;

    protected static final int FLUSH_ALL = -1;

    private boolean isStart = true;

    // /**
    // * ���µ�ʱ��x����
    // */
    // private int xDown;
    // /**
    // * ���µ�ʱ��y������
    // */
    // private int yDown;
    // /**
    // * ������ʾԲ���İ뾶
    // */
    // private float radius;
    // private int alpha;

    /*
     * 1�����ι��캯��
     */
    public WaterWave1(Context context, AttributeSet attrs) {
        super(context, attrs);
        waveList = Collections.synchronizedList(new ArrayList<Wave>());
    }

    /**
     * onMeasure������ȷ���ؼ���С������ʹ��Ĭ�ϵ�
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    /**
     * ������Ҫ��ͼ�εķ�������������ȽϹؼ�
     */
    protected void onDraw(Canvas canvas) {
        // �ػ�����Բ��
        for (int i = 0; i < waveList.size(); i++) {
            Wave wave = waveList.get(i);
            canvas.drawCircle(wave.xDown, wave.yDown, wave.radius, wave.paint);
        }

    }

    /**
     * ��ʼ��paint
     */
    private Paint initPaint(int alpha, float width) {
        /*
         * �½�һ������
         */
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setStrokeWidth(width);

        // �����ǻ��η�ʽ����
        paint.setStyle(Paint.Style.STROKE);

        // System.out.println(alpha= + alpha);
        paint.setAlpha(alpha);
        // System.out.println(�õ���͸���ȣ� + paint.getAlpha());

        paint.setColor(Color.RED);
        return paint;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    flushState();

                    invalidate();

                    if (waveList != null && waveList.size() > 0) {
                        handler.sendEmptyMessageDelayed(0, 80);
                    }

                    break;

                default:
                    break;
            }
        }

    };

    /**
     * ˢ��״̬
     */
    private void flushState() {
        for (int i = 0; i < waveList.size(); i++) {
            Wave wave = waveList.get(i);
            if (isStart == false && wave.alpha == 0) {
                /*waveList.remove(i);
                wave.paint = null;
                wave = null;*/
                resetWave(wave, wave.xDown, wave.yDown);
                continue;
            } else if (isStart == true) {
                isStart = false;
            }
            wave.radius += 10;
            wave.alpha -= 20;
            if (wave.alpha < 0) {
                wave.alpha = 0;
            }
            wave.width = wave.radius / 4;
            wave.paint.setAlpha(wave.alpha);
            wave.paint.setStrokeWidth(/*wave.width*/5);
        }

    }

    // private Paint paint;
    // private float width;

    @Override
    /**
     * �����¼��ķ���
     */
    public boolean onTouchEvent(final MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                final int x = (int) event.getX();
                final int y = (int) event.getY();
                addWave(x, y);
                // ���֮��ˢϴһ��ͼ��
                invalidate();
                if (isStart) {
                    handler.sendEmptyMessage(0);
                }
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addWave(x, y);
                    }
                }, 160);
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;

            default:
                break;
        }

        return true;
    }

    private void addWave(int x, int y) {
        Wave wave = new Wave();
        resetWave(wave, x, y);
        if (waveList.size() == 0) {
            isStart = true;
        }
        waveList.add(wave);
    }

    private void resetWave(Wave wave, int x, int y) {
        wave.width = 0;
        wave.radius = 0;
        wave.alpha = MAX_ALPHA;
        wave.width = wave.radius / 4;
        wave.xDown = x;
        wave.yDown = y;
        wave.paint = initPaint(wave.alpha, wave.width);
    }

    private class Wave {
        int waveX;
        int waveY;
        /**
         * ������ʾԲ���İ뾶
         */
        float radius;
        Paint paint;
        /**
         * ���µ�ʱ��x����
         */
        int xDown;
        /**
         * ���µ�ʱ��y������
         */
        int yDown;
        float width;
        int alpha;
    }

}
