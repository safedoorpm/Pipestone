package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.SimpleUniqueIntegerIdGenerator;
import com.obtuse.util.SimpleUniqueLongIdGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.SortedMap;
import java.util.TreeMap;

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

    private static final SimpleUniqueLongIdGenerator s_idGenerator = new SimpleUniqueLongIdGenerator( InstanceId.class.getCanonicalName() + " - entity id generator" );
    private static final SimpleUniqueIntegerIdGenerator s_typeIdGenerator = new SimpleUniqueIntegerIdGenerator( InstanceId.class.getCanonicalName() + " - type id generator" );

    private static final SortedMap<EntityTypeName2,Integer> s_typeNamesToTypeIds = new TreeMap<EntityTypeName2, Integer>();
    private static final SortedMap<Integer,EntityTypeName2> s_typeIdsToTypeNames = new TreeMap<Integer, EntityTypeName2>();

    private final Long _entityId;

    private final int _typeId;

    private final EntityTypeName2 _typeName;

    public InstanceId( EntityTypeName2 typeName ) {
	super();

	_typeId = allocateTypeId( typeName );

	_typeName = typeName;
	_entityId = s_idGenerator.getUniqueId();

    }

    public static int allocateTypeId( EntityTypeName2 typeName ) {

	synchronized ( s_typeNamesToTypeIds ) {

	    Integer typeId = lookupTypeId( typeName );
	    if ( typeId == null ) {

		typeId = s_typeIdGenerator.getUniqueId();
		s_typeNamesToTypeIds.put( typeName, typeId );
		s_typeIdsToTypeNames.put( typeId, typeName );

	    }

	    return typeId;

	}

    }

    public static EntityTypeName2 lookupTypeName( int typeId ) {

	return s_typeIdsToTypeNames.get( typeId );

    }

    public static Integer lookupTypeId( EntityTypeName2 typeName ) {

	return s_typeNamesToTypeIds.get( typeName );

    }

    public long getEntityId() {

	return _entityId;

    }

    public int getTypeId() {

	return _typeId;

    }

    public EntityTypeName2 getTypeName() {

	return _typeName;

    }

    public int compareTo( @NotNull InstanceId rhs ) {

	int rval = _typeName.compareTo( rhs._typeName );
	if ( rval == 0 ) {

	    rval = _entityId.compareTo( rhs._entityId );

	}

	return rval;

    }

    public boolean equals( Object rhs ) {

	return rhs instanceof InstanceId && compareTo( (InstanceId) rhs ) == 0;

    }

    public int hashCode() {

	return _entityId.hashCode();

    }

    public String toString() {

	return "InstanceId( " + _typeName + ", " + _entityId + " )";

    }

}
