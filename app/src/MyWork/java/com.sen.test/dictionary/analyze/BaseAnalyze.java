package com.sen.test.dictionary.analyze;

import android.graphics.Color;

import com.sen.test.dictionary.view.TextObtainer;


/**
 * Editor: sgc
 * Date: 2015/01/20
 */
public abstract class BaseAnalyze implements ImlAnalyze<TextObtainer> {



    /**
     * 数据
     */
    private byte[] byteBuffer = null;

    /**
     * 数据开始解析的位置
     */
    private int startIndex = 0;

    /**
     * 关键词
     */
    private String keyWord;

    /**
     * 行首是否需要添加关键词
     */
    private boolean isAddKeyWord;

    /**
     * 停止解析
     */
    private boolean isStop = false;

    /**
     * 计算字体位置和颜色的监听器
     */
    private TextObtainer analyzeTextObtainer = null;

    public BaseAnalyze(byte[] byteBuffer, int startIndex, String keyWord, boolean isAddKeyWord){
        this.byteBuffer = byteBuffer;
        this.startIndex = startIndex;
        this.keyWord = keyWord;
        this.isAddKeyWord = isAddKeyWord;
    }

    @Override
    public void setObtainer(TextObtainer analyzeTextObtainer) {
        this.analyzeTextObtainer = analyzeTextObtainer;
    }

    @Override
    public void start() {

        /**
         * step 1
         */
        if (isAddKeyWord && keyWord != null) {
            if (analyzeTextObtainer != null) {
                analyzeTextObtainer.getCharArray(TextObtainer.ANALY_TEXT, keyWord.toCharArray(), Color.BLACK);
                analyzeTextObtainer.getCharArray(TextObtainer.ANALY_NEW_LINE, null, 0);//换行
            }
        }

        /**
         * step 2
         */
        filterExplain(byteBuffer, startIndex);

        /**
         * step 3
         */
        if (analyzeTextObtainer != null) {//解析完毕
            analyzeTextObtainer.getCharArray(TextObtainer.ANALY_FINISH, null, 0);
        }
        analyzeTextObtainer = null;
        byteBuffer = null;
        isStop = true;
    }

    @Override
    public void stop() {
        isStop = true;
    }

    @Override
    public void clear() {
        analyzeTextObtainer = null;
        byteBuffer = null;
        keyWord = null;
    }

    public boolean isStop() {
        return isStop;
    }

    public TextObtainer getAnalyzeTextObtainer() {
        return analyzeTextObtainer;
    }

    public abstract void filterExplain(byte[] byteBuffer, int position);

}
