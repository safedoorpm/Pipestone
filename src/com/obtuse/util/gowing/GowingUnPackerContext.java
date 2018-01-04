package com.obtuse.util.gowing;

import com.obtuse.util.gowing.p2a.GowingEntityReference;
import com.obtuse.util.gowing.p2a.StdGowingTokenizer;
import com.obtuse.util.gowing.p2a.GowingUnPackerParsingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

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

    boolean isEntityKnown( GowingEntityReference er );

    GowingPackable recallPackableEntity( @NotNull GowingEntityReference er );

    Collection<GowingEntityReference> getSeenEntityReferences();

    void rememberPackableEntity( StdGowingTokenizer.GowingToken2 token, GowingEntityReference etr, GowingPackable entity );

    @NotNull
    Collection<EntityTypeName> getNewTypeNames();

    @NotNull
    GowingTypeIndex getTypeIndex();

    void clearUnFinishedEntities();

    Collection<GowingEntityReference> getUnfinishedEntities();

    void markEntitiesUnfinished( Collection<GowingEntityReference> unFinishedEntities );

    boolean isEntityFinished( @Nullable GowingEntityReference er );

    void markEntityFinished( GowingEntityReference er );

    void addUnfinishedEntities( Collection<GowingEntityReference> collection );

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

    void saveTypeAlias( StdGowingTokenizer.GowingToken2 typeIdToken, StdGowingTokenizer.GowingToken2 typeNameToken )
	    throws GowingUnPackerParsingException;

    void setInputFile( File inputFile );

    File getInputFile();

    void setRequestorContext( GowingRequestorContext requestorContext );

    GowingRequestorContext getRequestorContext();

}
