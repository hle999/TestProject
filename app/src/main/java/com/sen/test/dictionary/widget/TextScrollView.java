package com.sen.test.dictionary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sen.lib.view.BaseItemAdapter;
import com.sen.lib.view.VerticalScrollWidget;
import com.sen.test.dictionary.analyze.ImlAnalyze;
import com.sen.test.dictionary.analyze.CharsNormalAnalyze;
import com.sen.test.dictionary.file.AnalyzeCodeDictionary;
import com.sen.test.dictionary.file.ParaseWorker;
import com.sen.test.dictionary.info.AnalysisInfo;
import com.sen.test.dictionary.info.CharsInfo;
import com.sen.test.dictionary.info.LinesInfo;
import com.sen.test.dictionary.parse.CharsParase;
import com.sen.test.dictionary.utils.BaseHandler;
import com.sen.test.dictionary.view.ICharsAnalysisObtainer;
import com.sen.test.dictionary.view.ImlDrawText;
import com.sen.test.dictionary.view.PageText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sen on 2015/6/15.
 */
public class TextScrollView extends VerticalScrollWidget implements ImlDrawText<Integer>{

    private TextHandler mHandler;

    private int textSize;

    /**
     * �ı������С����λ��
     */
    private int changeLine = -1;

    /**
     * �ı������С
     */
    private int changeTextSize;

    private int contentHeight;

    private AnalyzeCodeDictionary analyzeCodeDictionary;

    private AnalysisInfo analysisInfo;

    private List<PageInfo> pageList;

    private BaseItemAdapter adapter;

    private Paint mPaint;

    private boolean isSelectText = false;

    private boolean hasTargetPosition = false;
    private int targetX = 0;
    private int targetY = 0;

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        if (mPaint != null) {
            mPaint.setTextSize(this.textSize);
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
        CharsNormalAnalyze normalAnalyze = new CharsNormalAnalyze(buffer);
        normalAnalyze.setObtainer(mHandler);
        setText(normalAnalyze);
    }

    public void setChangeLineTextSize(int changLine, int changeTextSize) {
        this.changeLine = changLine;
        this.changeTextSize = changeTextSize;
    }

    private void setText(final ImlAnalyze<ICharsAnalysisObtainer> imlAnalyze) {
        if (getTextSize() != 0) {
            /**
             * View�п������ڳ�ʼ��������Ϊ0��������Ϣ���У��ȳ�ʼ����ִ��
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

    private void init() {
        setVerticalScrollBarEnabled(true);
        mPaint = new Paint();
        mPaint.setTextSize(textSize);
        mPaint.setColor(Color.BLACK);
        mPaint.setSubpixelText(true);
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
                v.invalidate();
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

    private void setText(final ImlAnalyze<ICharsAnalysisObtainer> imlAnalyze, final int textSize) {
        stopAnalyze();
        analyzeCodeDictionary = new AnalyzeCodeDictionary(imlAnalyze);
        analyzeCodeDictionary.start();
    }

    private void touch(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (pageList != null) {
                    PageInfo po = null;
                    for (PageInfo pageInfo : pageList) {
                        if (pageInfo.isInner((int)event.getX(), (int)event.getY() + getScrollY())) {
                            po = pageInfo;
                            break;
                        }
                    }
                    if (po != null) {
                        List<LinesInfo> linesInfoList = po.data;
                        LinesInfo lo = null;
                        for (LinesInfo linesInfo : po.data) {
                            if ((linesInfo.y + po.y) > (event.getY() + getScrollY())) {
                                if (lo == null) {
                                    lo = po.data.get(0);
                                }
                                break;
                            }
                            lo = linesInfo;
                        }
                        if (lo != null) {
                            CharsInfo ci = null;
                            for (CharsInfo charsInfo : lo.data) {
                                if (charsInfo.x > event.getX()- getPaddingLeft()) {
                                    if (ci == null) {
                                        ci = lo.data.get(0);
                                    }
                                    break;
                                }
                                ci = charsInfo;
                            }
                            if (ci != null) {
                                float[] measureWidth = new float[1];
                                if (analysisInfo.charList.get(ci.index) instanceof char[]) {
                                    char[] chars = (char[])analysisInfo.charList.get(ci.index);
                                    int offset = mPaint.breakText(chars,
                                            ci.start, ci.offset, event.getX() - getPaddingLeft() - ci.x, measureWidth);
                                    if (offset > 0) {
                                        if (event.getX() - getPaddingLeft() - ci.x > measureWidth[0]) {
                                            if (ci.offset >= offset + 1) {
                                                offset++;
                                            }
                                        }

                                        System.out.println("char: "+
                                                ((char[])analysisInfo.charList.get(ci.index))[ci.start + offset - 1]);
                                    } else {
                                        System.out.println("char: "+
                                                ((char[])analysisInfo.charList.get(ci.index))[ci.start]);
                                    }
                                } else if (analysisInfo.charList.get(ci.index) instanceof Character) {
                                    System.out.println("char: "+
                                            (Character)analysisInfo.charList.get(ci.index));
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    private void stopAnalyze() {
        if (analyzeCodeDictionary != null) {
            analyzeCodeDictionary.setStop();
        }
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
    public void removeAllViews() {
        stopAnalyze();
        mHandler = null;
        super.removeAllViews();
    }

    @Override
    public void draw(Canvas canvas, Integer integer) {
        if (analysisInfo != null && analysisInfo.charList != null) {
            List<Object> objectLists = analysisInfo.charList;
            PageInfo pageInfo = pageList.get(integer);
            if (pageInfo != null && pageInfo.data != null) {
                for (LinesInfo linesInfo : pageInfo.data) {
                    for (CharsInfo charsInfo : linesInfo.data) {
                        if (analysisInfo.colorMap.get(charsInfo.index) == null) {
                            if (mPaint.getColor() != analysisInfo.colorMap.get(-1)) {
                                mPaint.setColor(analysisInfo.colorMap.get(-1));
                            }
                        } else if (mPaint.getColor() != analysisInfo.colorMap.get(charsInfo.index)) {
                            mPaint.setColor(analysisInfo.colorMap.get(charsInfo.index));
                        }
                        if (objectLists.get(charsInfo.index) instanceof char[]) {
                            canvas.drawText((char[]) objectLists.get(charsInfo.index), charsInfo.start,
                                    charsInfo.offset, charsInfo.x, linesInfo.y - mPaint.getFontMetrics().top, mPaint);
                        } else if (objectLists.get(charsInfo.index) instanceof Character) {
                            canvas.drawText((Character) objectLists.get(charsInfo.index)+"", charsInfo.start,
                                    charsInfo.offset, charsInfo.x, linesInfo.y - mPaint.getFontMetrics().top, mPaint);
                        }
                    }
                }


            }
        }
    }

    public static class TextHandler extends BaseHandler<TextScrollView> implements ICharsAnalysisObtainer {

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
                            CharsParase charsParase = new CharsParase(mTextScrollView.analysisInfo);
                            charsParase.init(mTextScrollView.getItemGroup().getWidth(),
                                    mTextScrollView.getHeight() + 0.0f,
                                    mTextScrollView.textSize, mTextScrollView.changeLine, mTextScrollView.changeTextSize);
                            if (paraseWorker != null) {
                                if (paraseWorker.getParase() != null) {
                                    paraseWorker.getParase().setCharsParaseObtainer(null);
                                }
                                paraseWorker.setListener(null);
                                mTextScrollView.clear();
                            }
                            paraseWorker = new ParaseWorker(charsParase);
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
                                if (msg.arg1 == CharsParase.UN_INVALUE
                                        && msg.arg2 == CharsParase.UN_INVALUE && mTextScrollView.hasTargetPosition) {
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

}
