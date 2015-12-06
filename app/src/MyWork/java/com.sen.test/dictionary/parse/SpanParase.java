package com.sen.test.dictionary.parse;

import android.graphics.Paint;

import com.sen.test.dictionary.info.AnalysisInfo;
import com.sen.test.dictionary.info.SpanInfo;
import com.sen.test.dictionary.info.LinesInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sgc on 2015/6/16.
 */
public class SpanParase {

    public final static int UN_INVALUE = -1;

    private float mScreenWidth;
    private float mScreenHeight;

    private AnalysisInfo analysisInfo;
    private ISpanParaseObtainer iObtainer;
    private List<SpanInfo> lineSpanInfoList;
    private List<LinesInfo> linesInfoList;

    public void setCharsParaseObtainer(ISpanParaseObtainer iObtainer) {
        this.iObtainer = iObtainer;
    }

    public SpanParase(AnalysisInfo analysisInfo) {
        this.analysisInfo = analysisInfo;
    }

    public void init(float mScreenWidth, float mScreenHeight) {
        this.mScreenWidth = mScreenWidth;
        this.mScreenHeight = mScreenHeight;
    }

    public void start() {
        parase(0, UN_INVALUE, UN_INVALUE, 0);
    }

    public void parase(int intCharListIndex, int intCharStart, int intCharOffset, int intLine) {
        if (analysisInfo != null && mScreenWidth > 0 && mScreenHeight > 0) {
            Paint mPaint = new Paint();
            mPaint.setSubpixelText(true);
            if (analysisInfo.charList != null && iObtainer != null) {
                String tag = iObtainer.toString();
                Map newLineMap = analysisInfo.newLineMap;
                List<Object> objects = analysisInfo.charList;
                int number = objects.size();
                float textWidth = 0.0f;
                float textHeight = 0.0f;
                float[] measureWidth = new float[1];
                float h = measureWordHeight(0, mPaint, intLine);
                for (int ii = intCharListIndex ; ii < number ; ii++) {
                    Object obj = null;
                    if (objects != null) {
                        obj = objects.get(ii);
                    }
                    if (obj != null) {
                        if (obj instanceof char[]) {
                            char[] chars = (char[]) obj;
                            int start = 0;
                            int length = chars.length;
                            int leave = length;
                            if (intCharStart > UN_INVALUE && intCharOffset > UN_INVALUE) {
                                start = intCharStart;
                                leave = intCharOffset;
                            }
                            while (leave != 0) {
                                int offset = UN_INVALUE;
                                if (chars != null) {
                                    offset = mPaint.breakText(chars, start, leave,
                                            mScreenWidth - textWidth, measureWidth);
                                } else {
                                    break;
                                }
                                if (offset > 0) {
                                    int end = start + offset - 1;
                                    int sufIndex = sufMatchSymbol(chars, end);
                                    if (sufIndex > end) {
                                        int preIndex = preMatchSymbol(chars, end);
                                        addCharsInfoToLine(ii, start,
                                                preIndex - start, textWidth, textHeight);

                                        addLineToList(intLine, textHeight, mPaint.getTextSize());
                                        intLine++;

                                        start = preIndex;
                                        leave = length - start;
                                        textHeight += h;
                                        textWidth = 0.0f;
                                        if (textHeight >= mScreenHeight) {
                                            addPage(tag, textHeight);
                                            textHeight = 0.0f;
                                        }
                                        if (analysisInfo != null) {
                                            h = measureWordHeight(h, mPaint, intLine);
                                        }
                                    } else {
                                        addCharsInfoToLine(ii, start,
                                                end + 1 - start, textWidth, textHeight);
                                        textWidth += measureWidth[0];
                                        start = end + 1;
                                        leave = length - start;
                                    }
                                } else if (offset != UN_INVALUE){

                                    addLineToList(intLine, textHeight, mPaint.getTextSize());
                                    intLine++;

                                    textHeight += h;
                                    textWidth = 0.0f;
                                    if (textHeight >= mScreenHeight) {
                                        addPage(tag, textHeight);
                                        textHeight = 0.0f;
                                    }
                                    if (analysisInfo != null) {
                                        h = measureWordHeight(h, mPaint, intLine);
                                    }
                                }
                            }
                        } else if (obj instanceof Character) {
                            char c;
                            try {
                                c = (char)obj;
                                measureWidth[0] = mPaint.measureText(c+"");
                            } catch (ClassCastException e) {
                                e.printStackTrace();
                                break;
                            }
                            if (textWidth + measureWidth[0] > mScreenWidth) {

                                addLineToList(intLine, textHeight, mPaint.getTextSize());
                                intLine++;

                                textHeight += h;
                                textWidth = 0;

                                if (textHeight >= mScreenHeight) {
                                    addPage(tag, textHeight);
                                    textHeight = 0.0f;
                                }
                                if (analysisInfo != null) {
                                    h = measureWordHeight(h, mPaint, intLine);

                                }

                                addCharsInfoToLine(ii, 0, 1, textWidth, textHeight);
                                textWidth = measureWidth[0];
                            } else {
                                addCharsInfoToLine(ii, 0, 1, textWidth, textHeight);

                                textWidth += measureWidth[0];
                            }
                        } else {
                            continue;
                        }
                        if (newLineMap != null && newLineMap.get(ii) instanceof Boolean) {

                            addLineToList(intLine, textHeight, mPaint.getTextSize());
                            intLine++;

                            textHeight += h;
                            textWidth = 0.0f;
                            if (textHeight >= mScreenHeight) {
                                addPage(tag, textHeight);
                                textHeight = 0.0f;
                            }
                            if (analysisInfo != null) {
                                h = measureWordHeight(h, mPaint, intLine);
                            }
                        }
                    } else {
                        break;
                    }
                    if (isStop()) {
                        break;
                    }
                }
                if (!isStop()) {

                    addLineToList(intLine, textHeight, mPaint.getTextSize());
                    intLine++;

                    textHeight += h;
                    addPage(tag, textHeight);
                    addResult(tag, null, UN_INVALUE, UN_INVALUE);
//                    charsInfoList = null;
                    linesInfoList = null;
                    textHeight = 0.0f;
                } else {
                    if (lineSpanInfoList != null) {
                        lineSpanInfoList.clear();
                        lineSpanInfoList = null;
                    }
                }
            }
            analysisInfo = null;
            iObtainer = null;
        }
    }

    private boolean isStop() {
        if (analysisInfo == null || iObtainer == null || iObtainer.isStop()) {
            return true;
        }
        return false;
    }

    /*private void addPageCharsList(String tag, List<CharsInfo> charsInfoList, float height, float width) {
        if (iObtainer != null) {
            iObtainer.getParaseResult(tag, charsInfoList, height, width);
        }
    }*/

    private void addLineToList(int lineIndex, float textHeight, float textSize) {
        if (lineSpanInfoList != null) {
            LinesInfo linesInfo = new LinesInfo();
            linesInfo.index = lineIndex;
            linesInfo.y = textHeight;
            linesInfo.data = lineSpanInfoList;
            if (linesInfoList == null) {
                linesInfoList = new ArrayList<>();
            }
            linesInfoList.add(linesInfo);
            lineSpanInfoList = null;
        }
    }

    private void addPage(String tag, float textHeight) {
        addResult(tag, linesInfoList, textHeight, mScreenWidth);
        linesInfoList = null;
    }

    private void addResult(String tag, Object data, float height, float width) {
        if (iObtainer != null) {
            iObtainer.getParaseResult(tag, data, height, width);
        }
    }

    private void addCharsInfoToLine(int index, int start, int offset, float x, float y) {
        SpanInfo spanInfo = new SpanInfo();
        spanInfo.index = index;
        spanInfo.start = start;
        spanInfo.offset = offset;
        spanInfo.x = x;
//        charsInfo.y = y;
        if (lineSpanInfoList == null) {
            lineSpanInfoList = new ArrayList<>();
        }
        lineSpanInfoList.add(spanInfo);
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

    /*private CharsInfo getCharsInfo(int index, int start, int offset, float x, float y) {
        CharsInfo charsInfo = new CharsInfo();
        charsInfo.index = index;
        charsInfo.start = start;
        charsInfo.offset = offset;
        charsInfo.x = x;
        charsInfo.y = y;
        return charsInfo;
    }*/

    /*private float getWordHeight(float wordHeight, Paint mPaint,
                                Map textSizeMap, int lineIndex) {
        if (resetPaint(mPaint, textSizeMap, lineIndex) ) {
            wordHeight = mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top;
        }
        return wordHeight;
    }

    private boolean resetPaint(Paint paint, Map textSizeMap, int lineIndex) {
        boolean result = false;
        if (textSizeMap != null && textSizeMap.get(lineIndex) instanceof Integer) {
            if ((int) paint.getTextSize() != (Integer) textSizeMap.get(lineIndex)) {
                paint.setTextSize((Integer) textSizeMap.get(lineIndex));
                result = true;
            }
        } else {
            if (textSizeMap != null && textSizeMap.get(-1) instanceof Integer
                    && (Integer) textSizeMap.get(-1) != (int) paint.getTextSize()) {
                paint.setTextSize((Integer) textSizeMap.get(-1));
                result = true;
            }
        }
        return result;
    }*/

    /**
     * 向前搜索
     *
     * @param chars
     * @param index
     * @return index
     */
    private int preMatchSymbol(char[] chars, int index) {

        if (chars != null && chars.length > index && index >= 0) {
            if (isSpecialSymbol(chars[index])) {
                while ((index - 1) >= 0 && chars != null && isSpecialSymbol(chars[index - 1])) {
                    index--;
                }
            }
        }

        return index;
    }

    /**
     * 向后搜索
     *
     * @param chars
     * @param index
     * @return index
     */
    private int sufMatchSymbol(char[] chars, int index) {

        if (chars != null && chars.length > index && index >= 0) {
            if (isSpecialSymbol(chars[index])) {
                while (chars != null && chars.length > (index + 1) && isSpecialSymbol(chars[index + 1])) {
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
