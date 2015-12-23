package com.example.velibisk.rssreader;

import com.example.velibisk.rssreader.rss.RSSModule;

/**
 * Created by attacco on 23.12.2015.
 */
public class Application extends android.app.Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .rSSModule(new RSSModule())
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}