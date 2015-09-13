package com.obtuse.util.packers.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.packers.packer2.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 Carry around the result of a parse..
 */

public class UnpackedEntityGroup {

    private final FormatVersion _version;

    private final FormattingLinkedList<Packable2> _entities = new FormattingLinkedList<Packable2>();

    protected UnpackedEntityGroup( @NotNull FormatVersion version ) {
	super();

	_version = version;

    }

    public FormatVersion getFormatVersion() {

	return _version;

    }

    public EntityName2 getGroupName() {

	return _version.getGroupName();

    }

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
