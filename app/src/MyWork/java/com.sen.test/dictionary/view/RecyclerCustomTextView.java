package com.sen.test.dictionary.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.sen.test.dictionary.adapter.RecyclerTextAdapter;
import com.sen.test.dictionary.file.AnalyzeCodeDictionary;
import com.sen.test.dictionary.info.CachePageInfo;
import com.sen.test.dictionary.info.LineInfo;
import com.sen.test.dictionary.info.LineTextInfo;
import com.sen.test.dictionary.parse.TextParse;

import java.util.List;

/**
 * Editor: sgc
 * Date: 2015/02/02
 */
public class RecyclerCustomTextView extends RecyclerView implements ITextContrler{

    /**
     * 加载一页的内容
     */
    private final int ADD_PAGE_TO_VIEW = 0x01;

    private RecyclerTextAdapter recyclerTextAdapter;

    private AnalyzeCodeDictionary analyzeCodeDictionary;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_PAGE_TO_VIEW:
                    CachePageInfo cachePageInfo = (CachePageInfo)msg.obj;
                    if (cachePageInfo != null) {
                        List<LineInfo> dataPage = (List<LineInfo>)cachePageInfo.data;
                        int offset = dataPage.size()-1;
                        int width = getWidth();
                        int height = dataPage.get(offset).y + dataPage.get(offset).height
                                - dataPage.get(0).y;
                        cachePageInfo.width = width;
                        cachePageInfo.height = height;
                        cachePageInfo.y = dataPage.get(0).y;
                        if (mHandler != null) {
                            recyclerTextAdapter.addData(cachePageInfo);
//                            if (20 > recyclerTextAdapter.getData().size()) {
                                recyclerTextAdapter.notifyItemInserted(recyclerTextAdapter.getData().size()-1);
//                            }
                        } else {
                            clearLineInfoList(dataPage);
                        }
                    }
                    break;
            }
        }
    };

    public RecyclerCustomTextView(Context context) {
        super(context);
        init();
    }

    public RecyclerCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerCustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (recyclerTextAdapter != null && recyclerTextAdapter.getData() != null) {
            for (CachePageInfo cachePageInfo:recyclerTextAdapter.getData()) {
                clearLineInfoList(cachePageInfo.data);
            }
        }
        super.onDetachedFromWindow();
    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(linearLayoutManager);
        recyclerTextAdapter = new RecyclerTextAdapter();
        setAdapter(recyclerTextAdapter);
    }


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
                    if (mHandler != null) {
                        sendBlockMessages(ADD_PAGE_TO_VIEW, cachePageInfo, 0);
                        return true;
                    }
                    return false;
                }

                @Override
                public void haveARest() {
                    if (RecyclerCustomTextView.this.analyzeCodeDictionary != null) {
                        RecyclerCustomTextView.this.analyzeCodeDictionary.haveARest();
                    }
                }
            });
        }

        if (this.analyzeCodeDictionary != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    RecyclerCustomTextView.this.analyzeCodeDictionary.start();
                }
            });
        }
    }

    @Override
    public void clearSelectWords() {

    }

    private void sendBlockMessages(int id, Object data, int delayMillis) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(id);
            msg.what = id;
            msg.obj = data;
            mHandler.sendMessageDelayed(msg, delayMillis);
        }
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

    @Override
    public void clearHandler() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void setSelectTextState(boolean isSelectText) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause(boolean isActivityPause) {

    }

    @Override
    public void setOnDictTextViewListener(CustomTextView.DictTextViewListener dictTextViewListener) {

    }
}
