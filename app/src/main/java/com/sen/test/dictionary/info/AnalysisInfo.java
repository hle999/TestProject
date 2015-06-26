package com.sen.test.dictionary.info;

import android.graphics.Paint;

import java.util.List;
import java.util.Map;

/**
 * Created by Sgc on 2015/6/15.
 */
public class AnalysisInfo {

    public List<Object> charList;
    public Map<Integer, Integer> colorMap;
    public Map<Integer, Integer> textSizeMap;
    public Map<Integer, Boolean> newLineMap;


    public float measureWordHeight(float wordHeight, Paint paint, int lineIndex) {
        if (resetPaint(paint, lineIndex) ) {
            wordHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
        }
        return wordHeight;
    }

    public void destroy() {
        if (charList != null) {
            charList.clear();
        }
        if (colorMap != null) {
            colorMap.clear();
        }
        if (textSizeMap != null) {
            textSizeMap.clear();
        }
        if (newLineMap != null) {
            newLineMap.clear();
        }
    }

    private boolean resetPaint(Paint paint, int lineIndex) {
        boolean result = false;
        if (textSizeMap != null && paint != null) {
            if (textSizeMap.get(lineIndex) != null
                    && (int) paint.getTextSize() != textSizeMap.get(lineIndex)) {
                paint.setTextSize(textSizeMap.get(lineIndex));
                result = true;
            } else if (textSizeMap.get(-1) != null
                    && (int) paint.getTextSize() != textSizeMap.get(-1)) {
                paint.setTextSize(textSizeMap.get(-1));
                result = true;
            }
        }
        return result;
    }

}
