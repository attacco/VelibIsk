package com.example.velibisk.rssreader;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by attacco on 23.12.2015.
 */
@Module
public class ApplicationModule {
    private final Application application;

    ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }
}