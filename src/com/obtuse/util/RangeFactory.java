/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

import java.util.SortedMap;

/**
 * Create (presumably) customized {@link com.obtuse.util.Range} instances.
 */

public interface RangeFactory<T extends Comparable<T>> {

    Range<T> createMergedRange( SortedMap<T, Range<T>> sortedByStartValue, SortedMap<T, Range<T>> sortedByEndValue );

}
