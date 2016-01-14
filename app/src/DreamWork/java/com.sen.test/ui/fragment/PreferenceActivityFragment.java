package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sen.test.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Senny on 2015/12/1.
 */
public class PreferenceActivityFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preference_activity, null);
        /*view.findViewById(R.id.fragment_preference_activity_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomPreferenceActivity.class);
//                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, PreferenceHeadersFragment.class.getName());
                startActivity(intent);
            }
        });*/
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new ItemAdapter());
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            int currentScrollY;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    int descent = recyclerView.getHeight() / 2;
                    int childCount = recyclerView.getChildCount();
                    int dY = 0;
                    for (int i = 0;i < childCount;i++) {
                        View child = recyclerView.getChildAt(i);
                        int childY = recyclerView.getChildPosition(child);
                        if (childY == (currentScrollY + descent)) {
                            return;
                        }
                        int offsetY = childY - (currentScrollY + descent);
                        if (dY == 0 ||  Math.abs(dY) > Math.abs(offsetY)) {
                            dY = offsetY;
                        }
                    }
                    recyclerView.smoothScrollBy(0, dY);

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                currentScrollY += dy;
                int descent = recyclerView.getHeight() / 2;
                int childCount = recyclerView.getChildCount();
                for (int i = 0;i < childCount;i++) {
                    View child = recyclerView.getChildAt(i);
                    int childY = recyclerView.getChildPosition(child);
                    float ratioY = (childY - (dy + descent) + 0.0f) / descent;
                    ratioY = Math.abs(ratioY);
                    if (ratioY > 1.0f) {
                        ratioY = 1.0f;
                    }
                    ratioY = 1.0f - ratioY;
                }

            }
        });
        return view;
    }

    private class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Map<Integer, Float> localYMap;


        ItemAdapter() {
            localYMap = new HashMap<>();
        }

        public void destroy() {
            if (localYMap != null) {
                localYMap.clear();
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, null);
            ItemHolder itemHolder = new ItemHolder(view);
            return itemHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.index = position;
            itemHolder.name.setText(" " + position);
        }

        @Override
        public int getItemCount() {
            return 30;
        }

        class ItemHolder extends RecyclerView.ViewHolder {

            int index;
            TextView name;

            public ItemHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }
}
