package com.obtuse.util.gowing.packer2;

import com.obtuse.util.gowing.packer2.p2a.GowingEntityReference;
import com.obtuse.util.gowing.packer2.p2a.GowingTokenizer2;
import com.obtuse.util.gowing.packer2.p2a.GowingUnPacker2ParsingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Describe something that manages type ids and entity ids during an unpacking operation.
 <p/>Note that the lifespan of a packing context is intended to be the duration of a single unpacking operation.
 */

public interface GowingUnPackerContext2 {

//    PackingId2 allocatePackingId( EntityTypeName2 entityTypeName );

//    PackingId2 allocatePackingId( EntityTypeName2 entityTypeName, long idWithinType );

//    long getHighestPackingIdForType( EntityTypeName2 entityTypeName );

//    int getOrAllocateTypeReferenceId( Packable2 entity );

//    int getOrAllocateTypeReferenceId( EntityTypeName2 typeName );

    @Nullable
    Integer findTypeReferenceId( EntityTypeName2 typeName );

    int getTypeReferenceId( EntityTypeName2 typeName );

    boolean isEntityKnown( GowingEntityReference er );

    GowingPackable2 recallPackableEntity( @NotNull GowingEntityReference er );

    Collection<GowingEntityReference> getSeenEntityReferences();

    void rememberPackableEntity( GowingTokenizer2.GowingToken2 token, GowingEntityReference etr, GowingPackable2 entity );

//    @NotNull
//    Packable2 getInstance( InstanceId instanceId );

    @NotNull
    Collection<EntityTypeName2> getNewTypeNames();

//    @Nullable
//    StdPackingContext2.PackingAssociation findPackingAssociation( InstanceId instanceId );

    @NotNull
    GowingTypeIndex2 getTypeIndex();

    void clearUnFinishedEntities();

    Collection<GowingEntityReference> getUnfinishedEntities();

    void markEntitiesUnfinished( Collection<GowingEntityReference> unFinishedEntities );

    boolean isEntityFinished( GowingEntityReference er );

    void markEntityFinished( GowingEntityReference er );

    void addUnfinishedEntities( Collection<GowingEntityReference> collection );

    @Nullable
    EntityTypeName2 findTypeByTypeReferenceId( int typeReferenceId );

    @NotNull
    EntityTypeName2 getTypeByTypeReferenceId( int typeReferenceId );

    @Nullable
    EntityTypeInfo2 findTypeInfo( int typeReferenceId );

    @NotNull
    EntityTypeInfo2 getTypeInfo( int typeReferenceId );

    @Nullable
    EntityTypeInfo2 findTypeInfo( @NotNull EntityTypeName2 typeName );

    @NotNull
    EntityTypeInfo2 getTypeInfo( @NotNull EntityTypeName2 typeName );

    boolean isTypeNameKnown( EntityTypeName2 typeName );

    @SuppressWarnings("UnusedReturnValue")
    EntityTypeInfo2 registerFactory( GowingEntityFactory2 factory );

//    Collection<InstanceId> getSeenInstanceIds();

    void saveTypeAlias( GowingTokenizer2.GowingToken2 typeIdToken, GowingTokenizer2.GowingToken2 typeNameToken )
	    throws GowingUnPacker2ParsingException;

}
