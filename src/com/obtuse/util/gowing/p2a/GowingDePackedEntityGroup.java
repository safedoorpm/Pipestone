package com.obtuse.util.gowing.p2a;

import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.TreeSorter;
import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPackable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Used internally by {@link StdGowingUnPacker} to carry around the result of a parse.
 <p/>Should probably be an inner class although other unpackers might find it useful.
 */

public class GowingDePackedEntityGroup {

    private final GowingFormatVersion _version;

    private final FormattingLinkedList<GowingPackable> _allEntities = new FormattingLinkedList<GowingPackable>();

    private final TreeSorter<EntityName,GowingPackable> _namedClasses = new TreeSorter<EntityName, GowingPackable>();

    @SuppressWarnings("WeakerAccess")
    protected GowingDePackedEntityGroup( @NotNull GowingFormatVersion version ) {
	super();

	_version = version;

    }

    public GowingFormatVersion getFormatVersion() {

	return _version;

    }

    @SuppressWarnings("WeakerAccess")
    public EntityName getGroupName() {

	return _version.getGroupName();

    }

    @SuppressWarnings("UnusedReturnValue")
    public GowingDePackedEntityGroup add( @NotNull Collection<EntityName> classNames, @NotNull GowingPackable newEntity ) {

	_allEntities.add( newEntity );
	for ( EntityName className : classNames ) {

	    _namedClasses.add( className, newEntity );

	}

	return this;

    }

    public TreeSorter<EntityName, GowingPackable> getNamedClasses() {

	return _namedClasses;

    }

    public FormattingLinkedList<GowingPackable> getAllEntities() {

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
