package com.sen.test.ui.fragment;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sen.lib.graphics.drawable.InvertedDrawable;
import com.sen.lib.graphics.drawable.RoundCornerDrawable;
import com.sen.test.R;

/**
 * Editor: sgc
 * Date: 2015/03/30
 */
public class FFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_f, null);

        RoundCornerDrawable roundCornersDrawable1 = new RoundCornerDrawable(null,
                BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.s), 17, 17, 17, 17, 0);
        InvertedDrawable invertedDrawable1 = new InvertedDrawable(roundCornersDrawable1.getBitmap());
        /*RoundCornerDrawable roundCornersDrawable2 = new RoundCornerDrawable(null,
                BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.f), 77, 200, 77, 77, 0);
        InvertedDrawable invertedDrawable2 = new InvertedDrawable(roundCornersDrawable2.getBitmap());
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, invertedDrawable1);
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, invertedDrawable2);*/
        view.findViewById(R.id.ffragment_img).setBackground(invertedDrawable1);


        final LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setAnimator(LayoutTransition.APPEARING, null);
        layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
                objectAnimator.setDuration(500);
                objectAnimator.start();
            }

            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

            }
        });


//                ((LinearLayout)view.findViewById(R.id.container)).addView(textView);
//        layoutTransition.addChild((LinearLayout)view.findViewById(R.id.container), textView);

        ((LinearLayout)view.findViewById(R.id.container)).setLayoutTransition(layoutTransition);

        view.findViewById(R.id.ffragment_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final TextView textView = new TextView(getActivity());
                textView.setText(R.string.text_label);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(50);
//                ((LinearLayout)view.findViewById(R.id.container)).startLayoutAnimation();
                ((LinearLayout)view.findViewById(R.id.container)).addView(textView);
//                layoutTransition.addChild((LinearLayout)view.findViewById(R.id.container), textView);
//                ((LinearLayout)view.findViewById(R.id.container)).startLayoutAnimation();

            }
        });



//        ((Button) view.findViewById(R.id.ffragment_img));





        return view;
    }
}
