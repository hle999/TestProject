package com.sen.test.ui.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sen.test.R;
import com.sen.test.provider.TestTable;
import com.sen.test.ui.fragment.BaseFragment;

/**
 * Editor: sgc
 * Date: 2015/04/06
 */
public class GFragment extends BaseFragment {

    TextView resultView;
    Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        uri = Uri.parse("content://"+getString(R.string.provider_authority)+"/"+TestTable.TABLE_NAME);
        View view = inflater.inflate(R.layout.fragment_g, null);
        resultView = (TextView)view.findViewById(R.id.textview_result);
        view.findViewById(R.id.button_qurrey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projection[] = /*new String[]{UserDictionary.Words.APP_ID, UserDictionary.Words.WORD}*/new String[]{TestTable.NAME, TestTable.MESSAGE};
                Cursor cursor = getActivity().getContentResolver().query(uri,
                                                                        projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(0);
                    String msg = cursor.getString(1);
                    resultView.setText("GFragment: " + name + " " + msg);
                } else {
//                    resultView.setText("error!");
                    ContentValues contentValues = new ContentValues();
                    /*contentValues.put(UserDictionary.Words.APP_ID, 102);
                    contentValues.put(UserDictionary.Words.WORD, "hello my ContentProvider!");*/
                    contentValues.put(TestTable.NAME, "John");
                    contentValues.put(TestTable.MESSAGE, "like to meet you!");
                    getActivity().getContentResolver().insert(uri, contentValues);
                }
            }
        });

        return view;
    }
}
