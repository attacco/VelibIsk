package com.example.velibisk.rssreader.ui;

import com.example.velibisk.rssreader.rss.RSSItem;
import com.example.velibisk.rssreader.rss.RSSSource;

/**
 * Created by attacco on 23.12.2015.
 */
class ListItem extends RSSItem {
    public ListItem(RSSSource source) {
        super(source);
    }

    private boolean expanded = false;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
