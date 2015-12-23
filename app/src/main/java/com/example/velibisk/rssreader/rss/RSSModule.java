package com.example.velibisk.rssreader.rss;

import com.example.velibisk.rssreader.Application;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by attacco on 23.12.2015.
 */
@Module()
public class RSSModule {
    private final static long CACHE_DIR_SIZE = 1024 * 1024;

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application application) {
        final OkHttpClient http = new OkHttpClient();
        http.setCache(new Cache(new File(application.getCacheDir(), "http"), CACHE_DIR_SIZE));
        http.setConnectTimeout(3, TimeUnit.SECONDS);
        http.setReadTimeout(3, TimeUnit.SECONDS);
        return http;
    }
}