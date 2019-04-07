package com.obtuse.util.lrucache;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;
import java.util.function.Consumer;

/**
 A relatively simple LRU-based cache.
 <p>Instances of this class are threadsafe.</p>
 <p>Instances of this class are NOT safe if certain of their methods are called recursively by a single thread.
 For example, <em>very bad things might happen</em> if a call by an instance of this class to
 its {@link Fetcher} instance's {@link Fetcher#fetch(Object, boolean)} method calls back into the same instance of this class.
 Instances of this class protect themselves from this sort of nonsense by prohibiting <b><u>ANY</u></b>
 calls back into the class while a call to one of this class's public methods is still underway.
 If a recursive call is detected, you will become the not-so-proud holder of an {@link InvalidLruCacheOperationException}
 (a derivation of {@link IllegalArgumentException}).</p>
 <p>It should be noted that this protection mechanism might not cover all cases.
 It is intended as more of a debugging aid.
 Do not rely on it to always keep you out of trouble.</p>
 <p>Note that methods of this class which only return information are generally safe to call from anywhere.
 Those which are safe are clearly noted in their JavaDocs.
 </p>
 */

public class LruCache<K,R> implements Iterable<CachedThing<K,R>> {

    private final String _cacheName;

    private int _nullRvalCount = 0;
    private int _nonNullRvalCount = 0;
    private String _activeMethod = null;

    private boolean _thingsRequireCleanup = false;

    private static Thread s_mainThread;

    public static void identifyMainThread( final Thread currentThread ) {

        s_mainThread = currentThread;

    }

    @NotNull
    public static Thread getMainThread() {

        return s_mainThread;

    }

    public interface Fetcher<K,R> {

        @NotNull
        Optional<CachedThing<K,R>> fetch( @NotNull final K key, final boolean nullOk );

    }

    public interface ThingRequiringCleanup<KK> {

        void doCleanupPriorToCacheDeletion( KK key );

    }

    public static final int MINIMUM_CACHE_SIZE = 5;
    public static final int DEFAULT_MAXIMUM_CACHE_SIZE = 10;

    private Map<K, CachedThing<K,R>> _cache = new HashMap<>();

    private SortedMap<Long,CachedThing<K,R>> _lru = new TreeMap<>();

    private int _maximumCacheSize;

    private int _totalFetchCount = 0;
    private int _actualFetchCount = 0;

    private final String _className;

    private final Fetcher<K,R> _fetcher;

    private boolean _crashWhenFull = false;

    /**
     Create an LRU cache.
     <p>Instances of this class prevent runaway cache growth by discarding the least recently used (LRU)
     element whenever the cache would otherwise have too many elements.
     See {@link #setMaximumCacheSize(int)} and {@link #getMaximumCacheSize()} for more info.</p>@param maximumCacheSize the maximum number of elements allowed in the cache at any given time.
     <p/>An arbitrary limit of {@link Integer#MAX_VALUE}{@code -1_000_000} is silently enforced.
     This is to respect the fact that the {@code Map#size()} method that is called to discover how many entries
     are in the cache (i.e. there is simply no practical way to actually support more than
     {@link Integer#MAX_VALUE} elements)
     and to provide for a bit of general paranoia on my part (I, Danny, don't want to have to test
     what happens if the size of the cache reaches truly milestone values like {@link Integer#MAX_VALUE}).
     Besides, you are going to need a LOT of memory to actually create a cache of even one billion relatively tiny things.
     @param cacheName the name of this cache (used when throwing exceptions).
     @param maximumCacheSize the maximum size of this cache (see above discussion in this method's JavaDocs for details).
     @param fetcher the {@link Fetcher}{@code <K,R>} that will called to deal with cache misses.
     */

    public LruCache(
            @NotNull final String cacheName,
            final int maximumCacheSize,
            @NotNull final Fetcher<K, R> fetcher
    ) {
        super();

        _cacheName = cacheName;

        _maximumCacheSize = Math.min( Integer.MAX_VALUE - 1_000_000, maximumCacheSize );

        _fetcher = fetcher;

        _className = getClass().getCanonicalName();

    }

    public LruCache( @NotNull final String cacheName, @NotNull final Fetcher<K,R> fetcher ) {
        this( cacheName, DEFAULT_MAXIMUM_CACHE_SIZE, fetcher );
    }

    private void checkOnEventThread( @NotNull final String who ) {

        if ( !SwingUtilities.isEventDispatchThread() && !Thread.currentThread().equals( s_mainThread ) ) {

            throw new HowDidWeGetHereError( "LruCache:  call to " + who + " is NOT on the event thread or main thread" );

        }

    }

    public void setThingsRequireCleanup( boolean thingsRequireCleanup ) {

        checkOnEventThread( "setThingsRequireCleanup" );

        _thingsRequireCleanup = thingsRequireCleanup;

    }

    public boolean doThingsRequireCleanup() {

        checkOnEventThread( "doThingsRequireCleanup" );

        return _thingsRequireCleanup;

    }

    public void forceThingsCleanup() {

        checkOnEventThread( "forceThingsCleanup" );

        if ( doThingsRequireCleanup() ) {

            for ( K key : _cache.keySet() ) {

                CachedThing<K, R> cachedThing = _cache.get( key );
                R thing = cachedThing.getThing();
                if ( thing instanceof ThingRequiringCleanup ) {

                    @SuppressWarnings("unchecked") ThingRequiringCleanup<K> th = (ThingRequiringCleanup<K>)thing;
                    th.doCleanupPriorToCacheDeletion( cachedThing.getKey() );

                }

            }

        }

    }

    @NotNull
    public String getCacheName() {

        checkOnEventThread( "getCacheName" );

        return _cacheName;

    }

    @NotNull
    @Override
    public Iterator<CachedThing<K, R>> iterator() {

        checkOnEventThread( "iterator" );

        return _cache.values().iterator();

    }

    @Override
    public void forEach( final Consumer<? super CachedThing<K, R>> action ) {

        checkOnEventThread( "foreach" );

        _cache.values().forEach( action );


    }

    @Override
    public Spliterator<CachedThing<K, R>> spliterator() {

        checkOnEventThread( "spliterator" );

        return _cache.values().spliterator();

    }

    /**
     Clear (empty) the cache.
     @throws InvalidLruCacheOperationException if called recursively by the calling thread.
     */

    @SuppressWarnings("unused")
    public synchronized void clear() {

        checkOnEventThread( "clear" );

        _activeMethod = checkForRecursion( "clear()" );
        try {

            _cache.clear();
            _lru.clear();

        } finally {

            _activeMethod = null;

        }

    }

    @NotNull
    private synchronized String checkForRecursion( @NotNull final String who ) {

        checkOnEventThread( "checkForRecursion" );

        if ( _activeMethod != null ) {

            throw new InvalidLruCacheOperationException( this, _activeMethod, who );

        }

        return who;

    }

    /**
     Clear any statistics that this class maintains.
     <p>See {@link #getNonNullRvalCount()} and {@link #getNullRvalCount()} for more info.</p>
     <p>Recursive calls to this method are safe.</p>
     */

    @SuppressWarnings("unused")
    public synchronized void clearStats() {

        checkOnEventThread( "clearStats" );
        _nonNullRvalCount = 0;
        _nullRvalCount = 0;

    }

    /**
     Get the current cache size (count of elements currently in the cache).
     <p>Recursive calls to this method are safe.</p>
     */

    public synchronized int size() {

        checkOnEventThread( "size" );
        return _cache.size();

    }

    /**
     Change the current maximum cache size (maximum elements allowed in the cache).
     @param maximumCacheSize the new maximum cache size.
     Attempts to set the maximum cache size to less than {@link #MINIMUM_CACHE_SIZE} result in
     the cache size being silently set to {@code MINIMUM_CACHE_SIZE}.
     @return the new maximum cache size.
     @throws InvalidLruCacheOperationException if called recursively by the calling thread.
     */

    @SuppressWarnings("unused")
    public synchronized int setMaximumCacheSize( int maximumCacheSize ) {

        checkOnEventThread( "setMaximumCacheSize" );

        _activeMethod = checkForRecursion( "setMaximumCacheSize( " + maximumCacheSize + ")" );
        try {

            _maximumCacheSize = Math.max( MINIMUM_CACHE_SIZE, maximumCacheSize );

            makeRoom();

            return _maximumCacheSize;

        } finally {

            _activeMethod = null;

        }

    }

    /**
     Get the current maximum cache size.
     @return the current maximum cache size.
     <p>Recursive calls to this method are safe.</p>
     */

    @SuppressWarnings("unused")
    public synchronized int getMaximumCacheSize() {

        checkOnEventThread( "getMaximumCacheSize" );
        return _maximumCacheSize;

    }

    /**
     A fast way to grab a specified element if it happens to already be in the cache.
     <p>A call to this method only returns the requested element if it is already in the cache.
     In particular, calls to this method never trigger a fetch operation to fulfill a cache-miss
     and do not 'age' the element in the LRU queue.</p>
     @param key the key for the specified element.
     @return the element corresponding to the key if the element is already in the cache.
     @throws InvalidLruCacheOperationException if called recursively by the calling thread.
     */

    public synchronized Optional<CachedThing<K,R>> getNoFetch( @NotNull final K key ) {

        checkOnEventThread( "getNoFetch" );

        _activeMethod = checkForRecursion( "getNoFetch()" );
        try {

            CachedThing<K, R> rval = _cache.get( key );

            return Optional.ofNullable( rval );

        } finally {

            _activeMethod = null;

        }

    }

    /**
     Determine if a specified element is already in the cache.
     <p>Invoking this method will <b>NEVER</b> trigger the loading of the element.</p>
     @param key the key for the specified element.
     @return {@code true} if the specified element is already in the cache; {@code false} otherwise.
     */

    public boolean isElementAlreadyCached( @NotNull final K key ) {

        checkOnEventThread( "isElementAlreadyCached" );

        return getNoFetch( key ).isPresent();

    }

    /**
     Get a specified element from/via the cache.
     <p>This method operates as follows:
     <blockquote>
     <ul>
     <li>if the specified element is not currently in the cache then it is fetched
     using the {@link Fetcher} specified when the cache was created and, if the fetch yields a non-null result,
     it is put into the cache.</li>
     <li>if the specified element is now in the cache then it is returned; otherwise, {@code null} is returned.</li>
     </ul>
     </blockquote>
     </p>
     @return the specified value if, by the end of this call to this method, the specified value is in the cache;
     {@code null} otherwise.
     @throws HowDidWeGetHereError if the number of keys on the LRU list is different than the number of
     keys or the number of elements in the cache when this method is called or when it returns.
     @param key the key for the specified element.
     @param nullOk {@code true} if the fetcher, if it is invoked, is to be told that a {@code null} result is acceptable;
     {@code false} if the fetcher, if it is invoked, is to be told that a {@code null} result is unacceptable.
     <p>Note that if the fetcher is invoked then the value that it returned becomes the return value for
     this call to this method.
     See {@link #getMandatory(Object)} and {@link #getOptional(Object)} for more information.</p>
     <p>RECURSIVE CALLS TO THIS METHOD ARE NOT SAFE.</p>
     <p>THE PROTECTION MECHANISM DESCRIBED ABOVE DOES NOT DETECT RECURSIVE CALLS TO THIS METHOD.</p>
     */

    private synchronized CachedThing<K, R> innerGet( @NotNull final K key, final boolean nullOk ) {

        checkOnEventThread( "innerGet" );

        _totalFetchCount += 1;

        if ( _lru.size() != _cache.size() ) {

            throw new HowDidWeGetHereError( _className + ":  size imbalance #1" );

        }

        CachedThing<K, R> rval = (
                _cache.computeIfAbsent(
                        key,
                        keyx -> {

                            _actualFetchCount += 1;

                            Optional<CachedThing<K, R>> rv = _fetcher.fetch( keyx, nullOk );
                            return rv.orElse( null );

                        }
                )
        );

        if ( rval == null ) {

            _nullRvalCount += 1;

            return null;

        } else {

            _nonNullRvalCount += 1;

            // We cannot note the reference earlier as doing so could change the length
            // of the LRU list which could (and does) result in a con-check because
            // the size of the LRU list is then different than the size of the cache.
            // We also cannot do this earlier because we'd risk a size mismatch if we
            // note a reference which corresponds to a null element.
            //
            // I'm not 100% that both of these reasons are actually valid. I am certain
            // that separating the noting of references from the making of room is a
            // risky game to play.

            noteReference( rval );

            makeRoom();

            if ( _lru.size() != _cache.size() ) {

                throw new HowDidWeGetHereError( _className + ":  size imbalance #2" );

            }

            return rval;

        }

    }

    /**
     Insert an element into the cache.
     <p>This method allows the cache to be 'seeded' with values that come from unknown or, probably more accurately,
     atypical places.
     The element is put into the cache if it is not already present.
     In either event, the element is given the exalted if almost surely fleeting title of 'most recently used element'.
     </p>
     */

    @SuppressWarnings("UnusedReturnValue")
    public CachedThing<K, R> insertElementIntoCache( @NotNull final CachedThing<K,R> cachedThing, boolean replaceOk ) {

        checkOnEventThread( "insertElementIntoCache" );

        if ( _lru.size() != _cache.size() ) {

            throw new HowDidWeGetHereError( _className + ":  size imbalance #1" );

        }

        CachedThing<K, R> valueInCache = _cache.get( cachedThing.getKey() );
        if ( valueInCache == null ) {

            _cache.put( cachedThing.getKey(), cachedThing );
            noteReference( cachedThing );

        } else if ( valueInCache.getThing() == cachedThing.getThing() ) {

            noteReference( valueInCache );

        } else if ( replaceOk ) {

            _cache.put( cachedThing.getKey(), cachedThing );
            replaceReference( valueInCache, cachedThing );

        } else {

            Logger.logErr(
                    "LruCache.forceElementIntoCache:  " +
                    "attempt to replace value at " + ObtuseUtil.enquoteJavaObject( cachedThing.getKey() ) +
                    "->" +
                    ObtuseUtil.enquoteJavaObject( valueInCache.getThing() ) +
                    " with " +
                    ObtuseUtil.enquoteJavaObject( cachedThing.getThing() )
            );

            throw new IllegalArgumentException(
                    "LruCache.forceElementIntoCache:  " +
                    "attempt to replace value at " + ObtuseUtil.enquoteJavaObject( cachedThing.getKey() ) +
                    " with a different value (see log file at " +
                    ObtuseUtil.enquoteToJavaString( Logger.getStderr().getOutputFileName() ) +
                    " for more info)"
            );

        }

        makeRoom();

        if ( _lru.size() != _cache.size() ) {

            throw new HowDidWeGetHereError( _className + ":  size imbalance #3" );

        }

        return cachedThing;

    }

    /**
     Get a specified element from/via the cache.
     <p>A call to this method is exactly equivalent to
     <blockquote>
     <code>{@link #innerGet}( key, false )</code>
     </blockquote>
     If the call to {@link #innerGet} returns {@code null} then an {@link IllegalStateException} is thrown
     when this method attempts to return the {@code null} in violation of its {@code @NotNull} annotation.</p>
     @return the specified element.
     @throws java.lang.IllegalStateException if the specified element does not exist.
     @param key the key for the specified element.
     @throws InvalidLruCacheOperationException if called recursively by the calling thread.
     */

    @SuppressWarnings("unused")
    public CachedThing<K, R> getMandatory( @NotNull final K key ) {

        checkOnEventThread( "getMandatory" );
        _activeMethod = checkForRecursion( "getMandatory()" );
        try {

            return innerGet( key, false );

        } finally {

            _activeMethod = null;

        }

    }

    /**
     Attempt to get a specified element from/via the cache.
     <p>A call to this method is exactly equivalent to
     <blockquote>
     <code>Optional.ofNullable( {@link #innerGet}( key, true ) )</code>
     </blockquote>
     </p>
     @return the specified element (if it exists in the cache) or {@code null} wrapped in an {@link Optional}.
     @param key the key for the specified element.
     @throws InvalidLruCacheOperationException if called recursively by the calling thread.
     */

    @SuppressWarnings("unused")
    public Optional<CachedThing<K, R>> getOptional( @NotNull final K key ) {

        checkOnEventThread( "getOptional" );
        _activeMethod = checkForRecursion( "getOptional()" );
        try {

            CachedThing<K, R> rval = innerGet( key, true );

            return Optional.ofNullable( rval );

        } finally {

            _activeMethod = null;

        }

    }

    /**
     Conceptually move the just requested element to the front of the LRU list.
     <p>What this method actually does is:
     <blockquote>
     <ul>
     <li>if the key used by the most recent call to
     {@link #innerGet} (i.e. as called by the most recent call to {@link #getMandatory(Object)} or
     {@link #getOptional(Object)}) is already in the LRU list then it is moved to the front of the LRU list
     </li>
     <li>otherwise, the key is inserted at the front of the LRU list.</li>
     </blockquote>
     The effect is that the keys in the LRU list are always in order of how long ago they were last used
     to get something from the cache.
     Colloquially speaking, the more recently used keys are towards the front of the LRU list and
     less recently used keys are towards the end).
     Strictly speaking, there is nothing 'colloquial' about what this method does - it maintains the LRU list with
     more recently used keys <b>ALWAYS</b> in front of less recently used keys.
     </p>
     When a call to {@link #innerGet} returns, the number of keys in the LRU list will be <b>EXACTLY</b> equal to
     the number of keys and the number of elements in the actual cache, and
     <b>NO</b> key will ever appear more than once on the LRU list.</p>
     @param element the element in question.
     */

    private synchronized void noteReference( @NotNull final CachedThing<K,R> element ) {

        checkOnEventThread( "noteReference" );

        _lru.remove( element.getVirtualLastReferenceTime() );
//        _lru.remove( element );
        _lru.put( element.noteNewReference(), element );

    }

    private synchronized void replaceReference( @NotNull final CachedThing<K,R> oldElement, @NotNull final CachedThing<K,R> newElement ) {

        checkOnEventThread( "replaceReference" );

        if ( _lru.containsKey( newElement.getVirtualLastReferenceTime() ) ) {

            throw new HowDidWeGetHereError(
                    "LruCache.replaceReference:  new element's virtual last reference time " +
                    newElement.getVirtualLastReferenceTime() + " is already in the LRU"
            );

        }

        CachedThing<K, R> trueOldElement = _lru.remove( oldElement.getVirtualLastReferenceTime() );

        // Provide breakpoints so we can watch for replaces that are or are not actually additions.

        if ( trueOldElement == null ) {

            ObtuseUtil.doNothing();

        } else {

            //noinspection ResultOfMethodCallIgnored
            ObtuseUtil.always();

        }

        _lru.put( newElement.noteNewReference(), newElement );

    }

    public void setCrashWhenFull( boolean crashWhenFull ) {

//        checkOnEventThread( "setCrashWhenFull" );

        _crashWhenFull = crashWhenFull;

    }

    public boolean crashWhenFull() {

        checkOnEventThread( "crashWhenFull" );

        return _crashWhenFull;

    }

    /**
     Ensure that the cache does not have more than <em>maximum cache size</em> keys.
     <p>A call to this method deletes keys and their corresponding elements from the cache
     until the cache has not more than the <em>maximum cache size</em> keys (and corresponding elements).</p>
     @return the size of of the cache after the <em>making room</em> work is done.
     This size will never exceed the current <em>maximum cache size</em>}.
     It could be less than the <em>maximum cache size</em> if the <em>maximum cache size</em>
     has recently been raised or if the cache has simply never yet reached the <em>maximum cache size</em>.
     */

    @SuppressWarnings("UnusedReturnValue")
    private synchronized int makeRoom() {

        ObtuseUtil.doNothing();

        checkOnEventThread( "makeRoom" );

        while ( _cache.size() > _maximumCacheSize ) {

            if ( crashWhenFull() ) {

                throw new CacheIsFullException( this );

            }

            long oldestVirtualTime = _lru.firstKey();
            CachedThing<K,R> oldest = _lru.remove( oldestVirtualTime );

            Logger.logMsg( "LruCache(" + _cacheName + "):  removing " + oldest.getKey() );

            R thing = oldest.getThing();
            if ( _thingsRequireCleanup && thing instanceof ThingRequiringCleanup ) {

                @SuppressWarnings("unchecked") ThingRequiringCleanup<K> th = (ThingRequiringCleanup<K>)thing;
                th.doCleanupPriorToCacheDeletion( oldest.getKey() );

            }

            _cache.remove( oldest.getKey() );

            oldest.uncached();

        }

        return _cache.size();

    }

    /**
     Get the number of {@code null} values returned by {@link #innerGet}.
     @return the number of {@code null} values returned by {@link #innerGet}
     since the most recent call to {@link #clearStats()} or, if {@code clearStats()} has never been called,
     since this instance was created.</p>
     <p>Recursive calls to this method are safe.</p>
     */

    @SuppressWarnings("unused")
    public int getNullRvalCount() {

        checkOnEventThread( "getNullRvalCount" );

        return _nullRvalCount;

    }

    /**
     Get the number of non-{@code null} values returned by {@link #innerGet}.
     @return the number of non-{@code null} values returned by {@link #innerGet}
     since the most recent call to {@link #clearStats()} or, if {@code clearStats()} has never been called,
     since this instance was created.</p>
     <p>Recursive calls to this method are safe.</p>
     */

    @SuppressWarnings("unused")
    public int getNonNullRvalCount() {

        checkOnEventThread( "getNonNullRvalCount" );

        return _nonNullRvalCount;

    }

    /**
     Generates and returns a string describing the current status of this instance.
     <p>The format of the string is subject to change. It is intended for a fairly technical audience.</p>
     @return a string describing the current status of this instance.
     */

    public String toString() {

        if ( SwingUtilities.isEventDispatchThread() ) {

            return "" +
                   _totalFetchCount + " fetches, " +
                   ( _totalFetchCount - _actualFetchCount ) + " already in memory, " +
                   _actualFetchCount + " fetched from disk, " +
                   ObtuseUtil.lpad(
                           Math.round(
                                   Math.round(
                                           100 * (
                                                   1.0 - ObtuseUtil.safeDivide(
                                                           _actualFetchCount,
                                                           (double)_totalFetchCount
                                                   )
                                           )
                                   )
                           ),
                           0
                   ) +
                   "% efficient, " +
                   "maximumSize=" + getMaximumCacheSize();

        } else {

            return "LruCache.toString called NOT called from event thread";

        }

    }

}
