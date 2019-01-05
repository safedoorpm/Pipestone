package com.obtuse.util.lrucache;

import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

/**
 Created by danny on 2018/11/29.
 */

public class InvalidLruCacheOperationException extends IllegalArgumentException {

    private LruCache _lruCache;
    private final String _alreadyActiveMethod;
    private final String _newlyCalledMethod;

    public InvalidLruCacheOperationException(
            @NotNull LruCache lruCache,
            String alreadyActiveMethod,
            String newlyCalledMethod
    ) {

        super(
                "LruCache(" +
                ObtuseUtil.enquoteToJavaString( lruCache.getCacheName() ) +
                "):  " +
                "call to " +
                newlyCalledMethod +
                " when " +
                alreadyActiveMethod +
                " is already active in this LruCache"
        );

        _lruCache = lruCache;
        _alreadyActiveMethod = alreadyActiveMethod;
        _newlyCalledMethod = newlyCalledMethod;

    }

    @SuppressWarnings("unused")
    public LruCache getLruCache() {

        return _lruCache;

    }

    @SuppressWarnings("unused")
    public String getAlreadyActiveMethod() {

        return _alreadyActiveMethod;

    }

    @SuppressWarnings("unused")
    public String getNewlyCalledMethod() {

        return _newlyCalledMethod;

    }

}
