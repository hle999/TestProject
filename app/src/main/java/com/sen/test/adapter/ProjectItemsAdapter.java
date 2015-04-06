package com.sen.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.sen.test.R;

import java.util.List;

/**
 * Editor: sgc
 * Date: 2015/03/12
 */
public class ProjectItemsAdapter extends RecyclerView.Adapter {

    private List<String> data;

    private OnItemSelectLinstener onItemSelectLinstener;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public void setOnItemSelectLinstener(OnItemSelectLinstener onItemSelectLinstener) {
        this.onItemSelectLinstener = onItemSelectLinstener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View textView = LayoutInflater.from(parent.getContext()).
                                    inflate(R.layout.adatper_item, parent, false);
        TextHolder textHolder = new TextHolder(textView);
        textView.setTag(textHolder);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemSelectLinstener != null) {
                    TextHolder holder = (TextHolder)v.getTag();
                    onItemSelectLinstener.selectItem(holder.label, holder.position);
                }
            }
        });
        return textHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (data != null && data.size() > position) {
            TextHolder textHolder = (TextHolder)holder;
            textHolder.position = position;
            textHolder.label = data.get(position);
            int id = textHolder.itemView.getResources().getIdentifier(textHolder.label, "string", textHolder.itemView.getContext().getPackageName());
            if (id != 0) {
                textHolder.itemView.setText(((TextHolder)holder).itemView.getResources().getString(id));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    private class TextHolder extends RecyclerView.ViewHolder {

        public int position;
        public String label;
        public TextView itemView;

        public TextHolder(View itemView) {
            super(itemView);
            this.itemView = (TextView)itemView.findViewById(R.id.item_text);
        }
    }

    public interface OnItemSelectLinstener {
        public void selectItem(String label, int position);
    }
}
