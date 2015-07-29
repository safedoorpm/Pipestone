package com.obtuse.util.packer;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

/**
 * Describe how a packer behaves.
 */

public interface Packer {

    @NotNull
    PackingId packEntity( Packable entity );

    @NotNull
    PackingId packOptionalEntity( Packable optEntity );

}
