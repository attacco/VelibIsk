package com.example.velibisk.rssreader;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.example.velibisk.rssreader.rss.RSSItem;

import java.util.List;

/**
 * Created by attacco on 22.12.2015.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<RSSItem>>, FeedFragment.Listener {
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
        return ((Application)getApplication()).getApplicationComponent().getFeedLoader();
    }

    @Override
    public void onLoadFinished(Loader<List<RSSItem>> loader, final List<RSSItem> data) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FeedFragment fragment = (FeedFragment) fragmentManager.findFragmentByTag(FEED_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = ((Application)getApplication()).getApplicationComponent().getFeedFragment();
            fragment.setArguments(FeedFragment.createArguments(data));
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment, FEED_FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        } else {
            fragment.update(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<RSSItem>> loader) {
        FeedFragment fragment = (FeedFragment) getSupportFragmentManager().findFragmentByTag(FEED_FRAGMENT_TAG);
        if (fragment != null) {
            fragment.update(null);
        }
    }

    @Override
    public void onWantRefresh(FeedFragment fragment) {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

}