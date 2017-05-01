package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.gowing.EntityName;
import com.obtuse.util.gowing.GowingPackable;
import com.obtuse.util.gowing.GowingPackableThingHolder;
import com.obtuse.util.gowing.GowingPacker;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Manage the serialization and de-serialization of simple types.
 */

public abstract class GowingAbstractPackableHolder implements GowingPackableThingHolder {

    public enum Kind {
        SCALAR,
	PRIMITIVE_ARRAY,
	CONTAINER_ARRAY
    }

    private final EntityName _name;
    private final char _tag;
    private final Object _objectValue;
    private final boolean _mandatory;
    private final Kind _arrayKind;

    @SuppressWarnings("WeakerAccess")
    protected GowingAbstractPackableHolder( EntityName name, char tag, Object objectValue, boolean mandatory ) {
	super();

	_tag = tag;
	_name = name;
	_objectValue = objectValue;
	_mandatory = mandatory;
	_arrayKind = Kind.SCALAR;

	if ( _mandatory && _objectValue == null ) {

	    throw new IllegalArgumentException( "mandatory " + name + " value is null" );

	}

    }

    @SuppressWarnings("WeakerAccess")
    protected GowingAbstractPackableHolder( EntityName name, char tag, Object objectValue, boolean mandatory, boolean primitiveArray ) {
	super();

	_tag = tag;
	_name = name;
	_objectValue = objectValue;
	_mandatory = mandatory;
	_arrayKind = primitiveArray ? Kind.PRIMITIVE_ARRAY : Kind.CONTAINER_ARRAY;

	if ( _mandatory && _objectValue == null ) {

	    throw new IllegalArgumentException( "mandatory " + name + " value is null" );

	}

    }

    @NotNull
    Kind getKind() {

        return _arrayKind;

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
    public byte[] PrimitiveByteArrayValue() {

	return (byte[])getObjectValue();

    }

    @Override
    public Byte[] ContainerByteArrayValue() {

	return (Byte[])getObjectValue();

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
    public GowingPackable[] PackableArrayValue() {

	return (GowingPackable[])getObjectValue();

    }

    public EntityName getName() {

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
    public boolean pack( GowingPacker packer2 ) {

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

    public int compareTo( @NotNull GowingPackableThingHolder rhs ) {

	return _name.compareTo( rhs.getName() );

    }

    public int hashCode() {

	return _name.hashCode();

    }

    public boolean equals( Object rhs ) {

	return rhs instanceof GowingPackableThingHolder && _name.equals( ((GowingPackableThingHolder)rhs).getName() );

    }

    public String toString() {

	return "NTH2( \"" + getName() + "\", " + getObjectValue() + " )";

    }

}
