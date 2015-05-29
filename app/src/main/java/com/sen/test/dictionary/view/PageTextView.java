package com.sen.test.dictionary.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;


import com.sen.test.dictionary.info.DcitTextColorInfo;
import com.sen.test.dictionary.info.LineInfo;
import com.sen.test.dictionary.info.LineTextInfo;
import com.sen.test.dictionary.parse.TextParse;

import java.util.List;

public class PageTextView extends View {

    /**
     * 行内容
     */
    private List<LineInfo> lineInfoList = null;

    /**
     * 笔刷
     */
    private Paint paint = null;

    /**
     * 开始行位置
     */
    private int start = 0;

    /**
     * 行数
     */
    private int offset = 0;

    private int verticalY;

    private int bitmapWidth;

    private int bitmapHeight;

    private Bitmap cacheBitmap;

    public PageTextView(Context context) {
        super(context);
    }

    public PageTextView(Context context, List<LineInfo> lineInfoList, int start, int offset) {
        super(context);
        this.lineInfoList = lineInfoList;
        this.start = start;
        this.offset = offset;

        this.paint = new Paint();
//        this.paint.setDither(true);
        this.paint.setAntiAlias(true);
//        this.paint.setStyle(Paint.Style.FILL);
        /**
         * bug:
         *  页面过多，滑动时会出现抖动
         * resolve method:
         *  主动打开硬件加速
         */
        openHardwareAccelerated();
    }

    public void reset(List<LineInfo> lineInfoList, int start, int offset, int width, int height, int verticalY) {
        this.lineInfoList = lineInfoList;
        this.start = start;
        this.offset = offset;

        this.paint = new Paint();
//        this.paint.setDither(true);
        this.paint.setAntiAlias(true);
        this.verticalY = verticalY;
        this.bitmapWidth = width;
        this.bitmapHeight = height;
//        this.paint.setStyle(Paint.Style.FILL);
        /**
         * bug:
         *  页面过多，滑动时会出现抖动
         * resolve method:
         *  主动打开硬件加速
         */
//        openHardwareAccelerated();

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(width, height);
            setLayoutParams(layoutParams);
        } else {
            if (layoutParams.width != width || layoutParams.height != height) {
                layoutParams.width = width;
                layoutParams.height = height;
                setLayoutParams(layoutParams);
            }
        }

    }

    public int getListIndexStart() {
        return start;
    }

    public int getListIndexEnd() {
        return start+offset;
    }

    private void openHardwareAccelerated() {

        /**
         * 硬件加速第一种方式
         */
//        this.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        /**
         * 硬件加速第二种方式
         */
        /*try {
            Field mLayerType = View.class.getDeclaredField("mLayerType");
            mLayerType.setAccessible(true);
            mLayerType.set(this, View.LAYER_TYPE_HARDWARE);

            Field mLocalDirtyRect = View.class.getDeclaredField("mLocalDirtyRect");
            mLocalDirtyRect.setAccessible(true);
            mLocalDirtyRect.set(this, new Rect());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }*/

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        cacheBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
//        new CacheDrawThread().start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (cacheBitmap != null) {
            cacheBitmap.recycle();
            cacheBitmap = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (lineInfoList != null && lineInfoList.size() > 0) {
            for (int i=start;i<(start+offset+1);i++) {
                drawLine(canvas, verticalY, lineInfoList.get(i));
            }
        }
//        Log.i("RecyclerViewText", "drawing... "+this.toString());
        /*if (cacheBitmap != null) {
            canvas.drawBitmap(cacheBitmap, 0, 0, null);
        }*/
    }

    private void drawCache() {
        if (cacheBitmap != null) {
            Canvas canvas = new Canvas(cacheBitmap);
            if (lineInfoList != null && lineInfoList.size() > 0) {
                for (int i=start;i<(start+offset+1);i++) {
                    if (canvas != null) {
                        drawLine(canvas, verticalY, lineInfoList.get(i));
                    }
                }
            }
        }
    }

    private void drawLine(Canvas canvas, int startY, LineInfo lineInfo) {
        if (lineInfo != null && lineInfo.lineTextInfos != null) {
            if (lineInfo.selectStart != -1) {
//                drawLineSelectText(canvas, startY, lineInfo);
                drawLineSelectText(canvas, startY, lineInfo);
            } else {
                drawLineUnSelectText(canvas, startY, lineInfo);
            }
        }
    }

    private void drawLineUnSelectText(Canvas canvas, int startY, LineInfo lineInfo) {
        float textWidth = lineInfo.paddingLeft;
        if (lineInfo.textSize != paint.getTextSize()) {
            paint.setTextSize(lineInfo.textSize);
        }
        for (LineTextInfo lineTextInfo:lineInfo.lineTextInfos) {
            if (lineTextInfo.color != paint.getColor()) {
                paint.setColor(lineTextInfo.color);
            }
            drawText(canvas, lineTextInfo, textWidth, lineInfo.y-startY, paint);
            textWidth += paint.measureText(lineTextInfo.charArray, lineTextInfo.startIndex, lineTextInfo.startOffset);
        }
    }

    /** 取词(改变字体背景颜色)
     * 计算取词的数组、设置颜色
     * @param canvas
     * @param startY
     * @param lineInfo
     * @param backgroundColor
     */
    private void drawLineSelectText(Canvas canvas, int startY, LineInfo lineInfo, int backgroundColor) {
        if (lineInfo != null) {
            int lineSelectStart = lineInfo.selectStart;
            int lineSelectOffset = lineInfo.selectOffset;
            float lineWidth=lineInfo.paddingLeft;
            int currentArrayLength = 0;

            float rectLeft = -1.0f;
            float rectTop = lineInfo.y + 0.0f;
            float rectRight = -1.0f;
            float rectBottom = lineInfo.y + lineInfo.height + 0.0f;

            paint.setColor(backgroundColor);
            if (lineInfo.textSize != paint.getTextSize()) {
                paint.setTextSize(lineInfo.textSize);
            }
            if (lineInfo.selectTotalLine) {
                rectLeft = 0.0f;
                rectRight = getWidth();
                drawRect(canvas, rectLeft, rectTop-startY, rectRight, rectBottom-startY, paint);
            } else {
                for (LineTextInfo lineTextInfo:lineInfo.lineTextInfos) {
                    char[] charArray = lineTextInfo.charArray;
                    int start = lineTextInfo.startIndex;
                    int offset = lineTextInfo.startOffset;
                    float textWidth = paint.measureText(charArray, start, offset);

                    int region[] = TextParse.computeRegionInTextInfo(lineSelectStart, lineSelectOffset, start, offset, currentArrayLength);

                    if (region != null) {
                        if (region[1] > 0) {
                            if (region[0] > start) {
                                float tempWidth = lineWidth + paint.measureText(charArray, start, region[0]-start);
                                if (rectLeft == -1.0f) {
                                    rectLeft = tempWidth;
                                }
                                rectRight = tempWidth + paint.measureText(charArray, region[0], region[1]);
                            } else if (region[0] == start) {
                                if (rectLeft == -1.0f) {
                                    rectLeft = lineWidth;
                                }
                                rectRight = lineWidth + paint.measureText(charArray, region[0], region[1]);
                            }
                        }
                    }
                    region = null;
                    lineWidth += textWidth;
                    currentArrayLength += offset;
                }
                if (rectLeft == lineInfo.paddingLeft) {
                    rectLeft = 0.0f;
                }

                if ((lineInfo.selectStart+lineInfo.selectOffset) == lineInfo.getLineTextArrayLength()) {
                    rectRight = getWidth();
                }
                drawRect(canvas, rectLeft, rectTop-startY, rectRight, rectBottom-startY, paint);
            }
            drawLineUnSelectText(canvas, startY, lineInfo);
        }

    }

    /**
     * 取词(改变字体颜色)
     * 计算取词的数组、设置颜色
     * @param canvas
     * @param startY
     * @param lineInfo
     */
    private void drawLineSelectText(Canvas canvas, int startY, LineInfo lineInfo) {
        if (lineInfo != null) {
            int lineSelectStart = lineInfo.selectStart;
            int lineSelectOffset = lineInfo.selectOffset;
            float lineWidth=lineInfo.paddingLeft;
            int currentArrayLength = 0;
            if (lineInfo.textSize != paint.getTextSize()) {
                paint.setTextSize(lineInfo.textSize);
            }
            for (LineTextInfo lineTextInfo:lineInfo.lineTextInfos) {
                char[] charArray = lineTextInfo.charArray;
                int start = lineTextInfo.startIndex;
                int offset = lineTextInfo.startOffset;
                float textWidth = paint.measureText(charArray, start, offset);

                int region[] = TextParse.computeRegionInTextInfo(lineSelectStart, lineSelectOffset, start, offset, currentArrayLength);

                SelectCharArray selectCharArray = new SelectCharArray();
                selectCharArray.charArray = charArray;
                selectCharArray.y = (int)(lineInfo.y-startY);

                if (region != null && region[0] > start) {
                    selectCharArray.startIndex = start;
                    selectCharArray.startOffset = region[0]-start;
                    selectCharArray.x = lineWidth;
                    paint.setColor(lineTextInfo.color);
                    drawText(canvas, selectCharArray, paint);

                    selectCharArray.startIndex = region[0];
                    selectCharArray.startOffset = region[1];
                    selectCharArray.x = lineWidth + textWidth - paint.measureText(charArray, region[0], start+offset-region[0]);
//                        selectCharArray.x = lineWidth + paint.measureText(charArray, start, i1-start);
                    paint.setColor(getSelectColor(lineTextInfo.color));
                    drawText(canvas, selectCharArray, paint);

                    if ((region[0]+region[1]) != (start+offset)) {
                        selectCharArray.startIndex = region[0]+region[1];
                        selectCharArray.startOffset = (start+offset)-(region[0]+region[1]);
                        selectCharArray.x = lineWidth + textWidth - paint.measureText(charArray, region[0]+region[1], (start+offset)-(region[0]+region[1]));
//                            selectCharArray.x = lineWidth + paint.measureText(charArray, start, i1+i2-start);
                        paint.setColor(lineTextInfo.color);
                        drawText(canvas, selectCharArray, paint);
                    }
                } else if (region != null && region[0] == start) {
                    selectCharArray.startIndex = region[0];
                    selectCharArray.startOffset = region[1];
                    selectCharArray.x = lineWidth;
                    paint.setColor(getSelectColor(lineTextInfo.color));
                    drawText(canvas, selectCharArray, paint);

                    if ((region[0]+region[1]) != (start+offset)) {
                        selectCharArray.startIndex = region[0]+region[1];
                        selectCharArray.startOffset = (start+offset)-(region[0]+region[1]);
                        selectCharArray.x = lineWidth + textWidth - paint.measureText(charArray, region[0]+region[1], (start+offset)-(region[0]+region[1]));
//                            selectCharArray.x = lineWidth + paint.measureText(charArray, i1, i2);
                        paint.setColor(lineTextInfo.color);
                        drawText(canvas, selectCharArray, paint);
                    }
                } else {
                    selectCharArray.startIndex = start;
                    selectCharArray.startOffset = offset;
                    selectCharArray.x = lineWidth;
                    paint.setColor(lineTextInfo.color);
                    drawText(canvas, selectCharArray, paint);
                }
                lineWidth += textWidth;
                currentArrayLength += offset;
            }
        }
    }

    private void drawText(Canvas canvas, LineTextInfo lineTextInfo, float x, int y, Paint paint) {
        if (canvas != null && lineTextInfo != null) {
            canvas.drawText(lineTextInfo.charArray, lineTextInfo.startIndex,
                    lineTextInfo.startOffset, x, y-(int)paint.getFontMetrics().top, paint);
        }
    }

    private void drawText(Canvas canvas, SelectCharArray drawCharArray, Paint paint) {
        if (canvas != null && drawCharArray != null) {
            canvas.drawText(drawCharArray.charArray, drawCharArray.startIndex, drawCharArray.startOffset,
                    drawCharArray.x, drawCharArray.y-(int)paint.getFontMetrics().top, paint);
        }
    }

    private void drawRect(Canvas canvas, float left, float top, float right, float bottom, Paint paint) {
        if (canvas != null && paint != null){
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    private int getSelectColor(int color) {
        for (int s=0;s< DcitTextColorInfo.COLOR_LIST.length;s++){
            if (DcitTextColorInfo.COLOR_LIST[s] == color){
                color = DcitTextColorInfo.REVERSED_COLOR_LIST[s];
                return color;
            }
        }
        return color;
    }

    class SelectCharArray {
        public char[] charArray;
        public int startIndex;
        public int startOffset;
        public float x;
        public int y;
    }

    class CacheDrawThread extends Thread {

        @Override
        public void run() {
            drawCache();
//            postInvalidate();
        }
    }
}
