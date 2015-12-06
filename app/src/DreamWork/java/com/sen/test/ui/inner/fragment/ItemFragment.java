package com.sen.test.ui.inner.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sen.test.R;

/**
 * Created by Senny on 2015/12/4.
 */
public class ItemFragment extends Fragment {

    private PreferenceActivity.Header header;
    private OnItemFragmentListener listener;

    public void setHeader(PreferenceActivity.Header header) {
        this.header = header;
    }

    public void setOnItemFragmentListener(OnItemFragmentListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.preference_header_item, container, false);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView summary = (TextView) view.findViewById(R.id.summary);
        icon.setImageResource(header.iconRes);
        title.setText(header.getTitle(container.getResources()));
        CharSequence summarys = header.getSummary(container.getResources());
        if (!TextUtils.isEmpty(summarys)) {
            summary.setVisibility(View.VISIBLE);
            summary.setText(summarys);
        } else {
            summary.setVisibility(View.GONE);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(header);
                }
            }
        });
        return view;
    }

    public interface OnItemFragmentListener {
        void onClick(PreferenceActivity.Header header);
    }
}
