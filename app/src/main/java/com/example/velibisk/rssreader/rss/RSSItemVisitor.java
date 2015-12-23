package com.example.velibisk.rssreader.rss;

/**
 * Created by attacco on 23.12.2015.
 */
public interface RSSItemVisitor<T extends RSSItem> {
    boolean visit(T item);
}