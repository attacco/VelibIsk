package com.example.velibisk.rssreader.rss;

import android.content.Context;

import com.example.velibisk.rssreader.R;

/**
 * Created by attacco on 23.12.2015.
 */
public enum RSSSource {
    LENTA ("http://lenta.ru/rss") {
        @Override
        public String getLocalizedName(Context context) {
            return context.getString(R.string.rss_source_lenta_name);
        }
    },

    GAZETA ("http://www.gazeta.ru/export/rss/lenta.xml") {
        @Override
        public String getLocalizedName(Context context) {
            return context.getString(R.string.rss_source_gazeta_name);
        }
    };

    private final String uri;

    RSSSource(String uri) {
        this.uri = uri;
    }

    public abstract String getLocalizedName(Context context);

    public String getUri() {
        return uri;
    }
}