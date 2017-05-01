package com.obtuse.util.gowing;

import com.obtuse.util.gowing.p2a.GowingDePackedEntityGroup;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.Optional;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Top level unpacking API.
 */

public interface GowingUnPacker extends Closeable {

    @NotNull
    Optional<GowingDePackedEntityGroup> unPack();

    GowingPackable resolveReference( GowingEntityReference er );

    GowingUnPackerContext getUnPackerContext();

    boolean isEntityFinished( GowingEntityReference entityReference );

}
