package com.example.velibisk.rssreader.ui;

import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.os.OperationCanceledException;
import android.util.Log;
import android.widget.Toast;

import com.example.velibisk.rssreader.Application;
import com.example.velibisk.rssreader.R;
import com.example.velibisk.rssreader.rss.RSSClient;
import com.example.velibisk.rssreader.rss.RSSItemVisitor;
import com.example.velibisk.rssreader.rss.RSSSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by attacco on 23.12.2015.
 */
public class FeedLoader extends AsyncTaskLoader<List<ListItem>> {
    private final static String LOGGER_TAG = "Loader";
    private final RSSClient client;
    private final Handler handler;
    private List<ListItem> data;

    @Inject
    public FeedLoader(Application application, RSSClient client) {
        super(application);
        handler = new Handler();
        this.client = client;
    }

    @Override
    public List<ListItem> loadInBackground() {
        final List<ListItem> items = new ArrayList<>();
        final RSSItemVisitor<ListItem> visitor = new RSSItemVisitor<ListItem>() {
            @Override
            public boolean visit(ListItem item) {
                items.add(item);
                return true;
            }
        };

        // left for demonstration of initial progress fragment
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new OperationCanceledException();
        }
        if (isLoadInBackgroundCanceled()) {
            throw new OperationCanceledException();
        }

        final List<RSSSource> failedSources = new ArrayList<>();
        for (RSSSource source : RSSSource.values()) {
            try {
                client.read(source, visitor);
            } catch (Exception e) {
                Log.e(LOGGER_TAG, "Failed to complete reading source: " + source.name(), e);
                failedSources.add(source);
            }
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isStarted()) {
                    String toastText = null;
                    if (failedSources.size() == 1) {
                        toastText = getContext().getString(R.string.activity_main_failed_read_source_toast,
                                failedSources.get(0).getLocalizedName(getContext()));
                    } else if (failedSources.size() > 1) {
                        toastText = getContext().getString(R.string.activity_main_failed_read_several_sources_toast);
                    }
                    if (toastText != null) {
                        Toast.makeText(getContext(), toastText, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        Collections.sort(items, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem lhs, ListItem rhs) {
                final long ldate = lhs.getDate().getTime();
                final long rdate = rhs.getDate().getTime();
                return ldate < rdate ? -1 : ldate == rdate ? 0 : 1;
            }
        });

        return items;
    }

    @Override
    public void deliverResult(List<ListItem> data) {
        this.data = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (data != null) {
            deliverResult(data);
        }
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        data = null;
    }
}
