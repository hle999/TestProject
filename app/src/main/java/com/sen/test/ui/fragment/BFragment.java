package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sen.test.R;
import com.sen.test.dictionary.analyze.NormalAnalyze;
import com.sen.test.dictionary.io.DictFile;
import com.sen.test.dictionary.search.DictWordSearch;
import com.sen.test.dictionary.view.ScrollTextView;

/**
 * Editor: sgc
 * Date: 2015/02/02
 */
public class BFragment extends Fragment implements View.OnClickListener{

    private ScrollTextView scrollTextView;
    private DictWordSearch dictWordSearch;
    private int wordIndex=0;
    private String keyWord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, null);
        view.findViewById(R.id.previous).setOnClickListener(this);
        view.findViewById(R.id.next).setOnClickListener(this);
        scrollTextView = (ScrollTextView)view.findViewById(R.id.text_show);
        dictWordSearch = new DictWordSearch();
        dictWordSearch.open(DictFile.ID_LWDICT);
        wordIndex = dictWordSearch.getSearchKeyIndex("go");
        keyWord = dictWordSearch.getKeyWord(wordIndex);
        byte[] exp = dictWordSearch.getExplainByte(wordIndex);
        NormalAnalyze normalAnalyze = new NormalAnalyze(exp, dictWordSearch.getExplainByteStart(exp), keyWord, false);
        scrollTextView.showText(normalAnalyze, 30, -1, -1);

        return view;
    }

    @Override
    public void onDestroyView() {
        dictWordSearch.close();
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.previous:

                 break;

            case R.id.next:

                 break;

        }

    }
}
