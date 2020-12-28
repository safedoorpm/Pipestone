package com.obtuse.util.gowing;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Keep track of the known types.
 */

public class GowingTypeIndex {

    private final SortedMap<EntityTypeName,EntityTypeInfo> _typeNameToTypeInfoMapping = new TreeMap<>();

    private final String _typeIndexName;

    private int _nextReferenceIndex = 1;

    public GowingTypeIndex( final String typeIndexName ) {
	super();

	_typeIndexName = typeIndexName;

    }

    /**
     Add a new factory to this type index.
     <p/>
     IMPORTANT:
     Considerable care should be taken to ensure that once people start to rely on the ability to restore old save files or to
     have old versions of application components communicate with current or future versions of application components that neither
     the name nor the id of a type change.
     @param newFactory the factory to be added.
     @throws IllegalArgumentException if the new factory's name or type id already exists in this index.
     */

    @NotNull
    public EntityTypeInfo addFactory( final @NotNull GowingEntityFactory newFactory ) {

	Optional<EntityTypeInfo> maybeTypeInfo = findTypeInfo( newFactory.getEntityTypeName() );
	//noinspection OptionalIsPresent
	if ( maybeTypeInfo.isPresent() ) {

	    throw new IllegalArgumentException( "" + newFactory + ":  cannot add factory because type index already contains entry for type name \"" + maybeTypeInfo.get().getTypeName() + "\"" );

	}

	int referenceId = _nextReferenceIndex;
	_nextReferenceIndex += 1;
	EntityTypeInfo typeInfo = new EntityTypeInfo( referenceId, newFactory );
	_typeNameToTypeInfoMapping.put(
		newFactory.getEntityTypeName(),
		typeInfo
	);

	return typeInfo;

    }

    /**
     Get this index's name.
     @return this index's name.
     */

    @SuppressWarnings("unused")
    public String getTypeIndexName() {

	return _typeIndexName;

    }

    /**
     Find info about a type via its type name.
     @param typeName the name of the type of interest.
     @return the corresponding type's info or <code>null</code> if we don't have the specified type in our index.
     */

    @NotNull
    public Optional<EntityTypeInfo> findTypeInfo( final @NotNull EntityTypeName typeName ) {

	return Optional.ofNullable( _typeNameToTypeInfoMapping.get( typeName ) );

    }

    /**
     Determine if there type info available for a specified type name.
     @param typeName the name of the type of interest.
     @return <tt>true</tt> if we have type info for the specified type; <tt>false</tt> otherwise.
     */

    public boolean hasTypeInfo( final @NotNull EntityTypeName typeName ) {

        return _typeNameToTypeInfoMapping.containsKey( typeName );

    }

    /**
     Get info about a type via its type name when failure is not an option.
     @param typeName the name of the type of interest.
     @return the corresponding type's info.
     @throws IllegalArgumentException if the specified type is not known to this type index.
     */

    @SuppressWarnings("unused")
    @NotNull
    public EntityTypeInfo getTypeInfo( final @NotNull EntityTypeName typeName ) {

        Optional<EntityTypeInfo> maybeEntityFactory = findTypeInfo( typeName );
	return maybeEntityFactory.orElseThrow( ()->new IllegalArgumentException( "no type info found for type named \"" + typeName + "\"" ) );

    }

    public String toString() {

	return "TypeIndex( \"" + _typeIndexName + "\", " + _typeNameToTypeInfoMapping.size() + " type entries )";

    }

}
