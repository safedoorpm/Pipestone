package com.obtuse.util.gowing;

import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 A reasonable base class for something that is packable.
 */

public abstract class GowingAbstractPackableEntity implements GowingPackable {

    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    protected GowingAbstractPackableEntity() {
	super();

    }

    @Override
    @NotNull
    public final GowingInstanceId getInstanceId() {

	return _instanceId;

    }

}
