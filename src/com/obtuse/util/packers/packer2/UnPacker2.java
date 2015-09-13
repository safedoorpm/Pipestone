package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.packers.packer2.p2a.EntityReference;

/**
 Top level unpacking API.
 */

public interface UnPacker2 {

    Packable2 resolveReference( EntityReference er );

}
