package com.example.velibisk.rssreader.ui;

import com.example.velibisk.rssreader.ApplicationModule;
import com.example.velibisk.rssreader.rss.RSSModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by attacco on 23.12.2015.
 */
@Singleton
@Component(modules = {ApplicationModule.class, RSSModule.class, UIModule.class})
public interface UIComponent {

    FeedLoader getFeedLoader();

    FeedFragment getFeedFragment();

    AboutDialogFragment getAboutDialogFragment();

    void inject(FeedFragment feedFragment);

}