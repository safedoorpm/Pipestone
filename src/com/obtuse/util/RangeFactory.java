package com.obtuse.util;

import java.util.SortedMap;

/**
 * Create (presumably) customized {@link com.obtuse.util.Range} instances.
 * <p/>
 * Copyright © 2009 Obtuse Systems Corporation.
 */

public interface RangeFactory<T extends Comparable<T>> {

    Range<T> createMergedRange( SortedMap<T, Range<T>> sortedByStartValue, SortedMap<T, Range<T>> sortedByEndValue );

}
