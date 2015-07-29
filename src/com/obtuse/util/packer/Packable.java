package com.obtuse.util.packer;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 * Describe something which is packable and unpackable by this facility.
 */

public interface Packable {

    void finishUnpacking( UnPacker unPacker );

    void packThyself( com.obtuse.util.packer.Packer packer );
}
