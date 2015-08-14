package com.obtuse.util.packers.packer1;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 %%% Something clever goes here.
 */

public class TypeIndex1 {

    private final SortedMap<String,EntityFactory1> _typeNameToFactoryMapping = new TreeMap<String, EntityFactory1>();
    private final SortedMap<Short,EntityFactory1> _typeIdToFactoryMapping = new TreeMap<Short, EntityFactory1>();

    private final String _typeIndexName;

    public TypeIndex1( String typeIndexName ) {
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

    public void addFactory( @NotNull EntityFactory1 newFactory ) {

	if ( !newFactory.isLockedDown() ) {

	    throw new IllegalArgumentException( "" + newFactory + ":  cannot add unlocked factory to type index" );

	}

	EntityFactory1 existingFactory = findFactory( newFactory.getEntityName() );
	if ( existingFactory != null ) {

	    throw new IllegalArgumentException( "" + newFactory + ":  cannot add factory because type index already contains entry for type name \"" + existingFactory.getEntityName() + "\" associated with type id " + existingFactory.getEntityTypeId() );

	}

	existingFactory = findFactory( newFactory.getEntityTypeId() );
	if ( existingFactory != null ) {

	    throw new IllegalArgumentException( "" + newFactory + ":  cannot add factory because type index already contains entry for type id " + existingFactory.getEntityTypeId() + " associated with type name \"" + existingFactory.getEntityName() + "\"" );

	}

	_typeNameToFactoryMapping.put( newFactory.getEntityName(), newFactory );
	_typeIdToFactoryMapping.put( newFactory.getEntityTypeId(), newFactory );

    }

    /**
     Append an unlocked type factory to this type index.
     @param newFactory the still unlocked type factory.
     The specified type factory is assigned the next available type id in this type index (0 if this type index is empty, the highest existing type id plus one otherwise).
     The specified type factory is then locked down and added to this type index.
     @throws IllegalArgumentException if any of the following are true:
     <ol>
     <li>the specified type factory is already locked down.</li>
     <li>if the specified type factory's type name already exists in this type index.</li>
     <li>if there is already a type in this index with type id 32767 (i.e. the index is effectively full).</li>
     */

    public void appendFactory( @NotNull EntityFactory1 newFactory ) {

	if ( newFactory.isLockedDown() ) {

	    throw new IllegalArgumentException( "" + newFactory + ":  cannot append a factory which is already locked down" );

	}

	short highestExistingTypeId = 0;
	if ( !_typeIdToFactoryMapping.isEmpty() ) {

	    highestExistingTypeId = _typeIdToFactoryMapping.lastKey();

	}

	if ( highestExistingTypeId == Short.MAX_VALUE ) {

	    throw new IllegalArgumentException( "" + newFactory + ":  type index is full (highest existing type id is 32767 which leaves no room for another one)" );

	}

	newFactory.reconfigTypeId( (short) ( highestExistingTypeId + 1 ) );

	addFactory( newFactory.lockdown() );

    }

    /**
     Get this index's name.
     @return this index's name.
     */

    public String getTypeIndexName() {

	return _typeIndexName;

    }

    /**
     Find an {@link EntityFactory1} via its type name.
     @param typeName the name of the type of interest.
     @return the corresponding entity factory or <code>null</code> if no such factory exists within this type index.
     */

    @Nullable
    EntityFactory1 findFactory( @NotNull String typeName ) {

	return _typeNameToFactoryMapping.get( typeName );

    }

    /**
     Get an {@link EntityFactory1} via its type name when failure is not an option.
     @param typeName the name of the type of interest.
     @return the corresponding entity factory.
     @throws IllegalArgumentException if no such factory exists within this type index.
     */

    @NotNull
    EntityFactory1 getFactory( @NotNull String typeName ) {

	EntityFactory1 entityFactory = findFactory( typeName );
	if ( entityFactory == null ) {

	    throw new IllegalArgumentException( "no factory found for type named \"" + typeName + "\"" );

	}

	return entityFactory;

    }

    /**
     Find an {@link EntityFactory1} via its type id.
     @param typeId the id of the type of interest.
     @return the corresponding entity factory or <code>null</code> if no such factory exists within this type index.
     */

    @Nullable
    EntityFactory1 findFactory( short typeId ) {

	return _typeIdToFactoryMapping.get( typeId );

    }

    /**
     Get an {@link EntityFactory1} via its type id when failure is not an option.
     @param typeId the id of the type of interest.
     @return the corresponding entity factory.
     @throws IllegalArgumentException if no such factory exists within this type index.
     */

    @NotNull
    EntityFactory1 getFactory( short typeId ) {

	EntityFactory1 entityFactory = findFactory( typeId );
	if ( entityFactory == null ) {

	    throw new IllegalArgumentException( "no factory found for type id " + typeId );

	}

	return entityFactory;

    }

    public String toString() {

	return "TypeIndex( \"" + _typeIndexName + "\", " + _typeNameToFactoryMapping.size() + " type entries )";

    }

}
