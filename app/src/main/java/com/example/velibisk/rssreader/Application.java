package com.example.velibisk.rssreader;

import com.example.velibisk.rssreader.rss.RSSModule;
import com.example.velibisk.rssreader.ui.DaggerUIComponent;
import com.example.velibisk.rssreader.ui.UIComponent;
import com.example.velibisk.rssreader.ui.UIModule;

/**
 * Created by attacco on 23.12.2015.
 */
public class Application extends android.app.Application {
    private UIComponent uiComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        uiComponent = DaggerUIComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .rSSModule(new RSSModule())
                .uIModule(new UIModule())
                .build();
    }

    public UIComponent getUIComponent() {
        return uiComponent;
    }
}