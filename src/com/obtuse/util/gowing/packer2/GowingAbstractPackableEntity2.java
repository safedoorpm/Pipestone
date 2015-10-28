package com.obtuse.util.gowing.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 A reasonable base class for something that is packable.
 */

public abstract class GowingAbstractPackableEntity2 implements GowingPackable2 {

    private final GowingInstanceId _instanceId = new GowingInstanceId( getClass() );

    protected GowingAbstractPackableEntity2() {
	super();

    }

    @Override
    @NotNull
    public final GowingInstanceId getInstanceId() {

	return _instanceId;

    }

}
