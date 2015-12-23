package com.example.velibisk.rssreader.rss;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by attacco on 23.12.2015.
 */
public class RSSItem implements Serializable {
    private static final long serialVersionUID = 8916988661798227461L;

    private final RSSSource source;
    String title;
    String imgUri;
    Date date;
    String description;

    public RSSItem(RSSSource source) {
        this.source = source;
    }

    public RSSSource getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public String getImgUri() {
        return imgUri;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "RSSItem{" +
                "source=" + source +
                ", title='" + title + '\'' +
                ", imgUri='" + imgUri + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }

    public String getDescription() {
        return description;
    }

}