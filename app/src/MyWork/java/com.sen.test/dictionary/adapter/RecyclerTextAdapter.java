package com.sen.test.dictionary.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sen.test.dictionary.info.CachePageInfo;
import com.sen.test.dictionary.view.PageTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Editor: sgc
 * Date: 2014/12/29
 */
public class RecyclerTextAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CachePageInfo> data;

    public void addData(CachePageInfo item) {
        if (data == null) {
            data = new ArrayList<CachePageInfo>();
        }
        data.add(item);
    }

    public List<CachePageInfo> getData() {
        return data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        PageTextView dictPageText = new PageTextView(viewGroup.getContext());
        MyHolder myHolder = new MyHolder(dictPageText);
        Log.i("RecyclerViewText", "createHodler "+myHolder.toString());
        return myHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (data != null && data.size() > i) {
            CachePageInfo cachePageInfo = data.get(i);
            int start = 0;
            int offset = cachePageInfo.data.size()-1;

            ((MyHolder)viewHolder).textView.reset(cachePageInfo.data, start, offset,
                    cachePageInfo.width, cachePageInfo.height, cachePageInfo.y);
        }
        Log.i("RecyclerViewText", "usedHodler "+viewHolder.toString()+" "+i);
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    private class MyHolder extends RecyclerView.ViewHolder {

        public PageTextView textView;

        public MyHolder(View itemView) {
            super(itemView);
            textView = (PageTextView)itemView;
        }
    }

}
