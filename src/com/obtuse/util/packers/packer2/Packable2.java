package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 %%% Something clever goes here.
 */

public interface Packable2 {

    void packThyself( Packer2 packer );

    void finishUnpacking( UnPacker2 unPacker );

    PackingId2 getPackingId();

}
