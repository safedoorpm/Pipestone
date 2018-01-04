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

    void rememberPackableEntity( EntityName entityName, GowingPackable entity );

    @NotNull
    EntityNames getEntityNames( GowingInstanceId instanceId );

    @NotNull
    Collection<Integer> getNewTypeIds();

    Collection<GowingInstanceId> getSeenInstanceIds();

    int rememberTypeName( EntityTypeName typeName );

    void setRequestorContext( GowingRequestorContext requestorContext );

    GowingRequestorContext getRequestorContext();

}
