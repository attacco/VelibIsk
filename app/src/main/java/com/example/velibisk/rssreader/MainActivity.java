package com.example.velibisk.rssreader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.os.OperationCanceledException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.velibisk.rssreader.rss.RSSClient;
import com.example.velibisk.rssreader.rss.RSSItem;
import com.example.velibisk.rssreader.rss.RSSItemVisitor;
import com.example.velibisk.rssreader.rss.RSSSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by attacco on 22.12.2015.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<RSSItem>> {
    private final static String PROGRESS_FRAGMENT_TAG = "progress_fragment";
    private final static String FEED_FRAGMENT_TAG = "feed_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, new ProgressFragment(), PROGRESS_FRAGMENT_TAG)
                .commit();

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<RSSItem>> onCreateLoader(int id, Bundle args) {
        return new LoaderImpl(this);
    }

    @Override
    public void onLoadFinished(Loader<List<RSSItem>> loader, final List<RSSItem> data) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FeedFragment fragment = (FeedFragment) fragmentManager.findFragmentByTag(FEED_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new FeedFragment();
            fragment.setArguments(FeedFragment.createArguments(data));
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment, FEED_FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<RSSItem>> loader) {
        FeedFragment fragment = (FeedFragment) getSupportFragmentManager().findFragmentByTag(FEED_FRAGMENT_TAG);
        if (fragment != null) {
            fragment.update(null);
        }
    }

    private static class LoaderImpl extends AsyncTaskLoader<List<RSSItem>> {
        private final static String LOGGER_TAG = "Loader";
        private final RSSClient client;
        private List<RSSItem> data;

        public LoaderImpl(Context context) {
            super(context);
            // todo use DI instead
            client = new RSSClient(getContext());
        }

        @Override
        public List<RSSItem> loadInBackground() {
            final List<RSSItem> items = new ArrayList<>();
            final RSSItemVisitor visitor = new RSSItemVisitor() {
                @Override
                public boolean visit(RSSItem item) {
                    items.add(item);
                    return true;
                }
            };

            // todo remove it
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new OperationCanceledException();
            }

            for (RSSSource source : RSSSource.values()) {
                try {
                    client.read(source, visitor);
                } catch (Exception e) {
                    Log.e(LOGGER_TAG, "Failed to complete reading source: " + source.name(), e);
                }
                if (isLoadInBackgroundCanceled()) {
                    throw new OperationCanceledException();
                }
            }

            Collections.sort(items, new Comparator<RSSItem>() {
                @Override
                public int compare(RSSItem lhs, RSSItem rhs) {
                    final long ldate = lhs.getDate().getTime();
                    final long rdate = rhs.getDate().getTime();
                    return ldate < rdate ? -1 : ldate == rdate ? 0 : 1;
                }
            });

            return items;
        }

        @Override
        public void deliverResult(List<RSSItem> data) {
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
}