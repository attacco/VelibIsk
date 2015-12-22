package com.example.velibisk.rssreader;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;

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
    private final static String LENTA_RU_RSS_FEED_URL = "http://lenta.ru/rss";
    private final static String GAZETA_RU_RSS_FEED_URL = "http://www.gazeta.ru/export/rss/lenta.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootContainer, new ProgressFragment(), PROGRESS_FRAGMENT_TAG)
                .commit();

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<RSSItem>> onCreateLoader(int id, Bundle args) {
        return new LoaderImpl(this);
    }

    @Override
    public void onLoadFinished(Loader<List<RSSItem>> loader, List<RSSItem> data) {
        FeedFragment fragment = (FeedFragment) getSupportFragmentManager().findFragmentByTag(FEED_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new FeedFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rootContainer, fragment, FEED_FRAGMENT_TAG)
                    .commit();
        }
        fragment.update(data);
    }

    @Override
    public void onLoaderReset(Loader<List<RSSItem>> loader) {
        FeedFragment fragment = (FeedFragment) getSupportFragmentManager().findFragmentByTag(FEED_FRAGMENT_TAG);
        if (fragment != null) {
            fragment.update(null);
        }
    }

    private static class LoaderImpl extends AsyncTaskLoader<List<RSSItem>> {
        private final PkRSS pkRSS;
        private List<RSSItem> data;

        public LoaderImpl(Context context) {
            super(context);
            this.pkRSS = new PkRSS.Builder(getContext()).handler(null).loggingEnabled(true).build();
        }

        @Override
        public List<RSSItem> loadInBackground() {
            // todo skip cache only if user manually refreshes

            final List<Article> articles = new ArrayList<>();

            final Callback emptyCallback = new Callback() {
                @Override
                public void onPreload() {
                }

                @Override
                public void onLoaded(List<Article> newArticles) {
                }

                @Override
                public void onLoadFailed() {
                }
            };
            try {
                articles.addAll(pkRSS.load(LENTA_RU_RSS_FEED_URL).callback(emptyCallback).get());
            } catch (Exception e) {
                // todo show FAILURE view
                throw new RuntimeException(e);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && isLoadInBackgroundCanceled()) {
                throw new android.os.OperationCanceledException();
            }

            try {
                articles.addAll(pkRSS.load(GAZETA_RU_RSS_FEED_URL).callback(emptyCallback).get());
            } catch (Exception e) {
                // todo show FAILURE view
                throw new RuntimeException(e);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && isLoadInBackgroundCanceled()) {
                throw new android.os.OperationCanceledException();
            }

            ArrayList<RSSItem> result = new ArrayList<>(articles.size());
            for (Article a : articles) {
                result.add(new RSSItem(a));
            }

            Collections.sort(result, new Comparator<RSSItem>() {
                @Override
                public int compare(RSSItem lhs, RSSItem rhs) {
                    final long ldate = lhs.getArticle().getDate();
                    final long rdate = rhs.getArticle().getDate();
                    return ldate < rdate ? -1 : ldate == rdate ? 0 : 1;
                }
            });

            return result;
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
            pkRSS.clearData();
        }
    }
}