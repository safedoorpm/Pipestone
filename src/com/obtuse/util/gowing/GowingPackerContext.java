package com.obtuse.util.gowing;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Describe something that manages type ids and entity ids during a packing operation.
 <p/>Note that the lifespan of a packing context is intended to be the duration of a single packing operation.
 */

public interface GowingPackerContext {

//    PackingId2 allocatePackingId( EntityTypeName entityTypeName );

//    PackingId2 allocatePackingId( EntityTypeName entityTypeName, long idWithinType );

//    long getHighestPackingIdForType( EntityTypeName entityTypeName );

//    int getOrAllocateTypeReferenceId( Packable2 entity );

//    int getOrAllocateTypeReferenceId( EntityTypeName typeName );

//    @Nullable
//    Integer findTypeReferenceId( EntityTypeName typeName );

//    int getTypeReferenceId( EntityTypeName typeName );

    void rememberPackableEntity( EntityName entityName, GowingPackable entity );

    @NotNull
    EntityNames getEntityNames( GowingInstanceId instanceId );

    @NotNull
    Collection<Integer> getNewTypeIds();

//    @Nullable
//    StdPackingContext2.PackingAssociation findPackingAssociation( InstanceId instanceId );

//    @NotNull
//    TypeIndex2 getTypeIndex();

//    @Nullable
//    EntityTypeName findTypeByTypeReferenceId( int typeReferenceId );
//
//    @NotNull
//    EntityTypeName getTypeByTypeReferenceId( int typeReferenceId );

//    @Nullable
//    EntityTypeInfo findTypeInfo( int typeReferenceId );
//
//    @NotNull
//    EntityTypeInfo getTypeInfo( int typeReferenceId );
//
//    @Nullable
//    EntityTypeInfo findTypeInfo( @NotNull EntityTypeName typeName );
//
//    @NotNull
//    EntityTypeInfo getTypeInfo( @NotNull EntityTypeName typeName );

//    boolean isTypeNameKnown( EntityTypeName typeName );

//    EntityTypeInfo registerFactory( EntityFactory2 factory );

    Collection<GowingInstanceId> getSeenInstanceIds();

    int rememberTypeName( EntityTypeName typeName );

    void setRequestorContext( GowingRequestorContext requestorContext );

    GowingRequestorContext getRequestorContext();

//    void saveTypeAlias( P2ATokenizer.P2AToken typeIdToken, P2ATokenizer.P2AToken typeNameToken )
//	    throws UnPacker2ParseError;

}
