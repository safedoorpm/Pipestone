package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.packers.packer2.p2a.EntityReference;
import com.obtuse.util.packers.packer2.p2a.UnpackedEntityGroup;
import org.jetbrains.annotations.Nullable;

/**
 Top level unpacking API.
 */

public interface UnPacker2 {

    @Nullable
    UnpackedEntityGroup unPack();

    Packable2 resolveReference( EntityReference er );

    UnPackerContext2 getUnPackerContext();
}
