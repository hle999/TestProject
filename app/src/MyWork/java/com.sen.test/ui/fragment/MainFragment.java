package com.sen.test.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.test.MaterialDesignActivity;
import com.sen.test.R;
import com.sen.test.adapter.ProjectItemsAdapter;
import com.sen.test.ui.work.DictionaryDataFragment;
import com.sen.test.ui.work.MenuChooseFragment;
import com.sen.test.ui.work.LearnCloudFragment;

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

            case R.string.project_copy_parentmanager_databases:
                 BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new AFragment(), null);
                 break;

            case R.string.project_show_dictionary_data:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new BFragment(), null);
                 break;

            case R.string.project_intenet_comunication:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new CFragment(), null);
                 break;

            case R.string.project_wifi_and_gprs_connect:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new DFragment(), null);
                 break;

            case R.string.project_material_design:
                Intent intent = new Intent(getActivity(), MaterialDesignActivity.class);
                startActivity(intent);
                 break;

            case R.string.project_shadows:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new EFragment(), null);
                 break;

            case R.string.project_animator:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new FFragment(), null);
                break;

            case R.string.project_dictionary_remote_data:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new DictionaryDataFragment(), null);
                 break;

            case R.string.project_bluetooth:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new HFragment(), null);
                break;

            case R.string.project_content_providers:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new GFragment(), null);
                break;
            case R.string.project_socket:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new IFragment(), null);

                break;
            case R.string.project_http_download:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new JFragment(), null);
                break;
            case R.string.project_scrollview:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new KFragment(), null);
                break;

            case R.string.project_scroll_item:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new KFragment(), null);
                break;

            case R.string.project_video_play:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new LFragment(), null);
                break;

            case R.string.project_inject:
//                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new MFragment_(), null);

                break;

            case R.string.project_thread:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new NFragment(), null);

                break;

            case R.string.project_api_cloud:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new MenuChooseFragment(), null);
                break;

            case R.string.project_learn_cloud:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new LearnCloudFragment(), null);
                break;

            case R.string.project_ring_progress:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new RingProgressFragment(), null);
                break;

            case R.string.project_deskclock:
                BaseFragment.startAnimationFragmentWithCanBack(getActivity().getSupportFragmentManager(), new DeskClockFragment(), null);
                break;
        }

    }
}
