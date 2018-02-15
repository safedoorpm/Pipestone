/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 Turn an ordered array or {@link Collection}{@code <T>} of values into an ordered collection of wrapped values where
 each wrapper knows the zero-origin index position on the original list.
 */

@SuppressWarnings("unused")
public class Ix<T> implements IxInterface<T> {

    /**
     A factory that creates Ix instances.
     */

    public interface IxFactory<U> {


        IxInterface<U> createInstance( final int ix, final @NotNull U item );

    }

    /**
     An example IxFactory that creates wrapped String instances.
     */

    public static IxFactory<String> IxStringFactory = ( ix, item ) -> new Ix<>( ix, item );

    /**
     A factory that creates IxC instances.
     */

    public interface CxFactory<U> {

        IxCInterface<U> createInstance( final int ix, final @NotNull U item );

    }

    /**
     An example CxFactory that creates wrapped String instances.
     */

    public static IxFactory<String> CxStringFactory = ( ix, item ) -> new IxC<>( ix, item );

    /**
     The index of this item in the original array or {@link Collection}{@code <T>}.
     */

    public final int ix;

    /**
     The item at index {@code ix} in the original array or {@link Collection}{@code <T>}.
     <p>Will be {@code null} if the corresponding item in the original array or {@link Collection}{@code <T>} was {@code null}.</p>
     */

    public final T item;

    /**
     Create a wrapper containing the position of an item in some original array or {@link Collection}{@code <T>} and the item itself.
     @param ix the index of this item in the original array or {@link Collection}{@code <T>}.
     @param item the item at index {@code ix} in the original array or {@link Collection<T>}.
     */

    public Ix( final int ix, final T item ) {

        super();

        this.ix = ix;
        this.item = item;

    }

    @Override
    public int ix() {

        return this.ix;

    }

    @Override
    public T item() {

        return this.item;

    }

    /**
     Wrap an array of items into an {@code ArrayList<IxInterface<W>>}.
     @param items the array of items (any of which might be {@code null}).
     @param factory a factory that creates the wrappers for each item in {@code items}.
     @return an {@code ArrayList<IxInterface<W>>} of the wrapped items from the original array in the same order that they appeared in the original array.
     */

    public static <W> ArrayList<? extends IxInterface<W>> arrayList( final @NotNull W@NotNull[] items, final @NotNull IxFactory<W> factory ) {

        ArrayList<IxInterface<W>> rval = new ArrayList<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( factory.createInstance( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap an array of items into an {@code ArrayList<Ix<W>>}.
     @param items the array of items (any of which might be {@code null}).
     @return an {@code ArrayList<Ix<W>>} of the wrapped items from the original array in the same order that they appeared in the original array.
     */

    public static <W> ArrayList<Ix<W>> arrayList( final @NotNull W@NotNull[] items ) {

        ArrayList<Ix<W>> rval = new ArrayList<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( new Ix<>( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap a {@link Collection<W>} of items into an {@code ArrayList<IxInterface<W>>}.
     @param items the collection of items (any of which might be {@code null}).
     @param factory a factory that creates the wrappers for each item in {@code items}.
     @return an {@code ArrayList<IxInterface<W>>} of the wrapped items from the original collection in the same order that they appeared in the original collection.
     */

    public static <W> ArrayList<? extends IxInterface<W>> arrayList( final @NotNull Collection<W> items, final @NotNull IxFactory<W> factory ) {

        ArrayList<IxInterface<W>> rval = new ArrayList<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( factory.createInstance( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap a {@link Collection<W>} of items into an {@code ArrayList<Ix<W>>}.
     @param items the collection of items (any of which might be {@code null}).
     @return an {@code ArrayList<Ix<W>>} of the wrapped items from the original collection in the same order that they appeared in the original collection.
     */

    public static <W> ArrayList<Ix<W>> arrayList( final @NotNull Collection<W> items ) {

        ArrayList<Ix<W>> rval = new ArrayList<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( new Ix<>( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap an array of items into a {@code LinkedList<IxInterface<W>>}.
     @param items the array of items (any of which might be {@code null}).
     @param factory a factory that creates the wrappers for each item in {@code items}.
     @return an {@code LinkedList<IxInterface<W>>} of the wrapped items from the original array in the same order that they appeared in the original array.
     */

    public static <W> LinkedList<? extends IxInterface<W>> linkedList( final @NotNull W@NotNull[] items, final @NotNull IxFactory<W> factory ) {

        LinkedList<IxInterface<W>> rval = new LinkedList<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( factory.createInstance( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap an array of items into a {@code LinkedList<Ix<W>>}.
     @param items the array of items (any of which might be {@code null}).
     @return an {@code LinkedList<Ix<W>>} of the wrapped items from the original array in the same order that they appeared in the original array.
     */

    public static <W> LinkedList<Ix<W>> linkedList( final @NotNull W@NotNull[] items ) {

        LinkedList<Ix<W>> rval = new LinkedList<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( new Ix<>( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap a {@link Collection<W>} of items into a {@code LinkedList<IxInterface<W>>}.
     @param items the collection of items (any of which might be {@code null}).
     @param factory a factory that creates the wrappers for each item in {@code items}.
     @return an {@code ArrayList<IxInterface<W>>} of the wrapped items from the original collection in the same order that they appeared in the original collection.
     */

    public static <W> LinkedList<? extends IxInterface<W>> linkedList( final @NotNull Collection<W> items, final @NotNull IxFactory<W> factory ) {

        LinkedList<IxInterface<W>> rval = new LinkedList<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( factory.createInstance( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap a {@link Collection<W>} of items into a {@code LinkedList<Ix<W>>}.
     @param items the collection of items (any of which might be {@code null}).
     @return an {@code ArrayList<Ix<W>>} of the wrapped items from the original collection in the same order that they appeared in the original collection.
     */

    public static <W> LinkedList<Ix<W>> linkedList( final @NotNull Collection<W> items ) {

        LinkedList<Ix<W>> rval = new LinkedList<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( new Ix<>( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap an array of items into a {@code TreeSet<IxC<W>>}.
     @param items the array of items (any of which might be {@code null}).
     @param factory a factory that creates the wrappers for each item in {@code items}.
     @return an {@code TreeSet<IxC<W>>} of the wrapped items from the original array in the order that the
     {@link Comparable#compareTo(Object)} method associated with the {@code W} class implements.
     <p>For example, if the array passed to this method is an {@link Integer}[] then the elements in the returned
     sorted set will be in the order implied by {@link Integer#compareTo(Integer)} (i.e. the usual ascending order
     for integers).</p>
     <p>Similarly, if the array passed to this method is an {@code Xyz[]} array then the {@code Xyz} class must
     implement the {@code Comparable<Xyz>} interface.</p>
     <p>Also note that the {@code TreeSet} instance returned by this method will be
     populated with instances of the {@link IxC}{@code <W>} class.
     The {@link IxC} class is a derivative of this {@link Ix}{@code }{@code <T>} class which provides
     a {@code compareTo()} method that compares that compares
     IxC instances by comparing their {@code item} fields.</p>
     <p>That's all getting a bit technical. Here's a slightly short version:
     <ul>
     <li>this method requires an array who's elements implement the {@link Comparable} interface.</li>
     <li>entire array <b>MUST</b> be instances of a class which provides a {@code compareTo()} method
     which is capable of comparing that element to any
     other element in the array (i.e. the {@code compareTo()} methods associated with each element must
     collectively implement a well defined ordering.</li>
     <li>life is likely to be MUCH simpler if each element of the array provides the exactly same
     {@code compareTo()} method implementation.</li>
     <li>there must be <b><u>NO</u></b> {@code null} elements in the array
     (you <b>WILL</b> get a {@link NullPointerException} thrown at you if there is).</li>
     <li>what you get back from this method will behave like a {@code TreeSet} containing {@link Ix}
     instances (the fact that they are actually
     {@link IxC} instances is really only apparent when their implementation of the
     {@link Comparable}{@code <T>} interface matters (i.e. when they are being sorted
     by classes like {@link TreeSet})).</li>
     <li>You cannot pass an array of primitive types to this method.
     For example, if you try to pass an {@code int[]} array to this method then the Java 8 compiler will give you
     a rather cryptic error message about not being able to find a suitable method for
     {@code sortedSet(int[])}.
     </li>
     </ol>
     </p>
     */

    public static <W extends Comparable<W>> TreeSet<? extends IxCInterface<W>> sortedSet( final @NotNull W@NotNull[] items, final @NotNull CxFactory<W> factory ) {

        TreeSet<IxCInterface<W>> rval = new TreeSet<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( factory.createInstance( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap an array of items into a {@code TreeSet<IxC<W>>}.
     <p>See {@link #sortedSet(Comparable[], CxFactory)} for more info.</p>
     @param items the array of items (any of which might be {@code null}).
     @return an {@code TreeSet<IxC<W>>} of the wrapped items from the original array in the order that the
     {@link Comparable#compareTo(Object)} method associated with the {@code W} class implements.
     */

     public static <W extends Comparable<W>> TreeSet<IxC<W>> sortedSet( final @NotNull W@NotNull[] items ) {

        TreeSet<IxC<W>> rval = new TreeSet<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( new IxC<>( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap a collection of items into a {@code TreeSet<IxC<W>>}.
     @param items the collection of items (any of which might be {@code null}).
     @param factory a factory that creates the wrappers for each item in {@code items}.
     @return an {@code TreeSet<IxInterface<W>>} of the wrapped items from the original collection in the order that the
     {@link Comparable#compareTo(Object)} associated with the {@code W} class implements.
     <p>For example, if the collection passed to this method contains {@link Integer} instances then the elements
     in the returned sorted set will be in the order implied by {@link Integer#compareTo(Integer)}
     (i.e. the usual ascending order for integers).</p>
     <p>Similarly, if the collection passed to this method contains {@code Xyz[]} instances then the {@code Xyz} class must
     implement the {@code Comparable<Xyz>} interface.</p>
     <p>Also note that the {@code TreeSet} instance returned by this method will be
     populated with instances of the {@link IxC}{@code <W>} class.
     This class is a derivative of this {@link Ix}{@code <T>} class which provides
     a {@code compareTo()} method that compares that compares
     IxC instances by comparing their {@code item} fields.</p>
     <p>That's all getting a bit technical. Here's a slightly short version:
     <ul>
     <li>this method requires a collection who's elements implement the {@link Comparable} interface.</li>
     <li>every element in the collection <b>MUST</b> be instances of a class which provides a {@code compareTo()} method
     which is capable of comparing that element to any
     other element in the collection (i.e. the {@code compareTo()} methods associated with each element must
     collectively implement a well defined ordering.</li>
     <li>life is likely to be MUCH simpler if each element in the collection provides the exactly same
     {@code compareTo()} method implementation.</li>
     <li>there must be <b><u>NO</u></b> {@code null} elements in the collection
     (you <b>WILL</b> get a {@link NullPointerException} thrown at you if there is).</li>
     <li>what you get back from this method will behave like a {@code TreeSet} containing {@link Ix}
     instances (the fact that they are actually
     {@link IxC} instances is really only apparent when their implementation of the
     {@link Comparable}{@code <T>} interface matters (i.e. when they are being sorted
     by classes like {@link TreeSet})).</li>
     <li>Since it is impossible to create a collection of primitive types, I don't have to tell you that you
     cannot pass a collection containing primitive types to this method (you can pass a collection of
     the various primitive type wrapper classes like {@link Integer} although be careful about the rule stated above
     that the {@code compareTo()} methods of all elements in the collection must collectively implement
     a well defined ordering).
     </li>
     </ol>
     </p>
     */

    public static <W extends Comparable<W>> TreeSet<? extends IxCInterface<W>> sortedSet( final @NotNull Collection<W> items, final @NotNull CxFactory<W> factory ) {

        TreeSet<IxCInterface<W>> rval = new TreeSet<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( factory.createInstance( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap a collection of items into a {@code TreeSet<IxC<W>>}.
     <p>See {@link #sortedSet(Collection, CxFactory)} for more info.</p>
     @param items the collection of items (any of which might be {@code null}).
     @return an {@code TreeSet<IxInterface<W>>} of the wrapped items from the original collection in the order that the
     {@link Comparable#compareTo(Object)} associated with the {@code W} class implements.
     */

     public static <W extends Comparable<W>> TreeSet<IxC<W>> sortedSet( final @NotNull Collection<W> items ) {

        TreeSet<IxC<W>> rval = new TreeSet<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( new IxC<>( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap an array of items into a {@code HashSet<IxInterface<W>>}.
     @param items the array of items (none of which can be {@code null} unless you really like {@link NullPointerException}s being thrown at you).
     @param factory a factory that creates the wrappers for each item in {@code items}.
     @return a {@code HashSet<IxInterface<W>>} of the wrapped items from the original array.
     Note that the hashing is performed using the {@link Object#hashCode()} and {@link Object#equals(Object)} methods implemented by
     the {@code W} class (i.e. the class of each of the elements that fill the array).
     @throws NullPointerException if any of the elements in the array are {@code null}.
     */

    public static <W> HashSet<? extends IxInterface<W>> hashSet( final @NotNull W@NotNull[] items, final @NotNull IxFactory<W> factory ) {

        HashSet<IxInterface<W>> rval = new HashSet<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( factory.createInstance( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap an array of items into a {@code HashSet<IxC<W>>}.
     @param items the array of items (none of which can be {@code null} unless you really like {@link NullPointerException}s being thrown at you).
     @return a {@code HashSet<IxC<W>>} of the wrapped items from the original array.
     Note that the hashing is performed using the {@link Object#hashCode()} and {@link Object#equals(Object)} methods implemented by
     the {@code W} class (i.e. the class of each of the elements that fill the array).
     @throws NullPointerException if any of the elements in the array are {@code null}.
     */

    public static <W> HashSet<Ix<W>> hashSet( final @NotNull W@NotNull[] items ) {

        HashSet<Ix<W>> rval = new HashSet<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( new Ix<>( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap a collection of items into a {@code HashSet<IxInterface<W>>}.
     @param items the collection of items (none of which can be {@code null} unless you really like {@link NullPointerException}s being thrown at you).
     @param factory a factory that creates the wrappers for each item in {@code items}.
     @return a {@code HashSet<IxInterface<W>>} of the wrapped items from the original collection.
     Note that the hashing is performed using the {@link Object#hashCode()} and {@link Object#equals(Object)} methods implemented by
     the {@code W} class (i.e. the class of items in the collection).
     @throws NullPointerException if any of the elements in the collection are {@code null}.
     */

    public static <W> HashSet<? extends IxInterface<W>> hashSet( final @NotNull Collection<W> items, final @NotNull IxFactory<W> factory ) {

        HashSet<IxInterface<W>> rval = new HashSet<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( factory.createInstance( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap a collection of items into a {@code HashSet<Ix<W>>}.
     @param items the collection of items (none of which can be {@code null} unless you really like {@link NullPointerException}s being thrown at you).
     @return a {@code HashSet<Ix<W>>} of the wrapped items from the original collection.
     Note that the hashing is performed using the {@link Object#hashCode()} and {@link Object#equals(Object)} methods implemented by
     the {@code W} class (i.e. the class of items in the collection).
     @throws NullPointerException if any of the elements in the collection are {@code null}.
     */

    public static <W> HashSet<Ix<W>> hashSet( final @NotNull Collection<W> items ) {

        HashSet<Ix<W>> rval = new HashSet<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( new Ix<>( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap an array of items into a {@code Vector<IxInterface<W>>}.
     @param items the array of items (any of which might be {@code null}).
     @param factory a factory that creates the wrappers for each item in {@code items}.
     @return an {@code Vector<IxInterface<W>>} of the wrapped items from the original array in the same order that they appeared in the original array.
     */

    public static <W> Vector<? extends IxInterface<W>> vector( final @NotNull W@NotNull[] items, final @NotNull IxFactory<W> factory ) {

        Vector<IxInterface<W>> rval = new Vector<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( factory.createInstance( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap an array of items into a {@code Vector<Ix<W>>}.
     @param items the array of items (any of which might be {@code null}).
     @return an {@code Vector<Ix<W>>} of the wrapped items from the original array in the same order that they appeared in the original array.
     */

    public static <W> Vector<Ix<W>> vector( final @NotNull W@NotNull[] items ) {

        Vector<Ix<W>> rval = new Vector<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( new Ix<>( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap an array of items into a {@code Vector<IxInterface<W>>}.
     @param items the array of items (any of which might be {@code null}).
     @param factory a factory that creates the wrappers for each item in {@code items}.
     @return an {@code Vector<IxInterface<W>>} of the wrapped items from the original array in the same order that they appeared in the original array.
     */

    public static <W> Vector<? extends IxInterface<W>> vector( final @NotNull Collection<W> items, final @NotNull IxFactory<W> factory ) {

        Vector<IxInterface<W>> rval = new Vector<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( factory.createInstance( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Wrap an array of items into a {@code Vector<Ix<W>>}.
     @param items the array of items (any of which might be {@code null}).
     @return an {@code Vector<Ix<W>>} of the wrapped items from the original array in the same order that they appeared in the original array.
     */

    public static <W> Vector<Ix<W>> vector( final @NotNull Collection<W> items ) {

        Vector<Ix<W>> rval = new Vector<>();

        int ix = 0;
        for ( W item : items ) {

            rval.add( new Ix<>( ix, item ) );

            ix += 1;

        }

        return rval;

    }

    /**
     Return a {@link String} containing the index and the value of this instance's item.
     <p>Assuming that {@code abc} is an instance of this class, {@code abc.toString()}
     returns <b>exactly</b> the value of the following expression:
     <blockquote>{@code ( "" + abc.ix + ':' + abc.item )}</blockquote></p>
     @return this instance in a somewhat terse format (see above).
     */

    public String toString() {

        return "" + ix + ':' + item;

    }

    /**
     Return {@code this.item.hashCode()}.
     @return {@code this.item.hashCode()}.
     */

    public int hashCode() {

        return item.hashCode();

    }

    /**
     Compare the {@code item} field of this instance to the {@code item} field of another instance of this class.
     @param rhs anything at all that you can convince the Java compiler to let you pass to this method.
     @return {@code true} if and only iff the following expression yields {@code true}:
     <blockquote>{@code rhs instanceof Ix && this.item.equals( ((Ix)rhs).item )}</blockquote>
     */

    public boolean equals( final Object rhs ) {

        return rhs instanceof Ix && item.equals(((Ix)rhs).item );

    }

}
