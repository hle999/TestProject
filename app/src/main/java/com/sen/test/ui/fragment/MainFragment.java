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
        View view = inflater.inflate(R.layout.fragment_main, null);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.project_items_list);
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
        }

    }
}
