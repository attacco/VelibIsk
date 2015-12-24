package com.example.velibisk.rssreader.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.velibisk.rssreader.Application;
import com.example.velibisk.rssreader.R;
import com.example.velibisk.rssreader.Util;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
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
    private final static String PICASSO_REQUEST_TAG = FeedFragment.class.getName();

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.listView)
    ListView listView;

    @BindColor(R.color.colorAccent)
    int accentColor;

    @Inject
    Picasso picasso;

    private Listener listener;
    private AdapterImpl adapter;

    @Inject
    public FeedFragment() {
    }

    public static Bundle createArguments(List<ListItem> items) {
        Bundle b = new Bundle();
        b.putSerializable(ARGUMENTS_ITEMS_KEY, Util.toArrayList(items));
        return b;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((Application) getActivity().getApplication()).getUIComponent().inject(this);
        if (getActivity() instanceof Listener) {
            listener = (Listener) getActivity();
        }
        adapter = new AdapterImpl();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        adapter = null;
        picasso.cancelTag(PICASSO_REQUEST_TAG);
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
            ArrayList<ListItem> items = (ArrayList<ListItem>) bundle.getSerializable(ARGUMENTS_ITEMS_KEY);
            update(items);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        picasso.pauseTag(PICASSO_REQUEST_TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        picasso.resumeTag(PICASSO_REQUEST_TAG);
    }

    public void update(List<ListItem> items) {
        swipeRefreshLayout.setRefreshing(false);
        adapter.setData(Util.toArrayList(items));
        adapter.notifyDataSetChanged();
    }

    public interface Listener {
        void onWantRefresh(FeedFragment fragment);
    }

    private class AdapterImpl extends BaseAdapter {
        private final LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        private final DateFormat dtf = DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.SHORT, getActivity().getResources().getConfiguration().locale);
        private ArrayList<ListItem> items;

        public AdapterImpl() {
            setData(Collections.<ListItem>emptyList());
            ButterKnife.bind(this, getActivity());
        }

        public void setData(List<ListItem> items) {
            this.items = items instanceof ArrayList ?
                    (ArrayList<ListItem>) items : new ArrayList<>(items);
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
        public View getView(int position, View v, ViewGroup parent) {
            if (v == null) {
                v = layoutInflater.inflate(R.layout.layout_feed_item, parent, false);
                v.setTag(new Companion(v));
            }

            final ListItem item = (ListItem) getItem(position);
            final Companion companion = (Companion) v.getTag();
            companion.renderImage(item);
            companion.renderTitle(item);
            companion.renderDescription(item);
            companion.renderInfo(item);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setExpanded(!item.isExpanded());
                    notifyDataSetChanged();
                }
            });

            return v;
        }

        private class Companion {
            private final ImageView imageView;
            private final TextView titleTextView;
            private final TextView descriptionTextView;
            private final TextView infoTextView;

            public Companion(View v) {
                imageView = ButterKnife.findById(v, R.id.imageView);
                titleTextView = ButterKnife.findById(v, R.id.titleTextView);
                descriptionTextView = ButterKnife.findById(v, R.id.descriptionTextView);
                infoTextView = ButterKnife.findById(v, R.id.infoTextView);
            }

            public void renderInfo(ListItem item) {
                infoTextView.setText(getString(R.string.list_item_source_text,
                        item.getSource().getLocalizedName(getActivity()), dtf.format(item.getDate())));
            }

            public void renderDescription(ListItem item) {
                final String description = item.isExpanded() ? item.getDescription() : null;
                final boolean expanded;
                if (description != null && !"".equals(description)) {
                    expanded = true;
                    descriptionTextView.setText(description);
                } else {
                    expanded = false;
                    descriptionTextView.setText(null); // free memory :)
                }
                descriptionTextView.setVisibility(expanded ? View.VISIBLE : View.GONE);
            }

            public void renderTitle(ListItem item) {
                titleTextView.setTypeface(item.isExpanded() ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
                titleTextView.setText(item.getTitle());
            }

            public void renderImage(ListItem item) {
                picasso.load(item.getImgUri()).tag(PICASSO_REQUEST_TAG).into(imageView);
            }
        }
    }
}