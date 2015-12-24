package com.example.velibisk.rssreader.rss;

import android.util.Xml;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.Util;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.example.velibisk.rssreader.Util.safeTrim;

/**
 * Created by attacco on 23.12.2015.
 */
public class RSSClient {
    private final OkHttpClient http;
    private final RSSItemFactory itemFactory;

    @Inject
    @Singleton
    public RSSClient(OkHttpClient okHttpClient, RSSItemFactory itemFactory) {
        this.http = okHttpClient;
        this.itemFactory = itemFactory;
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
                    item = itemFactory.createItem(source);
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
                        item.title = safeTrim(text);
                    }
                } else if ("enclosure".equals(p.getName())) {
                    if (item != null) {
                        item.imgUri = text;
                    }
                } else if ("description".equals(p.getName())) {
                    if (item != null) {
                        item.description = safeTrim(text);
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