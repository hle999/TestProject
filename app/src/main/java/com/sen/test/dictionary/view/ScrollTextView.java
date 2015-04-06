package com.sen.test.dictionary.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sen.test.dictionary.analyze.ImlAnalyze;
import com.sen.test.dictionary.analyze.NormalAnalyze;
import com.sen.test.dictionary.file.AnalyzeCodeDictionary;
import com.sen.test.dictionary.parse.TextParse;


public class ScrollTextView extends LinearLayout {

    /**
     * 刷新窗口
     */
    private final int REFLASH_VIEW = 0x01;
    private Context mContext = null;

    /**
     * 当前显示宽度
     */
    private int currentViewWidth = 0;

    /**
     * 当前显示高度
     */
    private int currentViewHeight = 0;

    /**
     * 改变字体大小的行位置
     */
    private int changeTextSizeLine = -1;

    /**
     * 改变字体大小
     */
    private int changeLineTextSize = 0;

    /**
     * 是否取词
     */
    private boolean isSelectText = false;

    private int textPaddingRight = 0;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFLASH_VIEW:
                    if (msg.obj != null && msg.obj instanceof DataInfo){
                        showTextView(((DataInfo)msg.obj).data, ((DataInfo)msg.obj).textSize,
                                ((DataInfo)msg.obj).start, ((DataInfo)msg.obj).keyWord, ((DataInfo)msg.obj).isAddKeyWord);
                    }
                    break;

            }
        }
    };

    public ScrollTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        if (mContext == null) {
            init(context);
        }
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        if (mContext == null) {
            init(context);
        }
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    private void init(Context mContext) {
        this.mContext = mContext;
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setSelectTextState(boolean isSelectText) {
        this.isSelectText = isSelectText;
        /**
         *  注意: 快速点击可能产生多个子view,取最近添加的一个
         */
        if (getChildCount() > 0) {
            ITextContrler iTextContrler = (ITextContrler)getChildAt(getChildCount() - 1);
            if (iTextContrler != null) {
                iTextContrler.setSelectTextState(this.isSelectText);
            }
        }
    }

    public boolean getSelectTextState() {
        return isSelectText;
    }

    public void showText(byte[] byteData, int textSize, int startIndex, String keyWord, boolean isAddKeyWord) {
        final DataInfo dataInfo = new DataInfo();
        dataInfo.data = byteData;
        dataInfo.textSize = textSize;
        dataInfo.start = startIndex;
        dataInfo.keyWord = keyWord;
        dataInfo.isAddKeyWord = isAddKeyWord;
        /**
         * View有可能正在初始化，长宽都为0，放入消息队列，等初始完再执行
         */
        if (currentViewWidth == 0 || currentViewHeight == 0) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    sendMessages(REFLASH_VIEW, dataInfo, 0);
                }
            });
        } else {
            sendMessages(REFLASH_VIEW, dataInfo, 0);
        }
    }

    public void setChangeLineTextSize(int changeTextSizeLine, int changeLineTextSize) {
        this.changeTextSizeLine = changeTextSizeLine;
        this.changeLineTextSize = changeLineTextSize;
    }

    public void setTextPaddingRight(int textPaddingRight) {
        this.textPaddingRight = textPaddingRight;
    }

    public void showText(final ImlAnalyze<TextObtainer> imlAnalyze, final int textSize, int changeTextSizeLine, int changeLineTextSize) {
        this.changeTextSizeLine = changeTextSizeLine;
        this.changeLineTextSize = changeLineTextSize;
        /**
         * View有可能正在初始化，长宽都为0，放入消息队列，等初始完再执行
         */
        if (currentViewWidth == 0 || currentViewHeight == 0) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    showText(imlAnalyze, textSize);
                }
            });
        } else {
            showText(imlAnalyze, textSize);
        }
    }

    /*private void showText(ImlAnalyze<TextObtainer> imlAnalyze, int textSize) {
        stopAllViewAnalyzeThread();
        RecyclerCustomTextView dictTextView = new RecyclerCustomTextView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addView(dictTextView, params);

        TextParse textParse = new TextParse();
        textParse.setTextSize(textSize+0.0f);
        textParse.setViewSize(currentViewWidth-textPaddingRight, currentViewHeight);
        textParse.setChangeLineTextSize(changeTextSizeLine, changeLineTextSize);

        imlAnalyze.setObtainer(textParse);

        AnalyzeCodeDictionary newAnalyzeDictThread = new AnalyzeCodeDictionary(imlAnalyze);
        dictTextView.loadData(newAnalyzeDictThread, textParse);
    }*/

    private void showText(ImlAnalyze<TextObtainer> imlAnalyze, int textSize) {
        stopAllViewAnalyzeThread();
        View dictTextView = new CustomTextView(getContext());
        ((ITextContrler)dictTextView).setOnDictTextViewListener(new CustomTextView.DictTextViewListener() {

            @Override
            public void onAttachedPageToWindow() {
                /**
                 * 只能含有唯一个子view，把多余的都清除
                 * 注意: 快速点击可能产生多个子view,所以要把最近之前的View都清除
                 */
                while ((getChildCount() - 1) > 0) {
                    ITextContrler cv = (ITextContrler) getChildAt(0);
                    ScrollTextView.this.removeViewAt(0);
                    cv.setOnDictTextViewListener(null);
                    cv.onResume();
                    cv.stop();
//                    cView.removeAllViews();
                }

            }

            @Override
            public void onSizeChanged(View parentView) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onSelectText(String selectText) {
                // TODO Auto-generated method stub
                if (onScrollTextViewListener != null) {
                    onScrollTextViewListener.getSelectText(selectText);
                }
            }

            @Override
            public void touchedView() {
                if (onScrollTextViewListener != null) {
                    onScrollTextViewListener.touchedView();
                }
            }

            @Override
            public void childOnDraw(int scrollY, int viewWidth, int viewHeight, int height) {
                if (onScrollTextViewChildDrawListener != null) {
                    onScrollTextViewChildDrawListener.childOnDraw(scrollY, viewWidth, viewHeight, height);
                }
            }

        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addView(dictTextView, params);
        ((ITextContrler)dictTextView).setSelectTextState(isSelectText);

        TextParse textParse = new TextParse();
        textParse.setTextSize(textSize+0.0f);
        textParse.setViewSize(currentViewWidth-textPaddingRight, currentViewHeight);
        textParse.setChangeLineTextSize(changeTextSizeLine, changeLineTextSize);

        imlAnalyze.setObtainer(textParse);

        AnalyzeCodeDictionary newAnalyzeDictThread = new AnalyzeCodeDictionary(imlAnalyze);
        ((ITextContrler)dictTextView).loadData(newAnalyzeDictThread, textParse);
    }

    private void showTextView(byte[] byteData, int textSize, int startIndex, String keyWord, boolean isAddKeyWord) {
        stopAllViewAnalyzeThread();
        View dictTextView = new CustomTextView(getContext());
        ((ITextContrler)dictTextView).setOnDictTextViewListener(new CustomTextView.DictTextViewListener() {

            @Override
            public void onAttachedPageToWindow() {
                /**
                 * 只能含有唯一个子view，把多余的都清除
                 * 注意: 快速点击可能产生多个子view,所以要把最近之前的View都清除
                 */
                while ((getChildCount() - 1) > 0) {
                    ITextContrler cv = (ITextContrler) getChildAt(0);
                    ScrollTextView.this.removeViewAt(0);
                    cv.setOnDictTextViewListener(null);
                    cv.onResume();
                    cv.stop();
//                    cView.removeAllViews();
                }

            }

            @Override
            public void onSizeChanged(View parentView) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onSelectText(String selectText) {
                // TODO Auto-generated method stub
                if (onScrollTextViewListener != null) {
                    onScrollTextViewListener.getSelectText(selectText);
                }
            }

            @Override
            public void touchedView() {
                if (onScrollTextViewListener != null) {
                    onScrollTextViewListener.touchedView();
                }
            }

            @Override
            public void childOnDraw(int scrollY, int viewWidth, int viewHeight, int height) {
                if (onScrollTextViewChildDrawListener != null) {
                    onScrollTextViewChildDrawListener.childOnDraw(scrollY, viewWidth, viewHeight, height);
                }
            }

        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addView(dictTextView, params);
        ((ITextContrler)dictTextView).setSelectTextState(isSelectText);

        TextParse textParse = new TextParse();
        textParse.setTextSize(textSize+0.0f);
        textParse.setViewSize(currentViewWidth-textPaddingRight, currentViewHeight);
        textParse.setChangeLineTextSize(changeTextSizeLine, changeLineTextSize);

        ImlAnalyze<TextObtainer> normalAnalyze = new NormalAnalyze(byteData, startIndex, keyWord, isAddKeyWord);
        normalAnalyze.setObtainer(textParse);

        AnalyzeCodeDictionary newAnalyzeDictThread = new AnalyzeCodeDictionary(normalAnalyze);
        ((ITextContrler)dictTextView).loadData(newAnalyzeDictThread, textParse);
    }

    private void sendMessages(int what, Object obj, int delayMillis) {
        if (mHandler != null) {
            mHandler.removeMessages(what);
            Message msg = Message.obtain();
            msg.what = what;
            msg.obj = obj;
            mHandler.sendMessageDelayed(msg, delayMillis);
        }
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        currentViewWidth = r-l;
        currentViewHeight = b-t;
        if ((currentViewWidth-getPaddingLeft()-getPaddingRight()) > 0) {
            currentViewWidth = currentViewWidth-getPaddingLeft()-getPaddingRight();
        }
        if ((currentViewHeight-getPaddingTop()-getPaddingBottom()) > 0) {
            currentViewHeight = currentViewHeight-getPaddingTop()-getPaddingBottom();
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        stopAllViewAnalyzeThread();

        onScrollTextViewListener = null;
        onScrollTextViewChildDrawListener = null;

        super.onDetachedFromWindow();
//        this.removeAllViews();
    }

    public void onPause(boolean isActivityOnPause) {
        if (getChildCount() > 0) {
            ITextContrler iTextContrler = (ITextContrler)getChildAt(getChildCount() - 1);
            iTextContrler.onPause(isActivityOnPause);
        }
    }

    public void onResume() {
        for (int i=0;i < getChildCount();i++) {
            ITextContrler iTextContrler = (ITextContrler)getChildAt(i);
            iTextContrler.onResume();
        }
    }

    private void stopAllViewAnalyzeThread() {
        for(int i=0;i<getChildCount();i++) {
            ITextContrler iTextContrler = (ITextContrler)getChildAt(i);
            iTextContrler.clearHandler();
            iTextContrler.stop();
            iTextContrler.setSelectTextState(false);
            iTextContrler.onResume();
        }
    }

    public void clearSelectWords() {
        if (getChildCount() > 0) {
            ITextContrler iTextContrler = (ITextContrler)this.getChildAt(this.getChildCount() - 1);
            iTextContrler.clearSelectWords();
        }
    }

    public void clear() {
        stopAllViewAnalyzeThread();
        this.removeAllViews();
    }

    class DataInfo {
        byte[] data;
        int start;
        int end;
        int textSize;
        String keyWord;
        boolean isAddKeyWord;
    }

    private OnScrollTextViewListener onScrollTextViewListener = null;

    private OnScrollTextViewChildDrawListener onScrollTextViewChildDrawListener = null;

    public void setOnScrollTextViewListener(OnScrollTextViewListener onScrollTextViewListener) {
        this.onScrollTextViewListener = onScrollTextViewListener;
    }

    public void setOnScrollTextViewChildDrawListener(OnScrollTextViewChildDrawListener onScrollTextViewChildDrawListener) {
        this.onScrollTextViewChildDrawListener = onScrollTextViewChildDrawListener;
    }

    public interface OnScrollTextViewListener {
        public void getSelectText(String selectStr);
        public void touchedView();
    }

    public interface OnScrollTextViewChildDrawListener {
        public void childOnDraw(int scrollY, int viewWidth, int viewHeight, int height);
    }
}
