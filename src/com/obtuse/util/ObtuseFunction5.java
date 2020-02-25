/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util;

/**
 A variant of {@link java.util.function.Function} that takes five arguments.
 @param <K1> the type of the first input to the function.
 @param <K2> the type of the second input to the function.
 @param <K3> the type of the third input to the function.
 @param <K4> the type of the fourth input to the function.
 @param <K5> the type of the fourth input to the function.
 @param <R> the type of the result of the function.
 */

public interface ObtuseFunction5<K1, K2, K3, K4, K5, R> {

    /**
     Applies this function to the specified arguments.
     @param key1 the first argument.
     @param key2 the second argument.
     @param key3 the third argument.
     @param key4 the fourth argument.
     @param key5 the fifth argument.
     @return the return value.
     */

    R apply( K1 key1, K2 key2, K3 key3, K4 key4, K5 key5 );

}
