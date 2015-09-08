package com.obtuse.util.packers.packer2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SortedMap;
import java.util.TreeMap;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Keep track of the known types.
 */

public class TypeIndex2 {

    private final SortedMap<EntityTypeName2,EntityTypeInfo2> _typeNameToTypeInfoMapping = new TreeMap<EntityTypeName2, EntityTypeInfo2>();
//    private final SortedMap<Integer,EntityTypeInfo2> _referenceIdToTypeInfoMapping = new TreeMap<Integer, EntityTypeInfo2>();

    private final String _typeIndexName;

    private int _nextReferenceIndex = 1;

    public TypeIndex2( String typeIndexName ) {
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
    public EntityTypeInfo2 addFactory( @NotNull EntityFactory2 newFactory ) {

//	if ( !newFactory.isLockedDown() ) {
//
//	    throw new IllegalArgumentException( "" + newFactory + ":  cannot add unlocked factory to type index" );
//
//	}

	EntityTypeInfo2 typeInfo = findTypeInfo( newFactory.getEntityTypeName() );
	if ( typeInfo != null ) {

	    throw new IllegalArgumentException( "" + newFactory + ":  cannot add factory because type index already contains entry for type name \"" + typeInfo.getTypeName() + "\"" );

	}

//	typeInfo = findFactory( newFactory.getEntityTypeId() );
//	if ( typeInfo != null ) {
//
//	    throw new IllegalArgumentException( "" + newFactory + ":  cannot add factory because type index already contains entry for type id " + typeInfo.getEntityTypeId() + " associated with type name \"" + typeInfo.getEntityName() + "\"" );
//
//	}

	int referenceId = _nextReferenceIndex;
	_nextReferenceIndex += 1;
	typeInfo = new EntityTypeInfo2( referenceId, newFactory );
	_typeNameToTypeInfoMapping.put(
		newFactory.getEntityTypeName(),
		typeInfo
	);
//	_typeIdToFactoryMapping.put( newFactory.getEntityTypeId(), newFactory );

	return typeInfo;

    }

//    /**
//     Append an unlocked type factory to this type index.
//     @param newFactory the still unlocked type factory.
//     The specified type factory is assigned the next available type id in this type index (0 if this type index is empty, the highest existing type id plus one otherwise).
//     The specified type factory is then locked down and added to this type index.
//     @throws IllegalArgumentException if any of the following are true:
//     <ol>
//     <li>the specified type factory is already locked down.</li>
//     <li>if the specified type factory's type name already exists in this type index.</li>
//     <li>if there is already a type in this index with type id 32767 (i.e. the index is effectively full).</li>
//     */
//
//    public void appendFactory( @NotNull EntityFactory2 newFactory ) {
//
//	if ( newFactory.isLockedDown() ) {
//
//	    throw new IllegalArgumentException( "" + newFactory + ":  cannot append a factory which is already locked down" );
//
//	}
//
//	short highestExistingTypeId = 0;
//	if ( !_typeIdToFactoryMapping.isEmpty() ) {
//
//	    highestExistingTypeId = _typeIdToFactoryMapping.lastKey();
//
//	}
//
//	if ( highestExistingTypeId == Short.MAX_VALUE ) {
//
//	    throw new IllegalArgumentException( "" + newFactory + ":  type index is full (highest existing type id is 32767 which leaves no room for another one)" );
//
//	}
//
//	newFactory.reconfigTypeId( (short) ( highestExistingTypeId + 1 ) );
//
//	addFactory( newFactory.lockdown() );
//
//    }

    /**
     Get this index's name.
     @return this index's name.
     */

    public String getTypeIndexName() {

	return _typeIndexName;

    }

    /**
     Find info about a type via its type name.
     @param typeName the name of the type of interest.
     @return the corresponding type's info or <code>null</code> if we don't have the specified type in our index.
     */

    @Nullable
    public EntityTypeInfo2 findTypeInfo( @NotNull EntityTypeName2 typeName ) {

	return _typeNameToTypeInfoMapping.get( typeName );

    }

    /**
     Get info about a type via its type name when failure is not an option.
     @param typeName the name of the type of interest.
     @return the corresponding type's info.
     @throws IllegalArgumentException if the specified type is not known to this type index.
     */

    @NotNull
    public EntityTypeInfo2 getTypeInfo( @NotNull EntityTypeName2 typeName ) {

	EntityTypeInfo2 entityFactory = findTypeInfo( typeName );
	if ( entityFactory == null ) {

	    throw new IllegalArgumentException( "no type info found for type named \"" + typeName + "\"" );

	}

	return entityFactory;

    }

//    /**
//     Find an {@link EntityFactory2} via its type id.
//     @param typeId the id of the type of interest.
//     @return the corresponding entity factory or <code>null</code> if no such factory exists within this type index.
//     */
//
//    @Nullable
//    EntityFactory2 findFactory( short typeId ) {
//
//	return _typeIdToFactoryMapping.get( typeId );
//
//    }
//
//    /**
//     Get an {@link EntityFactory2} via its type id when failure is not an option.
//     @param typeId the id of the type of interest.
//     @return the corresponding entity factory.
//     @throws IllegalArgumentException if no such factory exists within this type index.
//     */
//
//    @NotNull
//    EntityFactory2 getFactory( short typeId ) {
//
//	EntityFactory2 entityFactory = findFactory( typeId );
//	if ( entityFactory == null ) {
//
//	    throw new IllegalArgumentException( "no factory found for type id " + typeId );
//
//	}
//
//	return entityFactory;
//
//    }

    public String toString() {

	return "TypeIndex( \"" + _typeIndexName + "\", " + _typeNameToTypeInfoMapping.size() + " type entries )";

    }

}
