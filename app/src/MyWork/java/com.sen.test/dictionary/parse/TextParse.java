package com.sen.test.dictionary.parse;

import android.graphics.Paint;

import com.sen.test.dictionary.info.CachePageInfo;
import com.sen.test.dictionary.info.LineInfo;
import com.sen.test.dictionary.info.LineTextInfo;
import com.sen.test.dictionary.view.CharFilterRule;
import com.sen.test.dictionary.view.TextObtainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 计算字体位置和颜色
 * Editor: sgc
 * Date: 2015/01/04
 */
public class TextParse implements TextObtainer<TextParse.TextHandler> {

    /**
     * 开始显示时，要加载的数量
     */
    private int firstLoadPageNumber = 3;

    /**
     * 字体大小
     */
    private float textSize = 25.0f;

    /**
     * 行高度
     */
    private int lineHeight = 0;

    /**
     * 指定哪一行改变字体大小
     */
    private int changeLineIndex = -1;

    /**
     * 那一行字体要改变的大小
     */
    private float changeLineTextSize = 0.0f;

    /**
     * 显示的宽度
     */
    private int currentViewWidth = 0;

    /**
     * 显示的高度
     */
    private int currentViewHeight = 0;

    /**
     * 解析时的笔刷
     */
    private Paint cachePaint = null;

    /**
     * 缓存解析时的所有行内容
     */
    private List<LineInfo> cacheLineInfoList = null;

    /**
     * 缓存解析时的分页
     */
    private List<CachePageInfo> cachePageInfoList = null;

    /**
     * 缓存行内容
     */
    private LineInfo cacheLineInfo = null;

    /**
     * 当前解析的是哪一行
     */
    private int lineIndex = 0;

    /**
     * 解析时当前内容行宽度
     */
    private int analyzeTextWidth = 0;

    /**
     * 解析时的内容总高度
     */
    private int analyzeTextHeight = 0;

    /**
     * 当前已经解析的页数量
     */
    private int pageNum = 0;

    /**
     * 存储测量值
     */
    private float[] measuredWidth = null;

    /**
     * 左边距
     */
    private int paddingLeft = 0;

    private TextHandler textHandler;

    public TextParse() {
        cachePaint = new Paint();
        this.cachePaint.setDither(true);
        this.cachePaint.setAntiAlias(true);
        this.cachePaint.setTextSize(textSize);

        cacheLineInfoList = new ArrayList<LineInfo>();
        cachePageInfoList = new ArrayList<CachePageInfo>();

        lineHeight = (int)(this.cachePaint.getFontMetrics().bottom -
                this.cachePaint.getFontMetrics().top);
    }

    public void setTextSize(float textSize) {
        /**
         * bug:
         *  经常切换，偶尔出现个别字体显示不良、花屏
         * reason:
         *  系统会缓存字体纹理，下次显示时候，根据大小和其它属性从缓存中读取
         * fix method:
         *  让每次显示的字体大小是不一样，避免从系统缓存中读取。增加一个误差值，取(0,1)区间值
         */
        float textSizeOffset = (new Random().nextFloat()) / 10;
        this.textSize = textSize+textSizeOffset;
        if (cachePaint != null) {
            cachePaint.setTextSize(this.textSize);
            lineHeight = (int)(this.cachePaint.getFontMetrics().bottom -
                    this.cachePaint.getFontMetrics().top);
        }
    }

    private float getTextSize() {
        return textSize;
    }

    public void setViewSize(int currentViewWidth, int currentViewHeight) {
        this.currentViewWidth = currentViewWidth;
        this.currentViewHeight = currentViewHeight;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        this.analyzeTextWidth = paddingLeft;
    }

    public void setChangeLineTextSize(int changeLineIndex, int changeLineTextSize) {
        this.changeLineIndex = changeLineIndex;
        this.changeLineTextSize = changeLineTextSize+0.0f;
    }

    @Override
    public void setHandler(TextHandler textHandler) {
        this.textHandler = textHandler;
    }

    @Override
    public void getCharArray(int state, char[] charArray, int color) {
        // TODO Auto-generated method stub

        switch (state) {
            case TextObtainer.ANALY_TEXT:
                if (textHandler != null && !textHandler.isException()) {
                    breakCharArray(charArray, color);
                }
                break;

            case TextObtainer.ANALY_NEW_LINE:
                if (textHandler != null && !textHandler.isException()) {
                    addLineToCache();
                }
                break;
            case TextObtainer.ANALY_FINISH:
                if (textHandler != null && !textHandler.isException()) {
                    if (cacheLineInfo != null) {
                        addLineToCache();
                    }
                    if (cacheLineInfoList != null && cacheLineInfoList.size() > 0) {
                        CachePageInfo cachePageInfo = new CachePageInfo();
                        cachePageInfo.data = cacheLineInfoList;
                        cachePageInfo.num = pageNum;
                        cachePageInfoList.add(cachePageInfo);
                        cacheLineInfoList = null;
                        pageNum++;
                    }
                }
                while(textHandler != null && !textHandler.isException() &&
                        cachePageInfoList != null && cachePageInfoList.size() > 0){
                    int number = cachePageInfoList.size();
                    if (number > 2) {
                        number =2;
                    }
                    for (;number>0;number--){
//                            CachePageInfo ci = cachePageInfoList.get(0);
//                            sendBlockMessages(ADD_PAGE_TO_VIEW, ci, 0);
                        if (textHandler != null) {
                            if (textHandler.handleData(cachePageInfoList.get(0))) {
                                cachePageInfoList.remove(0);
                            }
                        }
                    }
                    if (textHandler != null) {
                        textHandler.haveARest();
                    }
                }
                if (textHandler == null || textHandler.isException()) {
                    onDestroy();
                }
                clear();
//                System.out.println("NewAnalyzeDictThread ...  2: "+analyzeTextHeight);
//                Log.i("Dictionary", "h: "+analyzeTextHeight);
                break;
        }

    }

    /**
     * 注意: Paint.breakText()方法在判断'f'和"I"时会出现错误
     *
     * @param charArray
     * @param start
     * @param offset
     * @param color
     */
    private int breakCharArray(char[] charArray, int start, int offset, int color) {
        if (measuredWidth == null) {
            measuredWidth = new float[1];
        }

        if (charArray != null) {
            boolean needBreak =false;
            int index = cachePaint.breakText(charArray, start, offset, currentViewWidth-analyzeTextWidth, measuredWidth);
            if (index == 0) {
                addLineToCache();
                index = cachePaint.breakText(charArray, start, offset, currentViewWidth-analyzeTextWidth, measuredWidth);
            }
            if (offset > index && index != 0) {
                int tempCharWidth = (int)cachePaint.measureText(charArray, start+index, 1);
                if (currentViewWidth > (analyzeTextWidth+measuredWidth[0]+tempCharWidth)) {
                    int tempIndex = cachePaint.breakText(charArray, start+index, charArray.length-(start+index),
                            currentViewWidth-(analyzeTextWidth+measuredWidth[0]), null);
                    index += tempIndex;
                }
            }
            if (index == 0) {
                return -1;
            }
            if (offset > index) {
                index--;
                if (CharFilterRule.filter(charArray[start + index])) {

                    //向后匹配
                    for (int i=index;i<offset;i++) {
                        if (!CharFilterRule.filter(charArray[start+i])) {
                            if (index != i){
                                needBreak = true;
                            }
                            break;
                        }
                    }
                    if (needBreak) {
                        //向前匹配
                        for(int i=index;i>0;i--) {
                            if (!CharFilterRule.filter(charArray[start + i])) {
                                index = i;
                                break;
                            }
                        }
                    }
                }
                index++;
                LineTextInfo lineTextInfo = new LineTextInfo();
                lineTextInfo.charArray = charArray;
                lineTextInfo.startIndex = start;
                lineTextInfo.startOffset = index;
                lineTextInfo.color = color;
                addTextToLine(lineTextInfo);
                addLineToCache();

//                breakCharArray(charArray, start+index, offset-index, color);
                return index;
            } else {
                LineTextInfo lineTextInfo = new LineTextInfo();
                lineTextInfo.charArray = charArray;
                lineTextInfo.startIndex = start;
                lineTextInfo.startOffset = index;
                lineTextInfo.color = color;
                addTextToLine(lineTextInfo);
                analyzeTextWidth += (int)measuredWidth[0];
            }
        }
        return -1;
    }

    public void breakCharArray(char[] charArray, int color) {
        int start = 0;
        int offset = charArray.length;
        int index = 0;
        while (index != -1 && textHandler != null && !textHandler.isException()) {
            start += index;
            offset -= index;
            index = breakCharArray(charArray, start, offset, color);
        }
    }

    private void addTextToLine(LineTextInfo lineTextInfo) {
        if (cacheLineInfo == null) {
            cacheLineInfo = new LineInfo();
            cacheLineInfo.paddingLeft = paddingLeft;
        }
        cacheLineInfo.addLineTextInfo(lineTextInfo);
    }

    private void addLineToCache() {
        if (isBlankLine(cacheLineInfo)) {
            return;
        }
        if (cacheLineInfoList == null) {
            cacheLineInfoList = new ArrayList<LineInfo>();
        }
        if (cacheLineInfo != null) {
            cacheLineInfo.y = analyzeTextHeight;
            if (lineIndex == changeLineIndex) {
                cacheLineInfo.textSize = changeLineTextSize;
                this.cachePaint.setTextSize(changeLineTextSize);
                cacheLineInfo.height = (int)(this.cachePaint.getFontMetrics().bottom -
                        this.cachePaint.getFontMetrics().top);
                analyzeTextHeight += cacheLineInfo.height;
                this.cachePaint.setTextSize(textSize);
            } else {
                cacheLineInfo.height = lineHeight;
                cacheLineInfo.textSize = textSize;
                analyzeTextHeight += lineHeight;
            }
            cacheLineInfoList.add(cacheLineInfo);
            lineIndex++;
            computePageText();
        }
        cacheLineInfo = null;
//        analyzeTextWidth = 0;
        analyzeTextWidth = paddingLeft;

    }

    private void computePageText() {
        int num = analyzeTextHeight/currentViewHeight;
        if (num > pageNum) {
            CachePageInfo cachePageInfo = new CachePageInfo();
            cachePageInfo.data = cacheLineInfoList;
            cachePageInfo.num = pageNum;

            pageNum++;
            cachePageInfoList.add(cachePageInfo);
            if (firstLoadPageNumber > 0) {
//                sendBlockMessages(ADD_PAGE_TO_VIEW, cachePageInfoList.get(0), 0);
                if (textHandler != null) {
                    if (textHandler.handleData(cachePageInfoList.get(0))) {
                        cachePageInfoList.remove(0);
                        firstLoadPageNumber--;
                    }
                }
            } else {
                if (cachePageInfoList.size() > 1) {
                    for (int i=2;i>0;i--) {
//                        CachePageInfo ci = cachePageInfoList.get(0);
//                        sendBlockMessages(ADD_PAGE_TO_VIEW, ci, 0);
                        if (textHandler != null) {
                            if (textHandler.handleData(cachePageInfoList.get(0))) {
                                cachePageInfoList.remove(0);
                            }
                        }
                    }
                }

            }
            if (textHandler != null) {
                textHandler.haveARest();
            }

            cacheLineInfoList = null;
        }
    }

    /**
     * 判断整行是否空格
     * @param lineInfo
     * @return
     */
    private boolean isBlankLine(LineInfo lineInfo) {
        if (lineInfo != null) {
            if (lineInfo.lineTextInfos != null) {
                for (LineTextInfo lineTextInfo:lineInfo.lineTextInfos) {
                    if (lineTextInfo.charArray != null) {
                        String str = new String(lineTextInfo.charArray, lineTextInfo.startIndex,
                                lineTextInfo.startOffset);
                        if (!str.trim().isEmpty()) {
                            str = null;
                            return false;
                        }
                        str = null;
                    }
                }
            }
        }
        return true;
    }

    public void clear() {
        if (cacheLineInfoList != null) {
            cacheLineInfoList.clear();
        }
        if (cachePageInfoList != null) {
            cachePageInfoList.clear();
        }
        cacheLineInfoList = null;
        cachePageInfoList = null;
        cacheLineInfo = null;
        cachePaint = null;
        measuredWidth = null;
    }

    public void onDestroy() {
        if (cacheLineInfo != null) {
            for (LineTextInfo lineTextInfo:cacheLineInfo.lineTextInfos) {
                lineTextInfo.charArray = null;
            }
            cacheLineInfo.clearArray();
        }
        if (cacheLineInfoList != null) {
            for (LineInfo lineInfo:cacheLineInfoList) {
                for (LineTextInfo lineTextInfo:lineInfo.lineTextInfos) {
                    lineTextInfo.charArray = null;
                }
                lineInfo.lineTextInfos.clear();
            }
            cacheLineInfoList.clear();
        }
        if (cachePageInfoList != null) {
            for (CachePageInfo cachePageInfo:cachePageInfoList) {
                for (LineInfo lineInfo:cachePageInfo.data) {
                    for (LineTextInfo lineTextInfo:lineInfo.lineTextInfos) {
                        lineTextInfo.charArray = null;
                    }
                    lineInfo.lineTextInfos.clear();
                }
                cachePageInfo.data.clear();
            }
            cachePageInfoList.clear();
        }
    }

    /**
     * 获取当前LineTextInfo的数组取词范围
     * @param lineSelectStart
     * @param lineSelectOffset
     * @param arrayStart
     * @param arrayOffset
     * @param currentArrayLength
     * @return
     */
    public static int[] computeRegionInTextInfo(int lineSelectStart, int lineSelectOffset, int arrayStart, int arrayOffset, int currentArrayLength) {
        int region[] = new int[2];
        int arrayRelativeStart=0;
        int arrayRelativeOffset=arrayOffset;
        if (currentArrayLength != 0) {
            arrayRelativeStart = currentArrayLength + arrayStart;
        }
        if (lineSelectStart > (arrayRelativeStart+arrayRelativeOffset) || arrayRelativeStart > (lineSelectStart+lineSelectOffset)) {
            return null;
        }
        if (arrayRelativeStart >= lineSelectStart) {
            region[0] = arrayStart;
        } else {
            region[0] = arrayStart+(lineSelectStart-arrayRelativeStart);
        }
        int lineSelectEnd = lineSelectStart+lineSelectOffset;
        int arrayRelativeEnd = arrayRelativeStart+arrayRelativeOffset;
        if (lineSelectEnd >= arrayRelativeEnd) {
            region[1] = arrayOffset;
        } else {
            region[1] = arrayOffset - (arrayRelativeEnd-lineSelectEnd);
        }
        region[1] -= (region[0]-arrayStart);
        return region;
    }

    public interface TextHandler {
        /**
         * 是否有异常
         * @return 是，返回true
         */
        public boolean isException();

        /**
         * 获取处理好的数据
         * @param cachePageInfo
         * @return
         */
        public boolean handleData(CachePageInfo cachePageInfo);

        /**
         * 休眠
         */
        public void haveARest();
    }
}
