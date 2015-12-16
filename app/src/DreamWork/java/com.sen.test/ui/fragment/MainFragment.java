package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.test.R;
import com.sen.test.adapter.ProjectItemsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Editor: sgc
 * Date: 2015/03/12
 */
public class MainFragment extends Fragment implements ProjectItemsAdapter.OnItemSelectLinstener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return init(inflater);
    }

    private View init(LayoutInflater inflater) {
        final View view = inflater.inflate(R.layout.fragment_main, null);
        final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.project_items_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ProjectItemsAdapter projectItemsAdapter = new ProjectItemsAdapter();
        recyclerView.setAdapter(projectItemsAdapter);
        projectItemsAdapter.setOnItemSelectLinstener(this);
        List<String> data = new ArrayList<>();
        String projectItems[] = getActivity().getResources().getStringArray(R.array.project_items);
        for (String item:projectItems) {
            data.add(item);
        }
        projectItemsAdapter.setData(data);
        projectItemsAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void selectItem(String label, int position) {
        int id = getActivity().getResources().getIdentifier(label, "string", getActivity().getPackageName());
        selectItem(id);
    }

    public void selectItem(int id) {

        switch (id) {

            case R.string.project_ring_progress:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new RingProgressFragment(), null);
                break;

            case R.string.project_deskclock:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new DeskClockFragment(), null);
                break;

            case R.string.project_setting:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new SettingFragment(), null);
                break;

            case R.string.project_rippe_button:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new WavePatternFragment(), null);
                break;

            case R.string.project_g_sensor:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new GSensorFragment(), null);
                break;

            case R.string.project_preference_activity:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new PreferenceActivityFragment(), null);
                break;

            case R.string.project_fling:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new FlingFragment(), null);

                break;

            case R.string.project_setting_roll:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new SettingRollFragment(), null);
                break;
        }

    }
}
