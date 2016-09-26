/*
 Copyright © 2014 Daniel Boulet
 */

package com.obtuse.util;

/**
 * A {@link com.obtuse.util.NamedEntity} which has a unique name within some namespace.
 * <p/>It is, of course, the developer's responsibility to ensure that instances of classes which implement this interface actually
 * have names which are unique within the appropriate namespace.
 * <p/>Note that this is just a marker interface since it extends {@link NamedEntity} which already specifies how things with names should behave.
 */

@SuppressWarnings("UnusedDeclaration")
public interface UniquelyNamedEntity extends NamedEntity {

//    public String getUniqueName();

}
