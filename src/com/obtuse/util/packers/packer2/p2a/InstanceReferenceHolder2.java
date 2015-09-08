package com.obtuse.util.packers.packer2.p2a;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.packers.packer2.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Pack a {@link com.obtuse.util.packers.packer2.Packable2} value.
 */
@Deprecated
public class InstanceReferenceHolder2 /*extends AbstractPackableHolder2*/ {

    private InstanceReferenceHolder2() {
	super();

    }

//    private final TypeIndex2 _typeIndex;
//
////    private final EntityTypeName2 _typeName;
//
//    private final EntityFactory2 _factory;
//
//    public InstanceReferenceHolder2(
//	    @NotNull EntityName2 name,
//	    @NotNull TypeIndex2 typeIndex,
////	    @NotNull EntityTypeName2 typeName,
//	    @NotNull EntityFactory2 factory,
//	    @Nullable Packable2 v,
//	    boolean mandatory
//    ) {
//
//	super( name, StdPacker2a.TAG_ENTITY_REFERENCE, v, mandatory );
//
//	_typeIndex = typeIndex;
////	_typeName = typeName;
//	_factory = factory;
//
//    }
//
//    @Override
//    public boolean pack( Packer2 packer2, String separator ) {
//
//	if ( separator != null ) {
//
//	    packer2.emit( separator );
//
//	}
//
//	Object value = getObjectValue();
//	if ( value == null ) {
//
//	    if ( isMandatory() ) {
//
//		throw new HowDidWeGetHereError( "mandatory " + getTypeName() + " found to be null after we should have already bailed because it was null" );
//
//	    }
//
//	    packer2.emitNull();
//
//	    return true;
//
//	}
//
//	packer2.emitName( getName() );
//	packer2.emit( '=' );
//
//	EntityTypeInfo2 typeInfo = _typeIndex.findTypeInfo( getTypeName() );
//	if ( typeInfo == null ) {
//
//	    typeInfo = _typeIndex.addFactory( _factory );
//
//	}
//
//
//
//	    packer2.emit( StdPacker2a.TAG_ENTITY_UNKNOWN );
//	    packer2.emit( '<' );
//	    packer2.emit( getTypeName() );
//	    packer2.emit( ',' );
//	    packer2.emit( typeInfo.getReferenceId() );
//	    packer2.emit( '>' );
//
//	    packer2.emit( '(' );
//	    emitRepresentation( packer2 );
//	    packer2.emit( ')' );
//
//	}
//
//	return true;
//
//    }
//
//    public void emitRepresentation( Packer2 packer2 ) {
//
//	Object value = getObjectValue();
//	if ( isMandatory() || value != null ) {
//
//	    packer2.emit( (String) value );
//
//	} else {
//
//	    packer2.emitNull();
//
//	}
//
//    }
//
//    @NotNull
//    public TypeIndex2 getTypeIndex() {
//
//	return _typeIndex;
//
//    }
//
//    @NotNull
//    public EntityTypeName2 getTypeName() {
//
//	return getFactory().getTypeName();
//
//    }
//
//    @NotNull
//    public EntityFactory2 getFactory() {
//
//	return _factory;
//
//    }

}
