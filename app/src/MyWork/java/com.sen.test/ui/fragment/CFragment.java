package com.sen.test.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sen.test.R;
import com.sen.test.util.UpdateLimitTimeBean;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Editor: sgc
 * Date: 2015/03/02
 */
public class CFragment extends Fragment implements View.OnClickListener {

    private RequestQueue mRequestQueue;

    private String url = "http://yunfudao.strongwind.cn/cgi-bin/parent_getlimittime.cgi";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c, null);
        view.findViewById(R.id.button_connect).setOnClickListener(this);
        mRequestQueue = Volley.newRequestQueue(container.getContext());
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_connect:
                Map<String, String> mapTree = new TreeMap<String, String>();
                mapTree.put("device_id", "8210000000016");
                mapTree.put("sign", "397a097d64ec4ede1c299ebc778e9187");
                url = consturctURL(url, mapTree);
                 mRequestQueue.add(new TestJsonRequest<UpdateLimitTimeBean>(UpdateLimitTimeBean.class, url, null));
                 break;

        }

    }

    class TestJsonRequest<T> extends Request {

        private Class<T> clazz;

        public TestJsonRequest(Class<T> clazz, String url, Response.ErrorListener listener) {
            super(Method.GET, url, listener);
            this.clazz = clazz;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            System.out.println("TestJsonRequest headers: "+super.getHeaders().toString());
            return super.getHeaders();
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            System.out.println("TestJsonRequest params: "+super.getParams());
            return super.getParams();
        }

        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            String responseString = null;
            String charset = HttpHeaderParser.parseCharset(response.headers);

            Gson gson = new Gson();

            T result;
            try {
                responseString = new String(response.data, charset);
                result = gson.fromJson(responseString, clazz);
            } catch (UnsupportedEncodingException e) {
                return Response.error(new VolleyError(e));
            }

            return Response.success(result, null);
        }

        @Override
        protected void deliverResponse(Object response) {
            UpdateLimitTimeBean updateLimitTimeBean = (UpdateLimitTimeBean)response;
            System.out.println("CFragment result: "+response);
        }

//        @Override
//        public int compareTo(Object another) {
//            return 0;
//        }
    }

    private String consturctURL(String url, Map<String, String> treeMap) {
        Iterator iterator = treeMap.entrySet().iterator();
        if (iterator.hasNext()) {
            url += "?";
            Map.Entry<String, String> entry = (Map.Entry<String, String>)iterator.next();
            url += entry.toString();
            while (iterator.hasNext()) {
                entry = (Map.Entry<String, String>)iterator.next();
                url += "&"+entry.toString();
            }
        }
        return url;
    }

//    class ResultReponse implements Response.Listener<UpdateLimitTimeBean> {
//
//        @Override
//        public void onResponse(UpdateLimitTimeBean response) {
//
//        }
//    }
//
//    class ErrorReponse implements Response.ErrorListener {
//
//        @Override
//        public void onErrorResponse(VolleyError error) {
//
//        }
//    }

}
