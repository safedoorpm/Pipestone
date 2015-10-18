package com.obtuse.util.gowing.packer2.p2a;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.TreeSorter;
import com.obtuse.util.gowing.packer2.EntityName2;
import com.obtuse.util.gowing.packer2.GowingPackable2;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 Used internally by {@link StdGowingUnPacker2A} to carry around the result of a parse.
 <p/>Should probably be an inner class although other unpackers might find it useful.
 */

public class GowingDePackedEntityGroup {

    private final GowingFormatVersion _version;

    private final FormattingLinkedList<GowingPackable2> _allEntities = new FormattingLinkedList<GowingPackable2>();

    private final TreeSorter<EntityName2,GowingPackable2> _namedClasses = new TreeSorter<EntityName2, GowingPackable2>();

    @SuppressWarnings("WeakerAccess")
    protected GowingDePackedEntityGroup( @NotNull GowingFormatVersion version ) {
	super();

	_version = version;

    }

    public GowingFormatVersion getFormatVersion() {

	return _version;

    }

    @SuppressWarnings("WeakerAccess")
    public EntityName2 getGroupName() {

	return _version.getGroupName();

    }

    @SuppressWarnings("UnusedReturnValue")
    public GowingDePackedEntityGroup add( @NotNull Collection<EntityName2> classNames, @NotNull GowingPackable2 newEntity ) {

	_allEntities.add( newEntity );
	for ( EntityName2 className : classNames ) {

	    _namedClasses.add( className, newEntity );

	}

	return this;

    }

    public TreeSorter<EntityName2, GowingPackable2> getNamedClasses() {

	return _namedClasses;

    }

    public FormattingLinkedList<GowingPackable2> getAllEntities() {

	return _allEntities;

    }

    public int size() {

	return _allEntities.size();

    }

    public boolean isEmpty() {

	return _allEntities.isEmpty();

    }

    public String toString() {

	return "GowingDePackedEntityGroup( \"" + getGroupName() + "\", " + getAllEntities() + " )";

    }

}
