package com.obtuse.util.gowing.packer2;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Accumulator;
import com.obtuse.util.Logger;
import com.obtuse.util.TreeAccumulator;
import com.obtuse.util.gowing.packer2.p2a.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Provide a packing id space for entities that are packed and/or unpacked together.
 */

public class StdGowingUnPackerContext2 implements GowingUnPackerContext2 {

    private final SortedMap<EntityTypeName2,Integer> _seenTypeNames = new TreeMap<EntityTypeName2, Integer>();
    private final SortedMap<Integer,EntityTypeName2> _usedTypeIds = new TreeMap<Integer, EntityTypeName2>();

    private final List<EntityTypeName2> _newTypeNames = new LinkedList<EntityTypeName2>();

    private final SortedMap<GowingEntityReference,GowingPackable2> _seenInstanceIds = new TreeMap<GowingEntityReference,GowingPackable2>();

    private final SortedSet<GowingEntityReference> _unFinishedEntities = new TreeSet<GowingEntityReference>();

    private int _nextTypeReferenceId = 1;

    @NotNull
    private final GowingTypeIndex2 _typeIndex;

    public StdGowingUnPackerContext2( @NotNull GowingTypeIndex2 typeIndex ) {
	super();

	_typeIndex = typeIndex;

    }

    @Override
    public void saveTypeAlias( GowingTokenizer2.GowingToken2 typeIdToken, GowingTokenizer2.GowingToken2 typeNameToken )
	    throws GowingUnPacker2ParsingException {

	int typeReferenceId = typeIdToken.intValue();
	EntityTypeName2 typeName = new EntityTypeName2( typeNameToken.stringValue() );

	if ( findTypeByTypeReferenceId( typeReferenceId ) != null ) {

	    throw new GowingUnPacker2ParsingException( "type reference id " + typeReferenceId + " already defined in type alias definition", typeIdToken );

	}

	Integer existingTypeReferenceId = findTypeReferenceId( typeName );
	if ( existingTypeReferenceId != null ) {

	    throw new GowingUnPacker2ParsingException(
		    "type \"" + typeName + "\" already has type id " + existingTypeReferenceId
		    + ", cannot associate it with type id " + typeReferenceId,
		    typeNameToken
	    );

	}

	_seenTypeNames.put( typeName, typeReferenceId );
	_newTypeNames.add( typeName );
	_usedTypeIds.put( typeReferenceId, typeName );
	_nextTypeReferenceId = Math.max( typeReferenceId + 1, _nextTypeReferenceId );

    }

    @Override
    public void clearUnFinishedEntities() {

	_unFinishedEntities.clear();

    }

    @Override
    public Collection<GowingEntityReference> getUnfinishedEntities() {

	return new TreeSet<GowingEntityReference>( _unFinishedEntities );

    }

    @Override
    public void markEntitiesUnfinished( Collection<GowingEntityReference> unFinishedEntities ) {

	_unFinishedEntities.addAll( unFinishedEntities );

    }

    @Override
    public boolean isEntityFinished( GowingEntityReference er ) {

	return !_unFinishedEntities.contains( er );

    }

    @Override
    public void markEntityFinished( GowingEntityReference er ) {

	if ( isEntityFinished( er ) ) {

	    throw new HowDidWeGetHereError( "a previously finished entity " + er + " being marked as finished again" );

	}

	_unFinishedEntities.remove( er );

    }

    @Override
    public void addUnfinishedEntities( Collection<GowingEntityReference> collection ) {

	collection.addAll( _unFinishedEntities );

    }

    @Override
    @Nullable
    public EntityTypeName2 findTypeByTypeReferenceId( int typeReferenceId ) {

	return _usedTypeIds.get( typeReferenceId );

    }

    @Override
    @NotNull
    public EntityTypeName2 getTypeByTypeReferenceId( int typeReferenceId ) {

	EntityTypeName2 typeName = findTypeByTypeReferenceId( typeReferenceId );
	if ( typeName == null ) {

	    throw new IllegalArgumentException( "unknown type reference id " + typeReferenceId );

	}

	return typeName;

    }

    @Override
    @Nullable
    public Integer findTypeReferenceId( EntityTypeName2 typeName ) {

	return _seenTypeNames.get( typeName );

    }

    @Override
    public int getTypeReferenceId( EntityTypeName2 typeName ) {


	Integer typeReferenceId = _seenTypeNames.get( typeName );
	if ( typeReferenceId == null ) {

	    throw new IllegalArgumentException( "unknown type name \"" + typeName + "\"" );

	}

	return typeReferenceId;

    }

    @Override
    public boolean isEntityKnown( GowingEntityReference er ) {

	return _seenInstanceIds.containsKey( er );

    }

    @Override
    public GowingPackable2 recallPackableEntity( @NotNull GowingEntityReference er ) {

	GowingPackable2 packable2 = _seenInstanceIds.get( er );
	Logger.logMsg( "recalling " + er + " as " + packable2 );

	return packable2;

    }

    @Override
    public Collection<GowingEntityReference> getSeenEntityReferences() {

	return new LinkedList<GowingEntityReference>( _seenInstanceIds.keySet() );

    }

    @Override
    public void rememberPackableEntity( GowingTokenizer2.GowingToken2 token, GowingEntityReference er, GowingPackable2 entity ) {

	Logger.logMsg( "remembering " + er + " = " + entity );

	if ( isEntityKnown( er ) ) {

	    throw new IllegalArgumentException( "Entity with er " + er + " already existing within this unpacking session" );

	}

	_seenInstanceIds.put( er, entity );

    }

    @Override
    @NotNull
    public Collection<EntityTypeName2> getNewTypeNames() {

	LinkedList<EntityTypeName2> rval = new LinkedList<EntityTypeName2>( _newTypeNames );
	_newTypeNames.clear();

	return rval;

    }

    @Override
    @NotNull
    public GowingTypeIndex2 getTypeIndex() {

	return _typeIndex;

    }

    /**
     Find info about a type via its type name.
     @param typeName the name of the type of interest.
     @return the corresponding type's info or <code>null</code> if the specified type is unknown to this type index.
     */

    @Override
    @Nullable
    public EntityTypeInfo2 findTypeInfo( @NotNull EntityTypeName2 typeName ) {

	return _typeIndex.findTypeInfo( typeName );

    }

    /**
     Get info about a type via its type name when failure is not an option.
     @param typeName the name of the type of interest.
     @return the corresponding type's info.
     @throws IllegalArgumentException if the specified type is not known to this type index.
     */

    @Override
    @NotNull
    public EntityTypeInfo2 getTypeInfo( @NotNull EntityTypeName2 typeName ) {

	EntityTypeInfo2 rval = findTypeInfo( typeName );
	if ( rval == null ) {

	    throw new IllegalArgumentException( "unknown type \"" + typeName + "\"" );

	}

	return rval;

    }

    /**
     Find info about a type via its type reference id.
     @param typeReferenceId the id of the type of interest.
     @return the corresponding type's info or <code>null</code> if the specified type reference id is unknown to this type index.
     */

    @Override
    @Nullable
    public EntityTypeInfo2 findTypeInfo( int typeReferenceId ) {

	EntityTypeName2 typeName = findTypeByTypeReferenceId( typeReferenceId );
	if ( typeName == null ) {

	    return null;

	}

	return _typeIndex.findTypeInfo( typeName );

    }

    /**
     Get info about a type via its type reference id when failure is not an option.
     @param typeReferenceId the id of the type of interest.
     @return the corresponding type's info.
     @throws IllegalArgumentException if the specified type reference id is not known to this type index.
     */

    @Override
    @NotNull
    public EntityTypeInfo2 getTypeInfo( int typeReferenceId ) {

	EntityTypeInfo2 rval = findTypeInfo( typeReferenceId );
	if ( rval == null ) {

	    throw new IllegalArgumentException( "unknown type reference id " + typeReferenceId );

	}

	return rval;

    }

    public EntityTypeInfo2 registerFactory( GowingEntityFactory2 factory ) {

	EntityTypeInfo2 rval = _typeIndex.findTypeInfo( factory.getTypeName() );
	if ( rval == null ) {

	    return _typeIndex.addFactory( factory );

	} else {

	    return rval;

	}
    }

//    }

    @Override
    public boolean isTypeNameKnown( EntityTypeName2 typeName ) {

	return findTypeInfo( typeName ) != null;

    }

}
