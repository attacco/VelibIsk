package com.example.velibisk.rssreader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by attacco on 23.12.2015.
 */
public class FeedFragment extends Fragment {
    private AdapterImpl adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_feed, container, false);

        final ListView listView = (ListView) v.findViewById(R.id.listView);
        adapter = new AdapterImpl(Collections.<RSSItem>emptyList());
        listView.setAdapter(adapter);

        return v;
    }

    public void update(List<RSSItem> items) {
        if (adapter != null) {
            adapter.setData(items == null ? Collections.<RSSItem>emptyList() : items);
            adapter.notifyDataSetChanged();
        }
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
            textView.setText(item.getArticle().getTitle());
            return textView;
        }
    }
}