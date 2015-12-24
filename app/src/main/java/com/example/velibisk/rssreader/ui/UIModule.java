package com.example.velibisk.rssreader.ui;

import com.example.velibisk.rssreader.Application;
import com.example.velibisk.rssreader.rss.RSSItemFactory;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

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

    @Provides
    @Singleton
    Picasso providePicasso(Application application, OkHttpClient okHttpClient) {
        return new Picasso.Builder(application)
                .downloader(new OkHttpDownloader(okHttpClient))
                // .indicatorsEnabled(BuildConfig.DEBUG)
                .build();
    }

}
