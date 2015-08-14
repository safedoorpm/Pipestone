package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 %%% Something clever goes here.
 */

public interface PackingContext2 {

    PackingId2 allocatePackingId( EntityTypeName2 entityTypeName );

    PackingId2 allocatePackingId( EntityTypeName2 entityTypeName, long idWithinType );

    long getHighestPackingIdForType( EntityTypeName2 entityTypeName );

    @Nullable
    EntityFactory2 findFactory( @NotNull EntityTypeName2 typeName );

    @NotNull
    EntityFactory2 getFactory( @NotNull EntityTypeName2 typeName );

    boolean isTypeNameKnown( EntityTypeName2 typeName );
}
