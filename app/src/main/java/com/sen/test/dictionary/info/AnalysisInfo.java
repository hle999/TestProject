package com.sen.test.dictionary.info;

import java.util.List;
import java.util.Map;

/**
 * Created by Sgc on 2015/6/15.
 */
public class AnalysisInfo {

    public List<Object> charList;
    public Map<Integer, Integer> colorMap;
    public Map<Integer, Boolean> newLineMap;

    public void destroy() {
        if (charList != null) {
            charList.clear();
        }
        if (colorMap != null) {
            colorMap.clear();
        }
        if (newLineMap != null) {
            newLineMap.clear();
        }
    }

}
