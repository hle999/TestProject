package com.sen.test.dictionary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;

import com.sen.test.dictionary.file.AnalyzeCodeDictionary;
import com.sen.test.dictionary.info.CachePageInfo;
import com.sen.test.dictionary.info.LineInfo;
import com.sen.test.dictionary.info.LineTextInfo;
import com.sen.test.dictionary.parse.TextParse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CustomTextView extends ScrollView implements ITextContrler{

    /**
     * 加载一页的内容
     */
    private final int ADD_PAGE_TO_VIEW = 0x01;

    /**
     * 开始显示时，要加载的数量
     */
    private int firstLoadPageNumber = 3;

    /**
     * 是否取词
     */
    private boolean isSelectText = false;

    /**
     * 是否滚动
     */
    private boolean isScrolling = false;

    /**
     * 能否可以加载已经解析好的页内容
     */
    private boolean canAddPageToView = true;

    /**
     * 绘制加载内容的笔刷
     */
    private Paint drawTextPaint = null;

    /**
     * 已经解析完的行内容
     */
    private List<LineInfo> resultLineInfoList = null;

    /**
     * 已经解析完的页
     */
    private List<CachePageInfo> resultPageInfoList = null;

    /**
     * 滚动的停止
     */
    private OverScroller overScroller = null;

    /**
     * 加载分页的容器
     */
    private LinearLayout textLayout = null;

    /**
     * 是否正在触摸取词
     */
    private boolean isSelectTextTouch = false;

    private boolean isHasSelectText = false;

    /**
     * 上次滑动位置
     */
    private int mLastScrollTouchMoveY = 0;

    private boolean isTouchMoveShowPageText = false;

    private AnalyzeCodeDictionary analyzeCodeDictionary;

    public void loadData(AnalyzeCodeDictionary analyzeCodeDictionary, TextParse textParse) {
        this.analyzeCodeDictionary = analyzeCodeDictionary;
        if (textParse != null) {
            textParse.setHandler(new TextParse.TextHandler() {
                @Override
                public boolean isException() {
                    if (mHandler == null) {
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean handleData(CachePageInfo cachePageInfo) {
                    if (mHandler != null && canAddPageToView) {
                        sendBlockMessages(ADD_PAGE_TO_VIEW, cachePageInfo, 0);
                        return true;
                    }
                    return false;
                }

                @Override
                public void haveARest() {
                    if (CustomTextView.this.analyzeCodeDictionary != null) {
                        CustomTextView.this.analyzeCodeDictionary.haveARest();
                    }
                }
            });
        }

        if (this.analyzeCodeDictionary != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    CustomTextView.this.analyzeCodeDictionary.start();
                }
            });
        }
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustomTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dictTextViewListener != null && textLayout != null) {
            dictTextViewListener.childOnDraw(getScrollY(), getWidth(), getHeight(), textLayout.getHeight());
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (dictTextViewListener != null) {
            dictTextViewListener.onSizeChanged(this);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        canAddPageToView = !isScrolling && !isSelectTextTouch;
        isScrolling = false;
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        isScrolling = true;

    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY,
                                   int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY,
                                   boolean isTouchEvent) {
        int showNum = isTouchEvent ? 2:4;// 显示数目
        boolean isUp = 0 > deltaY;
        if (isTouchEvent) {
            if (reShowPageText(showNum, scrollY+deltaY, isUp, false)) {
                isTouchMoveShowPageText = true;
            }
        } else {
            if (reShowPageText(showNum, scrollY+deltaY, isUp, true)) {
                return true;
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        canShowLeftOfPageTextView = true;
//        if (dictTextViewListener != null) {
//            dictTextViewListener.touchedView();
//        }
        if (isSelectText) {
            touchEvent(event);
            canAddPageToView = !isScrolling && !isSelectTextTouch;
            return true;
        }
        if (isTouchMoveShowPageText) {
            isTouchMoveShowPageText = false;
            setMLastMotionY(((int)event.getY()+mLastScrollTouchMoveY)/2);
        }
        mLastScrollTouchMoveY = (int)event.getY();
        isSelectTextTouch = false;
        return super.onTouchEvent(event);
    }

    @Override
    public void onDetachedFromWindow() {
        clearHandler();
        if (analyzeCodeDictionary != null) {
            analyzeCodeDictionary.setStop();
            analyzeCodeDictionary = null;
        }
        if (resultLineInfoList != null) {
            for (LineInfo lineInfo:resultLineInfoList) {
                for (LineTextInfo lineTextInfo:lineInfo.lineTextInfos) {
                    lineTextInfo.charArray = null;
                }
                lineInfo.lineTextInfos.clear();
            }
            resultLineInfoList.clear();
        }
        if (selectTextInfoList != null) {
            for (LineInfo lineInfo:selectTextInfoList) {
                for (LineTextInfo lineTextInfo:lineInfo.lineTextInfos) {
                    lineTextInfo.charArray = null;
                }
                lineInfo.lineTextInfos.clear();
            }
            selectTextInfoList.clear();
        }
        resultLineInfoList = null;
        selectTextInfoList = null;
        resultPageInfoList = null;
        drawTextPaint = null;
        overScroller = null;
        dictTextViewListener = null;
//        if (textLayout != null) {
//            textLayout.removeAllViews();
        textLayout = null;
//        }
//        this.removeAllViews();
        super.onDetachedFromWindow();
    }

    public void onPause(boolean isActivityPause) {
        if (analyzeCodeDictionary != null) {
            analyzeCodeDictionary.onPause();
        }
        stopScrolling();
        if (isActivityPause) {
            onPausePageText();
        }
    }

    /**
     * 唤醒线程
     */
    public void onResume() {
        if (analyzeCodeDictionary != null) {
            analyzeCodeDictionary.onResume();
        }
    }

    public void stop() {
        if (analyzeCodeDictionary != null) {
            analyzeCodeDictionary.setStop();
        }
        mHandler = null;
    }

    private void init() {

        drawTextPaint = new Paint();
//        this.drawTextPaint.setDither(true);
        this.drawTextPaint.setAntiAlias(true);

        resultLineInfoList = new ArrayList<LineInfo>();
        selectTextInfoList = new ArrayList<LineInfo>();
        selectPageTextList = new ArrayList<PageTextView>();

        this.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
        textLayout = new LinearLayout(getContext());
        textLayout.setPadding(0, 0, 0, 7);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addView(textLayout, params);

        try {
            Field overScrolledFied = ScrollView.class.getDeclaredField("mScroller");
            if (overScrolledFied != null) {
                overScrolledFied.setAccessible(true);
                overScroller = (OverScroller)overScrolledFied.get(this);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 问题: 从后台返回前台时，会重新刷新数据，如果存在大量的PageTextView，耗大量时间
     * 解决: 隐藏部分PageTextView
     */
    public void onPausePageText() {
        stopScrolling();
        hidePageText(2);
//        canShowLeftOfPageTextView = false;
    }

    public void setSelectTextState(boolean isSelectText) {
        this.isSelectText = isSelectText;
        if (isSelectText) {
            stopScrolling();
        }
    }

    public boolean getSelectTextState() {
        return isSelectText;
    }

    /**
     * 停止滚动
     */
    public void stopScrolling() {
        if (overScroller != null) {
            overScroller.abortAnimation();
        }
    }

    private boolean setMLastMotionY(int value) {
        try {
            Field mLastMotionY = ScrollView.class.getDeclaredField("mLastMotionY");
            if (mLastMotionY != null) {
                mLastMotionY.setAccessible(true);
                mLastMotionY.set(this, value);
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getCanAddPageToView(boolean canAddPageToView) {
        this.canAddPageToView = canAddPageToView;
    }

    /**
     * 向上或向下相对当前屏恢复显示多少屏
     * @param num
     * @param isUp
     */
    private boolean reShowPageText(int num, int y, boolean isUp, boolean checkVisible) {
        boolean isShow = false;
        int indexPageText = -1;
        int start = -1;
        int end = -1;
        if (isUp) {
            indexPageText = getPageTexIndextByY(y);
            if (indexPageText >= 0) {
                View view = textLayout.getChildAt(indexPageText);
                if (view.getVisibility() != View.VISIBLE || !checkVisible) {
                    start = indexPageText;
                    end = indexPageText;
                    start -= num-1;
                    if (0 > start) {
                        start = 0;
                    }
                    for (int i=end;i>=start;i--) {
                        PageTextView pageTextView = (PageTextView)textLayout.getChildAt(i);
                        if (pageTextView.getVisibility() != View.VISIBLE) {
                            isShow = true;
                            pageTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        } else {
            indexPageText = getPageTexIndextByY(y+getHeight());
            if (indexPageText >= 0) {
                View view = textLayout.getChildAt(indexPageText);
                if (view.getVisibility() != View.VISIBLE || !checkVisible) {
                    start = indexPageText;
                    end = indexPageText;
                    end += num-1;
                    if (end >= textLayout.getChildCount()) {
                        end = textLayout.getChildCount()-1;
                    }
                    for (int i=start;end>=i;i++) {
                        PageTextView pageTextView = (PageTextView)textLayout.getChildAt(i);
                        if (pageTextView.getVisibility() != View.VISIBLE) {
                            isShow = true;
                            pageTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
        if (isShow) {
            stopScrolling();
        }
//        if (canShow) {
//            for (int i=start;end>=i;i++) {
//                PageTextView pageTextView = (PageTextView)textLayout.getChildAt(i);
//                if (pageTextView.getVisibility() != View.VISIBLE) {
//                    stopScrolling();
//                    pageTextView.setVisibility(View.VISIBLE);
//                }
//            }
//
//        }
        return isShow;
    }

    /**
     * 隐藏部分PageTextView
     * @param num 只显示多少个PageTextView
     */
    private void hidePageText(int num) {
        int indexPageText = getPageTexIndextByY(getScrollY());
        if (indexPageText >=0) {
            /**
             * 在onResume的时候，只重新刷新num个，其它先隐藏
             */
            int start = indexPageText;
            int end = indexPageText;

            int count = (num-1)/2;
            while (count > 0) {
                if (start > 0) {
                    start--;
                } else {
                    break;
                }
                count--;
            }
            end += num-(indexPageText-start)-1;
            if (end >= textLayout.getChildCount()) {
                end = textLayout.getChildCount()-1;
            }
            for (int i=0;i<textLayout.getChildCount();i++) {
                if (start > i || i > end) {
                    PageTextView pageTextView = (PageTextView)textLayout.getChildAt(i);
                    pageTextView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }



    public void clearHandler() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    /** -------------------------------------------------------
    * 触摸操作
    * --------------------------------------------------------
    */

    /**
     * 上次触摸X点
     */
    private int lastTouchX = 0;

    /**
     * 上次触摸Y点
     */
    private int lastTouchY = 0;

    /**
     * 上次触摸行位置
     */
    private int lastTouchLineIndex = -1;

    /**
     * 取词所选中的行
     */
    private List<LineInfo> selectTextInfoList = null;

    /**
     * 取词时所选中的分页
     */
    private List<PageTextView> selectPageTextList = null;

    public void clearSelectWords() {
        clearSelectText();
        reFlashPageTextView();
        clearSelectPageText();
    }

    private void clearSelectText() {
        if (selectTextInfoList != null) {
            for(LineInfo lineInfo:selectTextInfoList) {
                lineInfo.clearSelect();
            }
            selectTextInfoList.clear();
        }

    }

    private void clearSelectPageText() {
        if (selectPageTextList != null) {
            selectPageTextList.clear();
        }
    }

    private View getPageTextByLine(int lineIndex) {
        for (int i=0;i<this.textLayout.getChildCount();i++) {
            PageTextView dictPageText = (PageTextView)this.textLayout.getChildAt(i);
            if (lineIndex >= dictPageText.getListIndexStart() &&
                    dictPageText.getListIndexEnd() >= lineIndex) {
                return dictPageText;
            }
        }
        return null;
    }

    private View getPageTextByY(int y) {
        int index = getPageTexIndextByY(y);
        if (textLayout != null && textLayout.getChildCount() > index && index >= 0) {
            return textLayout.getChildAt(index);
        }
        return null;
    }

    private int getPageTexIndextByY(int y) {
        for (int i=0;i<this.textLayout.getChildCount();i++) {
            PageTextView dictPageText = (PageTextView)this.textLayout.getChildAt(i);
            if (y >= dictPageText.getY() &&
                    (dictPageText.getY()+dictPageText.getHeight()) >= y) {
                return i;
            }
        }
        return -1;
    }

    private void addSelectText(LineInfo lineInfo, int lineIndex) {
        if (selectTextInfoList != null) {
            selectTextInfoList.add(lineInfo);
        }
        PageTextView selectView = (PageTextView)getPageTextByLine(lineIndex);
        if (selectPageTextList != null && selectView != null && selectPageTextList.indexOf(selectView) == -1) {
            selectPageTextList.add(selectView);
        }
    }

    private void reFlashPageTextView() {
        if (selectPageTextList != null) {
            for (PageTextView pageTextView:selectPageTextList) {
                pageTextView.postInvalidate();
            }
        }
    }

    public void touchEvent(MotionEvent event) {

        /**
         * 判断点击范围
         */
        int x = (int)event.getX();
        int y = (int)event.getY();
        if ( 0 > x) {
            x = 0;
        } else if (x > getWidth()) {
            x = getWidth();
        }
        /*if (0 > y) {
            y = 0;
        } else if (y > getHeight()) {
            y = getHeight();
        }*/
        /**
         * 微调
         */
        int heightOffset=20;
        if ((heightOffset-5) > y) {
            y = (heightOffset-5);
        } else if (y > (getHeight()-heightOffset)) {
            y = getHeight()-heightOffset;
        }

        y += this.getScrollY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isSelectTextTouch = true;
                clearSelectText();
                clearSelectPageText();
                lastTouchX = x;
                lastTouchY = y;
                lastTouchLineIndex = getPreciseLine(lastTouchY);
                if (lastTouchLineIndex != -1) {
                    LineInfo lineInfo = resultLineInfoList.get(lastTouchLineIndex);
                    int[] region = getSelectWordOfLineRegion(lineInfo, lastTouchX);
                    if (region != null) {
                        lineInfo.selectStart = region[0];
                        lineInfo.selectOffset = region[1];
                        lineInfo.selectOffset -= lineInfo.selectStart;
                        addSelectText(lineInfo, lastTouchLineIndex);
                    }
                }
                reFlashPageTextView();
                break;
            case MotionEvent.ACTION_MOVE:
                isSelectTextTouch = true;
                clearSelectText();

                int tempLastLineIndex = lastTouchLineIndex;
                int tempNextLineIndex = -1;

                if (tempLastLineIndex == -1) {
                    tempLastLineIndex = getBlurLine(lastTouchY);
                }
                tempNextLineIndex = getBlurLine(y);

                if (tempNextLineIndex > tempLastLineIndex) {
                    collectSelectLines(tempLastLineIndex, tempNextLineIndex, lastTouchX, x);
                } else if (tempNextLineIndex == tempLastLineIndex) {

                    if (resultLineInfoList == null || tempLastLineIndex >= resultLineInfoList.size() || tempNextLineIndex >= resultLineInfoList.size()) {
                        return;
                    }

                    LineInfo tempLastLineInfo = resultLineInfoList.get(tempLastLineIndex);
                    LineInfo tempNextLineInfo = resultLineInfoList.get(tempNextLineIndex);

                    int[] tempLastLineRegion = getSelectWordOfLineRegion(tempLastLineInfo, lastTouchX);
                    int[] tempNextLineRegion = getSelectWordOfLineRegion(tempNextLineInfo, x);

                    if (tempLastLineRegion == null && tempNextLineRegion == null) {
                        reFlashPageTextView();
                        clearSelectPageText();
                        break;
                    }

                    if (tempLastLineRegion == null) {
                        tempLastLineRegion = getLineLastTextRegion(tempLastLineInfo);
                    }
                    if (tempNextLineRegion == null) {
                        tempNextLineRegion = getLineLastTextRegion(tempNextLineInfo);
                    }
                    tempLastLineInfo.selectStart = tempLastLineRegion[0] < tempNextLineRegion[0] ?
                            tempLastLineRegion[0]:tempNextLineRegion[0];
                    tempLastLineInfo.selectOffset = tempLastLineRegion[1] > tempNextLineRegion[1] ?
                            tempLastLineRegion[1]:tempNextLineRegion[1];
                    tempLastLineInfo.selectOffset -= tempLastLineInfo.selectStart;
                    addSelectText(tempLastLineInfo, tempLastLineIndex);
                } else {
                    collectSelectLines(tempNextLineIndex, tempLastLineIndex, x, lastTouchX);
                }
                reFlashPageTextView();
                break;
            case MotionEvent.ACTION_UP:
                String touchUpSelectText = getSelectText();
                if (dictTextViewListener != null && touchUpSelectText != null && !touchUpSelectText.trim().isEmpty()) {
                    dictTextViewListener.onSelectText(touchUpSelectText);
                } else {
                    clearSelectText();
                    reFlashPageTextView();
                    clearSelectPageText();
                }
                lastTouchLineIndex = -1;
                break;
            case MotionEvent.ACTION_OUTSIDE:
                String touchOutSideSelectText = getSelectText();
                if (dictTextViewListener != null) {
                    dictTextViewListener.onSelectText(touchOutSideSelectText);
                }
                lastTouchLineIndex = -1;

                isSelectTextTouch = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                clearSelectText();
                reFlashPageTextView();
                clearSelectPageText();
                lastTouchLineIndex = -1;

                isSelectTextTouch = false;
                break;
        }

    }

    private void collectSelectLines(int lastIndex, int nextIndex, int lastX, int nextX) {

        if (resultLineInfoList == null || lastIndex >= resultLineInfoList.size() || nextIndex >= resultLineInfoList.size()) {
            return;
        }

        LineInfo lastLineInfo = resultLineInfoList.get(lastIndex);
        LineInfo nextLineInfo = resultLineInfoList.get(nextIndex);

        int[] lastLineRegion = getSelectWordOfLineRegion(lastLineInfo, lastX);
        int[] nextLineRegion = getSelectWordOfLineRegion(nextLineInfo, nextX);

        if (lastLineRegion == null) {
            lastLineRegion = new int[2];
            lastLineRegion[0] = 0;
            lastLineRegion[1] = lastLineInfo.getLineTextArrayLength();
        }
        if (nextLineRegion == null) {
            nextLineRegion = new int[2];
            nextLineRegion[0] = 0;
            nextLineRegion[1] = nextLineInfo.getLineTextArrayLength();
        }

        lastLineInfo.selectStart = lastLineRegion[0];
        lastLineInfo.selectOffset = lastLineInfo.getLineTextArrayLength()-lastLineRegion[0];
        if (lastLineInfo.selectStart == 0) {
            lastLineInfo.selectTotalLine = true;
        }
        addSelectText(lastLineInfo, lastIndex);
        for (int i=lastIndex+1;i<nextIndex;i++) {
            LineInfo li = resultLineInfoList.get(i);
            li.selectStart = 0;
            li.selectOffset = li.getLineTextArrayLength();
            li.selectTotalLine = true;
            addSelectText(li, i);
        }
        nextLineInfo.selectStart = 0;
        nextLineInfo.selectOffset = nextLineRegion[1];
        if (nextLineInfo.selectOffset == nextLineInfo.getLineTextArrayLength()) {
            nextLineInfo.selectTotalLine = true;
        }
        addSelectText(nextLineInfo, nextIndex);
        lastLineRegion = null;
        nextLineRegion = null;
    }

    private String getSelectText() {
        if (selectTextInfoList != null && selectTextInfoList.size() > 0) {
            String selectText = null;
            for (LineInfo lineInfo:selectTextInfoList) {
                if (selectText == null) {
                    selectText = getLineSelectText(lineInfo);
                } else {
                    selectText += getLineSelectText(lineInfo);
                }
            }
            return selectText;
        }

        return null;
    }



    /*private String getLineSelectText(LineInfo lineInfo) {
        if (lineInfo != null) {
            int selectStart = lineInfo.selectStart;
            int selectOffset = lineInfo.selectOffset;
            int tempArrayLength=0;
            String selectText = null;
            for (LineTextInfo lineTextInfo:lineInfo.lineTextInfos) {
                char[] charArray = lineTextInfo.charArray;
                int start = lineTextInfo.startIndex;
                int offset = lineTextInfo.startOffset;
                int i1=0;
                int i2=0;
                if (selectStart > (tempArrayLength+offset)) {
                    tempArrayLength += offset;
                    continue;
                } else {
                    if (selectStart > tempArrayLength) {
                        i1 = start+selectStart-tempArrayLength;
                    } else {
                        i1 = start;
                    }
                    if (selectOffset > (tempArrayLength+offset)) {
                        i2 = start+offset-i1;
                    } else {
                        i2 = start+selectOffset-tempArrayLength-i1;
                    }
                    if (i2 == 0) {
                        i2 = 1;
                    }
                    if (selectText == null) {
                        selectText = new String(charArray, i1, i2);
                    } else {
                        selectText += new String(charArray, i1, i2);
                    }
                }
                tempArrayLength += offset;
                if (tempArrayLength > selectOffset) {
                    break;
                }
            }
            return selectText;
        }
        return null;
    }*/

    private String getLineSelectText(LineInfo lineInfo) {
        if (lineInfo != null) {
            String selectText = null;
            int lineSelectStart = lineInfo.selectStart;
            int lineSelectOffset = lineInfo.selectOffset;
            int currentArrayLength=0;

            for (LineTextInfo lineTextInfo:lineInfo.lineTextInfos) {
                char[] charArray = lineTextInfo.charArray;
                int start = lineTextInfo.startIndex;
                int offset = lineTextInfo.startOffset;

                int region[] = TextParse.computeRegionInTextInfo(lineSelectStart, lineSelectOffset, start, offset, currentArrayLength);

                if (region != null) {
                    if (region[1] > 0) {
//                        textSelectOffset = 1;
                        if (selectText == null) {
                            selectText = new String(charArray, region[0], region[1]);
                        } else {
                            selectText += new String(charArray, region[0], region[1]);
                        }
                    }
                }
                region = null;
                currentArrayLength += offset;
                if ((currentArrayLength-1) >= (lineSelectStart+lineSelectOffset)) {
                    break;
                }

            }
            return selectText;
        }
        return null;
    }

    /**
     * 取行末单个生单词的范围
     * @param lineInfo
     * @return
     */
    private int[] getLineLastTextRegion(LineInfo lineInfo) {
        if (lineInfo != null && lineInfo.lineTextInfos != null) {
            int[] p = new int[2];
            LineTextInfo lastLineTextInfo = lineInfo.lineTextInfos.get(lineInfo.lineTextInfos.size()-1);
            char[] charArray = lastLineTextInfo.charArray;
            int start = lastLineTextInfo.startIndex;
            int offset = lastLineTextInfo.startOffset;
            int index = lastLineTextInfo.startIndex+lastLineTextInfo.startOffset;
            int tempArrayLength = 0;
            for (LineTextInfo lx:lineInfo.lineTextInfos) {
                if (lineInfo.lineTextInfos.indexOf(lastLineTextInfo) == -1) {
                    break;
                }
                tempArrayLength += lx.startOffset;
            }
            index--;
            if (CharFilterRule.filter(charArray[index])) {
                //向前匹配
                for(int i=index;i>=start;i--) {
                    if (!CharFilterRule.filter(charArray[i])) {
                        p[0] = tempArrayLength+i-start+1;
                        break;
                    }
                    if (i == start) {
                        p[0] = tempArrayLength;
                    }
                }
                //向后匹配
                for (int i=index;(start+offset-1)>=i;i++) {
                    if (!CharFilterRule.filter(charArray[i])) {
                        p[1] = tempArrayLength+i-start;
                        break;
                    }
                    if (i == (start+offset-1)) {
                        p[1] = tempArrayLength+offset;
                    }
                }

            } else {
                p[0] = tempArrayLength+index-start;
                p[1] = tempArrayLength+index+1-start;
            }
            return p;
        }
        return null;
    }

    /**
     * 精确获取当前点所取的单个生单词范围
     * @param lineInfo
     * @param x
     * @return
     */
    private int[] getSelectWordOfLineRegion(LineInfo lineInfo, int x) {
        if (lineInfo != null && lineInfo.lineTextInfos != null) {
            int[] p = new int[2];
            drawTextPaint.setTextSize(lineInfo.textSize);
            int tempLineWidth = 0;
            int tempArrayLength = 0;
            for (LineTextInfo lineTextInfo:lineInfo.lineTextInfos) {
                char[] charArray = lineTextInfo.charArray;
                int start = lineTextInfo.startIndex;
                int offset = lineTextInfo.startOffset;
                int index = -1;
                int tempTextWidth = (int)drawTextPaint.measureText(charArray,
                        start, offset);
                if (x > (tempLineWidth+tempTextWidth)) {
                    tempLineWidth += tempTextWidth;
                    tempArrayLength += offset;
                    continue;
                } else if (x == (tempLineWidth+tempTextWidth)) {
                    index = start+offset-1;
                } else {
                    index = start+drawTextPaint.breakText(charArray, start, offset, x-tempLineWidth, null);
                }
                if (index == -1) {
                    return null;
                }
                if (CharFilterRule.filter(charArray[index])) {
                    //向前匹配
                    for(int i=index;i>=start;i--) {
                        if (!CharFilterRule.filter(charArray[i])) {
                            p[0] = tempArrayLength+i-start+1;
                            break;
                        }
                        if (i == start) {
                            p[0] = tempArrayLength;
                        }
                    }
                    //向后匹配
                    for (int i=index;(start+offset-1)>=i;i++) {
                        if (!CharFilterRule.filter(charArray[i])) {
                            p[1] = tempArrayLength+i-start;
                            break;
                        }
                        if (i == (start+offset-1)) {
                            p[1] = tempArrayLength+offset;
                        }
                    }
                } else {
                    p[0] = tempArrayLength+index-start;
                    p[1] = tempArrayLength+index+1-start;
                }
                return p;
            }
        }
        return null;
    }

    /**
     * 模糊匹配
     * @param y
     * @return 不返回-1,
     */
    private int getBlurLine(int y) {
        if (resultLineInfoList != null) {
            for (int i=0;i<resultLineInfoList.size();i++) {
                int top = resultLineInfoList.get(i).y;
                int bottom = resultLineInfoList.get(i).y + resultLineInfoList.get(i).height;
                if (bottom >= y && y >= top) {
                    return i;
                }
                if (i == resultLineInfoList.size()-1 && y > bottom) {
                    return i;
                } else if (i == 0 && top > y) {
                    return 0;
                }
            }
        }
        return 0;
    }

    /**
     * 精确匹配
     * @param y
     * @return 可返回-1
     */
    private int getPreciseLine(int y) {
        if (resultLineInfoList != null) {
            for (int i=0;i<resultLineInfoList.size();i++) {
                int top = resultLineInfoList.get(i).y;
                int bottom = resultLineInfoList.get(i).y + resultLineInfoList.get(i).height;
                if (bottom >= y && y >= top) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_PAGE_TO_VIEW:
                    CachePageInfo cachePageInfo = (CachePageInfo)msg.obj;
                    if (cachePageInfo != null) {
                        List<LineInfo> dataPage = (List<LineInfo>)cachePageInfo.data;
                        if (mHandler != null) {
                            addPageToView(dataPage);
                            /**
                             * 加载页的监听监听
                             */
                            if (dictTextViewListener != null) {
                                dictTextViewListener.onAttachedPageToWindow();
                            }
                        } else {
                            clearLineInfoList(dataPage);
                        }
                    }
                    break;
            }
        }
    };

    private void sendBlockMessages(int id, Object data, int delayMillis) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(id);
            msg.what = id;
            msg.obj = data;
            mHandler.sendMessageDelayed(msg, delayMillis);
        }
    }



    private void addPageToView(List<LineInfo> lineInfoList) {
        if (lineInfoList != null && resultLineInfoList != null) {
            int start = resultLineInfoList.size();
            int offset = lineInfoList.size()-1;
            int height = lineInfoList.get(offset).y + lineInfoList.get(offset).height - lineInfoList.get(0).y;
            resultLineInfoList.addAll(lineInfoList);
            PageTextView dictPageText = new PageTextView(getContext(), resultLineInfoList, start, offset);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(getWidth(), height);
            textLayout.addView(dictPageText, params);
            if (start == 0) {
                startAnimationByView(dictPageText, 200);
            }
        }
    }

    /**
     * 动画
     * @param v
     * @param duration
     */
    private void startAnimationByView(View v, int duration) {
        Animation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(duration);
        v.startAnimation(animation);
    }

    private void clearLineInfoList(List<LineInfo> clearLineInfoList) {
        if (clearLineInfoList != null) {
            for (LineInfo lineInfo:clearLineInfoList) {
                if (lineInfo.lineTextInfos != null) {
                    for (LineTextInfo lineTextInfo:lineInfo.lineTextInfos) {
                        lineTextInfo.charArray = null;
                    }
                    lineInfo.lineTextInfos.clear();
                    lineInfo.lineTextInfos = null;
                }
            }
            clearLineInfoList.clear();
            clearLineInfoList = null;
        }
    }

    private DictTextViewListener dictTextViewListener = null;
    public void setOnDictTextViewListener(DictTextViewListener dictTextViewListener) {
        this.dictTextViewListener = dictTextViewListener;
    }
    public interface DictTextViewListener {
        public void onAttachedPageToWindow();
        public void onSizeChanged(View parentView);
        public void onSelectText(String selectText);
        public void touchedView();
        public void childOnDraw(int scrollY, int viewWidth, int viewHeight, int height);
    }



}
