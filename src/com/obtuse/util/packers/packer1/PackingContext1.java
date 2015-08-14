package com.obtuse.util.packers.packer1;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Describe how a packing context must behave.
 */

public interface PackingContext1 {

    PackingId1 allocatePackingId( short typeId );

    long getHighestPackingIdForType( short typeId );

    @NotNull
    TypeIndex1 getTypeIndex();

    short getTypeId( String typeName );

    @NotNull
    String getTypeName( short typeId );

    @Nullable
    EntityFactory1 findFactory( @NotNull String typeName );

    @NotNull
    EntityFactory1 getFactory( @NotNull String typeName );

    @Nullable
    EntityFactory1 findFactory( short typeId );

    @NotNull
    EntityFactory1 getFactory( short typeId );

    boolean isTypeNameKnown( String typeName );

    boolean isTypeIdKnown( short typeId );

    PackingId1 allocatePackingId( short typeId, long idWithinType );
}
