package com.obtuse.ui.entitySorter;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 Describe a sorting key.
 */

public class SortableKeySpace implements Comparable<SortableKeySpace> {

    private final String _keyName;

    private static SortedMap<String,SortableKeySpace> s_knownKeys = new TreeMap<>();

    private SortableKeySpace( @NotNull String keyName ) {
        super();

	if ( keyName.isEmpty() ) {

	    throw new IllegalArgumentException( "SortableKeySpace:  key space name must not be an empty string" );

	}

	_keyName = keyName;

    }

    @NotNull
    public String getName() {

        return _keyName;

    }

    /**
     Get a particular key.
     There are either zero or one keys with any particular name.
     @param keyName the name of the desired key.
     @return the requested key (created on-the-fly if it does not already exist).
     */

    @NotNull
    public synchronized static SortableKeySpace getKey( @NotNull String keyName ) {

        SortableKeySpace key = s_knownKeys.get( keyName );

        if ( key == null ) {

	    key = new SortableKeySpace( keyName );

	    s_knownKeys.put( keyName, key );

	}

	return key;

    }

    public String toString() {

        return "SortableKeySpace( \"" + _keyName + "\" )";

    }

    public boolean equals( Object rhs ) {

        return rhs instanceof SortableKeySpace && compareTo( (SortableKeySpace) rhs ) == 0;

    }

    public int compareTo( @NotNull SortableKeySpace rhs ) {

        return getName().compareTo( rhs.getName() );

    }

    public int hashCode() {

        return getName().hashCode();

    }

}
