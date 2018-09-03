/*
 * Copyright © 2018 Daniel Boulet
 * All rights reserved.
 */

package com.obtuse.util;

import com.obtuse.util.SimpleUniqueIntegerIdGenerator;
import com.obtuse.util.UniqueWithId;
import com.obtuse.util.UniquelyNamedEntity;
import org.jetbrains.annotations.NotNull;

/**
 Created by danny on 2018/06/30.
 */

public abstract class UniqueWidget implements UniquelyNamedEntity, UniqueWithId {

    public static final SimpleUniqueIntegerIdGenerator
            WIDGET_ID_GENERATOR = new SimpleUniqueIntegerIdGenerator( "CopierWidget family's id generator" );

    private final long _id;
    private final String _name;

    protected UniqueWidget( final @NotNull String name ) {
        super();

        _id = WIDGET_ID_GENERATOR.getUniqueId();

        _name = name + '-' + _id;

    }

    public final String getName() {

        return _name;

    }

    public final long getId() {

        return _id;

    }

    public abstract String toString();

}
