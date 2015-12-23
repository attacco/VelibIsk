package com.example.velibisk.rssreader.rss;

import android.content.Context;
import android.util.Xml;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.Util;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by attacco on 23.12.2015.
 */
public class RSSClient {
    private final static long CACHE_DIR_SIZE = 1024 * 1024;
    private final OkHttpClient http;

    public RSSClient(Context context) {
        http = new OkHttpClient();
        http.setCache(new Cache(new File(context.getCacheDir(), "http"), CACHE_DIR_SIZE));
        http.setConnectTimeout(3, TimeUnit.SECONDS);
        http.setReadTimeout(3, TimeUnit.SECONDS);
    }

    public void read(RSSSource source, RSSItemVisitor visitor) throws Exception {
        final Request request = new Request.Builder()
                .get()
                .url(source.getUri())
                .build();
        final Response response = http.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException(String.format("Unexpected response: %d %s", response.code(), response.message()));
        }

        final InputStream in = response.body().byteStream();
        try {
            final XmlPullParser p = Xml.newPullParser();
            p.setInput(in, response.body().contentType().charset(Charset.forName("utf-8")).name());
            parse(source, p, visitor);
        } finally {
            Util.closeQuietly(in);
        }
    }

    private void parse(RSSSource source, XmlPullParser p, RSSItemVisitor visitor) throws Exception {
        final DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

        RSSItem item = null;
        String text = null;
        while (p.getEventType() != XmlPullParser.END_DOCUMENT) {
            if (p.getEventType() == XmlPullParser.START_TAG) {
                if ("item".equals(p.getName())) {
                    item = new RSSItem(source);
                } else if ("title".equals(p.getName())) {
                    text = null;
                } else if ("enclosure".equals(p.getName())) {
                    text = p.getAttributeValue(null, "url");
                } else if ("description".equals(p.getName())) {
                    text = null;
                } else if ("pubDate".equals(p.getName())) {
                    text = null;
                }
            } else if (p.getEventType() == XmlPullParser.TEXT) {
                text = p.getText();
            } else if (p.getEventType() == XmlPullParser.END_TAG) {
                if ("item".equals(p.getName())) {
                    if (item != null) {
                        if (!visitor.visit(item)) {
                            break;
                        }
                        item = null;
                    }
                } else if ("title".equals(p.getName())) {
                    if (item != null) {
                        item.title = text;
                    }
                } else if ("enclosure".equals(p.getName())) {
                    if (item != null) {
                        item.imgUri = text;
                    }
                } else if ("description".equals(p.getName())) {
                    if (item != null) {
                        item.description = text;
                    }
                } else if ("pubDate".equals(p.getName())) {
                    if (item != null) {
                        item.date = formatter.parse(text);
                    }
                }
            }
            p.next();
        }
    }
}