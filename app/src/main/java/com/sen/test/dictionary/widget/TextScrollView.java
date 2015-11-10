package com.sen.test.dictionary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sen.lib.view.BaseItemAdapter;
import com.sen.lib.view.VerticalScrollWidget;
import com.sen.test.dictionary.analyze.ImlAnalyze;
import com.sen.test.dictionary.analyze.SpanNormalAnalyze;
import com.sen.test.dictionary.file.AnalyzeCodeDictionary;
import com.sen.test.dictionary.file.ParaseWorker;
import com.sen.test.dictionary.info.AnalysisInfo;
import com.sen.test.dictionary.info.SpanInfo;
import com.sen.test.dictionary.info.LinesInfo;
import com.sen.test.dictionary.parse.SpanParase;
import com.sen.test.dictionary.utils.BaseHandler;
import com.sen.test.dictionary.view.ISpanAnalysisObtainer;
import com.sen.test.dictionary.view.ImlDrawText;
import com.sen.test.dictionary.view.PageText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sen on 2015/6/15.
 */
public class TextScrollView extends VerticalScrollWidget implements ImlDrawText<Integer>{

    private final static int CURSOR_ELAPSE_TIME = 500;

    private TextHandler mHandler;

    private int textSize;

    /**
     * 改变字体大小的行位置
     */
    private int changeLine = 0;

    /**
     * 改变字体大小
     */
    private int changeTextSize = 50;

    private int contentHeight;

    private AnalyzeCodeDictionary analyzeCodeDictionary;

    private AnalysisInfo analysisInfo;

    private List<PageInfo> pageList;

    private BaseItemAdapter adapter;

    private Paint mTextPaint;
    private Paint mCursorPaint;

    private boolean isSelectText = false;

    private boolean hasTargetPosition;
    private boolean blnIsCursorVisible;
    private boolean blnCanCursorVisible = false;
    private int targetX = 0;
    private int targetY = 0;
    private float cursorStartX;
    private float cursorStartY;
    private float cursorEndX;
    private float cursorEndY;

    private Path pathSelectRegion;

    private long lngCursorLastTime = SystemClock.elapsedRealtime();

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        if (mTextPaint != null) {
            mTextPaint.setTextSize(this.textSize);
        }
        if (analysisInfo != null && mHandler != null) {
            mHandler.sendMessages(TextHandler.PARARSE_START, analysisInfo, 0, 0, 0);
        }
    }

    public void setTargetPositionAfterLoaded(int targetX, int targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
        hasTargetPosition = true;
    }

    public boolean isSelectText() {
        return isSelectText;
    }

    public void setSelectText(boolean isSelectText) {
        this.isSelectText = isSelectText;
    }

    public TextScrollView(Context context) {
        super(context);
    }

    public TextScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setText(byte[] buffer) {
        SpanNormalAnalyze normalAnalyze = new SpanNormalAnalyze(buffer);
        normalAnalyze.setObtainer(mHandler);
        setText(normalAnalyze);
    }

    public void setChangeLineTextSize(int changLine, int changeTextSize) {
        this.changeLine = changLine;
        this.changeTextSize = changeTextSize;
    }

    private void setText(final ImlAnalyze<ISpanAnalysisObtainer> imlAnalyze) {
        if (getTextSize() != 0) {
            /**
             * View有可能正在初始化，长宽都为0，放入消息队列，等初始完再执行
             */
            if (getWidth() == 0 || getHeight() == 0) {
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        setText(imlAnalyze, getTextSize());
                    }
                });
            } else {
                setText(imlAnalyze, getTextSize());
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSelectText) {

            touch(event);
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (blnCanCursorVisible) {
            long currentTime = SystemClock.elapsedRealtime();
            if ((currentTime - lngCursorLastTime) > CURSOR_ELAPSE_TIME) {
                blnIsCursorVisible = !blnIsCursorVisible;
                lngCursorLastTime = currentTime;
            }
            if (blnIsCursorVisible) {
                canvas.drawLine(cursorStartX, cursorStartY, cursorEndX, cursorEndY, mCursorPaint);
            }
            postInvalidateDelayed(CURSOR_ELAPSE_TIME);
        }
    }

    @Override
    public void removeAllViews() {
        stopAnalyze();
        mHandler = null;
        super.removeAllViews();
    }

    private void init() {

        mTextPaint = new Paint();
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setSubpixelText(true);

        mCursorPaint = new Paint();
        mCursorPaint.setTextSize(textSize);
        mCursorPaint.setColor(Color.BLACK);
        mCursorPaint.setSubpixelText(true);

        mHandler = new TextHandler(this);
        pageList = new ArrayList<>();
        adapter = new BaseItemAdapter() {
            @Override
            public View getView(View v, ViewGroup container, int postion) {
                if (v == null) {
                    v = new PageText(container.getContext());
                    ((PageText)v).setImlDrawText(TextScrollView.this);
                }

                ViewGroup.LayoutParams lp = v.getLayoutParams();
                if (lp == null) {
                    lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                lp.width = pageList.get(postion).width;
                lp.height = pageList.get(postion).height;
                v.setLayoutParams(lp);
                ((PageText)v).setIndex(postion);
                return v;
            }

            @Override
            public int getCount() {
                if (pageList != null) {
                    return pageList.size();
                }
                return 0;
            }
        };
        setAdapter(adapter);
    }

    private void setText(final ImlAnalyze<ISpanAnalysisObtainer> imlAnalyze, final int textSize) {
        stopAnalyze();
        analyzeCodeDictionary = new AnalyzeCodeDictionary(imlAnalyze);
        analyzeCodeDictionary.start();
    }

    private void touch(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                SelectSpanInfo downSelectSpanInfo = getSelectCharsInfo((int)event.getX() - getPaddingLeft(),
                        (int)event.getY() + getScrollY());
                if (downSelectSpanInfo != null) {
                    if (analysisInfo.charList.get(downSelectSpanInfo.charsIndex) instanceof char[]) {

//                        System.out.println("1..select: " + new String((char[])analysisInfo.charList.get(selectCharsInfo.charsIndex),
//                                selectCharsInfo.start, selectCharsInfo.offset));
                    } else {
//                        System.out.println("2..select: " + analysisInfo.charList.get(selectCharsInfo.charsIndex));
                    }
                    setCursorPosition(downSelectSpanInfo);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                SelectSpanInfo moveSelectSpanInfo = getSelectCharsInfo((int)event.getX() - getPaddingLeft(),
                        (int)event.getY() + getScrollY());
                if (moveSelectSpanInfo != null) {

                }
                break;
        }
    }

    private void setCursorPosition(SelectSpanInfo selectSpanInfo) {
        if (selectSpanInfo != null) {
            mCursorPaint.setStrokeWidth(mCursorPaint.getStrokeMiter());
            cursorStartX = getPaddingLeft() + selectSpanInfo.cursorRelativeX;
            cursorStartY = pageList.get(selectSpanInfo.pageIndex).y + pageList.get(selectSpanInfo.pageIndex).data.get(selectSpanInfo.relativeLineIndex).y;
            cursorEndX = cursorStartX;
            cursorEndY = cursorStartY + mCursorPaint.getFontMetrics().bottom - mCursorPaint.getFontMetrics().top;
        }
    }

    private void stopAnalyze() {
        if (analyzeCodeDictionary != null) {
            analyzeCodeDictionary.setStop();
        }
    }

    private SelectSpanInfo getSelectCharsInfo(int relativeX, int relativeY) {
        SelectSpanInfo selectSpanInfo = null;
        if (pageList != null) {
            PageInfo po = null;
            for (PageInfo pageInfo : pageList) {
                if (pageInfo.isInner(relativeX, relativeY)) {
                    po = pageInfo;
                    break;
                }
            }
            if (po != null) {
                List<LinesInfo> linesInfoList = po.data;
                LinesInfo lo = null;
                for (LinesInfo linesInfo : po.data) {
                    if ((linesInfo.y + po.y) > relativeY) {
                        if (lo == null) {
                            lo = po.data.get(0);
                        }
                        break;
                    }
                    lo = linesInfo;
                }
                if (lo != null) {
                    if (analysisInfo != null) {
                        measureWordHeight(0, mCursorPaint, lo.index);
                    }
                    SpanInfo ci = null;
                    for (SpanInfo spanInfo : lo.data) {
                        if (spanInfo.x > relativeX) {
                            if (ci == null) {
                                ci = lo.data.get(0);
                            }
                            break;
                        }
                        ci = spanInfo;
                    }
                    if (ci != null) {
                        float[] measureWidth = new float[1];
                        if (analysisInfo.charList.get(ci.index) instanceof char[]) {
                            char[] chars = (char[])analysisInfo.charList.get(ci.index);
                            int offset = mCursorPaint.breakText(chars,
                                    ci.start, ci.offset, relativeX - ci.x, measureWidth);
                            if (offset > 0) {
                                if (relativeX - ci.x > measureWidth[0]) {
                                    if (ci.offset >= offset + 1) {
                                        offset++;
                                    }
                                }
                                selectSpanInfo = new SelectSpanInfo();
                                selectSpanInfo.charsIndex = ci.index;
                                selectSpanInfo.relativeLineIndex = po.data.indexOf(lo);
                                selectSpanInfo.pageIndex = po.index;
                                selectSpanInfo.start = ci.start + offset - 1;
                                selectSpanInfo.offset = 1;
                                if (blnCanCursorVisible) {
                                    float charWidth = mCursorPaint.measureText(chars, ci.start + offset - 1, 1);
                                    if ((relativeX - ci.x - measureWidth[0]) > charWidth/2) {
                                        selectSpanInfo.cursorRelativeX = ci.x + mCursorPaint.measureText(chars, ci.start, offset);
                                    } else {
                                        if (offset > 1) {
                                            selectSpanInfo.cursorRelativeX = ci.x + mCursorPaint.measureText(chars, ci.start, offset - 1);
                                        } else {
                                            selectSpanInfo.cursorRelativeX = ci.x;
                                        }
                                    }
                                }
                            } else {
                                selectSpanInfo = new SelectSpanInfo();
                                selectSpanInfo.charsIndex = ci.index;
                                selectSpanInfo.relativeLineIndex = po.data.indexOf(lo);
                                selectSpanInfo.pageIndex = po.index;
                                selectSpanInfo.start = ci.start;
                                selectSpanInfo.offset = 1;
                                if (blnCanCursorVisible) {
                                    float charWidth = mCursorPaint.measureText(chars, ci.start, 1);
                                    if ((relativeX - ci.x) > charWidth/2) {
                                        selectSpanInfo.cursorRelativeX = ci.x + mCursorPaint.measureText(chars, ci.start, 1);
                                    } else {
                                        selectSpanInfo.cursorRelativeX = ci.x;
                                    }
                                }
                            }
                        } else if (analysisInfo.charList.get(ci.index) instanceof Character) {
                            Character character = (Character)analysisInfo.charList.get(ci.index);
                            selectSpanInfo = new SelectSpanInfo();
                            selectSpanInfo.charsIndex = ci.index;
                            selectSpanInfo.relativeLineIndex = po.data.indexOf(lo);
                            selectSpanInfo.pageIndex = po.index;
                            if (blnCanCursorVisible) {
                                float charWidth = mCursorPaint.measureText(character+"", ci.start, 1);
                                if ((relativeX - ci.x) > charWidth/2) {
                                    selectSpanInfo.cursorRelativeX = ci.x + charWidth;
                                } else {
                                    selectSpanInfo.cursorRelativeX = ci.x;
                                }
                            }
                        }
                    }
                }
            }
        }

        return selectSpanInfo;
    }

    private Path computeSelectRegion(SelectSpanInfo lastSelectSpanInfo, SelectSpanInfo currentSelectSpanInfo) {

        if (lastSelectSpanInfo != null && currentSelectSpanInfo != null) {
            if (lastSelectSpanInfo.pageIndex > currentSelectSpanInfo.pageIndex) {

            } else if (currentSelectSpanInfo.pageIndex > lastSelectSpanInfo.pageIndex) {

            } else {

            }
        }

        return null;
    }

    private void clear() {
        if (pageList != null) {
            for (PageInfo pageInfo : pageList) {
                if (pageInfo.data != null) {
                    pageInfo.data.clear();
                }
            }
            pageList.clear();
        }
        setAdapter(null);
    }

    @Override
    public void draw(Canvas canvas, Integer integer) {
        if (analysisInfo != null && analysisInfo.charList != null) {
            List<Object> objectLists = analysisInfo.charList;
            PageInfo pageInfo = pageList.get(integer);
            if (pageInfo != null && pageInfo.data != null) {
                for (LinesInfo linesInfo : pageInfo.data) {
                    if (analysisInfo != null) {
                        measureWordHeight(0, mTextPaint, linesInfo.index);
                    }
                    for (SpanInfo spanInfo : linesInfo.data) {
                        if (analysisInfo.colorMap.get(spanInfo.index) == null) {
                            if (mTextPaint.getColor() != analysisInfo.colorMap.get(-1)) {
                                mTextPaint.setColor(analysisInfo.colorMap.get(-1));
                            }
                        } else if (mTextPaint.getColor() != analysisInfo.colorMap.get(spanInfo.index)) {
                            mTextPaint.setColor(analysisInfo.colorMap.get(spanInfo.index));
                        }
                        if (objectLists.get(spanInfo.index) instanceof char[]) {
                            canvas.drawText((char[]) objectLists.get(spanInfo.index), spanInfo.start,
                                    spanInfo.offset, spanInfo.x, linesInfo.y - mTextPaint.getFontMetrics().top, mTextPaint);
                        } else if (objectLists.get(spanInfo.index) instanceof Character) {
                            canvas.drawText(objectLists.get(spanInfo.index)+"", spanInfo.start,
                                    spanInfo.offset, spanInfo.x, linesInfo.y - mTextPaint.getFontMetrics().top, mTextPaint);
                        }
                    }
                }
            }
        }
    }

    private float measureWordHeight(float wordHeight, Paint paint, int lineIndex) {
        if (resetPaint(paint, lineIndex) ) {
            wordHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
        }
        return wordHeight;
    }

    private boolean resetPaint(Paint paint, int lineIndex) {
        boolean result = false;
        if (analysisInfo != null) {
            Map<Integer, Integer> textSizeMap = analysisInfo.textSizeMap;
            if (textSizeMap != null && paint != null) {
                Integer textSize = textSizeMap.get(lineIndex);
                Integer defaultTextSize = textSizeMap.get(-1);
                if (textSize != null && (int) paint.getTextSize() != textSize) {
                    paint.setTextSize(textSize);
                    result = true;
                } else if (defaultTextSize != null && (int) paint.getTextSize() != defaultTextSize) {
                    paint.setTextSize(textSizeMap.get(-1));
                    result = true;
                }
            }
        }
        return result;
    }

    public static class TextHandler extends BaseHandler<TextScrollView> implements ISpanAnalysisObtainer {

        public final static int ANALYSIS_TEXT_FINISH = 0x001;
        public final static int PARARSE_START = 0x002;
        public final static int PARARSE_CHARS = 0x003;

        private ParaseWorker paraseWorker;

        TextHandler(TextScrollView mTextScrollView) {
            super(mTextScrollView);
        }

        @Override
        public void getResult(AnalysisInfo analysisInfo) {
            sendMessages(ANALYSIS_TEXT_FINISH, analysisInfo, 0, 0, 0);
        }

        @Override
        public void handleMessage(Message msg) {
            if (getReference() != null) {
                final TextScrollView mTextScrollView = getReference().get();
                switch (msg.what) {
                    case ANALYSIS_TEXT_FINISH:
                        if (mTextScrollView != null && msg.obj instanceof AnalysisInfo) {
                            sendMessages(PARARSE_START, msg.obj, 0, 0, 0);
                        }
                        break;

                    case PARARSE_START:
                        if (mTextScrollView != null && msg.obj instanceof AnalysisInfo) {
                            mTextScrollView.analysisInfo = (AnalysisInfo)msg.obj;
                            mTextScrollView.analysisInfo.textSizeMap = new HashMap<>();
                            mTextScrollView.analysisInfo.textSizeMap.put(-1, mTextScrollView.textSize);
                            if (mTextScrollView.changeLine != -1 && mTextScrollView.changeTextSize > 0) {
                                mTextScrollView.analysisInfo.textSizeMap.put(mTextScrollView.changeLine, mTextScrollView.changeTextSize);
                            }
                            SpanParase spanParase = new SpanParase(mTextScrollView.analysisInfo);
                            spanParase.init(mTextScrollView.getItemGroup().getWidth(), mTextScrollView.getHeight() + 0.0f);
                            if (paraseWorker != null) {
                                if (paraseWorker.getParase() != null) {
                                    paraseWorker.getParase().setCharsParaseObtainer(null);
                                }
                                paraseWorker.setListener(null);
                                mTextScrollView.clear();
                            }
                            paraseWorker = new ParaseWorker(spanParase);
                            paraseWorker.setListener(this);
                            paraseWorker.start();
                        }
                        break;

                    case PARARSE_CHARS:
                        List<LinesInfo> data = (List<LinesInfo>)msg.obj;
                        if (mTextScrollView != null && mTextScrollView.analysisInfo != null) {
                        }
                        if (msg.getData() != null) {
                            String tag = msg.getData().getString(BUNDLE_DEFAULT_STRING_KEY);
                            if (tag != null && paraseWorker != null && tag.contentEquals(paraseWorker.toString())) {
                                if (data != null) {
                                    mTextScrollView.contentHeight += msg.arg1;
                                    PageInfo pageInfo = mTextScrollView.new PageInfo();
                                    pageInfo.data = data;
                                    pageInfo.height = msg.arg1;
                                    pageInfo.width = msg.arg2;
                                    if (mTextScrollView.pageList.size() > 0) {
                                        pageInfo.y = mTextScrollView.pageList.get(mTextScrollView.pageList.size() - 1).y
                                                + mTextScrollView.pageList.get(mTextScrollView.pageList.size() - 1).height;
                                    }
                                    pageInfo.index = mTextScrollView.pageList.size();
                                    mTextScrollView.pageList.add(pageInfo);
                                    if (mTextScrollView.getAdpater() == null) {
                                        mTextScrollView.setAdapter(mTextScrollView.adapter);
                                    } else {
                                        mTextScrollView.getAdpater().notifyDataChange();
                                    }
                                }
                                if (msg.arg1 == SpanParase.UN_INVALUE
                                        && msg.arg2 == SpanParase.UN_INVALUE && mTextScrollView.hasTargetPosition) {
                                    mTextScrollView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mTextScrollView != null) {
                                                mTextScrollView.scrollTo(mTextScrollView.targetX, mTextScrollView.targetY);
                                                mTextScrollView.targetX = 0;
                                                mTextScrollView.targetY = 0;
                                                mTextScrollView.hasTargetPosition = false;
                                            }
                                        }
                                    });
                                }
                                return;
                            }
                        }
                        if (data != null) {
                            data.clear();
                        }
                        break;
                }
            }
        }
    }

    private class PageInfo {
        List<LinesInfo> data;
        int width;
        int height;
        int y;
        int index;

        boolean isInner(int relativeX, int relativeY) {
            if (/*width >= relativeX && relativeX >= 0.0f
                    &&*/ (y + height) >= relativeY && relativeY > y) {
                return true;
            }
            return false;
        }
    }

    private class SelectSpanInfo {
        int pageIndex;
        int relativeLineIndex;
        int charsIndex;
        int start = -1;
        int offset = -1;
        /**
         * 光标x坐标
         * cursor's x
         */
        float cursorRelativeX;
    }

}
