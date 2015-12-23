package com.example.velibisk.rssreader;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by attacco on 23.12.2015.
 */
public final class Util {

    private Util() {
    }

    public static <E> ArrayList<E> toArrayList(Collection<E> collection) {
        if (collection == null) {
            return null;
        }
        return collection instanceof ArrayList ? (ArrayList<E>)collection : new ArrayList<>(collection);
    }

}