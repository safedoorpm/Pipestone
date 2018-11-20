package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 Various (hopefully) useful array manipulation methods.
 */

public class ObtuseArrays {

    /**
     Turn a {@link Collection}{@code <}{@link Character}{@code >} into an array of primitive chars.
     @param values the provided {@link Collection}{@code <Character>}.
     @return a {@code long[]} containing the contents of the collection.
     The length of the returned array will equal the size of the provided collection.
     The order of elements in the returned array will be the natural ordering of elements in the collection.
     For example, if the provided collection is called {@code elements} then these two for loops will print the
     exact same values in the exact same order:
     <blockquote>
     {@code for ( Character v : elements ) System.out.println( v );}<br>
     {@code for ( char w : ObtuseUtil.charArray( elements ) ) System.out.println( w );}
     </blockquote>
     @throws NullPointerException if any of the elements of the provided collection are {@code null}.
     */

    public static char[] charArray( @NotNull Collection<Character> values ) {

        return primitiveArray( values.toArray( new Character[0] ) );

    }

    /**
     Turn a {@link Collection}{@code <}{@link Byte}{@code >} into an array of primitive bytes.
     @param values the provided {@link Collection}{@code <Byte>}.
     @return a {@code byte[]} containing the contents of the collection.
     The length of the returned array will equal the size of the provided collection.
     The order of elements in the returned array will be the natural ordering of elements in the collection.
     For example, if the provided collection is called {@code elements} then these two for loops will print the
     exact same values in the exact same order:
     <blockquote>
     {@code for ( Byte v : elements ) System.out.println( v );}<br>
     {@code for ( byte w : ObtuseUtil.byteArray( elements ) ) System.out.println( w );}
     </blockquote>
     @throws NullPointerException if any of the elements of the provided collection are {@code null}.
     */

    public static byte[] byteArray( @NotNull Collection<Byte> values ) {

        return primitiveArray( values.toArray( new Byte[0] ) );

    }

    /**
     Turn a {@link Collection}{@code <}{@link Short}{@code >} into an array of primitive shorts.
     @param values the provided {@link Collection}{@code <Short>}.
     @return a {@code short[]} containing the contents of the collection.
     The length of the returned array will equal the size of the provided collection.
     The order of elements in the returned array will be the natural ordering of elements in the collection.
     For example, if the provided collection is called {@code elements} then these two for loops will print the
     exact same values in the exact same order:
     <blockquote>
     {@code for ( Short v : elements ) System.out.println( v );}<br>
     {@code for ( short w : ObtuseUtil.shortArray( elements ) ) System.out.println( w );}
     </blockquote>
     @throws NullPointerException if any of the elements of the provided collection are {@code null}.
     */

    public static short[] shortArray( @NotNull Collection<Short> values ) {

        return primitiveArray( values.toArray( new Short[0] ) );

    }

    /**
     Turn a {@link Collection}{@code <}{@link Integer}{@code >} into an array of primitive ints.
     @param values the provided {@link Collection}{@code <Integer>}.
     @return a {@code int[]} containing the contents of the collection.
     The length of the returned array will equal the size of the provided collection.
     The order of elements in the returned array will be the natural ordering of elements in the collection.
     For example, if the provided collection is called {@code elements} then these two for loops will print the
     exact same values in the exact same order:
     <blockquote>
     {@code for ( Integer v : elements ) System.out.println( v );}<br>
     {@code for ( int w : ObtuseUtil.intArray( elements ) ) System.out.println( w );}
     </blockquote>
     @throws NullPointerException if any of the elements of the provided collection are {@code null}.
     */

    public static int[] intArray( @NotNull Collection<Integer> values ) {

        return primitiveArray( values.toArray( new Integer[0] ) );

    }

    /**
     Turn a {@link Collection}{@code <}{@link Long}{@code >} into an array of primitive longs.
     @param values the provided {@link Collection}{@code <Long>}.
     @return a {@code long[]} containing the contents of the collection.
     The length of the returned array will equal the size of the provided collection.
     The order of elements in the returned array will be the natural ordering of elements in the collection.
     For example, if the provided collection is called {@code elements} then these two for loops will print the
     exact same values in the exact same order:
     <blockquote>
     {@code for ( Long v : elements ) System.out.println( v );}<br>
     {@code for ( long w : ObtuseUtil.longArray( elements ) ) System.out.println( w );}
     </blockquote>
     @throws NullPointerException if any of the elements of the provided collection are {@code null}.
     */

    public static long[] longArray( @NotNull Collection<Long> values ) {

        return primitiveArray( values.toArray( new Long[0] ) );

    }

    /**
     Turn a {@link Collection}{@code <}{@link Float}{@code >} into an array of primitive floats.
     @param values the provided {@link Collection}{@code <Float>}.
     @return a {@code float[]} containing the contents of the collection.
     The length of the returned array will equal the size of the provided collection.
     The order of elements in the returned array will be the natural ordering of elements in the collection.
     For example, if the provided collection is called {@code elements} then these two for loops will print the
     exact same values in the exact same order:
     <blockquote>
     {@code for ( Float v : elements ) System.out.println( v );}<br>
     {@code for ( float w : ObtuseUtil.floatArray( elements ) ) System.out.println( w );}
     </blockquote>
     @throws NullPointerException if any of the elements of the provided collection are {@code null}.
     */

    public static float[] floatArray( @NotNull Collection<Float> values ) {

        return primitiveArray( values.toArray( new Float[0] ) );

    }

    /**
     Turn a {@link Collection}{@code <}{@link Double}{@code >} into an array of primitive doubles.
     @param values the provided {@link Collection}{@code <Double>}.
     @return a {@code double[]} containing the contents of the collection.
     The length of the returned array will equal the size of the provided collection.
     The order of elements in the returned array will be the natural ordering of elements in the collection.
     For example, if the provided collection is called {@code elements} then these two for loops will print the
     exact same values in the exact same order:
     <blockquote>
     {@code for ( Double v : elements ) System.out.println( v );}<br>
     {@code for ( double w : ObtuseUtil.doubleArray( elements ) ) System.out.println( w );}
     </blockquote>
     @throws NullPointerException if any of the elements of the provided collection are {@code null}.
     */

    public static double[] doubleArray( @NotNull Collection<Double> values ) {

        return primitiveArray( values.toArray( new Double[0] ) );

    }

    /**
     Turn a {@link Character}{@code []} array into an array of primitive chars.
     @param values the provided {@code Character[]} array.
     @return a {@code char[]} containing the same values in the same order as they appear in the provided array.
     @throws NullPointerException if any of the elements of the provided array are {@code null}.
     */

    public static char[] primitiveArray( @NotNull Character[] values ) {

        char[] rval = new char[values.length];
        int ix = 0;
        for ( char id : values ) {

            rval[ix] = id;

            ix += 1;

        }

        return rval;

    }

    /**
     Turn a {@link Byte}{@code []} array into an array of primitive bytes.
     @param values the provided {@code Byte[]} array.
     @return a {@code byte[]} containing the same values in the same order as they appear in the provided array.
     @throws NullPointerException if any of the elements of the provided array are {@code null}.
     */

    public static byte[] primitiveArray( @NotNull Byte[] values ) {

        byte[] rval = new byte[values.length];
        int ix = 0;
        for ( byte id : values ) {

            rval[ix] = id;

            ix += 1;

        }

        return rval;

    }

    /**
     Turn a {@link Short}{@code []} array into an array of primitive shorts.
     @param values the provided {@code Short[]} array.
     @return a {@code short[]} containing the same values in the same order as they appear in the provided array.
     @throws NullPointerException if any of the elements of the provided array are {@code null}.
     */

    public static short[] primitiveArray( @NotNull Short[] values ) {

        short[] rval = new short[values.length];
        int ix = 0;
        for ( short id : values ) {

            rval[ix] = id;

            ix += 1;

        }

        return rval;

    }

    /**
     Turn a {@link Integer}{@code []} array into an array of primitive ints.
     @param values the provided {@code Integer[]} array.
     @return a {@code int[]} containing the same values in the same order as they appear in the provided array.
     @throws NullPointerException if any of the elements of the provided array are {@code null}.
     */

    public static int[] primitiveArray( @NotNull Integer[] values ) {

        int[] rval = new int[values.length];
        int ix = 0;
        for ( int id : values ) {

            rval[ix] = id;

            ix += 1;

        }

        return rval;

    }

    /**
     Turn a {@link Long}{@code []} array into an array of primitive longs.
     @param values the provided {@code Long[]} array.
     @return a {@code long[]} containing the same values in the same order as they appear in the provided array.
     @throws NullPointerException if any of the elements of the provided array are {@code null}.
     */

    public static long[] primitiveArray( @NotNull Long[] values ) {

        long[] rval = new long[values.length];
        int ix = 0;
        for ( long id : values ) {

            rval[ix] = id;

            ix += 1;

        }

        return rval;

    }

    /**
     Turn a {@link Float}{@code []} array into an array of primitive floats.
     @param values the provided {@code Float[]} array.
     @return a {@code float[]} containing the same values in the same order as they appear in the provided array.
     @throws NullPointerException if any of the elements of the provided array are {@code null}.
     */

    public static float[] primitiveArray( @NotNull Float[] values ) {

        float[] rval = new float[values.length];
        int ix = 0;
        for ( float id : values ) {

            rval[ix] = id;

            ix += 1;

        }

        return rval;

    }

    /**
     Turn a {@link Double}{@code []} array into an array of primitive doubles.
     @param values the provided {@code Double[]} array.
     @return a {@code double[]} containing the same values in the same order as they appear in the provided array.
     @throws NullPointerException if any of the elements of the provided array are {@code null}.
     */

    public static double[] primitiveArray( @NotNull Double[] values ) {

        double[] rval = new double[values.length];
        int ix = 0;
        for ( double id : values ) {

            rval[ix] = id;

            ix += 1;

        }

        return rval;

    }
}
