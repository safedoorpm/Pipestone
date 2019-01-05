package com.obtuse.util.lrucache;

import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.SimpleUniqueLongIdGenerator;
import org.jetbrains.annotations.NotNull;

/**
 Created by danny on 2018/11/29.
 */
@SuppressWarnings("unused")
public class CachedThing<K, R> {

    private static SimpleUniqueLongIdGenerator s__virtualTimeGenerator =
            new SimpleUniqueLongIdGenerator( "LruCache virtual time generator" );

    private final K _key;
    private final R _thing;
    private long _virtualLastReferenceTime;

    public CachedThing( @NotNull final K key, @NotNull final R thing ) {

        super();

        _key = key;
        _thing = thing;
        _virtualLastReferenceTime = s__virtualTimeGenerator.getUniqueId();

    }

    @NotNull
    public K getKey() {

        return _key;

    }

    public long getVirtualLastReferenceTime() {

        return _virtualLastReferenceTime;

    }

    /**
     Give this thing a new virtual last reference time.
     <p>DO NOT INVOKE THIS METHOD IF THIS INSTANCE MIGHT BE IN THE LRU MAP!!!
     (we use this value as the key in the LRU map; chaos will result if
     an instance's virtual last reference time changes while the the instance
     is in the LRU map).</p>
     */

    /* package-private */ long noteNewReference() {

        _virtualLastReferenceTime = s__virtualTimeGenerator.getUniqueId();
        return _virtualLastReferenceTime;

    }

    @NotNull
    public R getThing() {

        return _thing;

    }

    public void uncached() {

        ObtuseUtil.doNothing();

    }

    public String toString() {

        return "CachedThing( " +
               "key=" + ObtuseUtil.enquoteJavaObject( _key ) + ", " +
               "thing=" + ObtuseUtil.enquoteJavaObject( _thing ) +
               " )";

    }

}
