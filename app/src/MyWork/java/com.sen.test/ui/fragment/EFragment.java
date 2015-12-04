package com.sen.test.ui.fragment;

import android.graphics.BitmapFactory;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.lib.graphics.drawable.InvertedDrawable;
import com.sen.lib.graphics.drawable.RoundCornerDrawable;
import com.sen.test.R;

/**
 * Editor: sgc
 * Date: 2015/03/13
 */
public class EFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_e, null);

        /*RoundCornerDrawable roundCornersDrawable1 = new RoundCornerDrawable(getResources(),
                BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.s), 200, 77, 77, 77, 5);
        RoundCornerDrawable roundCornersDrawable2 = new RoundCornerDrawable(getResources(),
                BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.f), 200, 77, 77, 77, 5);
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, roundCornersDrawable1);
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, roundCornersDrawable2);
        view.findViewById(R.id.efragment_img).setBackground(stateListDrawable);*/

        /*EdgeShadowDrawable edgeShadowDrawable = new EdgeShadowDrawable(getActivity().getResources(), EdgeShadowDrawable.RIGHT);
        view.findViewById(R.id.efragment_img).setBackground(edgeShadowDrawable);*/

        RoundCornerDrawable roundCornersDrawable1 = new RoundCornerDrawable(null,
                BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.s), 77, 200, 77, 77, 0);
        InvertedDrawable invertedDrawable1 = new InvertedDrawable(roundCornersDrawable1.getBitmap());
        RoundCornerDrawable roundCornersDrawable2 = new RoundCornerDrawable(null,
                BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.f), 77, 200, 77, 77, 0);
        InvertedDrawable invertedDrawable2 = new InvertedDrawable(roundCornersDrawable2.getBitmap());
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, invertedDrawable1);
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, invertedDrawable2);
        view.findViewById(R.id.efragment_img).setBackground(stateListDrawable);

        return view;
    }
}
