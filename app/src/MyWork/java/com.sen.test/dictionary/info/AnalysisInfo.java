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

}
