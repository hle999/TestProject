package com.sen.test.dictionary.analyze;

import com.sen.test.dictionary.view.ICharsAnalysisObtainer;


/**
 * Editor: sgc
 * Date: 2015/01/20
 */
public abstract class CharsBaseAnalyze implements ImlAnalyze<ICharsAnalysisObtainer> {

    /**
     * 数据
     */
    private byte[] byteBuffer = null;

    /**
     * 停止解析
     */
    private boolean isStop = false;

    /**
     * 计算字体位置和颜色的监听器
     */
    private ICharsAnalysisObtainer iCharsAnalysisObtainer = null;

    public CharsBaseAnalyze(byte[] byteBuffer){
        this.byteBuffer = byteBuffer;
    }

    @Override
    public void setObtainer(ICharsAnalysisObtainer iCharsAnalysisObtainer) {
        this.iCharsAnalysisObtainer = iCharsAnalysisObtainer;
    }

    @Override
    public void start() {

        /**
         * step 2
         */
        filterExplain(byteBuffer);
        iCharsAnalysisObtainer = null;
        byteBuffer = null;
        isStop = true;
    }

    @Override
    public void stop() {
        isStop = true;
    }

    @Override
    public void clear() {
        iCharsAnalysisObtainer = null;
        byteBuffer = null;
    }

    public boolean isStop() {
        return isStop;
    }

    public ICharsAnalysisObtainer getObtainer() {
        return iCharsAnalysisObtainer;
    }

    public abstract void filterExplain(byte[] byteBuffer);

}
