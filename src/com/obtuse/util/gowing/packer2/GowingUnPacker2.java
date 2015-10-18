package com.obtuse.util.gowing.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.gowing.packer2.p2a.GowingDePackedEntityGroup;
import com.obtuse.util.gowing.packer2.p2a.GowingEntityReference;
import org.jetbrains.annotations.Nullable;

/**
 Top level unpacking API.
 */

public interface GowingUnPacker2 {

    @Nullable
    GowingDePackedEntityGroup unPack();

    GowingPackable2 resolveReference( GowingEntityReference er );

    GowingUnPackerContext2 getUnPackerContext();

    boolean isEntityFinished( GowingEntityReference entityReference );

}
