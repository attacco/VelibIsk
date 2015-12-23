package com.example.velibisk.rssreader.ui;

import com.example.velibisk.rssreader.rss.RSSItemFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by attacco on 23.12.2015.
 */
@Module()
public class UIModule {

    @Provides
    @Singleton
    RSSItemFactory provideListItemFactory() {
        return new ListItemFactory();
    }

}
