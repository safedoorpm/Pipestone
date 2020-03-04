/*
 * Copyright © 2016 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.ui.layout.linear;

import com.obtuse.ui.layout.util.immutable.ImmutableSortedMap;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

/**
 An immutable box to carry around a set of {@link LinearFlagName} instances.
 <p/>This class implements the {@link SortedSet} and {@link Serializable} interfaces.
 */

public class LinearFlagMap extends ImmutableSortedMap<LinearFlagName,LinearFlagNameValue> implements Serializable {

    /**
     Create an immutable {@link SortedSet<LinearFlagName>} containing the specified flags.
     @param flags a {@link Collection<LinearFlagName>} containing the 'specified flags'. {@code null} values are silently stripped out.
                  Note that the contents of the collection of flags is copied into the newly created instance. Changes to the
                  provided collection after this constructor returns will have no impact on the instance that this constructor creates.
     */

    public LinearFlagMap( final Map<LinearFlagName,LinearFlagNameValue> flags ) {
	super( createCleanedMap( flags.values() ) );
    }

    /**
     Create a {link {@link LinearFlagMap} containing the specified flags.
     @param flags a {@link LinearFlagName} array containing the 'specified flags'. {@code null} values are silently stripped out.
     Note that the contents of the array of flags is copied into the newly created instance. Changes to the
     provided array after this constructor returns will have no impact on the instance that this constructor creates.
     */

    public LinearFlagMap( final LinearFlagNameValue[] flags ) {
	super( createCleanedMap( flags ) );
    }

    /**
     Create a {@link LinearFlagMap} containing precisely one flag.
     Intended to be used to create a set which can be either unioned (is that a word?) or intersected with some other {@link LinearFlagMap}.
     @param flag the flag which the set is to contain. Must not be {@code null}.
     @return a {@link LinearFlagMap} containing only the specified flag.
     */

    public static LinearFlagMap createSingletonSet( final @NotNull LinearFlagNameValue flag ) {

	return createCleanedMap( new LinearFlagNameValue[] { flag } );

    }

    /**
     A utility method that creates a {@link LinearFlagMap} containing the specified flags.
     @param flags a {@link LinearFlagMap} containing the 'specified flags'. The set is 'cleaned' in the sense that {@code null} values are silently omitted from the returned set.
     @return the resulting {@link LinearFlagMap} instance.
     */

    public static LinearFlagMap createCleanedMap( final Collection<LinearFlagNameValue> flags ) {

	SortedMap<LinearFlagName,LinearFlagNameValue> rval = new TreeMap<>();
	for ( LinearFlagNameValue flag : flags ) {

	    if ( flag != null ) {

		rval.put( (LinearFlagName) flag.getName(), flag );

	    }

	}

	return new LinearFlagMap( rval );

    }

    /**
     A utility method that creates a <b><u>mutable</u> {@link SortedMap}{@code <}{@link LinearFlagName}{@code ,}{@link LinearFlagNameValue}{@code >} containing the specified flags.
     @param flags a {@link LinearFlagNameValue} array containing the 'specified flags'. {@code null} values are silently stripped out.
     @return the resulting {@link LinearFlagMap} instance.
     */

    public static LinearFlagMap createCleanedMap( final LinearFlagNameValue[] flags ) {

	return createCleanedMap( Arrays.asList( flags ) );

    }

    /**
     Create a new flag set which is the set-union of this set and the specified collection of flags.
     <p/>Like all other LinearFlagMap instances, the resulting set is immutable.
     @param flags the specified collection of flags (treated as though it is a set even if it is some other kind of collection).
     @return the new flag set.
     */

    public LinearFlagMap addIfMissing( final Collection<LinearFlagNameValue> flags ) {

	SortedMap<LinearFlagName,LinearFlagNameValue> existing = new TreeMap<>( this );
	for ( LinearFlagNameValue flag : flags ) {

	    LinearFlagName key = (LinearFlagName) flag.getName();

	    if ( !existing.containsKey( key ) ) {

		existing.put( key, flag );

	    }

	}

	return new LinearFlagMap( existing );

    }

    /**
     Create a new flag set which is the set-intersection of this set and the specified collection of flags.
     <p/>Like all other LinearFlagMap instances, the resulting set is immutable.
     @param flags the specified collection of flags (treated as though it is a set even if it is some other kind of collection).
     @return the new flag set.
     */

    public LinearFlagMap keepThese( final Collection<LinearFlagName> flags ) {

	SortedMap<LinearFlagName,LinearFlagNameValue> existing = new TreeMap<>( this );
	SortedSet<LinearFlagName> keepers = new TreeSet<>( flags );

	existing.keySet().removeIf( flag -> !keepers.contains( flag ) );

	return new LinearFlagMap( existing );

    }

    /**
     Create a new flag map which contains everything in this instance which is not in the specified collection.
     This operation is roughly akin to performing a set-difference.
     The new {@link LinearFlagMap} instance will contain all of the elements in this instance which are NOT in the specified collection of flags.
     <p/>Like all other LinearFlagMap instances, the resulting set is immutable.
     @param flags the specified collection of flags (treated as though it is a set even if it is some other kind of collection).
     @return the new flag set.
     */

    public LinearFlagMap exceptThese( final Collection<LinearFlagName> flags ) {

	SortedMap<LinearFlagName,LinearFlagNameValue> existing = new TreeMap<>( this );
	for (LinearFlagName flag : flags ) {

	    existing.remove( flag );

	}

	return new LinearFlagMap( existing );

    }

    public String toString() {

	return LinearFlagName.describe( this.values() );

    }

}
