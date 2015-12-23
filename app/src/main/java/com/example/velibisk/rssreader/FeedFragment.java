package com.example.velibisk.rssreader;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.velibisk.rssreader.rss.RSSItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

/**
 * Created by attacco on 23.12.2015.
 */
public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final static String ARGUMENTS_ITEMS_KEY = "items";

    private final AdapterImpl adapter;

    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.listView) ListView listView;
    @BindColor(R.color.colorAccent) int accentColor;

    private Listener listener;

    @Inject
    public FeedFragment() {
        adapter = new AdapterImpl(Collections.<RSSItem>emptyList());
    }

    public static Bundle createArguments(List<RSSItem> items) {
        Bundle b = new Bundle();
        b.putSerializable(ARGUMENTS_ITEMS_KEY, Util.toArrayList(items));
        return b;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof Listener) {
            listener = (Listener) getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, v);

        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(accentColor);

        return v;
    }

    @Override
    public void onRefresh() {
        if (listener != null) {
            swipeRefreshLayout.setRefreshing(true);
            listener.onWantRefresh(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<RSSItem> items = (ArrayList<RSSItem>) bundle.getSerializable(ARGUMENTS_ITEMS_KEY);
            update(items);
        }
    }

    public void update(List<RSSItem> items) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setData(Util.toArrayList(items));
        adapter.notifyDataSetChanged();
    }

    public interface Listener {
        void onWantRefresh(FeedFragment fragment);
    }

    private class AdapterImpl extends BaseAdapter {
        private ArrayList<RSSItem> items;

        public AdapterImpl(List<RSSItem> items) {
            setData(items);
        }

        public void setData(List<RSSItem> items) {
            this.items = items instanceof ArrayList ?
                    (ArrayList<RSSItem>) items : new ArrayList<>(items);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(getContext());
            }

            final RSSItem item = (RSSItem) getItem(position);
            TextView textView = (TextView) convertView;
            textView.setText(item.getTitle());
            return textView;
        }
    }
}