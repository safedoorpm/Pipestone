/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.util;

import com.obtuse.ui.layout.flexigrid1.FlexiGridItemInfo;
import org.jetbrains.annotations.NotNull;

/**
 Encapsulate the minimum, preferred and maximum sizes of a component being managed by a {@link com.obtuse.ui.layout.flexigrid1.FlexiGridLayoutManager}.
 */

public class FlexiGridSliceSizes {

    private FlexiGridItemInfo _itemInfo;
    private int _min = Integer.MAX_VALUE;
    private int _pref = 0;
    private int _max = 0;

    public FlexiGridSliceSizes() {

        super();

    }

    public void consume( final @NotNull FlexiGridItemInfo itemInfo, int min, int pref, int max ) {

        _itemInfo = itemInfo;

        _min = Math.min( _min, min < 0 ? Integer.MAX_VALUE : min );
        _pref = Math.max( _pref, pref );
        _max = Math.max( _max, max );

    }

    @NotNull
    public FlexiGridItemInfo getItemInfo() {

        return _itemInfo;

    }

    public int min() {

        return _min;

    }

    public int pref() {

        return _pref;

    }

    public int max() {

        return _max;

    }

    public String toString() {

        return "FlexiGridSliceSizes( min=" + _min + ", pref=" + _pref + ", max=" + _max + " )";

    }

}
