/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

/**
 * A {@link com.obtuse.util.NamedEntity} which has a unique name within some namespace.
 */

@SuppressWarnings("UnusedDeclaration")
public interface UniquelyNamedEntity extends NamedEntity {

    public String getUniqueName();

}
