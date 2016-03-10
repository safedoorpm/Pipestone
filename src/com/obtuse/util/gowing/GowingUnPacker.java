package com.obtuse.util.gowing;

import com.obtuse.util.gowing.p2a.GowingDePackedEntityGroup;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import org.jetbrains.annotations.Nullable;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Top level unpacking API.
 */

public interface GowingUnPacker {

    @Nullable
    GowingDePackedEntityGroup unPack();

    GowingPackable resolveReference( GowingEntityReference er );

    GowingUnPackerContext getUnPackerContext();

    boolean isEntityFinished( GowingEntityReference entityReference );

}
