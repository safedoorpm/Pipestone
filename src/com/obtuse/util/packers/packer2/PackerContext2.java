package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 Describe something that manages type ids and entity ids during a packing operation.
 <p/>Note that the lifespan of a packing context is intended to be the duration of a single packing operation.
 */

public interface PackerContext2 {

//    PackingId2 allocatePackingId( EntityTypeName2 entityTypeName );

//    PackingId2 allocatePackingId( EntityTypeName2 entityTypeName, long idWithinType );

//    long getHighestPackingIdForType( EntityTypeName2 entityTypeName );

//    int getOrAllocateTypeReferenceId( Packable2 entity );

//    int getOrAllocateTypeReferenceId( EntityTypeName2 typeName );

//    @Nullable
//    Integer findTypeReferenceId( EntityTypeName2 typeName );

//    int getTypeReferenceId( EntityTypeName2 typeName );

    void rememberPackableEntity( Packable2 entity );

    @NotNull
    Packable2 getInstance( InstanceId instanceId );

    @NotNull
    Collection<Integer> getNewTypeIds();

//    @Nullable
//    StdPackingContext2.PackingAssociation findPackingAssociation( InstanceId instanceId );

//    @NotNull
//    TypeIndex2 getTypeIndex();

//    @Nullable
//    EntityTypeName2 findTypeByTypeReferenceId( int typeReferenceId );
//
//    @NotNull
//    EntityTypeName2 getTypeByTypeReferenceId( int typeReferenceId );

//    @Nullable
//    EntityTypeInfo2 findTypeInfo( int typeReferenceId );
//
//    @NotNull
//    EntityTypeInfo2 getTypeInfo( int typeReferenceId );
//
//    @Nullable
//    EntityTypeInfo2 findTypeInfo( @NotNull EntityTypeName2 typeName );
//
//    @NotNull
//    EntityTypeInfo2 getTypeInfo( @NotNull EntityTypeName2 typeName );

//    boolean isTypeNameKnown( EntityTypeName2 typeName );

//    EntityTypeInfo2 registerFactory( EntityFactory2 factory );

    Collection<InstanceId> getSeenInstanceIds();

    int rememberTypeName( EntityTypeName2 typeName );

//    void saveTypeAlias( P2ATokenizer.P2AToken typeIdToken, P2ATokenizer.P2AToken typeNameToken )
//	    throws UnPacker2ParseError;

}
