/*
 * Copyright © 2017 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout;

import com.obtuse.ui.layout.linear.LinearLayoutUtil;
import com.obtuse.util.Logger;
import com.obtuse.util.NamedEntity;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.WeakHashMap;

import static com.obtuse.ui.layout.linear.LinearLayoutUtil.fullName;

/**
 Manage a watchlist.
 */

public class WatchList<T extends Component> implements NamedEntity {

    private final WeakHashMap<T,Integer> _watchList = new WeakHashMap<>();

    private final String _name;

    public WatchList( final String name ) {
	super();

	_name = name;

    }

    public synchronized int addEntityToWatchList( @NotNull final T c ) {

	Logger.logMsg( "adding " + LinearLayoutUtil.fullName( c ) + " to watchlist" );

	Integer count = _watchList.get( c );
	if ( count == null ) {

	    count = 0;

	}

	int incrCount = count.intValue() + 1;
	_watchList.put( c, incrCount );

	return incrCount;

    }

    public synchronized int removeEntityFromWatchList( @NotNull final T c ) {

	Integer count = _watchList.get( c );
	if ( count == null ) {

	    Logger.logMsg( "Watchlist:  not on watch list - " + c );

	    return 0;

	} else {

	    int decrCount = count.intValue() - 1;
	    if ( decrCount <= 0 ) {

		_watchList.remove( c );

		return 0;

	    } else {

		_watchList.put( c, decrCount );

		return decrCount;

	    }

	}

    }

    public synchronized boolean isEntityOnWatchList( @NotNull final T c ) {

	return _watchList.containsKey( c );

    }

    @Override
    public String getName() {

	return _name;

    }

    public String toString() {

	return "WatchList( \"" + getName() + "\" )";

    }

}
