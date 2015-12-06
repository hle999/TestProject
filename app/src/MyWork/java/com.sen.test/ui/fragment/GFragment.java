package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sen.test.R;
import com.sen.test.provider.TestTable1;
import com.sen.test.provider.TestTable2;

/**
 * Editor: sgc
 * Date: 2015/04/06
 */
public class GFragment extends BaseFragment {

    TextView resultView;
//    Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        uri = Uri.parse("content://"+getString(R.string.provider_authority)+"/"+ TestTable1.TABLE_NAME);
        View view = inflater.inflate(R.layout.fragment_g, null);
        resultView = (TextView)view.findViewById(R.id.textview_result);
        view.findViewById(R.id.button_qurrey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Uri uri = Uri.parse("content://"+getString(R.string.provider_authority)+"/"+ TestTable1.TABLE_NAME);
                String projection[] = new String[]{TestTable2.NAME, TestTable2.MESSAGE};
                Cursor cursor = getActivity().getContentResolver().query(uri,
                                                                        projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(0);
                    String msg = cursor.getString(1);
                    resultView.setText("GFragment: " + name + " " + msg);
                } else {
//                    resultView.setText("error!");
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TestTable2.NAME, "John");
                    contentValues.put(TestTable2.MESSAGE, "like to meet you!");
                    getActivity().getContentResolver().insert(uri, contentValues);
                }*/
                new ProviderThread(ProviderThread.MSG_1).start();
                new ProviderThread(ProviderThread.MSG_2).start();
            }
        });

        return view;
    }

    private void querry1(int a, int b, int c) {
        /*Uri uri = Uri.parse("content://" + getString(R.string.provider_authority) + "/" + TestTable1.TABLE_NAME);
        String projection[] = new String[]{TestTable1.NAME, TestTable1.MESSAGE};
        ContentResolver cr = getActivity().getContentResolver();
        for (int i=0;i<100;i++) {
            Cursor cursor = cr.query(uri,
                    projection, null, null, null);
        }*/
        for (int i=0;i<100;i++) {
            System.out.println("Querry1: "+a+" "+b+" "+c);
        }
    }

    private void querry2(int a, int b, int c) {
        /*Uri uri = Uri.parse("content://"+getString(R.string.provider_authority)+"/"+ TestTable2.TABLE_NAME);
        String projection[] = new String[]{TestTable2.NAME, TestTable2.MESSAGE};
        ContentResolver cr = getActivity().getContentResolver();
        for (int i=0;i<100;i++) {
            Cursor cursor = cr.query(uri,
                    projection, null, null, null);
        }*/
        for (int i=0;i<100;i++) {
            System.out.println("Querry2: "+a+" "+b+" "+c);
        }
    }

    private void input(int a, int b, int c) {
        System.out.println("Querry...: "+a+" "+b+" "+c);
    }

    private class ProviderThread extends Thread {

        static final int MSG_1 = 1;
        static final int MSG_2 = 2;

        int type = MSG_1;

        ProviderThread(int type) {
            this.type = type;
        }

        @Override
        public void run() {

            for (int i=0;i<100;i++) {

                switch (type) {
                    case MSG_1:
                        input(1, 2, 3);
                        break;
                    case MSG_2:
                        input(4,5,6);
                        break;
                }
            }

        }
    }
}
