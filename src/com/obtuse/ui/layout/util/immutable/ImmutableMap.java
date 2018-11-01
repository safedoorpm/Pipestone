/*
 * Copyright (c) 1997, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.obtuse.ui.layout.util.immutable;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 %%% Something clever goes here.
 */

public class ImmutableMap<K, V> implements Map<K, V>, Serializable {

    private static final long serialVersionUID = -1034234728574286014L;

    private final Map<? extends K, ? extends V> m;

    ImmutableMap( final Map<? extends K, ? extends V> m ) {

        if ( m == null ) {
            throw new NullPointerException();
        }
        this.m = m;
    }

    public int size() {

        return m.size();
    }

    public boolean isEmpty() {

        return m.isEmpty();
    }

    public boolean containsKey( final Object key ) {

        return m.containsKey( key );
    }

    public boolean containsValue( final Object val ) {

        return m.containsValue( val );
    }

    public V get( final Object key ) {

        return m.get( key );
    }

    public V put( final K key, final V value ) {

        throw new UnsupportedOperationException();
    }

    public V remove( final Object key ) {

        throw new UnsupportedOperationException();
    }

    public void putAll( @NotNull final Map<? extends K, ? extends V> m ) {

        throw new UnsupportedOperationException();
    }

    public void clear() {

        throw new UnsupportedOperationException();
    }

    private transient Set<K> keySet;
    private transient Set<Map.Entry<K, V>> entrySet;
    private transient Collection<V> values;

    @NotNull
    public Set<K> keySet() {

        if ( keySet == null ) {
            keySet = Collections.unmodifiableSet( m.keySet() );
        }
        return keySet;
    }

    @NotNull
    public Set<Map.Entry<K, V>> entrySet() {

        if ( entrySet == null ) {
            entrySet = new ImmutableMap.UnmodifiableEntrySet<>( m.entrySet() );
        }
        return entrySet;
    }

    @NotNull
    public Collection<V> values() {

        if ( values == null ) {
            values = Collections.unmodifiableCollection( m.values() );
        }
        return values;
    }

    /**
     Returns true if the specified arguments are equal, or both null.
     <p>
     NB: Do not replace with Object.equals until JDK-8015417 is resolved.
     */
    static boolean eq( final Object o1, final Object o2 ) {

        return o1 == null ? o2 == null : o1.equals( o2 );
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals( final Object o ) {

        return o == this || m.equals( o );
    }

    public int hashCode() {

        return m.hashCode();
    }

    public String toString() {

        return m.toString();
    }

    // Override default methods in Map
    @Override
    @SuppressWarnings("unchecked")
    public V getOrDefault( final Object k, final V defaultValue ) {
        // Safe cast as we don't change the value
        return ( (Map<K, V>)m ).getOrDefault( k, defaultValue );
    }

    @Override
    public void forEach( final BiConsumer<? super K, ? super V> action ) {

        m.forEach( action );
    }

    @Override
    public void replaceAll( final BiFunction<? super K, ? super V, ? extends V> function ) {

        throw new UnsupportedOperationException();
    }

    @Override
    public V putIfAbsent( final K key, final V value ) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove( final Object key, final Object value ) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace( final K key, final V oldValue, final V newValue ) {

        throw new UnsupportedOperationException();
    }

    @Override
    public V replace( final K key, final V value ) {

        throw new UnsupportedOperationException();
    }

    @Override
    public V computeIfAbsent( final K key, final Function<? super K, ? extends V> mappingFunction ) {

        throw new UnsupportedOperationException();
    }

    @Override
    public V computeIfPresent(
            final K key,
            final BiFunction<? super K, ? super V, ? extends V> remappingFunction
    ) {

        throw new UnsupportedOperationException();
    }

    @Override
    public V compute(
            final K key,
            final BiFunction<? super K, ? super V, ? extends V> remappingFunction
    ) {

        throw new UnsupportedOperationException();
    }

    @Override
    public V merge(
            final K key, final V value,
            final BiFunction<? super V, ? super V, ? extends V> remappingFunction
    ) {

        throw new UnsupportedOperationException();
    }

    /**
     We need this class in addition to ImmutableSet as
     Map.Entries themselves permit modification of the backing Map
     via their setValue operation.  This class is subtle: there are
     many possible attacks that must be thwarted.

     @serial include
     */
    static class UnmodifiableEntrySet<K, V>
            extends ImmutableSet<Entry<K, V>> {

        private static final long serialVersionUID = 7854390611657943733L;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        UnmodifiableEntrySet( final Set<? extends Map.Entry<? extends K, ? extends V>> s ) {
            // Need to cast to raw in order to work around a limitation in the type system
            super( (Set)s );
        }

        static <K, V> Consumer<Entry<K, V>> entryConsumer( final Consumer<? super Entry<K, V>> action ) {

            return e -> action.accept( new ImmutableMap.UnmodifiableEntrySet.UnmodifiableEntry<>( e ) );
        }

        public void forEach( final Consumer<? super Entry<K, V>> action ) {

            Objects.requireNonNull( action );
            c.forEach( entryConsumer( action ) );
        }

        static final class UnmodifiableEntrySetSpliterator<K, V>
                implements Spliterator<Entry<K, V>> {

            final Spliterator<Map.Entry<K, V>> s;

            UnmodifiableEntrySetSpliterator( final Spliterator<Entry<K, V>> s ) {

                this.s = s;
            }

            @Override
            public boolean tryAdvance( final Consumer<? super Entry<K, V>> action ) {

                Objects.requireNonNull( action );
                return s.tryAdvance( entryConsumer( action ) );
            }

            @Override
            public void forEachRemaining( final Consumer<? super Entry<K, V>> action ) {

                Objects.requireNonNull( action );
                s.forEachRemaining( entryConsumer( action ) );
            }

            @Override
            public Spliterator<Entry<K, V>> trySplit() {

                Spliterator<Entry<K, V>> split = s.trySplit();
                return split == null
                        ? null
                        : new ImmutableMap.UnmodifiableEntrySet.UnmodifiableEntrySetSpliterator<>( split );
            }

            @Override
            public long estimateSize() {

                return s.estimateSize();
            }

            @Override
            public long getExactSizeIfKnown() {

                return s.getExactSizeIfKnown();
            }

            @Override
            public int characteristics() {

                return s.characteristics();
            }

            @Override
            public boolean hasCharacteristics( final int characteristics ) {

                return s.hasCharacteristics( characteristics );
            }

            @Override
            public Comparator<? super Entry<K, V>> getComparator() {

                return s.getComparator();
            }
        }

        @SuppressWarnings("unchecked")
        public Spliterator<Entry<K, V>> spliterator() {

            return new ImmutableMap.UnmodifiableEntrySet.UnmodifiableEntrySetSpliterator<>(
                    (Spliterator<Map.Entry<K, V>>)c.spliterator() );
        }

        @Override
        public Stream<Entry<K, V>> stream() {

            return StreamSupport.stream( spliterator(), false );
        }

        @Override
        public Stream<Entry<K, V>> parallelStream() {

            return StreamSupport.stream( spliterator(), true );
        }

        @NotNull
        public Iterator<Map.Entry<K, V>> iterator() {

            return new Iterator<>() {
                private final Iterator<? extends Map.Entry<? extends K, ? extends V>> i = c.iterator();

                public boolean hasNext() {

                    return i.hasNext();
                }

                public Map.Entry<K, V> next() {

                    return new ImmutableMap.UnmodifiableEntrySet.UnmodifiableEntry<>( i.next() );
                }

                public void remove() {

                    throw new UnsupportedOperationException();
                }
            };
        }

        @NotNull
        @SuppressWarnings("unchecked")
        public Object[] toArray() {

            Object[] a = c.toArray();
            for ( int i = 0; i < a.length; i++ ) {
                a[i] = new ImmutableMap.UnmodifiableEntrySet.UnmodifiableEntry<>( (Map.Entry<? extends K, ? extends V>)a[i] );
            }
            return a;
        }

        @NotNull
        @SuppressWarnings("unchecked")
        public <T> T[] toArray( @NotNull final T[] a ) {
            // We don't pass a to c.toArray, to avoid window of
            // vulnerability wherein an unscrupulous multithreaded client
            // could get his hands on raw (unwrapped) Entries from c.
            Object[] arr = c.toArray( a.length == 0 ? a : Arrays.copyOf( a, 0 ) );

            for ( int i = 0; i < arr.length; i++ ) {
                arr[i] = new ImmutableMap.UnmodifiableEntrySet.UnmodifiableEntry<>( (Map.Entry<? extends K, ? extends V>)arr[i] );
            }

            if ( arr.length > a.length ) {
                return (T[])arr;
            }

            System.arraycopy( arr, 0, a, 0, arr.length );
            if ( a.length > arr.length ) {
                a[arr.length] = null;
            }
            return a;
        }

        /**
         This method is overridden to protect the backing set against
         an object with a nefarious equals function that senses
         that the equality-candidate is Map.Entry and calls its
         setValue method.
         */
        public boolean contains( final Object o ) {

            if ( !( o instanceof Map.Entry ) ) {
                return false;
            }
            return c.contains(
                    new ImmutableMap.UnmodifiableEntrySet.UnmodifiableEntry<>( (Map.Entry<?, ?>)o ) );
        }

        /**
         The next two methods are overridden to protect against
         an unscrupulous List whose contains(Object o) method senses
         when o is a Map.Entry, and calls o.setValue.
         */
        public boolean containsAll( @NotNull final Collection<?> coll ) {

            for ( Object e : coll ) {
                if ( !contains( e ) ) // Invokes safe contains() above
                {
                    return false;
                }
            }
            return true;
        }

        public boolean equals( final Object o ) {

            if ( o == this ) {
                return true;
            }

            if ( !( o instanceof Set ) ) {
                return false;
            }
            Set<?> s = (Set<?>)o;
            if ( s.size() != c.size() ) {
                return false;
            }
            return containsAll( s ); // Invokes safe containsAll() above
        }

        /**
         This "wrapper class" serves two purposes: it prevents
         the client from modifying the backing Map, by short-circuiting
         the setValue method, and it protects the backing Map against
         an ill-behaved Map.Entry that attempts to modify another
         Map Entry when asked to perform an equality check.
         */
        private static class UnmodifiableEntry<K, V> implements Map.Entry<K, V> {

            private final Map.Entry<? extends K, ? extends V> e;

            UnmodifiableEntry( final Map.Entry<? extends K, ? extends V> e ) {

                this.e = Objects.requireNonNull( e );
            }

            public K getKey() {

                return e.getKey();
            }

            public V getValue() {

                return e.getValue();
            }

            public V setValue( final V value ) {

                throw new UnsupportedOperationException();
            }

            public int hashCode() {

                return e.hashCode();
            }

            public boolean equals( final Object o ) {

                if ( this == o ) {
                    return true;
                }
                if ( !( o instanceof Map.Entry ) ) {
                    return false;
                }
                Map.Entry<?, ?> t = (Map.Entry<?, ?>)o;
                return eq( e.getKey(), t.getKey() ) &&
                       eq( e.getValue(), t.getValue() );
            }

            public String toString() {

                return e.toString();
            }
        }
    }
}