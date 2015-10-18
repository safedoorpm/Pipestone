package com.obtuse.util.gowing.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Describe something that is packable.
 */

public interface GowingPackable2 {

    @NotNull
    GowingInstanceId getInstanceId();

    @NotNull
    GowingPackedEntityBundle bundleThyself( boolean isPackingSuper, GowingPacker2 packer );

    boolean finishUnpacking( GowingUnPacker2 unPacker );

}
