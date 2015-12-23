package com.example.velibisk.rssreader.ui;

import com.example.velibisk.rssreader.rss.RSSItemFactory;
import com.example.velibisk.rssreader.rss.RSSSource;

/**
 * Created by attacco on 23.12.2015.
 */
class ListItemFactory implements RSSItemFactory<ListItem> {

    @Override
    public ListItem createItem(RSSSource source) {
        return new ListItem(source);
    }
}
