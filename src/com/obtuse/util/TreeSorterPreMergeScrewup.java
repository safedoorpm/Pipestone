package com.obtuse.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 * Provide a way to sort data while automatically dealing with duplicate keys.
 * Analogous to a {@link TreeMap} except that the key-value associates are one-to-many.
 * For example, a single tree sorter can contain both <code>fred</code>-><code>hello</code> and <code>fred</code>-><code>world</code> associations.
 * <p/>
 * There is no <code>get()</code> method.  See the {@link #getValues} method for the presumably obvious analogous method.
 * <p/>
 * Duplicate entries are supported in the sense that if two
 * identical associations are placed into the sorter than the value associated with these two equal entries will
 * appear twice when the sorter is traversed using its iterator (equal entries appear
 * via the iterator in the same order that they were added to the sorter).
 * For example, if the association <code>fred</code>-><code>hello</code> is already in the tree sorter when the association
 * <code>fred</code>-><code>hello</code> is added to the tree sorter then a scan through all of the values associated with
 * <code>fred</code> will yield <code>hello</code> more than once.
 * <p/>
 * If two or more different associations which both use the same key are added to the tree sorter then a scan
 * through all of the values associated with the key will yield the values in the same order that their associations
 * were added to the tree sorter.
 * For example, if the associations <code>fred</code>-><code>how</code>, <code>fred</code>-><code>are</code>,
 * <code>fred</code>-><code>you</code> and <code>fred</code>-><code>today</code>
 * are added to a previously empty tree sorter in the specified order then a scan of all of the values associated with
 * the key <code>fred</code> will yield <code>how</code>, <code>are</code>, <code>you</code> and <code>today</code> in that order.
 * <p/>
 * Instances of this class are serializable if both the key and content objects
 * used to create the instance are serializable.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public class TreeSorterPreMergeScrewup<K extends Comparable<? super K>, V> implements Iterable<V>, Serializable {
//public class TreeSorter<K, V> implements Iterable<V>, Serializable {

    private final SortedMap<K, Collection<V>> _sortedData;

    private class TreeSorterIterator<VV> implements Iterator<VV> {

        private Iterator<K> _outerIterator;

        private K _currentKey = null;

        private Iterator<VV> _innerIterator = null;
        private Collection<VV> _currentList;

        private TreeSorterIterator() {
            super();

            _outerIterator = keySet().iterator();

        }

        public boolean hasNext() {

            while ( _innerIterator == null || !_innerIterator.hasNext() ) {

                if ( _outerIterator.hasNext() ) {

                    _currentKey = _outerIterator.next();
                    //noinspection unchecked
                    _currentList = (Collection<VV>)_sortedData.get( _currentKey );

                    _innerIterator = _currentList.iterator();

                } else {

                    return false;

                }

            }

            return _innerIterator.hasNext();

        }

        public VV next() {

            hasNext();
            return _innerIterator.next();

        }

        public void remove() {

            throw new UnsupportedOperationException( "remove not supported by this iterator" );

        }

    }

    /**
     * Construct a new, empty tree sorter, using the natural ordering of its keys.
     * All keys inserted in this tree sorter must implement the {@link Comparable} interface and must be
     * <i>mutually comparable</i>({@link TreeMap#TreeMap()} for a discussion of what this means).
     */

    public TreeSorterPreMergeScrewup() {
        super();

        _sortedData = new TreeMap<>();

    }

    /**
     * Construct a new, empty tree sorter ordered according to the specified comparator.
     * All keys inserted into this tree sorter must be mutually comparable by the specified comparator
     * (see {@link TreeMap#TreeMap(Comparator)} for a discussion of what this means).
     *
     * @param comparator the comparator that will be used to order this tree sorter.
     *                   If null, the natural ordering of the keys will be used.
     */

    public TreeSorterPreMergeScrewup( @Nullable Comparator<? super K> comparator ) {
        super();

        _sortedData = new TreeMap<>( comparator );

    }

    /**
     * Constructs a new tree sorter containing the same mappings as the specified map.
     * The keys will be the natural sorted order of <code>K</code>.
     * @param map the map whose mappings are to be used to create the new tree sorter.
     * @throws IllegalArgumentException if <code>map</code> is <code>null</code>.
     */

    public TreeSorterPreMergeScrewup( @NotNull Map<K, V> map ) {
        super();

        _sortedData = new TreeMap<>();
        for ( K key : map.keySet() ) {

            add( key, map.get( key ) );

        }

    }

    /**
     * Constructs a new tree sorter containing the same mappings as the specified sorted map.
     * The new tree sorter will use the same comparator as the specified sorted map.
     * @param map the map whose mappings are to be used to create the new tree sorter.
     * @throws IllegalArgumentException if <code>map</code> is <code>null</code>.
     */

    public TreeSorterPreMergeScrewup( @NotNull SortedMap<K, V> map ) {
        super();

        _sortedData = new TreeMap<>( map.comparator() );
        for ( K key : map.keySet() ) {

            add( key, map.get( key ) );

        }

    }

    /**
     * Construct a new tree sorter which is a copy of an existing tree sorter.
     * <p/>This method is equivalent to constructing a new tree sorter called <code>newSorter</code> using the following procedure:
     * <pre>
     * TreeSorter&lt;K,V&gt; newSorter = new TreeSorter&lt;K,V&gt;( sorter.comparator() );
     * for ( K key : sorter.keySet() ) {
     *
     *     newSorter.addAll( key, sorter.getValues( key ) );
     *
     * }
     * </pre>
     * @param sorter the tree sorter whose key associations are to be copied into the newly created tree sorter.
     * @throws IllegalArgumentException if <code>sorter</code> is <code>null</code>.
     */

    public TreeSorterPreMergeScrewup( @NotNull TreeSorterPreMergeScrewup<K, V> sorter ) {
        this();

        for ( K key : sorter.keySet() ) {

            addAll( key, sorter.getValues( key ) );

        }

    }

    /**
     * Construct a new tree sorter which is backed by a different tree sorter.
     * <p/>This method is used internally to implement the
     * {@link #headSorter}, {@link #tailSorter} and {@link #subSorter} methods.  It is not intended to be used for any
     * other purpose and probably should not be exposed to the general public.
     * @param map the map which is to form the basis of this tree sorter instance.
     * @param ignored an extra parameter to ensure that the signature of this constructor is different than
     *                that of one or more of the other publically available constructors.  This parameter is totally ignored.
     */

    private TreeSorterPreMergeScrewup( SortedMap<K, Collection<V>> map, int ignored ) {
        super();

        _sortedData = map;

    }

    /**
     * Returns a view of the portion of this tree sorter whose keys are strictly less than toKey.
     * <p/>
     * The returned tree sorter is backed by this tree sorter, so changes in the returned tree sorter are reflected in this
     * tree sorter, and vice-versa. The returned tree sorter will throw an <code>IllegalArgumentException</code> on an attempt
     * to insert a key outside its range.
     * <p/>
     * Analogous to {@link SortedMap#headMap(Object)}.
     *
     * @param toKey high endpoint (exclusive) of the headSorter.
     *
     * @return a view of this tree sorter whose keys are strictly less than <code>toKey</code>.
     *
     * @throws IllegalArgumentException if <code>toKey</code> is null or if this tree sorter
     * itself has a restricted range and <code>toKey</code> lies outside the bounds of the range.
     */

    public TreeSorterPreMergeScrewup<K, V> headSorter( K toKey ) {

        return new TreeSorterPreMergeScrewup<>( _sortedData.headMap( toKey ), 0 );

    }

    /**
     * Returns a view of the portion of this tree sorter whose keys are strictly greater than or equal to fromKey.
     * <p/>
     * The returned tree sorter is backed by this tree sorter, so changes in the returned tree sorter are reflected in this
     * tree sorter, and vice-versa. The returned tree sorter will throw an <code>IllegalArgumentException</code> on an attempt
     * to insert a key outside its range.
     * <p/>
     * Analogous to {@link SortedMap#tailMap(Object)}.
     *
     * @param fromKey low endpoint (inclusive) of the headSorter.
     *
     * @return a view of this tree sorter whose keys are greater than or equal to <code>fromKey</code>.
     *
     * @throws IllegalArgumentException if <code>fromKey</code> is null or if this tree sorter
     * itself has a restricted range and <code>fromKey</code> lies outside the bounds of the range.
     */

    public TreeSorterPreMergeScrewup<K, V> tailSorter( K fromKey ) {

        return new TreeSorterPreMergeScrewup<>( _sortedData.tailMap( fromKey ), 0 );

    }

    /**
     * Returns a view of this tree sorter from <code>fromKey</code>, inclusive, to <code>toKey</code>, exclusive (if <code>fromKey</code>
     * and <code>toKey</code> are equal then the returned tree sorter is empty).
     * <p/>
     * The returned tree sorter is backed by this tree sorter, so changes in the returned tree sorter are reflected in this
     * tree sorter, and vice-versa. The returned tree sorter will throw an <code>IllegalArgumentException</code> on an attempt
     * to insert a key outside its range.
     * <p/>
     * Analogous to {@link SortedMap#subMap(Object, Object)}.
     *
     * @param fromKey low endpoint (inclusive) of the keys in the returned tree sorter.
     * @param toKey high endpoint (exclusive) of the keys in the returned tree sorter.
     *
     * @return a view of the portion of this tree sorter specified by the keys.
     *
     * @throws IllegalArgumentException if any of the following are true:
     * <ul>
     *     <li><code>fromKey</code> is <code>null</code></li>
     *     <li><code>toKey</code> is <code>null</code></li>
     *     <li><code>fromKey</code> is greater than <code>toKey</code></li>
     *     <li>this tree sorter
     * itself has a restricted range, and <code>fromKey</code> or <code>toKey</code> lies outside the bounds of the range.</li>
     * </ul>
     */

    public TreeSorterPreMergeScrewup<K, V> subSorter( @NotNull K fromKey, @NotNull K toKey ) {

        return new TreeSorterPreMergeScrewup<>( _sortedData.subMap( fromKey, toKey ), 0 );

    }

    /**
     * Determines if the specified key exists within this tree sorter.
     * Analogous to {@link SortedMap#containsKey(Object)}.
     * @param key the specified key.
     * @return true if this tree sorter includes this key.
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>.
     */

    public boolean containsKey( @NotNull K key ) {

        return _sortedData.containsKey( key );

    }

    /**
     * Get the first key.
     * <p/>Based on {@link SortedMap#firstKey()}.
     * @return the lowest key in this sorter.
     * @throws NoSuchElementException if the sorter is empty.
     */

    @NotNull
    public K firstKey() {

        return _sortedData.firstKey();

    }

    /**
     * Get the last key.
     * <p/>Based on {@link SortedMap#lastKey()}.
     * @return the highest key in this sorter.
     * @throws NoSuchElementException if the sorter is empty.
     */

    @NotNull
    public K lastKey() {

        return _sortedData.lastKey();

    }

    /**
     * Return the values associated with a specified key.
     * The values in the returned collection appear in the order that they were added to this tree sorter.
     * The returned collection of values is immutable although the contents of the collection could change if
     * more data is added to this tree sorter.
     * <p/>This operation is always quite fast as it returns an immutable view into this tree sorter's data
     * (using {@link Collections@unmodifiableCollection}) rather than a copy of this tree sorter's data.
     *
     * @param key the specified key.
     *
     * @return the values associated with the specified key or an empty collection if this tree sorter has no values
     * associated with the specified key.
     * The returned collection is always wrapped using {@link Collections#unmodifiableCollection}.
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>.
     */

    @NotNull
    public Collection<V> getValues( @NotNull K key ) {

        Collection<V> values = _sortedData.get( key );

        return Collections.unmodifiableCollection( values == null ? new LinkedList<>() : values );

    }

    /**
     * Return all the values in this tree sorter in key order.
     * Values with unequal keys are returned in key-sorted order.
     * Values with equal keys are returned in the order that they were added to this tree sorter.
     * <p/>Every call to this method returns a distinct collection of values.  The caller is free to do
     * whatever they like to the returned collection.
     * <p/>This is potentially a rather expensive operation depending upon how much data is in this tree sorter instance.
     *
     * @return all the values in this tree sorter.
     */

    @NotNull
    public Collection<V> getAllValues() {

        Collection<V> allValues = new LinkedList<>();
        for ( K key : _sortedData.keySet() ) {

            allValues.addAll( getValues( key ) );

        }

        return allValues;

    }

    /**
     * Add a new key-value pair to this tree sorter.
     * Each tree sorter instance is capable of maintaining an arbitrary number of one to many key to value association.
     * For example, if the key-value pair <code>fred=hello</code> and <code>fred=world</code> are added to a previously
     * empty tree sorter then the key <code>fred</code> will have both <code>hello</code> and <code>world</code> associated
     * with it in the tree sorter.
     * <p/>
     * Adding the same key to value association to a tree sorter multiple times results in the value appearing multiple times
     * when the tree sorter is 'scanned'.
     * For example, if the key-value pair <code>fred=hello</code> is added to a tree sorter three
     * times then the collection returned by <code>getValues( "fred" )</code> will contain <code>hello</code> three times.
     * Analogous to {@link SortedMap#put(Object, Object)}.
     * @param key with which the specified value is to be associated.
     * @param value the value to be associated with the specified key.
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code> (note that <code>value</code> is allowed to be <code>null</code>).
     */

    public final void add( @NotNull K key, @Nullable V value ) {

        Collection<V> values = _sortedData.get( key );
        if ( values == null ) {

            values = new LinkedList<>();
            _sortedData.put( key, values );

        }

        values.add( value );

    }

    /**
     * Add all of the key value associations from a {@link Map} to this tree sorter.
     * This method is exactly equivalent to
     * <pre>
     * for ( K key : map.keySet() ) {
     *     treeSorter.add( key, map.get( key ) );
     * }
     * </pre>
     * @param map the map whose contents are to be added to this tree sorter.
     * @throws IllegalArgumentException if <code>map</code> is <code>null</code>.
     */

    public void addAll( @NotNull Map<? extends K, ? extends V> map ) {

        for ( K key : map.keySet() ) {

            add( key, map.get( key ) );

        }

    }

    /**
     * Add all of the associations from a different tree sorter to this tree sorter.
     * This method is exactly equivalent to
     * <pre>
     * for ( K key : sorter.keySet() ) {
     *     treeSorter.addAll( key, sorter.getValues( key ) );
     * }
     * </pre>
     * @param sorter the tree sorter whose contents are to be added to this tree sorter.
     * @throws IllegalArgumentException if <code>sorter</code> is <code>null</code> or
     * if an attempt is made to add the contents of a tree sorter to itself.
     */

    public void addAll( @NotNull TreeSorterPreMergeScrewup<K, V> sorter ) {

        if ( this == sorter ) {

            throw new IllegalArgumentException( "attempt to add a tree sorter to itself" );

        }

        for ( K key : sorter.keySet() ) {

            addAll( key, sorter.getValues( key ) );

        }

    }

    /**
     * Associate all of the values in a collection with a specified key.
     * This method is exactly equivalent to
     * <pre>
     * for ( V value : values ) {
     *     treeSorter.add( key, value );
     * }
     * </pre>
     * Truly disconcerting things will probably happen if the following is attempted:
     * <pre>
     *  treeSorter.addAll( key, treeSorter.getAll( key ) )
     * </pre>
     * @param key the key that all of the values in the specfied collection are to be associated with.
     * @param values the values which are to be associated with the specified key.
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code> or <code>values</code> is <code>null</code>.
     */

    public void addAll( @NotNull K key, @NotNull Collection<V> values ) {

        for ( V value : values ) {

            add( key, value );

        }

    }

    /**
     * Returns a Set view of the keys contained in this TreeSorter instance.
     * The set's iterator will return the keys in ascending order.
     * The map is backed by this TreeSorter instance, so changes to
     * this map are reflected in the Set, and vice-versa. The Set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <code>Iterator.remove</code>, <code>Set.remove</code>,
     * <code>Set.removeAll</code>, <code>Set.retainAll</code>, and <code>Set.clear</code> operations
     * It does not support the <code>Set.add</code> or <code>Set.addAll</code> operations.
     *
     * @return a set view of the keys in this TreeSorter.
     */

    @NotNull
    public Set<K> keySet() {

        return _sortedData.keySet();

    }

    /**
     * Removes all of the values associated with a specified key.
     * @param key the key for which all associated values are to be removed.
     * @return a collection of the values which were removed. The caller is free to do whatever they wish with the
     * returned collection. An empty collection is returned if there were no values associated with the specified key.
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>.
     */

    @NotNull
    public Collection<V> removeKeyAndValues( @NotNull K key ) {

        Collection<V> rval = _sortedData.remove( key );
        if ( rval == null ) {

            rval = new LinkedList<>();

        }

        return rval;

    }

    @NotNull
    public Collection<V> removeValue( @NotNull K key, @Nullable V value ) {

	return removeValue( key, value, new FormattingLinkedList<>() );

    }

    @NotNull
    public Collection<V> removeValue( @Nullable V value ) {

	Collection<V> deletedValues = new FormattingLinkedList<>();

	for ( Iterator<K> iterator = _sortedData.keySet().iterator(); iterator.hasNext(); ) {

	    K key = iterator.next();

	    removeValue( key, value, deletedValues );
	    if ( _sortedData.get( key ).isEmpty() ) {

		iterator.remove();

	    }

	}

	return deletedValues;

    }

    @NotNull
    public Collection<V> removeValue( @NotNull K key, @Nullable V value, @NotNull Collection<V> deletedValues ) {

	Collection<V> valuesAtKey = _sortedData.get( key );
	if ( valuesAtKey != null ) {

	    for ( Iterator<V> iterator = valuesAtKey.iterator(); iterator.hasNext(); ) {

		V aValue = iterator.next();

		if ( aValue == value ) {

		    deletedValues.add( aValue );
		    iterator.remove();

		}

	    }

	}

	return deletedValues;

    }

    /**
     * Get an iterator which iterates across all of the values.
     * Changes to the tree sorter while the iterator is in use could invalidate the iterator resulting in all sorts of
     * strange things happening.
     * @return an iterator which iterates across all of the values in this tree sorter.
     */

    @NotNull
    public Iterator<V> iterator() {

        @SuppressWarnings({ "UnnecessaryLocalVariable" })
        Iterator<V> iter = new TreeSorterIterator<>();

        return iter;

    }

    /**
     * Returns the number of values in this tree sorter.
     * <p/>This method could be fairly expensive if there are a lot of values in this tree sorter.
     * <p/>This method is equivalent to:
     * <pre>
     * int totalSize = 0;
     * for ( K key : treeSorter.keySet() ) {
     *     totalSize += treeSorter.getValues().size();
     * }
     * return totalSize;
     * </pre>
     * @return the number of values in this tree sorter.
     */

    public int size() {

        int totalSize = 0;
        for ( Collection<V> subList : _sortedData.values() ) {

            totalSize += subList.size();

        }

        return totalSize;

    }

    /**
     * Determine if this tree sorter has any key value associations in it.
     * <p/>This method is always very fast.
     * @return true if this tree sorter is empty; false otherwise.
     */

    public boolean isEmpty() {

        return _sortedData.isEmpty();

    }

    /**
     * Returns a string which states the current size of this tree sorter.
     * <p/>The returned string is of the form
     * <pre>
     * size = <i>n</i>
     * </pre>
     * where <code><i>n</i></code> is the current size of this tree sorter.
     * This method calls {@link #size()} which means that it could be somewhat expensive if there are a lot of values in
     * this tree sorter.
     * @return a string which states the current size of this tree sorter.
     */

    public String toString() {

//        return "size = " + size();

        return "TreeSorter( " + _sortedData + " )";

    }

    public static void main( String[] args ) {

        TreeSorterPreMergeScrewup<Integer, String> sorter = new TreeSorterPreMergeScrewup<>();

        sorter.add( 1, "one" );
        sorter.add( 2, "two" );
        sorter.add( 3, "three" );
        sorter.add( 1, "I" );

        for ( String v : sorter ) {

            //noinspection UseOfSystemOutOrSystemErr
            System.out.println( v );

        }

        System.out.println( "---" );

        Iterator<String> iter = sorter.iterator();
        for ( int i = 0; i < sorter.size(); i += 1 ) {

            System.out.println( iter.next() );

        }

    }

}
