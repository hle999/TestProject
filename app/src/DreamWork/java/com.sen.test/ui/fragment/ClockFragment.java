package com.sen.test.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.test.R;

/**
 * Created by Senny on 2015/11/24.
 */
public class ClockFragment extends Fragment {

    private final static float DEFAULT_SCALE = 0.7f;
    private View clockContent;
    private float scaleX = DEFAULT_SCALE;
    private float scaleY = DEFAULT_SCALE;
    private int currentItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_clock_1, null);
        clockContent = root.findViewById(R.id.fragment_clock_1_content);
        if (currentItem == 0) {
            clockContent.setBackgroundColor(Color.RED);

        } else if (currentItem == 1) {
            clockContent.setBackgroundColor(Color.BLUE);

        } else if (currentItem == 2) {
            clockContent.setBackgroundColor(Color.WHITE);

        }
        scale();
        return root;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    private void scale() {
        if (clockContent != null) {
            clockContent.setScaleX(scaleX);
            clockContent.setScaleY(scaleY);
        }
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        scale();
    }

}
