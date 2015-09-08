package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.SimpleUniqueLongIdGenerator;
import org.jetbrains.annotations.NotNull;

/**
 A value which uniquely identifies something which implements the {@link Packable2} interface.
 <p/>
 Notes:
 <ol>
 <li>Every packable entity must have a JVM-wide-unique instance id which never changes during the entity's lifespan within a particular JVM.</li>
 <li>The instance id of a packable entity must be created by invoking this class's default constructor.</li>
 <li>Packable entities <b>MUST NOT</b> assume that their instance id will remain the same if they are packed and then later unpacked.</li>
 </ol>
 */

public final class InstanceId implements Comparable<InstanceId> {

    private static final SimpleUniqueLongIdGenerator _idGenerator = new SimpleUniqueLongIdGenerator( InstanceId.class.getCanonicalName() );

    private final Long _id;

    private final EntityTypeName2 _typeName;

    public InstanceId( EntityTypeName2 typeName ) {
	super();

	_typeName = typeName;
	_id = _idGenerator.getUniqueId();

    }

    public long getId() {

	return _id;

    }

    public EntityTypeName2 getTypeName() {

	return _typeName;

    }

    public int compareTo( @NotNull InstanceId rhs ) {

	int rval = _typeName.compareTo( rhs._typeName );
	if ( rval == 0 ) {

	    rval = _id.compareTo( rhs._id );

	}

	return rval;

    }

    public boolean equals( Object rhs ) {

	return rhs instanceof InstanceId && compareTo( (InstanceId) rhs ) == 0;

    }

    public int hashCode() {

	return _id.hashCode();

    }

    public String toString() {

	return "InstanceId( " + _typeName + ", " + _id + " )";

    }

}
