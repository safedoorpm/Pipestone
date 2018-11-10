/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import com.obtuse.util.gowing.*;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.exceptions.GowingUnpackingException;
import com.obtuse.util.gowing.p2a.GowingUtil;
import com.obtuse.util.gowing.p2a.holders.GowingPackableEntityHolder;
import com.obtuse.util.gowing.p2a.holders.GowingPackableMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

/**
 Provide a way to sort data while automatically dealing with duplicate keys.
 Analogous to a {@link TreeMap} except that the key-value associates are one-to-many.
 For example, a single tree sorter can contain both <code>fred</code>-><code>hello</code> and <code>fred</code>-><code>world</code> associations.
 <p/>
 There is no <code>get()</code> method.  See the {@link #getValues} method for the presumably obvious analogous method.
 <p/>
 Duplicate entries are supported in the sense that if two
 identical associations are placed into the sorter than the value associated with these two equal entries will
 appear twice when the sorter is traversed using its iterator (equal entries appear
 via the iterator in the same order that they were added to the sorter).
 For example, if the association <code>fred</code>-><code>hello</code> is already in the tree sorter when the association
 <code>fred</code>-><code>hello</code> is added to the tree sorter then a scan through all of the values associated with
 <code>fred</code> will yield <code>hello</code> more than once.
 <p/>
 If two or more different associations which both use the same key are added to the tree sorter then a scan
 through all of the values associated with the key will yield the values in the same order that their associations
 were added to the tree sorter.
 For example, if the associations <code>fred</code>-><code>how</code>, <code>fred</code>-><code>are</code>,
 <code>fred</code>-><code>you</code> and <code>fred</code>-><code>today</code>
 are added to a previously empty tree sorter in the specified order then a scan of all of the values associated with
 the key <code>fred</code> will yield <code>how</code>, <code>are</code>, <code>you</code> and <code>today</code> in that order.
 <p/>
 Instances of this class are serializable if both the key and content objects
 used to create the instance are serializable.
 */

@SuppressWarnings({ "UnusedDeclaration" })
public class TreeSorter<K extends Comparable<? super K>, V>
        extends GowingAbstractPackableEntity
        implements Iterable<V>, Serializable {

    private static final EntityTypeName ENTITY_TYPE_NAME = new EntityTypeName( TreeSorter.class );

    private static final int VERSION = 1;

    private static final EntityName ELEMENTS_MAPPING_NAME = new EntityName( "_ec" );

    private GowingEntityReference _elementsMappingReference;

    private String _description;

    public static final GowingEntityFactory FACTORY = new GowingEntityFactory( ENTITY_TYPE_NAME ) {

        @Override
        public int getOldestSupportedVersion() {

            return VERSION;
        }

        @Override
        public int getNewestSupportedVersion() {

            return VERSION;
        }

        @NotNull
        @Override
        public GowingPackable createEntity(
                final @NotNull GowingUnPacker unPacker,
                final @NotNull GowingPackedEntityBundle bundle,
                final @NotNull GowingEntityReference er
        ) {

            return new TreeSorter( unPacker, bundle );

        }

    };

    private final SortedMap<K, List<V>> _sortedData;

    public TreeSorter( final @NotNull GowingUnPacker unPacker, final @NotNull GowingPackedEntityBundle bundle ) {

        super( unPacker, bundle.getSuperBundle() );

        _elementsMappingReference = bundle.getMandatoryEntityReference( ELEMENTS_MAPPING_NAME );

        _sortedData = new TreeMap<>();

    }


    @Override
    public @NotNull GowingPackedEntityBundle bundleThyself(
            final boolean isPackingSuper, @NotNull final GowingPacker packer
    ) {

        GowingPackedEntityBundle bundle = new GowingPackedEntityBundle(
                ENTITY_TYPE_NAME,
                VERSION,
                super.bundleRoot( packer ),
                packer.getPackingContext()
        );

        GowingPackableMapping<K,Collection<V>> elementsMapping = new GowingPackableMapping<>( _sortedData );

        bundle.addHolder( new GowingPackableEntityHolder( ELEMENTS_MAPPING_NAME, elementsMapping, packer, true ) );

        return bundle;

    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public boolean finishUnpacking( @NotNull final GowingUnPacker unPacker ) throws GowingUnpackingException {

        if ( !unPacker.isEntityFinished( _elementsMappingReference ) ) {

            return false;

        }

        GowingPackable packable = unPacker.resolveMandatoryReference( _elementsMappingReference );
        if ( ( packable instanceof GowingPackableMapping ) ) {

            // The temporary variable is required in order to make this assignment a declaration which allows
            // the @SuppressWarnings("unchecked") annotation (the annotation is not allowed on a simple assignment statement).
//            @SuppressWarnings("unchecked")
//            TreeMap<T1, TwoDimensionalSortedMap<T2, T3, V>> tmap =
//                    ( (GowingPackableMapping<T1,TwoDimensionalSortedMap<T2,T3,V>>)packable ).rebuildMap( new TreeMap<>() );
//            _map = tmap;

            @SuppressWarnings("unchecked")
            TreeMap<K, Collection<V>> tmap = ( (GowingPackableMapping<K, Collection<V>>)packable ).rebuildMap( new TreeMap<>() );
            for ( K key : tmap.keySet() ) {

                Collection<V> values = tmap.get( key );
                _sortedData.put( key, new ArrayList<>( values ) );

            }

        } else {

            GowingUtil.getGrumpy( "TreeSorter", "sorted map", GowingPackableMapping.class, packable );

        }

        return true;

    }

    @SuppressWarnings("unchecked")
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
                    _currentList = (Collection<VV>)_sortedData.get( _currentKey );

                    _innerIterator = _currentList.iterator();

                } else {

                    return false;

                }

            }

            return _innerIterator.hasNext();

        }

        public VV next() {

            return _innerIterator.next();

        }

        public void remove() {

            throw new UnsupportedOperationException( "remove not supported by this iterator" );

        }

        public String toString() {

            return "TreeSorterIterator( outerIterator=" + _outerIterator + ", currentKey=" + _currentKey + ", innerIterator=" + _innerIterator + " )";

        }

    }

    public interface ValueMatcher<V> {

        boolean doesValueMatch( V target );

    }

    /**
     Construct a new, empty tree sorter, using the natural ordering of its keys.
     All keys inserted in this tree sorter must implement the {@link Comparable} interface and must be
     <i>mutually comparable</i>({@link TreeMap#TreeMap()} for a discussion of what this means).
     */

    public TreeSorter() {

        super( new GowingNameMarkerThing() );

        _sortedData = new TreeMap<>();

    }

    /**
     Construct a new, empty tree sorter ordered according to the specified comparator.
     All keys inserted into this tree sorter must be mutually comparable by the specified comparator
     (see {@link TreeMap#TreeMap(java.util.Comparator)} for a discussion of what this means).

     @param comparator the comparator that will be used to order this tree sorter.
     */

    public TreeSorter( final Comparator<? super K> comparator ) {

        super( new GowingNameMarkerThing() );

        _sortedData = new TreeMap<>( comparator );

    }

    /**
     Constructs a new tree sorter containing the same mappings as the specified map.
     The keys will be the natural sorted order of <code>K</code>.

     @param map the map whose mappings are to be used to create the new tree sorter.
     @throws IllegalArgumentException if <code>map</code> is <code>null</code>.
     */

    public TreeSorter( final Map<K, V> map ) {

        super( new GowingNameMarkerThing() );

        _sortedData = new TreeMap<>();
        for ( K key : map.keySet() ) {

            add( key, map.get( key ) );

        }

    }

    /**
     Constructs a new tree sorter containing the same mappings as the specified sorted map.
     The new tree sorter will use the same comparator as the specified sorted map.

     @param map the map whose mappings are to be used to create the new tree sorter.
     @throws IllegalArgumentException if <code>map</code> is <code>null</code>.
     */

    public TreeSorter( final SortedMap<K, V> map ) {

        super( new GowingNameMarkerThing() );

        _sortedData = new TreeMap<>( map.comparator() );
        for ( K key : map.keySet() ) {

            add( key, map.get( key ) );

        }

    }

    /**
     Construct a new tree sorter which is a copy of an existing tree sorter.
     <p/>This method is equivalent to constructing a new tree sorter called <code>newSorter</code> using the following procedure:
     <pre>
     TreeSorter&lt;K,V&gt; newSorter = new TreeSorter&lt;K,V&gt;( sorter.comparator() );
     for ( K key : sorter.keySet() ) {

     newSorter.addAll( key, sorter.getValues( key ) );

     }
     </pre>

     @param sorter the tree sorter whose key associations are to be copied into the newly created tree sorter.
     @throws IllegalArgumentException if <code>sorter</code> is <code>null</code>.
     */

    @SuppressWarnings("CopyConstructorMissesField")
    public TreeSorter( final TreeSorter<K, V> sorter ) {

        this();

        for ( K key : sorter.keySet() ) {

            addAll( key, sorter.getValues( key ) );

        }

    }

    /**
     Construct a new tree sorter which is backed by a different tree sorter.
     <p/>This method is used internally to implement the
     {@link #headSorter}, {@link #tailSorter} and {@link #subSorter} methods.  It is not intended to be used for any
     other purpose and probably should not be exposed to the general public.

     @param map     the map which is to form the basis of this tree sorter instance.
     @param ignored an extra parameter to ensure that the signature of this constructor is different than
     that of one or more of the other publically available constructors.  This parameter is totally ignored.
     */

    private TreeSorter( final SortedMap<K, List<V>> map, final int ignored ) {

        super( new GowingNameMarkerThing() );

        _sortedData = map;

    }

    /**
     Returns a view of the portion of this tree sorter whose keys are strictly less than toKey.
     <p/>
     The returned tree sorter is backed by this tree sorter, so changes in the returned tree sorter are reflected in this
     tree sorter, and vice-versa. The returned tree sorter will throw an <code>IllegalArgumentException</code> on an attempt
     to insert a key outside its range.
     <p/>
     Analogous to {@link SortedMap#headMap(Object)}.

     @param toKey high endpoint (exclusive) of the headSorter.
     @return a view of this tree sorter whose keys are strictly less than <code>toKey</code>.
     @throws IllegalArgumentException if <code>toKey</code> is null or if this tree sorter
     itself has a restricted range and <code>toKey</code> lies outside the bounds of the range.
     */

    public TreeSorter<K, V> headSorter( final K toKey ) {

        return new TreeSorter<>( _sortedData.headMap( toKey ), 0 );

    }

    /**
     Returns a view of the portion of this tree sorter whose keys are strictly greater than or equal to fromKey.
     <p/>
     The returned tree sorter is backed by this tree sorter, so changes in the returned tree sorter are reflected in this
     tree sorter, and vice-versa. The returned tree sorter will throw an <code>IllegalArgumentException</code> on an attempt
     to insert a key outside its range.
     <p/>
     Analogous to {@link SortedMap#tailMap(Object)}.

     @param fromKey low endpoint (inclusive) of the headSorter.
     @return a view of this tree sorter whose keys are greater than or equal to <code>fromKey</code>.
     @throws IllegalArgumentException if <code>fromKey</code> is null or if this tree sorter
     itself has a restricted range and <code>fromKey</code> lies outside the bounds of the range.
     */

    public TreeSorter<K, V> tailSorter( final K fromKey ) {

        return new TreeSorter<>( _sortedData.tailMap( fromKey ), 0 );

    }

    /**
     Returns a view of this tree sorter from <code>fromKey</code>, inclusive, to <code>toKey</code>, exclusive (if <code>fromKey</code>
     and <code>toKey</code> are equal then the returned tree sorter is empty).
     <p/>
     The returned tree sorter is backed by this tree sorter, so changes in the returned tree sorter are reflected in this
     tree sorter, and vice-versa. The returned tree sorter will throw an <code>IllegalArgumentException</code> on an attempt
     to insert a key outside its range.
     <p/>
     Analogous to {@link SortedMap#subMap(Object, Object)}.

     @param fromKey low endpoint (inclusive) of the keys in the returned tree sorter.
     @param toKey   high endpoint (exclusive) of the keys in the returned tree sorter.
     @return a view of the portion of this tree sorter specified by the keys.
     @throws IllegalArgumentException if any of the following are true:
     <ul>
     <li><code>fromKey</code> is <code>null</code></li>
     <li><code>toKey</code> is <code>null</code></li>
     <li><code>fromKey</code> is greater than <code>toKey</code></li>
     <li>this tree sorter
     itself has a restricted range, and <code>fromKey</code> or <code>toKey</code> lies outside the bounds of the range.</li>
     </ul>
     */

    public TreeSorter<K, V> subSorter( final K fromKey, final K toKey ) {

        return new TreeSorter<>( _sortedData.subMap( fromKey, toKey ), 0 );

    }

    /**
     Determines if the specified key exists within this tree sorter.
     Analogous to {@link SortedMap#containsKey(Object)}.

     @param key the specified key.
     @return true if this tree sorter includes this key.
     @throws IllegalArgumentException if <code>key</code> is <code>null</code>.
     */

    public boolean containsKey( final K key ) {

        return _sortedData.containsKey( key );

    }

    /**
     Get the first key.
     <p/>Based on {@link SortedMap#firstKey()}.

     @return the lowest key in this sorter.
     @throws NoSuchElementException if the sorter is empty.
     */

    @NotNull
    public K firstKey() {

        return _sortedData.firstKey();

    }

    /**
     Get the last key.
     <p/>Based on {@link SortedMap#lastKey()}.

     @return the highest key in this sorter.
     @throws NoSuchElementException if the sorter is empty.
     */

    @NotNull
    public K lastKey() {

        return _sortedData.lastKey();

    }

    /**
     Return the values associated with a specified key.
     The values in the returned collection appear in the order that they were added to this tree sorter.
     The returned collection of values is immutable although the contents of the collection could change if
     more data is added to this tree sorter.
     <p/>This operation is always quite fast as it returns an immutable view into this tree sorter's data
     (using {@link Collections@unmodifiableCollection}) rather than a copy of this tree sorter's data.

     @param key the specified key.
     @return the values associated with the specified key or an empty collection if this tree sorter has no values
     associated with the specified key.
     The returned collection is always wrapped using {@link Collections#unmodifiableCollection}.
     @throws IllegalArgumentException if <code>key</code> is <code>null</code>.
     */

    @NotNull
    public List<V> getValues( final @NotNull K key ) {

        List<V> values = _sortedData.get( key );

        return Collections.unmodifiableList( values == null ? new ArrayList<>() : values );

    }

    @NotNull
    public V getSingleValue( final @NotNull K key ) {

        List<V> values = _sortedData.get( key );
        if ( values == null || values.isEmpty() ) {

            throw new IllegalArgumentException(
                    "TreeSorter.getSingleValue:  key " +
                    ObtuseUtil.enquoteJavaObject( key ) +
                    " does not exist in this TreeSorter"
            );

        } else if ( values.size() > 1 ) {

            throw new IllegalArgumentException(
                    "TreeSorter.getSingleValue:  key " +
                    ObtuseUtil.enquoteJavaObject( key ) +
                    " does not have a unique value (it has " + values.size() +
                    " values in this TreeSorter)"
            );

        } else {

            return values.get( 0 );

        }

    }

    @NotNull
    public Optional<V> getOptSingleValue( final @NotNull K key ) {

        List<V> values = _sortedData.get( key );
        if ( values == null || values.isEmpty() ) {

            return Optional.empty();

        } else if ( values.size() > 1 ) {

            throw new IllegalArgumentException(
                    "TreeSorter.getSingleValue:  key " +
                    ObtuseUtil.enquoteJavaObject( key ) +
                    " does not have a unique value (it has " + values.size() +
                    " values in this TreeSorter)"
            );

        } else {

            return Optional.of( values.get( 0 ) );

        }

    }

    /**
     Return the number of values associated with a specified key.
     If a particular value is associated more than once with the specified key then each occurrence is counted separately.
     For example, if this tree sorter has the same value associated twice with the same key and has no other values associated with the key then the return value of calling this method for that key will be <tt>2</tt>.
     <p/>
     This method is equivalent to but faster than
     <blockquote><tt>getValues( key ).size()</tt></blockquote>

     @param key the key of interest.
     @return the number of values associated with the specified key (0 if the key does not exist within this tree sorter).
     */

    public int countValues( final K key ) {

        Collection<V> values = _sortedData.get( key );
        return values == null ? 0 : values.size();

    }

    /**
     Return all the values in this tree sorter in key order.
     Values with unequal keys are returned in key-sorted order.
     Values with equal keys are returned in the order that they were added to this tree sorter.
     <p/>Every call to this method returns a distinct collection of values.  The caller is free to do
     whatever they like to the returned collection.
     <p/>This is potentially a rather expensive operation depending upon how much data is in this tree sorter instance.

     @return all the values in this tree sorter.
     */

    @NotNull
    public Collection<V> getAllValues() {

        Collection<V> allValues = new ArrayList<>();
        for ( K key : _sortedData.keySet() ) {

            allValues.addAll( getValues( key ) );

        }

        return allValues;

    }

    /**
     Get the index of the first occurrence of a specific target value within this tree sorter.
     The value returned by this method the final value of <tt>count</tt> yielded by the following:
     <blockquote>
     <pre>
     int index = 0;
     for ( V v : treeSorter ) {
     if ( v == targetValue ) {   // Note the use of reference equality rather than {@link Object#equals}.
     return index;
     }
     index += 1;
     }
     return -1;
     </pre>
     </blockquote>
     Note that this method returns <tt>-1</tt> if the target value does not exist within this tree sorter.
     <p/>
     The following code snippet is one way to determine if a tree sorter contains any <tt>null</tt> values:
     <blockquote>
     <pre>
     if ( treeSorter.getValueIndex( null ) == -1 ) {
     System.out.println( "tree sorter does not contain null values" );
     } else {
     System.out.println( "tree sorter contains at least one null value" );
     }
     </pre>
     </blockquote>
     <p/>
     While this method is probably faster than the about snippet might suggest, it is not exactly a speed demon.

     @param targetValue the value of interest.
     @return the index of the first occurrence of <tt>targetValue</tt> in this tree sorter or -1 if the value does not exist within this tree sorter.
     Note that the presence of target value is detected via a reference comparison (i.e. using == rather than a call to {@link Object#equals}).
     */

    public int getFullValueIndex( final V targetValue ) {

        int index = 0;
        for ( V value : this ) {

            if ( value == targetValue ) {

                return index;

            }

            index += 1;

        }

        return -1;

    }

    /**
     Get the indices of every occurrence of a specific target value within this tree sorter.
     <p/>
     This method is equivalent to {@link #getFullValueIndex(V)} with the difference that this method returns the index of each occurrence of the specified target value.
     This method returns an empty list of indices if the target value does not exist within this tree sorter.
     <p/>
     While this method is probably faster than the about snippet might suggest, it is not exactly a speed demon.

     @param targetValue the value of interest or -1 if the target value does not exist within this tree sorter.
     Note that the target value is detected via a reference comparison (i.e. using == rather than a call to {@link Object#equals}).
     */

    public List<Integer> getAllFullValueIndices( final V targetValue ) {

        List<Integer> indices = new ArrayList<>();
        int index = 0;
        for ( V value : this ) {

            if ( value == targetValue ) {

                indices.add( index );

            }

            index += 1;

        }

        return indices;

    }

    /**
     Get the index of the first occurrence of a specific target value referenced by a specific key within this tree sorter.
     <p/>
     This method is equivalent to
     <blockquote>
     <pre>
     List&lt;Integer> indices = getAllValueIndices( targetKey, targetValue );
     rval = indices.isEmpty() ? -1 : indices.get( 0 );
     </pre>
     </blockquote>
     This method returns -1 if the target value does not exist for the specified key within this tree sorter.
     <p/>
     While this method is probably faster than the about snippet might suggest, it is not exactly a speed demon.

     @param targetKey   the key of interest (must not be <tt>null</tt>).
     @param targetValue the value of interest or -1 if the target value does not exist within this tree sorter.
     Note that the target value is detected via a reference comparison (i.e. using == rather than a call to {@link Object#equals}).
     @return the index of the first value of interest. <tt>-1</tt> if no values of interest exist for the specified key.
     */

    public int getFullValueIndex( final @NotNull K targetKey, final V targetValue ) {

        return getFullValueIndex(
                targetKey,
                target -> targetValue == target
        );

    }

    /**
     Get the index of the first occurrence of a target value of interest referenced by a specific key within this tree sorter.
     <p/>
     A variation on {@link #getFullValueIndex(Comparable, Object)} that uses a matcher function to identify values of interest.

     @param targetKey the key of interest (must not be <tt>null</tt>).
     @param matcher   identifies values of interest (see {@link ValueMatcher} for more info).
     @return the index of the first value of interest. <tt>-1</tt> if no values of interest exist for the specified key.
     */

    public int getFullValueIndex( final @NotNull K targetKey, final ValueMatcher<V> matcher ) {

        int index = 0;
        for ( K key : keySet() ) {

            int comparison = key.compareTo( targetKey );
            if ( comparison >= 0 ) {

                if ( comparison == 0 ) {

                    for ( V value : getValues( targetKey ) ) {

                        if ( matcher.doesValueMatch( value ) ) {

                            return index;

                        }

                        index += 1;

                    }

                }

                return -1;

            } else {

                index += _sortedData.get( key ).size();

            }

        }

        return -1;

    }

    /**
     Get the indices of every occurrence of a specific target value referenced by a specific key within this tree sorter.
     <p/>
     This method is equivalent to {@link #getFullValueIndex(V)} with the difference that this method returns the index of each occurrence of the specified target value.
     This method returns an empty list of indices if the target value does not exist within this tree sorter.
     <p/>
     While this method is probably faster than the about snippet might suggest, it is not exactly a speed demon.

     @param targetKey   the key of interest (must not be <tt>null</tt>).
     @param targetValue the value of interest or -1 if the target value does not exist within this tree sorter.
     Note that the target value is detected via a reference comparison (i.e. using == rather than a call to {@link Object#equals}).
     */

    public List<Integer> getAllFullValueIndices( final @NotNull K targetKey, final V targetValue ) {

        List<Integer> indices = new ArrayList<>();
        int currentIndex = 0;
        for ( K key : keySet() ) {

            int comparison = key.compareTo( targetKey );
            if ( comparison >= 0 ) {

                if ( comparison == 0 ) {

                    for ( V value : getValues( targetKey ) ) {

                        if ( value == targetValue ) {

                            indices.add( currentIndex );

                        }

                        currentIndex += 1;

                    }

                }

                return indices;

            } else {

                currentIndex += _sortedData.get( key ).size();

            }

        }

        return indices;

    }

    /**
     Return a count of the number of values in this tree sorter which are associated with keys which are less than the specified key.
     <p/>
     This value also:
     <ul>
     <li>the index of the first/oldest value associated with the specified key assuming that the specified key has associated values</li>
     <li>a faster way of obtaining the value returned by <tt>{@link #headSorter}( key ).size()</tt></li>
     </ul>
     See also: {@link #headSorter}
     <p/>
     The value of <tt>countValuesBeforeKey( key ) + getValues( key ).size()</tt> is the index at which an about to be added value for this key would first appear.

     @param targetKey the key of interest.
     @return the number of values in this tree sorter which are associated with keys which are less than <tt>targetKey</tt>.
     */

    public int countValuesBeforeKey( final K targetKey ) {

        int currentIndex = 0;
        for ( K key : keySet() ) {

            int comparison = key.compareTo( targetKey );
            if ( comparison >= 0 ) {

                // If the key exists then index is the index of its first/oldest value.
                // If the key doesn't exist then index is the value of what its first/oldest value would be if the key did exist.

                return currentIndex;

            } else {

                currentIndex += _sortedData.get( key ).size();

            }

        }

        return currentIndex;

    }

    /**
     Add a new key-value pair to this tree sorter.
     Each tree sorter instance is capable of maintaining an arbitrary number of one to many key to value association.
     For example, if the key-value pair <code>fred=hello</code> and <code>fred=world</code> are added to a previously
     empty tree sorter then the key <code>fred</code> will have both <code>hello</code> and <code>world</code> associated
     with it in the tree sorter.
     <p/>
     Adding the same key to value association to a tree sorter multiple times results in the value appearing multiple times
     when the tree sorter is 'scanned'.
     For example, if the key-value pair <code>fred=hello</code> is added to a tree sorter three
     times then the collection returned by <code>getValues( "fred" )</code> will contain <code>hello</code> three times.
     Analogous to {@link SortedMap#put(Object, Object)}.

     @param key   with which the specified value is to be associated.
     @param value the value to be associated with the specified key.
     @throws IllegalArgumentException if <code>key</code> is <code>null</code> (note that <code>value</code> is allowed to be <code>null</code>).
     */

    public final void add( final @NotNull K key, @Nullable final V value ) {

        Collection<V> values;
        //noinspection UnusedAssignment
        ( values = _sortedData.computeIfAbsent( key, k -> new ArrayList<>() ) ).add( value );

    }

    /**
     Add all of the key value associations from a {@link Map} to this tree sorter.
     This method is exactly equivalent to
     <pre>
     for ( K key : map.keySet() ) {
     treeSorter.add( key, map.get( key ) );
     }
     </pre>

     @param map the map whose contents are to be added to this tree sorter.
     @throws IllegalArgumentException if <code>map</code> is <code>null</code>.
     */

    public void addAll( final @NotNull Map<? extends K, ? extends V> map ) {

        for ( K key : map.keySet() ) {

            add( key, map.get( key ) );

        }

    }

    /**
     Add all of the associations from a different tree sorter to this tree sorter.
     This method is exactly equivalent to
     <pre>
     for ( K key : sorter.keySet() ) {
     treeSorter.addAll( key, sorter.getValues( key ) );
     }
     </pre>

     @param sorter the tree sorter whose contents are to be added to this tree sorter.
     @throws IllegalArgumentException if <code>sorter</code> is <code>null</code> or
     if an attempt is made to add the contents of a tree sorter to itself.
     */

    public void addAll( final @NotNull TreeSorter<K, V> sorter ) {

        if ( this == sorter ) {

            throw new IllegalArgumentException( "attempt to add a tree sorter to itself" );

        }

        for ( K key : sorter.keySet() ) {

            addAll( key, sorter.getValues( key ) );

        }

    }

    /**
     Associate all of the values in a collection with a specified key.
     This method is exactly equivalent to
     <pre>
     for ( V value : values ) {
     treeSorter.add( key, value );
     }
     </pre>
     Truly disconcerting things will probably happen if the following is attempted:
     <pre>
     treeSorter.addAll( key, treeSorter.getAll( key ) )
     </pre>

     @param key    the key that all of the values in the specfied collection are to be associated with.
     @param values the values which are to be associated with the specified key.
     @throws IllegalArgumentException if <code>key</code> is <code>null</code> or <code>values</code> is <code>null</code>.
     */

    public void addAll( final @NotNull K key, final @NotNull Collection<V> values ) {

        for ( V value : values ) {

            add( key, value );

        }

    }

    /**
     Returns a Set view of the keys contained in this TreeSorter instance.
     The set's iterator will return the keys in ascending order.
     The map is backed by this TreeSorter instance, so changes to
     this map are reflected in the Set, and vice-versa. The Set
     supports element removal, which removes the corresponding
     mapping from the map, via the <code>Iterator.remove</code>, <code>Set.remove</code>,
     <code>Set.removeAll</code>, <code>Set.retainAll</code>, and <code>Set.clear</code> operations
     It does not support the <code>Set.add</code> or <code>Set.addAll</code> operations.

     @return a set view of the keys in this TreeSorter.
     */

    @NotNull
    public Set<K> keySet() {

        return _sortedData.keySet();

    }

    /**
     Cleanup dead keys.
     Gets rid of any keys which no longer have any values.
     <p/>There's got to be a way to do this safely on the fly but this will have to do for now.

     @return the number of dead keys removed from the tree sorter.
     */

    public int cleanupDeadKeys() {

        int count = 0;
        for ( Iterator<K> iterator = keySet().iterator(); iterator.hasNext(); ) {

            K key = iterator.next();

            if ( _sortedData.containsKey( key ) ) {

                Collection<V> values = _sortedData.get( key );
                if ( values == null ) {

                    Logger.logMsg( "cleaning up key \"" + key + "\" which has NO list" );
                    Logger.logMsg( "is this even possible?" );

                    iterator.remove();

                    count += 1;

                } else if ( values.isEmpty() ) {

                    Logger.logMsg( "cleaning up key \"" + key + "\" which has an empty list" );

                    iterator.remove();

                    count += 1;

                }

            }

        }

        return count;

    }

    /**
     Removes all of the values associated with a specified key.

     @param key the key for which all associated values are to be removed.
     @return a collection of the values which were removed. The caller is free to do whatever they wish with the
     returned collection. An empty collection is returned if there were no values associated with the specified key.
     @throws IllegalArgumentException if <code>key</code> is <code>null</code>.
     */

    @NotNull
    public Collection<V> removeKeyAndValues( final @NotNull K key ) {

        Collection<V> c = _sortedData.remove( key );

        @SuppressWarnings("UnnecessaryLocalVariable")
        ArrayList<V> rval =
                c == null
                        ?
                        new ArrayList<>()
                        :
                        (
                                c instanceof ArrayList
                                        ?
                                        (ArrayList<V>)c
                                        :
                                        new ArrayList<>( c )
                        );

        return rval;

    }

    @NotNull
    public Collection<V> removeValue( final @NotNull K key, @Nullable final V value ) {

        return removeValue( key, target -> value == target, new ArrayList<>() );

    }

    /**
     Remove all occurrences of a value from this tree sorter.

     @param matcher       identifies the values to be removed.
     @param deletedValues a collection into which any deleted values are placed.
     The specified value is added to this collection once each time it is found and removed from the sorter.
     @return The collection specified by the <code>deletedValues</code> parameter.
     @throws IllegalArgumentException if <code>deletedValues</code> is null.
     */

    @NotNull
    public Collection<V> removeValue( final @NotNull ValueMatcher<V> matcher, final @NotNull Collection<V> deletedValues ) {

        for ( Iterator<K> iterator = _sortedData.keySet().iterator(); iterator.hasNext(); ) {

            K key = iterator.next();

            removeValueCarefully( key, matcher, deletedValues, false );
            if ( _sortedData.get( key ).isEmpty() ) {

                iterator.remove();

            }

        }

        return deletedValues;

    }

    @NotNull
    public Collection<V> removeValue( final @NotNull K key, final @NotNull ValueMatcher<V> matcher ) {

        return removeValueCarefully( key, matcher, new ArrayList<>(), true );

    }

    /**
     Remove all occurrences of a value which is (are?) associated with a specified key.

     @param key           the key that references the values to be removed.
     @param matcher       identifies the values of interest. Only values which are in the 'row' that is referenced by the specified key are removed.
     @param deletedValues a collection into which any deleted values are placed.
     The specified value is added to this collection once each time it is found and removed from the sorter at the specified key.
     @return The collection specified by the <code>deletedValues</code> parameter.
     @throws IllegalArgumentException if <code>deletedValues</code> is null.
     */

    @NotNull
    public Collection<V> removeValue( final @NotNull K key, final @NotNull ValueMatcher<V> matcher, final @NotNull Collection<V> deletedValues ) {

        return removeValueCarefully( key, matcher, deletedValues, true );

    }

    Collection<V> removeValueCarefully( final @NotNull K key, final @NotNull ValueMatcher<V> matcher, final @NotNull Collection<V> deletedValues, final boolean removeKeys ) {

        Collection<V> valuesAtKey = _sortedData.get( key );
        if ( valuesAtKey != null ) {

            for ( Iterator<V> iterator = valuesAtKey.iterator(); iterator.hasNext(); ) {

                V aValue = iterator.next();

                if ( matcher.doesValueMatch( aValue ) ) {

                    deletedValues.add( aValue );
                    iterator.remove();

                }

            }

            if ( removeKeys && _sortedData.get( key ).isEmpty() ) {

                _sortedData.remove( key );

            }

        }

        return deletedValues;

    }

    /**
     Get an iterator which iterates across all of the values.
     Changes to the tree sorter while the iterator is in use could invalidate the iterator resulting in all sorts of
     strange things happening.

     @return an iterator which iterates across all of the values in this tree sorter.
     */

    @NotNull
    public Iterator<V> iterator() {

        @SuppressWarnings({ "UnnecessaryLocalVariable" })
        Iterator<V> iter = new TreeSorterIterator<>();

        return iter;

    }

    /**
     Returns the number of values in this tree sorter.
     <p/>This method could be fairly expensive if there are a lot of values in this tree sorter.
     <p/>This method is equivalent to:
     <pre>
     int totalSize = 0;
     for ( K key : treeSorter.keySet() ) {
     totalSize += treeSorter.getValues().size();
     }
     return totalSize;
     </pre>

     @return the number of values in this tree sorter.
     */

    public int size() {

        int totalSize = 0;
        for ( Collection<V> subList : _sortedData.values() ) {

            totalSize += subList.size();

        }

        return totalSize;

    }

    /**
     Empty this tree sorter.
     */

    public void clear() {

        _sortedData.clear();

    }

    /**
     Determine if this tree sorter has any key value associations in it.
     <p/>This method is always very fast.

     @return true if this tree sorter is empty; false otherwise.
     */

    public boolean isEmpty() {

        return _sortedData.isEmpty();

    }

    /**
     Determine if a specified key has exactly one value associated with it in this tree sorter.
     @return true if the key has exactly one value associated with it; false otherwise.
     */

    public boolean hasSingleValue( K key ) {

        Collection<V> values = _sortedData.get( key );

        return values != null && values.size() == 1;

    }

    public String toString() {

        return "TreeSorter( size = " + size() + ", contents = " + _sortedData + " )";

    }

    public static void main( final String[] args ) {

        TreeSorter<Integer, String> sorter = new TreeSorter<>();

        sorter.add( 1, "one" );
        sorter.add( 2, "two" );
        sorter.add( 4, "four" );
        sorter.add( 1, "one" );
        sorter.add( 1, "I" );
        sorter.add( 1, "one" );

        for ( int key : sorter.keySet() ) {

            for ( String v : sorter.getValues( key ) ) {

                System.out.println( "" + key + " -> " + v );

            }

        }
        System.out.println();

        for ( String v : sorter ) {

            //noinspection UseOfSystemOutOrSystemErr
            System.out.println( v );

        }
        System.out.println();

        String[] targetValues = { "one", "two", "I", "missing", null };
        for ( String targetValue : targetValues ) {

            int ix = sorter.getFullValueIndex( targetValue );
            if ( ix < 0 ) {

                System.out.println( "\"" + targetValue + "\" not found within sorter" );

            } else {

                System.out.println( "\"" + targetValue + "\" found at index " + ix );

            }

        }
        System.out.println();

        for ( int i = sorter.firstKey().intValue() - 1; i <= sorter.lastKey().intValue() + 1; i += 1 ) {

            System.out.println( "count of values before key " + i + " is " + sorter.countValuesBeforeKey( i ) );

        }
        System.out.println();

        for ( String targetValue : targetValues ) {

            List<Integer> indices = sorter.getAllFullValueIndices( targetValue );
            System.out.println( "\"" + targetValue + "\" found at " + indices );

        }

    }

}
