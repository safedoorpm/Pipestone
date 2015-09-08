package com.obtuse.util.packers.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.packers.packer2.p2a.StdPacker2a;

/**
 Describe a reference to a {@link Packable2} object (the object itself is described elsewhere).
 */
@Deprecated
public class InstanceReference {

    private InstanceReference() {
	super();

    }

//    private final TypeIndex2 _typeIndex;
//
//    private final EntityTypeInfo2 _entityTypeInfo;
//
//    private final int _entityInstanceId;
//
//    private InstanceReference( TypeIndex2 typeIndex, InstanceId instanceId ) {
//	super();
//
//	_typeIndex = typeIndex;
//	_entityTypeInfo = _typeIndex.getTypeInfo( entityTypeName );
//	_entityInstanceId = entityInstanceId;
//
//    }
//
//    public TypeIndex2 getTypeIndex() {
//
//	return _typeIndex;
//
//    }
//
//    public EntityTypeInfo2 getEntityTypeInfo() {
//
//	return _entityTypeInfo;
//
//    }
//
//    public EntityTypeName2 getEntityTypeName() {
//
//	return _entityTypeInfo.getTypeName();
//
//    }
//
//    public int getEntityInstanceId() {
//
//	return _entityInstanceId;
//
//    }
//
//    public String getReference() {
//
//	return Character.toString( StdPacker2a.TAG_ENTITY_REFERENCE ) + _entityTypeInfo.getReferenceId() + '#' + _entityInstanceId;
//
//    }
//
//    public String toString() {
//
//	return "IE( " + _entityTypeInfo + ", " + _entityInstanceId + " )";
//
//    }

}
