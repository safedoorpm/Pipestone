package com.obtuse.util.packers.packer1;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;

/**
 * Describe how a packer behaves.
 */

public interface Packer1 {

    @NotNull
    PackingId1 packEntity( Packable1 entity );

    @NotNull
    PackingId1 packOptionalEntity( Packable1 optEntity );

    void startPackingEntity( Packable1 entity );

    void packMandatoryString( String payload );

}
