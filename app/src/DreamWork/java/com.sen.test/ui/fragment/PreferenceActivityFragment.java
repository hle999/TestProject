package com.sen.test.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.test.R;
import com.sen.test.activity.CustomPreferenceActivity;
import com.sen.test.ui.inner.fragment.PreferenceHeadersFragment;

/**
 * Created by Senny on 2015/12/1.
 */
public class PreferenceActivityFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preference_activity, null);
        view.findViewById(R.id.fragment_preference_activity_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomPreferenceActivity.class);
//                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, PreferenceHeadersFragment.class.getName());
                startActivity(intent);
            }
        });
        return view;
    }
}
