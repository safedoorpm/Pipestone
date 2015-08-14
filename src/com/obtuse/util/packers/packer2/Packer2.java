package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import java.io.Closeable;

/**
 Top-level entity packing API.
 */

public interface Packer2 extends Closeable {

    BackRef2 packEntity( EntityName2 entityName, Packable2 entity );

}
