package com.sen.test.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sen.test.R;
import com.sen.test.ui.inner.fragment.PreferenceHeadersFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senny on 2015/12/1.
 */
public class CustomPreferenceActivity extends PreferenceActivity {

    private List<Header> mHeaders;
    private int XML_ID;

    /*@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public Intent getIntent() {
        Intent superIntent = super.getIntent();
        String startingFragment = getStartingFragmentClass(superIntent);
        if (startingFragment != null && !onIsMultiPane()) {
            Intent modIntent = new Intent(superIntent);
            modIntent.putExtra(EXTRA_SHOW_FRAGMENT, startingFragment);
            Bundle args = superIntent.getExtras();
            if (args != null) {
                args = new Bundle(args);
            } else {
                args = new Bundle();
            }
            args.putParcelable("intent", superIntent);
            modIntent.putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, superIntent.getExtras());
            return modIntent;
        }

        return superIntent;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_preference_settings);
        setTitle(R.string.project_setting);
//        switchToHeader(mHeaders.get(1));
//        getListView().setDividerHeight(-400);
        /*getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int childCount = view.getChildCount();
                for (int i = 0;i < childCount;i++) {
                    View childView = view.getChildAt(i);
                    if (childView != null && childView.getTag() instanceof HeaderAdapter.HeaderViewHolder) {
                        HeaderAdapter.HeaderViewHolder holder = (HeaderAdapter.HeaderViewHolder) childView.getTag();
                        if (holder.index == firstVisibleItem) {
                            if (firstVisibleItem == 0 && (childView.getHeight() - 400) > getVerticalScrollY(view)) {
                                childView.setScaleX(5.0f);
                                childView.setScaleY(5.0f);
                            } else {
                                childView.setScaleX(1.0f);
                                childView.setScaleY(1.0f);
                            }
                            System.out.println("CustomPreference: " + getVerticalScrollY(view) + " " + childView.getTop());
                        }
                        if (holder.index == firstVisibleItem + 1) {
                            if (firstVisibleItem == 0 && (childView.getHeight() - 400) > getVerticalScrollY(view)) {
                                childView.setScaleX(1.0f);
                                childView.setScaleY(1.0f);
                            } else {
                                childView.setScaleX(5.0f);
                                childView.setScaleY(5.0f);
                            }
                        }
                        if (holder.index == firstVisibleItem + 2) {
                            childView.setScaleX(1.0f);
                            childView.setScaleY(1.0f);
                        }
                    }
                }
            }

            int getVerticalScrollY(AbsListView view) {
                View c = view.getChildAt(0);
                if (c == null) {
                    return 0;
                }
                int firstVisiblePosition = view.getFirstVisiblePosition();
                int top = c.getTop();
                return -top + firstVisiblePosition * c.getHeight() ;
            }
        });*/
        /*Header header = new Header();
        header.fragment = PreferenceHeadersFragment.class.getName();
        switchToHeader(header);*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public Intent getIntent() {
        Intent intent = super.getIntent();
        if (intent == null || intent.getStringExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT) == null) {
            intent = new Intent();
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, PreferenceHeadersFragment.class.getName());
        }
        return intent;
    }

    @Override
    public void onBuildHeaders(List<Header> headers) {
        if (!onIsHidingHeaders()) {
            loadHeadersFromResource(R.xml.settings_headers, headers);
            mHeaders = headers;
        }

    }

    @Override
    public void setListAdapter(ListAdapter adapter) {
        super.setListAdapter(new HeaderAdapter(this, mHeaders));
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
//        return super.isValidFragment(fragmentName);
        return true;
    }

    /*@Override
    public boolean onIsMultiPane() {

        return false;
    }*/

    @Override
    public void onHeaderClick(Header header, int position) {
        if (header.fragment != null) {
            Fragment fragment = Fragment.instantiate(this, header.fragment, header.extras);
            startPreferenceFragment(fragment, true);
        } else if (header.intent != null) {
            startActivity(header.intent);
        }
    }

    public List<Header> getHeaders() {
        if (mHeaders == null) {
            mHeaders = new ArrayList<Header>();
            loadHeadersFromResource(R.xml.settings_headers, mHeaders);
        }
        return mHeaders;
    }

    private String getStartingFragmentClass(Intent intent) {
        String intentClass = intent.getComponent().getClassName();
        if (intentClass.equals(getClass().getName())) return null;
        return intentClass;
    }

    private static class HeaderAdapter extends ArrayAdapter<Header> {
        private static class HeaderViewHolder {
            ImageView icon;
            TextView title;
            TextView summary;
            int index;
        }

        private LayoutInflater mInflater;

        public HeaderAdapter(Context context, List<Header> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            View view;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.preference_header_item,
                        parent, false);
                holder = new HeaderViewHolder();
                holder.icon = (ImageView) view.findViewById(R.id.icon);
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.summary = (TextView) view.findViewById(R.id.summary);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (HeaderViewHolder) view.getTag();
            }

            // All view fields must be updated every time, because the view may be recycled
            Header header = getItem(position);
            holder.icon.setImageResource(header.iconRes);
            holder.title.setText(header.getTitle(getContext().getResources()));
            CharSequence summary = header.getSummary(getContext().getResources());
            if (!TextUtils.isEmpty(summary)) {
                holder.summary.setVisibility(View.VISIBLE);
                holder.summary.setText(summary);
            } else {
                holder.summary.setVisibility(View.GONE);
            }
            holder.index = position;
            /*if (((AbsListView) parent).getFirstVisiblePosition() == position - 1) {
                view.setScaleX(5.0f);
                view.setScaleY(5.0f);
            } else {
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
            }*/
            view.setMinimumHeight(parent.getHeight());
            return view;
        }
    }
}
