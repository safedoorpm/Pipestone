/*
 * Copyright © Daniel Boulet 2018. All rights reserved.
 */

package com.obtuse.ui.layout.flexigrid1.util;

import com.obtuse.ui.layout.flexigrid1.FlexiGridItemInfo;
import com.obtuse.util.ObtuseUtil;
import org.jetbrains.annotations.NotNull;

/**
 Encapsulate the minimum, preferred and maximum sizes of a component being managed by
 a {@link com.obtuse.ui.layout.flexigrid1.FlexiGridLayoutManager}.
 */

public class FlexiGridSliceConstraints {

    private FlexiGridItemInfo _itemInfo;
    private int _min = 0;
    private int _pref = 0;
    private int _max = Integer.MAX_VALUE;
    private boolean _gotValues = false;

    public FlexiGridSliceConstraints() {

        super();

    }

    public void consume( final @NotNull FlexiGridItemInfo itemInfo, int min, int pref, int max ) {

        if ( _gotValues ) {

            ObtuseUtil.doNothing();
            if ( _min != min || _pref != pref || _max != max ) {

                ObtuseUtil.doNothing();

            }

        }

        _itemInfo = itemInfo;

        String before = _min + "/" + _pref + "/" + _max;
        String latest = min + "/" + pref + "/" + max;
        _min = Math.min( _min, min < 0 ? Integer.MAX_VALUE : min );
        _pref = Math.max( _min, Math.max( _pref, pref ) );
        _max = Math.max( _pref, Math.max( _max, max ) );
        String after = _min + "/" + _pref + "/" + _max;

        if ( _min == Integer.MAX_VALUE ) {

            ObtuseUtil.doNothing();

        }

        _gotValues = true;

    }

    @NotNull
    public FlexiGridItemInfo getItemInfo() {

        return _itemInfo;

    }

    public int min() {

        if ( _min == 200 ) {

            ObtuseUtil.doNothing();

        }
        return _min;

    }

    public int pref() {

        if ( _pref == 200 ) {

            ObtuseUtil.doNothing();

        }
        return _pref;

    }

    public int max() {

        return _max;

    }

    public String toString() {

        return "FGSC( " + _min + "/" + _pref + "/" + _max + " )";

    }

}
