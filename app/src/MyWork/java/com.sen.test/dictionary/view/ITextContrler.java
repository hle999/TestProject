package com.sen.test.dictionary.view;

import com.sen.test.dictionary.file.AnalyzeCodeDictionary;
import com.sen.test.dictionary.parse.TextParse;

/**
 * Editor: sgc
 * Date: 2015/02/27
 */
public interface ITextContrler {

    public void clearHandler();
    public void stop();
    public void setSelectTextState(boolean isSelectText);
    public void onResume();
    public void onPause(boolean isActivityPause);
    public void setOnDictTextViewListener(CustomTextView.DictTextViewListener dictTextViewListener);
    public void loadData(AnalyzeCodeDictionary analyzeCodeDictionary, TextParse textParse);
    public void clearSelectWords();

}
