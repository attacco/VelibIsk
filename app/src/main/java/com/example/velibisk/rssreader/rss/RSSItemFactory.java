package com.example.velibisk.rssreader.rss;

/**
 * Created by attacco on 23.12.2015.
 */
public interface RSSItemFactory<T extends RSSItem> {

    T createItem(RSSSource source);

}
