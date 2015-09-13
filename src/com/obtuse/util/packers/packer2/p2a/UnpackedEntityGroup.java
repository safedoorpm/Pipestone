package com.obtuse.util.packers.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.packers.packer2.EntityName2;
import com.obtuse.util.packers.packer2.Packable2;
import org.jetbrains.annotations.NotNull;

/**
 Used internally by {@link StdUnPacker2a} to carry around the result of a parse.
 <p/>Should probably be an inner class although other unpackers might find it useful.
 */

public class UnpackedEntityGroup {

    private final FormatVersion _version;

    private final FormattingLinkedList<Packable2> _entities = new FormattingLinkedList<Packable2>();

    @SuppressWarnings("WeakerAccess")
    protected UnpackedEntityGroup( @NotNull FormatVersion version ) {
	super();

	_version = version;

    }

    public FormatVersion getFormatVersion() {

	return _version;

    }

    @SuppressWarnings("WeakerAccess")
    public EntityName2 getGroupName() {

	return _version.getGroupName();

    }

    @SuppressWarnings("UnusedReturnValue")
    public UnpackedEntityGroup add( Packable2 newEntity ) {

	_entities.add( newEntity );

	return this;

    }

    public FormattingLinkedList<Packable2> getEntities() {

	return _entities;

    }

    public int size() {

	return _entities.size();

    }

    public boolean isEmpty() {

	return _entities.isEmpty();

    }

    public String toString() {

	return "UnpackedEntityGroup( \"" + getGroupName() + "\", " + getEntities() + " )";

    }

}
