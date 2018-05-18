package com.obtuse.util.gowing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Describe something that manages type ids and entity ids during a packing operation.
 <p/>Note that the lifespan of a packing context is intended to be the duration of a single packing operation.
 */

public interface GowingPackerContext {

    void rememberPackableEntity( @Nullable EntityName entityName, @NotNull GowingPackable entity );

    @NotNull
    EntityNames getEntityNames( GowingInstanceId instanceId );

    @NotNull
    List<Integer> getAndResetNewTypeIds();

    @NotNull
    Set<Integer> getSeenTypeIds();

    @NotNull
    Set<GowingInstanceId> getSeenInstanceIds();

    int rememberTypeName( EntityTypeName typeName );

    void setRequestorContext( GowingRequestorContext requestorContext );

    GowingRequestorContext getRequestorContext();

    void rememberTopTypeId( int typeId );

    Set<Integer> getTopTypeIds();

    long remapEntityId( int typeId, long entityId );

}
