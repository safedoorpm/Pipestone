package com.obtuse.util;

import org.jetbrains.annotations.NotNull;

/**
 Created by danny on 2019/12/04.
 */

public abstract class NamedThing implements NamedEntity {

    private final String _name;
    private final String _enquotedName;

    public NamedThing( @NotNull final String name ) {
        super();

        _name = name;
        _enquotedName = ObtuseUtil.enquoteToJavaString( _name );

    }

    @Override
    public String getName() {

        return null;

    }

    public String toString() {

        return "NamedThing( " + _enquotedName + " )";

    }

}
