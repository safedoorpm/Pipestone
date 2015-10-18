package com.obtuse.util.gowing.packer2.p2a.holders;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.gowing.packer2.*;
import com.obtuse.util.gowing.packer2.EntityName2;
import com.obtuse.util.gowing.packer2.GowingPackable2;
import com.obtuse.util.gowing.packer2.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;

/**
 Manage the serialization and de-serialization of simple types.
 */

public abstract class GowingAbstractPackableHolder2 implements GowingPackable2ThingHolder2 {

    private final EntityName2 _name;
    private final char _tag;
    private final Object _objectValue;
    private final boolean _mandatory;

    @SuppressWarnings("WeakerAccess")
    protected GowingAbstractPackableHolder2( EntityName2 name, char tag, Object objectValue, boolean mandatory ) {
	super();

	_tag = tag;
	_name = name;
	_objectValue = objectValue;
	_mandatory = mandatory;

	if ( _mandatory && _objectValue == null ) {

	    throw new IllegalArgumentException( "mandatory " + name + " value is null" );

	}

    }

    @Override
    public boolean isNull() {

	return getObjectValue() == null;

    }

    @Override
    public GowingEntityReference EntityTypeReference() {

	return (GowingEntityReference) getObjectValue();

    }

    @Override
    public boolean booleanValue() {

	return BooleanValue().booleanValue();

    }

    @Override
    public Boolean BooleanValue() {

	return (Boolean)getObjectValue();

    }

    @Override
    public byte byteValue() {

	return ByteValue().byteValue();

    }

    @Override
    public Byte ByteValue() {

	return (Byte)getObjectValue();

    }

    @Override
    public short shortValue() {

	return ShortValue().shortValue();

    }

    @Override
    public Short ShortValue() {

	return (Short)getObjectValue();

    }

    @Override
    public int intValue() {

	return IntegerValue().intValue();

    }

    @Override
    public Integer IntegerValue() {

	return (Integer)getObjectValue();

    }

    @Override
    public long longValue() {

	return LongValue().longValue();

    }

    @Override
    public Long LongValue() {

	return (Long)getObjectValue();

    }

    @Override
    public float floatValue() {

	return FloatValue().floatValue();

    }

    @Override
    public Float FloatValue() {

	return (Float)getObjectValue();

    }

    @Override
    public double doubleValue() {

	return DoubleValue().doubleValue();

    }

    @Override
    public Double DoubleValue() {

	return (Double)getObjectValue();

    }

    @Override
    public Number NumberValue() {

	return (Number)getObjectValue();

    }

    @Override
    public char charValue() {

	return CharacterValue().charValue();

    }

    @Override
    public Character CharacterValue() {

	return (Character)getObjectValue();

    }

    @Override
    public String StringValue() {

	return (String)getObjectValue();

    }

    @Override
    public int[] PrimitiveIntegerArrayValue() {

	return (int[])getObjectValue();

    }

    @Override
    public Integer[] ContainerIntegerArrayValue() {

	return (Integer[])getObjectValue();

    }

    @Override
    public long[] PrimitiveLongArrayValue() {

	return (long[])getObjectValue();

    }

    @Override
    public Long[] ContainerLongArrayValue() {

	return (Long[])getObjectValue();

    }

    @Override
    public String[] StringArrayValue() {

	return (String[])getObjectValue();

    }

    @Override
    public GowingPackable2[] PackableArrayValue() {

	return (GowingPackable2[])getObjectValue();

    }

    public EntityName2 getName() {

	return _name;

    }

    public char getTag() {

	return _tag;

    }

    public Object getObjectValue() {

	return _objectValue;

    }

    @SuppressWarnings("WeakerAccess")
    public boolean isMandatory() {

	return _mandatory;

    }

    @Override
    public boolean pack( GowingPacker2 packer2 ) {

//	if ( separator != null ) {
//
//	    packer2.emit( separator );
//
//	}

//	packer2.emitName( getName() );

//	packer2.emit( '=' );
//	if ( !_mandatory ) {
//
//	    packer2.emit( '?' );
//
//	}
//	packer2.emit( _tag );

	emitRepresentation( packer2 );

//	packer2.emit( _mandatory ? '(' : '{' );
//	packer2.emit( _mandatory ? ')' : '}' );

	return false;

    }

//    /**
//     Emit the string representation using a mechanism that works for most types.
//     <p/>This method gets overridden in classes responsible for types that need special handling.
//     * @param packer2 where to send the string representation.
//     */
//
//    public void emitRepresentation( Packer2 packer2 ) {
//
//	Object value = getObjectValue();
//
//	if ( isMandatory() || value != null ) {
//
////	    packer2.emit( value.toString() );
//
//	    emitActualValue( packer2 );
//
//	} else {
//
//	    packer2.emitNull();
//
//	}
//
//    }

    public int compareTo( @NotNull GowingPackable2ThingHolder2 rhs ) {

	return _name.compareTo( rhs.getName() );

    }

    public int hashCode() {

	return _name.hashCode();

    }

    public boolean equals( Object rhs ) {

	return rhs instanceof GowingPackable2ThingHolder2 && _name.equals(((GowingPackable2ThingHolder2)rhs).getName() );

    }

    public String toString() {

	return "NTH2( \"" + getName() + "\", " + getObjectValue() + " )";

    }

}
