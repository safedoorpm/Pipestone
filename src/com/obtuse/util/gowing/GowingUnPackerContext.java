package com.obtuse.util.gowing;

import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.GowingUnpackingException;
import com.obtuse.util.gowing.p2a.StdGowingTokenizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Describe something that manages type ids and entity ids during an unpacking operation.
 <p/>Note that the lifespan of a packing context is intended to be the duration of a single unpacking operation.
 */

public interface GowingUnPackerContext {

    @NotNull
    Optional<Integer> findTypeReferenceId( EntityTypeName typeName );

    int getTypeReferenceId( EntityTypeName typeName );

    boolean isEntityKnown( @NotNull GowingEntityReference er );

    @NotNull
    Optional<GowingPackable> recallPackableEntity( @NotNull GowingEntityReference er );

    Collection<GowingEntityReference> getSeenEntityReferences();

    SortedMap<GowingEntityReference, GowingPackable> getSeenEntitiesMap();

    void rememberPackableEntity( StdGowingTokenizer.GowingToken2 token, GowingEntityReference er, GowingPackable entity );

    @NotNull
    Collection<EntityTypeName> getNewTypeNames();

    @NotNull
    GowingTypeIndex getTypeIndex();

    void clearUnFinishedEntities();

    /**
     Get a sorted set containing all currently unfinished entities.
     @return an unmodifiable {@link SortedSet}{@code <}{@link GowingEntityReference}{@code >} set containing all currently unfinished entities.
     The set will be empty if the unpacking process is finished.
     */

    SortedSet<GowingEntityReference> getUnfinishedEntityReferences();

    void markEntitiesUnfinished( Collection<GowingEntityReference> unFinishedEntityReferences );

    void markEntityUnfinished( GowingEntityReference unFinishedEntityReference );

    void markEntitiesFinished( Collection<GowingEntityReference> finishedEntityReferences );

    boolean isEntityFinished( @Nullable GowingEntityReference er );

    void markEntityFinished( GowingEntityReference er );

    @NotNull
    Optional<EntityTypeName> findTypeByTypeReferenceId( int typeReferenceId );

    @NotNull
    EntityTypeName getTypeByTypeReferenceId( int typeReferenceId );

    @NotNull
    Optional<EntityTypeInfo> findTypeInfo( int typeReferenceId );

    @NotNull
    EntityTypeInfo getTypeInfo( int typeReferenceId );

    @NotNull
    Optional<EntityTypeInfo> findTypeInfo( @NotNull EntityTypeName typeName );

    @NotNull
    EntityTypeInfo getTypeInfo( @NotNull EntityTypeName typeName );

    boolean isTypeNameKnown( EntityTypeName typeName );

    @SuppressWarnings("UnusedReturnValue")
    EntityTypeInfo registerFactory( GowingEntityFactory factory );

    @SuppressWarnings("UnusedReturnValue")
    int registerFactories( GowingEntityFactory[] factories );

    void saveTypeAlias( StdGowingTokenizer.GowingToken2 typeIdToken, StdGowingTokenizer.GowingToken2 typeNameToken )
            throws GowingUnpackingException;

    void setInputFile( File inputFile );

    File getInputFile();

    void setRequestorContext( GowingRequestorContext requestorContext );

    GowingRequestorContext getRequestorContext();

}
