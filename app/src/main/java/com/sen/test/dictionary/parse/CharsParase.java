package com.sen.test.dictionary.parse;

import android.graphics.Paint;

import com.sen.test.dictionary.info.AnalysisInfo;
import com.sen.test.dictionary.info.CharsInfo;
import com.sen.test.dictionary.io.DictIOFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sgc on 2015/6/16.
 */
public class CharsParase {

    private float mScreenWidth;
    private float mScreenHeight;
    private int mTextSize;
    private int changeLine = -1;
    private int changeTextSize = 0;

    private AnalysisInfo analysisInfo;
    private ICharsParaseObtainer iObtainer;
    private List<CharsInfo> charsInfoList;

    public void setCharsParaseObtainer(ICharsParaseObtainer iObtainer) {
        this.iObtainer = iObtainer;
    }

    public CharsParase(AnalysisInfo analysisInfo) {
        this.analysisInfo = analysisInfo;
    }

    public void init(float mScreenWidth, float mScreenHeight,
                     int mTextSize, int changeLine, int changeTextSize) {
        this.mScreenWidth = mScreenWidth;
        this.mScreenHeight = mScreenHeight;
        this.mTextSize = mTextSize;
        this.changeLine = changeLine;
        this.changeTextSize = changeTextSize;
    }

    public void start() {
        if (analysisInfo != null) {
            Paint mPaint = new Paint();
            float h = mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top;
            mPaint.setTextSize(mTextSize);
            mPaint.setSubpixelText(true);
            if (analysisInfo.charList != null) {
                String tag = analysisInfo.toString();
                int num = 0;
                int line = 0;
                float textWidth = 0.0f;
                float textHeight = 0.0f;
                Map textSizeMap = analysisInfo.colorMap;
                Map newLineMap = analysisInfo.newLineMap;
                float[] measureWidth = new float[1];
                for (Object obj : analysisInfo.charList) {
                    if (resetPaint(mPaint, textSizeMap, num, line) ) {
                        h = mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top;
                    }
                    if (obj != null) {
                        if (obj instanceof char[]) {
                            char[] chars = (char[]) obj;
                            int length = chars.length;
                            int leave = length;
                            int start = 0;
                            while (leave != 0) {
                                int offset = mPaint.breakText(chars, start, leave,
                                        mScreenWidth - textWidth, measureWidth);
                                if (offset > 0) {
                                    int end = start + offset - 1;
                                    int sufIndex = sufMatchSymbol(chars, end);
                                    if (sufIndex > end) {
                                        int preIndex = preMatchSymbol(chars, end);
                                        addCharsInfoToList(num, start,
                                                preIndex - start, textWidth, textHeight);
                                        start = preIndex;
                                        leave = length - start;
                                        textHeight += h;
                                        textWidth = 0.0f;
                                        if (textHeight >= mScreenHeight) {
                                            addPageCharsList(tag, charsInfoList, textHeight, mScreenWidth);
                                            charsInfoList = null;
                                            textHeight = 0.0f;
                                        }
                                    } else {
                                        addCharsInfoToList(num, start,
                                                end + 1 - start, textWidth, textHeight);
                                        textWidth += measureWidth[0];
                                        start = end + 1;
                                        leave = length - start;
                                    }
                                } else {
                                    textHeight += h;
                                    textWidth = 0.0f;
                                    if (textHeight >= mScreenHeight) {
                                        addPageCharsList(tag, charsInfoList, textHeight, mScreenWidth);
                                        charsInfoList = null;
                                        textHeight = 0.0f;
                                    }
                                }
                            }
                        } else if (obj instanceof Character) {
                            char c = (char)obj;
                            measureWidth[0] = mPaint.measureText(c+"");
                            if (textWidth + measureWidth[0] > mScreenWidth) {
                                textHeight += h;
                                textWidth = measureWidth[0];

                                if (textHeight >= mScreenHeight) {
                                    addPageCharsList(tag, charsInfoList, textHeight, mScreenWidth);
                                    charsInfoList = null;
                                    textHeight = 0.0f;
                                }
                                addCharsInfoToList(num, 0, 1, textWidth, textHeight);
                            } else {
                                addCharsInfoToList(num, 0, 1, textWidth, textHeight);

                                textWidth += measureWidth[0];
                            }
                        } else {
                            continue;
                        }
                        if (newLineMap.get(num) instanceof Boolean) {
                            textHeight += h;
                            textWidth = 0.0f;
                            if (textHeight >= mScreenHeight) {
                                addPageCharsList(tag, charsInfoList, textHeight, mScreenWidth);
                                charsInfoList = null;
                                textHeight = 0.0f;
                            }
                        }
                    } else {
                        break;
                    }
                    if (isStop()) {
                        break;
                    }
                    num++;
                }
                textHeight += h;
                addPageCharsList(tag, charsInfoList, textHeight, mScreenWidth);
                charsInfoList = null;
                textHeight = 0.0f;
            }
            analysisInfo = null;
            iObtainer = null;
        }
    }

    private boolean isStop() {
        if (iObtainer == null || iObtainer.isStop()) {
            return true;
        }
        return false;
    }

    private void addPageCharsList(String tag, List<CharsInfo> charsInfoList, float height, float width) {
        if (iObtainer != null) {
            iObtainer.getParaseResult(tag, charsInfoList, height, width);
        }
    }

    private void addCharsInfoToList(int index, int start, int offset, float x, float y) {
        CharsInfo charsInfo = new CharsInfo();
        charsInfo.index = index;
        charsInfo.start = start;
        charsInfo.offset = offset;
        charsInfo.x = x;
        charsInfo.y = y;
        if (charsInfoList == null) {
            charsInfoList = new ArrayList<>();
        }
        charsInfoList.add(charsInfo);
    }

    private boolean resetPaint(Paint paint, Map textSizeMap, int byteIndex, int lineIndex) {
        boolean result = false;
        if (changeLine == lineIndex && changeTextSize != 0) {
            paint.setTextSize(changeTextSize);
            result = true;
        } else {
            if (textSizeMap.get(byteIndex) instanceof Integer) {
                if ((int) paint.getTextSize() != (Integer) textSizeMap.get(byteIndex)) {
                    paint.setTextSize((Integer) textSizeMap.get(byteIndex));
                    result = true;
                }
            } else {
                if (textSizeMap.get(-1) instanceof Integer
                        && (Integer) textSizeMap.get(-1) != (int) paint.getTextSize()) {
                    paint.setTextSize((Integer) textSizeMap.get(-1));
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * ��ǰ����
     *
     * @param chars
     * @param index
     * @return index
     */
    private int preMatchSymbol(char[] chars, int index) {

        if (chars != null && chars.length > index && index >= 0) {
            if (isSpecialSymbol(chars[index])) {
                while ((index - 1) >= 0 && isSpecialSymbol(chars[index - 1])) {
                    index--;
                }
            }
        }

        return index;
    }

    /**
     * �������
     *
     * @param chars
     * @param index
     * @return index
     */
    private int sufMatchSymbol(char[] chars, int index) {

        if (chars != null && chars.length > index && index >= 0) {
            if (isSpecialSymbol(chars[index])) {
                while (chars.length > (index + 1) && isSpecialSymbol(chars[index + 1])) {
                    index++;
                }
            }
        }

        return index;
    }

    private boolean isSpecialSymbol(char c) {
        if (isEnglish(c) || c == '\'') {
            return true;
        }
        return false;
    }

    private boolean isEnglish(char c) {
        if ('z' >= c && c >= 'a' || 'Z' >= c && c >= 'A') {
            return true;
        }
        return false;
    }

}
