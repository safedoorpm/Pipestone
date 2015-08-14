package com.obtuse.util.packers.packer1;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 * Describe something which is packable and unpackable by this facility.
 */

public interface Packable1 {

    void finishUnpacking( UnPacker1 unPacker );

    void packThyself( Packer1 packer );

}
