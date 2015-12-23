package com.example.velibisk.rssreader;

import com.example.velibisk.rssreader.rss.RSSModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by attacco on 23.12.2015.
 */
@Singleton
@Component(modules = {ApplicationModule.class, RSSModule.class})
public interface ApplicationComponent {

    FeedLoader getFeedLoader();

    FeedFragment getFeedFragment();


}


