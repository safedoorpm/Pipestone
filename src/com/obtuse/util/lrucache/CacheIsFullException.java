package com.obtuse.util.lrucache;

import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

/**
 Created by danny on 2018/11/29.
 */
public class CacheIsFullException extends IllegalArgumentException {

    private LruCache _lruCache;

    public CacheIsFullException( @NotNull LruCache lruCache ) {

        super(
                "LruCache(" +
                ObtuseUtil.enquoteToJavaString( lruCache.getCacheName() ) +
                "):  " +
                "cache reached maximum size of " +
                lruCache.getMaximumCacheSize() +
                " and 'crashWhenFull()' returned true"
        );

        _lruCache = lruCache;

    }

    @SuppressWarnings("unused")
    public LruCache getLruCache() {

        return _lruCache;

    }

}
