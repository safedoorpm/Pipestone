package com.obtuse.util.gowing;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.Logger;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnPackerParsingException;
import com.obtuse.util.gowing.p2a.GowingUtil;
import com.obtuse.util.gowing.p2a.StdGowingTokenizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Provide a packing id space for entities that are packed and/or unpacked together.
 */

public class StdGowingUnPackerContext implements GowingUnPackerContext {

    private final SortedMap<EntityTypeName, Integer> _seenTypeNames = new TreeMap<>();
    private final SortedMap<Integer, EntityTypeName> _usedTypeIds = new TreeMap<>();

    private final List<EntityTypeName> _newTypeNames = new LinkedList<>();

    private final SortedMap<GowingEntityReference, GowingPackable> _seenInstanceIds = new TreeMap<>();

    private final SortedSet<GowingEntityReference> _unFinishedEntities = new TreeSet<>();

    private int _nextTypeReferenceId = 1;

    @NotNull
    private final GowingTypeIndex _typeIndex;

    private File _inputFile;

    private GowingRequestorContext _requestorContext;

    public StdGowingUnPackerContext( @NotNull final GowingTypeIndex typeIndex ) {

        super();

        _typeIndex = typeIndex;

    }

    @Override
    public void saveTypeAlias( final StdGowingTokenizer.GowingToken2 typeIdToken, final StdGowingTokenizer.GowingToken2 typeNameToken )
            throws GowingUnPackerParsingException {

        int typeReferenceId = typeIdToken.intValue();
        EntityTypeName typeName = new EntityTypeName( typeNameToken );

        if ( findTypeByTypeReferenceId( typeReferenceId ).isPresent() ) {

            throw new GowingUnPackerParsingException(
                    "type reference id " + typeReferenceId + " already defined in type alias definition",
                    typeIdToken
            );

        }

        Optional<Integer> maybeExistingTypeReferenceId = findTypeReferenceId( typeName );
        if ( maybeExistingTypeReferenceId.isPresent() ) {

            throw new GowingUnPackerParsingException(
                    "type \"" + typeName + "\" already has type id " + maybeExistingTypeReferenceId.get()
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
    public void setInputFile( final File inputFile ) {

        _inputFile = inputFile;

    }

    @Override
    public File getInputFile() {

        return _inputFile;

    }

    @Override
    public void setRequestorContext( final GowingRequestorContext requestorContext ) {

        if ( _requestorContext != null ) {

            throw new IllegalArgumentException( "a unpacker's requestor's context may only be set once" );

        }

        _requestorContext = requestorContext;

    }

    @Override
    public GowingRequestorContext getRequestorContext() {

        return _requestorContext;

    }

    @Override
    public void clearUnFinishedEntities() {

        _unFinishedEntities.clear();

    }

    @Override
    public Collection<GowingEntityReference> getUnfinishedEntities() {

        return new TreeSet<>( _unFinishedEntities );

    }

    @Override
    public void markEntitiesUnfinished( final Collection<GowingEntityReference> unFinishedEntities ) {

        _unFinishedEntities.addAll( unFinishedEntities );

    }

    @Override
    public boolean isEntityFinished( @Nullable final GowingEntityReference er ) {

        return !( er != null && _unFinishedEntities.contains( er ) );

    }

    @Override
    public void markEntityFinished( final GowingEntityReference er ) {

        if ( isEntityFinished( er ) ) {

            throw new HowDidWeGetHereError( "a previously finished entity " + er + " being marked as finished again" );

        }

        _unFinishedEntities.remove( er );

    }

    @Override
    public void addUnfinishedEntities( final Collection<GowingEntityReference> collection ) {

        collection.addAll( _unFinishedEntities );

    }

    @Override
    @NotNull
    public Optional<EntityTypeName> findTypeByTypeReferenceId( final int typeReferenceId ) {

        return Optional.ofNullable( _usedTypeIds.get( typeReferenceId ) );

    }

    @Override
    @NotNull
    public EntityTypeName getTypeByTypeReferenceId( final int typeReferenceId ) {

        Optional<EntityTypeName> maybeTypeName = findTypeByTypeReferenceId( typeReferenceId );
        return maybeTypeName.orElseThrow( () -> new IllegalArgumentException( "unknown type reference id " + typeReferenceId ) );

    }

    @Override
    @NotNull
    public Optional<Integer> findTypeReferenceId( final EntityTypeName typeName ) {

        return Optional.ofNullable( _seenTypeNames.get( typeName ) );

    }

    @Override
    public int getTypeReferenceId( final EntityTypeName typeName ) {


        Integer typeReferenceId = _seenTypeNames.get( typeName );
        if ( typeReferenceId == null ) {

            throw new IllegalArgumentException( "unknown type name \"" + typeName + "\"" );

        }

        return typeReferenceId.intValue();

    }

    @Override
    public boolean isEntityKnown( @NotNull final GowingEntityReference er ) {

        return _seenInstanceIds.containsKey( er );

    }

    @Override
    public GowingPackable recallPackableEntity( @NotNull final GowingEntityReference er ) {

        @SuppressWarnings("UnnecessaryLocalVariable") GowingPackable packable2 = _seenInstanceIds.get( er );

        return packable2;

    }

    @Override
    public Collection<GowingEntityReference> getSeenEntityReferences() {

        return new LinkedList<>( _seenInstanceIds.keySet() );

    }

    @Override
    public void rememberPackableEntity( final StdGowingTokenizer.GowingToken2 token, final GowingEntityReference er, final GowingPackable entity ) {

        if ( isEntityKnown( er ) ) {

            throw new IllegalArgumentException( "Entity with er " + er + " already existing within this unpacking session" );

        }

        _seenInstanceIds.put( er, entity );

    }

    @Override
    @NotNull
    public Collection<EntityTypeName> getNewTypeNames() {

        LinkedList<EntityTypeName> rval = new LinkedList<>( _newTypeNames );
        _newTypeNames.clear();

        return rval;

    }

    @Override
    @NotNull
    public GowingTypeIndex getTypeIndex() {

        return _typeIndex;

    }

    /**
     Find info about a type via its type name.

     @param typeName the name of the type of interest.
     @return the corresponding type's info or <code>null</code> if the specified type is unknown to this type index.
     */

    @Override
    @NotNull
    public Optional<EntityTypeInfo> findTypeInfo( @NotNull final EntityTypeName typeName ) {

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
    public EntityTypeInfo getTypeInfo( @NotNull final EntityTypeName typeName ) {

        Optional<EntityTypeInfo> maybeRval = findTypeInfo( typeName );
        return maybeRval.orElseThrow( () -> new IllegalArgumentException( "unknown type \"" + typeName + "\"" ) );

    }

    /**
     Find info about a type via its type reference id.

     @param typeReferenceId the id of the type of interest.
     @return the corresponding type's info or <code>null</code> if the specified type reference id is unknown to this type index.
     */

    @Override
    @NotNull
    public Optional<EntityTypeInfo> findTypeInfo( final int typeReferenceId ) {

        Optional<EntityTypeName> maybeTypeName = findTypeByTypeReferenceId( typeReferenceId );
        if ( maybeTypeName.isPresent() ) {

            // Can this actually fail?

            Optional<EntityTypeInfo> rval = _typeIndex.findTypeInfo( maybeTypeName.get() );
            if ( rval.isPresent() ) {

                return rval;

            } else {

                throw new HowDidWeGetHereError( "I don't see why this should ever fail to return a non-null value if the id exists (id = " +
                                                typeReferenceId +
                                                ")" );

            }

        } else {

            return Optional.empty();

        }

    }

    /**
     Get info about a type via its type reference id when failure is not an option.

     @param typeReferenceId the id of the type of interest.
     @return the corresponding type's info.
     @throws IllegalArgumentException if the specified type reference id is not known to this type index.
     */

    @Override
    @NotNull
    public EntityTypeInfo getTypeInfo( final int typeReferenceId ) {

        Optional<EntityTypeInfo> maybeRval = findTypeInfo( typeReferenceId );

        return maybeRval.orElseThrow( () -> new IllegalArgumentException( "unknown type reference id " + typeReferenceId ) );

    }

    public EntityTypeInfo registerFactory( final GowingEntityFactory factory ) {

        Optional<EntityTypeInfo> rval = _typeIndex.findTypeInfo( factory.getTypeName() );

        return rval.orElseGet( () -> _typeIndex.addFactory( factory ) );

    }

    @Override
    public int registerFactories( final GowingEntityFactory[] factories ) {

        int count = 0;
        for ( GowingEntityFactory factory : factories ) {

            if ( !isTypeNameKnown( factory.getTypeName() ) ) {

                registerFactory( factory );
                count += 1;

            }

        }

        return count;

    }

    @Override
    public boolean isTypeNameKnown( final EntityTypeName typeName ) {

        return findTypeInfo( typeName ).isPresent();

    }

    public String toString() {

        return "StdGowingUnPackerContext( input file = " + ( _inputFile == null ? "<<unspecified>>" : _inputFile ) + " )";

    }

}
