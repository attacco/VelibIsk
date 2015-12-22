package com.example.velibisk.rssreader;

import com.pkmmte.pkrss.Article;

/**
 * Created by attacco on 23.12.2015.
 */
public class RSSItem {
    private final Article article;

    public RSSItem(Article article) {
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }
}
