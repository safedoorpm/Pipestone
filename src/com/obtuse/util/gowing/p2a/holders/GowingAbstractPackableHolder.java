package com.obtuse.util.gowing.p2a.holders;

import com.obtuse.util.Logger;
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
    protected GowingAbstractPackableHolder( final EntityName name, final char tag, final Object objectValue, final boolean mandatory ) {

        super();

        _tag = tag;
        _name = name;
        _objectValue = objectValue;
        _mandatory = mandatory;
        _arrayKind = Kind.SCALAR;

        if ( _mandatory && _objectValue == null ) {

            throw new IllegalArgumentException( "GowingAbstractPackableHolder(scalar):  mandatory " + name + " value is null" );

        }

    }

    @SuppressWarnings("WeakerAccess")
    protected GowingAbstractPackableHolder( final EntityName name, final char tag, final Object objectValue, final boolean mandatory, final boolean primitiveArray ) {

        super();

        _tag = tag;
        _name = name;
        _objectValue = objectValue;
        _mandatory = mandatory;
        _arrayKind = primitiveArray ? Kind.PRIMITIVE_ARRAY : Kind.CONTAINER_ARRAY;

        if ( _mandatory && _objectValue == null ) {

            throw new IllegalArgumentException( "GowingAbstractPackableHolder(" + _arrayKind + "):  mandatory " + name + " value is null" );

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

    public GowingEntityReference EntityTypeReference() {

        try {

            return (GowingEntityReference)getObjectValue();

        } catch ( Exception e ) {

            Logger.logErr( "GowingAbstractPackableHolder.EntityTypeReference:  object value is not an entity reference (it is a " + getObjectValue().getClass().getCanonicalName() + ")", e );

            throw e;

        }

    }

    @Override
    public boolean booleanValue() {

        Boolean booleanValue = BooleanValue();
        if ( booleanValue == null ) {

            throw new IllegalArgumentException( "null value" );

        }

        return booleanValue.booleanValue();

    }

    @Override
    public Boolean BooleanValue() {

        return (Boolean)getObjectValue();

    }

    @Override
    public boolean[] PrimitiveBooleanArrayValue() {

        return (boolean[])getObjectValue();

    }

    @Override
    public Boolean[] ContainerBooleanArrayValue() {

        return (Boolean[])getObjectValue();

    }

    @Override
    public byte byteValue() {

        Byte byteValue = ByteValue();
        if ( byteValue == null ) {

            throw new IllegalArgumentException( "null value" );

        }

        return byteValue.byteValue();

    }

    @Override
    public Byte ByteValue() {

        return (Byte)getObjectValue();

    }

    @Override
    public short shortValue() {

        Short shortValue = ShortValue();
        if ( shortValue == null ) {

            throw new IllegalArgumentException( "null value" );

        }


        return shortValue.shortValue();

    }

    @Override
    public Short ShortValue() {

        return (Short)getObjectValue();

    }

    @Override
    public short[] PrimitiveShortArrayValue() {

        return (short[])getObjectValue();

    }

    @Override
    public Short[] ContainerShortArrayValue() {

        return (Short[])getObjectValue();

    }

    @Override
    public int intValue() {

        Integer intValue = IntegerValue();
        if ( intValue == null ) {

            throw new IllegalArgumentException( "null value" );

        }

        return intValue.intValue();

    }

    @Override
    public Integer IntegerValue() {

        return (Integer)getObjectValue();

    }

    @Override
    public long longValue() {

        Long longValue = LongValue();
        if ( longValue == null ) {

            throw new IllegalArgumentException( "null value" );

        }

        return longValue.longValue();

    }

    @Override
    public Long LongValue() {

        return (Long)getObjectValue();

    }

    @Override
    public float floatValue() {

        Float floatValue = FloatValue();
        if ( floatValue == null ) {

            throw new IllegalArgumentException( "null value" );

        }

        return floatValue.floatValue();

    }

    @Override
    public Float FloatValue() {

        return (Float)getObjectValue();

    }

    @Override
    public double doubleValue() {

        Double doubleValue = DoubleValue();
        if ( doubleValue == null ) {

            throw new IllegalArgumentException( "null value" );

        }

        return doubleValue.doubleValue();

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
    public Number[] NumberArrayValue() {

        // unimplemented
        return (Number[])getObjectValue();

    }

    @Override
    public char charValue() {

        Character charValue = CharacterValue();
        if ( charValue == null ) {

            throw new IllegalArgumentException( "null value" );

        }

        return charValue.charValue();

    }

    @Override
    public Character CharacterValue() {

        return (Character)getObjectValue();

    }

    @Override
    public char[] PrimitiveCharArrayValue() {

        return (char[])getObjectValue();

    }

    @Override
    public Character[] ContainerCharacterArrayValue() {

        return (Character[])getObjectValue();

    }

    @Override
    public String StringValue() {

        return (String)getObjectValue();

    }

    @Override
    @NotNull
    public String MandatoryStringValue() {

        String rval = (String)getObjectValue();
        if ( rval == null ) {

            throw new IllegalArgumentException( "null value" );

        }

        return rval;

    }

    @Override
    public EntityName EntityNameValue() {

        return (EntityName)getObjectValue();

    }

    @Override
    @NotNull
    public EntityName MandatoryEntityNameValue() {

        EntityName rval = (EntityName)getObjectValue();
        if ( rval == null ) {

            throw new IllegalArgumentException( "null value" );

        }

        return rval;

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
    public int[] PrimitiveIntArrayValue() {

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
    public float[] PrimitiveFloatArrayValue() {

        return (float[])getObjectValue();

    }

    @Override
    public Float[] ContainerFloatArrayValue() {

        return (Float[])getObjectValue();

    }

    @Override
    public double[] PrimitiveDoubleArrayValue() {

        return (double[])getObjectValue();

    }

    @Override
    public Double[] ContainerDoubleArrayValue() {

        return (Double[])getObjectValue();

    }

    @Override
    public String[] StringArrayValue() {

        return (String[])getObjectValue();

    }

    @Override
    public EntityName[] EntityNameArrayValue() {

        return (EntityName[])getObjectValue();

    }

    @Override
    public GowingPackable[] PackableArrayValue() {

        return (GowingPackable[])getObjectValue();

    }

    @Override
    public EntityName getName() {

        return _name;

    }

    @SuppressWarnings("unused")
    public char getTag() {

        return _tag;

    }

    public Object getObjectValue() {

        return _objectValue;

    }

    public boolean hasObjectValue() {

        return _objectValue != null;

    }

    @SuppressWarnings("WeakerAccess")
    public boolean isMandatory() {

        return _mandatory;

    }

    @Override
    public boolean pack( final GowingPacker packer2 ) {

        emitRepresentation( packer2 );

        return false;

    }

    public int compareTo( final @NotNull GowingPackableThingHolder rhs ) {

        return _name.compareTo( rhs.getName() );

    }

    public int hashCode() {

        return _name.hashCode();

    }

    public boolean equals( final Object rhs ) {

        return rhs instanceof GowingPackableThingHolder && _name.equals( ( (GowingPackableThingHolder)rhs ).getName() );

    }

    public String toString() {

        return "NTH2( \"" + getName() + "\", " + getObjectValue() + " )";

    }

}
