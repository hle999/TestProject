package com.sen.test.dictionary.widget;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;

import com.sen.lib.view.VerticalScrollWidget;
import com.sen.test.dictionary.analyze.ImlAnalyze;
import com.sen.test.dictionary.analyze.TestNormalAnalyze;
import com.sen.test.dictionary.file.AnalyzeCodeDictionary;
import com.sen.test.dictionary.file.ParaseWorker;
import com.sen.test.dictionary.info.AnalysisInfo;
import com.sen.test.dictionary.info.CharsInfo;
import com.sen.test.dictionary.parse.CharsParase;
import com.sen.test.dictionary.parse.ICharsParaseObtainer;
import com.sen.test.dictionary.utils.BaseHandler;
import com.sen.test.dictionary.view.ICharsAnalysisObtainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sen on 2015/6/15.
 */
public class TextScrollView extends VerticalScrollWidget {

    private TextHandler mHandler;

    private int textSize;

    /**
     * 改变字体大小的行位置
     */
    private int changeLine = -1;

    /**
     * 改变字体大小
     */
    private int changeTextSize;

    private int contentHeight;

    private AnalyzeCodeDictionary analyzeCodeDictionary;

    private AnalysisInfo analysisInfo;

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    public void setText(byte[] buffer) {
        TestNormalAnalyze normalAnalyze = new TestNormalAnalyze(buffer);
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

    private void init() {
        mHandler = new TextHandler(this);
    }

    private void setText(final ImlAnalyze<ICharsAnalysisObtainer> imlAnalyze, final int textSize) {
        stopAnalyze();
        analyzeCodeDictionary = new AnalyzeCodeDictionary(imlAnalyze);
        analyzeCodeDictionary.start();
    }

    private void stopAnalyze() {
        if (analyzeCodeDictionary != null) {
            analyzeCodeDictionary.setStop();
        }
    }

    @Override
    public void removeAllViews() {
        stopAnalyze();
        mHandler = null;
        super.removeAllViews();
    }

    public static class TextHandler extends BaseHandler<TextScrollView> implements ICharsAnalysisObtainer {

        public final static int ANALYSIS_TEXT_FINFISH = 0x001;
        public final static int PARARSE_CHARS = 0x002;

        TextHandler(TextScrollView mTextScrollView) {
            super(mTextScrollView);
        }

        @Override
        public void getResult(AnalysisInfo analysisInfo) {
            sendMessages(ANALYSIS_TEXT_FINFISH, analysisInfo, 0, 0, 0);
        }

        @Override
        public void handleMessage(Message msg) {
            if (getReference() != null) {
                TextScrollView mTextScrollView = getReference().get();
                switch (msg.what) {
                    case ANALYSIS_TEXT_FINFISH:
                        if (mTextScrollView != null && msg.obj instanceof AnalysisInfo) {
                            mTextScrollView.analysisInfo = (AnalysisInfo)msg.obj;
                            CharsParase charsParase = new CharsParase(mTextScrollView.analysisInfo);
                            charsParase.init(mTextScrollView.getWidth(), mTextScrollView.getHeight(),
                                    mTextScrollView.textSize, mTextScrollView.changeLine, mTextScrollView.changeTextSize);
                            ParaseWorker paraseWorker = new ParaseWorker(charsParase);
                            paraseWorker.setListener(this);
                            paraseWorker.start();
                        }
                        break;

                    case PARARSE_CHARS:
                        List<CharsInfo> data = (List<CharsInfo>)msg.obj;
                        if (mTextScrollView != null && mTextScrollView.analysisInfo != null) {
                            if (msg.getData() != null && mTextScrollView.analysisInfo != null) {
                                String tag = msg.getData().getString(BUNDLE_DEFAULT_STRING_KEY);
                                if (tag != null && tag.contentEquals(mTextScrollView.analysisInfo.toString())) {
                                    mTextScrollView.contentHeight += msg.arg1;
                                    System.out.println(" "+mTextScrollView.contentHeight);
                                    return;
                                }
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


}
