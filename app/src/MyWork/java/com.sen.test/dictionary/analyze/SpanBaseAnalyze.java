package com.sen.test.dictionary.analyze;

import com.sen.test.dictionary.view.ISpanAnalysisObtainer;


/**
 * Editor: sgc
 * Date: 2015/01/20
 */
public abstract class SpanBaseAnalyze implements ImlAnalyze<ISpanAnalysisObtainer> {

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
    private ISpanAnalysisObtainer iSpanAnalysisObtainer = null;

    public SpanBaseAnalyze(byte[] byteBuffer){
        this.byteBuffer = byteBuffer;
    }

    @Override
    public void setObtainer(ISpanAnalysisObtainer iSpanAnalysisObtainer) {
        this.iSpanAnalysisObtainer = iSpanAnalysisObtainer;
    }

    @Override
    public void start() {

        /**
         * step 2
         */
        filterExplain(byteBuffer);
        iSpanAnalysisObtainer = null;
        byteBuffer = null;
        isStop = true;
    }

    @Override
    public void stop() {
        isStop = true;
    }

    @Override
    public void clear() {
        iSpanAnalysisObtainer = null;
        byteBuffer = null;
    }

    public boolean isStop() {
        return isStop;
    }

    public ISpanAnalysisObtainer getObtainer() {
        return iSpanAnalysisObtainer;
    }

    public abstract void filterExplain(byte[] byteBuffer);

}
