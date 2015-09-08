package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

import java.util.SortedSet;

/**
 Describe something that is packable.
 */

public interface Packable2 {

    InstanceId getInstanceId();

    @NotNull
    PackedEntityBundle bundleThyself( PackingId2 packingId, boolean isPackingSuper, Packer2 packer );

    void finishUnpacking( UnPacker2 unPacker );

//    PackingId2 getPackingId();

}
