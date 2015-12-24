package com.example.velibisk.rssreader.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.velibisk.rssreader.Application;
import com.example.velibisk.rssreader.R;

import java.util.List;

/**
 * Created by attacco on 22.12.2015.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<ListItem>>, FeedFragment.Listener {
    private final static String PROGRESS_FRAGMENT_TAG = "progress_fragment";
    private final static String FEED_FRAGMENT_TAG = "feed_fragment";
    private final static String ABOUT_DIALOG_FRAGMENT_TAG = "about_dialog_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new ProgressFragment(), PROGRESS_FRAGMENT_TAG)
                .commit();

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                showAboutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAboutDialog() {
        getUIComponent().getAboutDialogFragment().show(getSupportFragmentManager(),
                ABOUT_DIALOG_FRAGMENT_TAG);
    }

    @Override
    public Loader<List<ListItem>> onCreateLoader(int id, Bundle args) {
        return getUIComponent().getFeedLoader();
    }

    @Override
    public void onLoadFinished(Loader<List<ListItem>> loader, final List<ListItem> data) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FeedFragment fragment = (FeedFragment) fragmentManager.findFragmentByTag(FEED_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = getUIComponent().getFeedFragment();
            fragment.setArguments(FeedFragment.createArguments(data));
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment, FEED_FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        } else {
            fragment.update(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ListItem>> loader) {
        FeedFragment fragment = (FeedFragment) getSupportFragmentManager().findFragmentByTag(FEED_FRAGMENT_TAG);
        if (fragment != null) {
            fragment.update(null);
        }
    }

    @Override
    public void onWantRefresh(FeedFragment fragment) {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    private UIComponent getUIComponent() {
        return ((Application) getApplication()).getUIComponent();
    }
}