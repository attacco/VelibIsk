package com.example.velibisk.rssreader;

import com.example.velibisk.rssreader.rss.RSSModule;
import com.example.velibisk.rssreader.ui.AboutDialogFragment;
import com.example.velibisk.rssreader.ui.FeedFragment;
import com.example.velibisk.rssreader.ui.UIModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by attacco on 23.12.2015.
 */
@Singleton
@Component(modules = {ApplicationModule.class, RSSModule.class, UIModule.class})
public interface ApplicationComponent {

    FeedLoader getFeedLoader();

    FeedFragment getFeedFragment();

    AboutDialogFragment getAboutDialogFragment();

}