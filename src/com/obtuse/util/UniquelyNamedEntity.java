package com.obtuse.util;

/*
 * Copyright © 2012 Obtuse Systems Corporation
 */

/**
 * A {@link com.obtuse.util.NamedEntity} which has a unique name within some namespace.
 */

public interface UniquelyNamedEntity extends NamedEntity {

    public String getUniqueName();

}
