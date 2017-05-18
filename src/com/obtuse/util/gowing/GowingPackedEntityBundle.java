package com.obtuse.util.gowing;

import com.obtuse.exceptions.HowDidWeGetHereError;
import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.ObtuseUtil;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.TreeMap;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

/**
 Carry around the packed form of an entity.
 */

public class GowingPackedEntityBundle extends TreeMap<EntityName,GowingPackableThingHolder> {

    private final EntityTypeName _typeName;

    private final int _version;

    private final GowingPackedEntityBundle _superBundle;

    private final int _typeId;

//    private final PackingId2 _packingId;

    public GowingPackedEntityBundle(
	    @NotNull EntityTypeName typeName,
	    int version,
	    @Nullable GowingPackedEntityBundle superBundle,
	    @NotNull GowingPackerContext packerContext
    ) {
	super();

	_typeName = typeName;
	_version = version;
	_typeId = packerContext.rememberTypeName( typeName );
	_superBundle = superBundle;

//	_packingId = new PackingId2( typeName, context.getTypeReferenceId( typeName ), entityId );

    }

    public GowingPackedEntityBundle(
	    @NotNull EntityTypeName typeName,
	    int typeId,
	    @Nullable GowingPackedEntityBundle superBundle,
	    int version,
	    @NotNull GowingUnPackerContext unPackerContext
    ) {
	super();

	_typeName = typeName;
	_typeId = typeId; // unPackerContext.rememberTypeName( typeName );
	_superBundle = superBundle;
	_version = version;

//	_packingId = new PackingId2( typeName, context.getTypeReferenceId( typeName ), entityId );

    }

//    public boolean hasSuper() {
//
//	return _superBundle != null;
//
//    }

//    @NotNull
//    public PackingId2 getPackingId() {
//
//	return _packingId;
//
//    }

    @NotNull
    public GowingPackedEntityBundle getSuperBundle() {

	return _superBundle;

    }

    public boolean hasSuperBundle() {

        return _superBundle != null;

    }

    public int getVersion() {

	return _version;

    }

    @SuppressWarnings("WeakerAccess")
    public EntityTypeName getTypeName() {

	return _typeName;

    }

    public int getTypeId() {

	return _typeId;

    }

    public String toString() {

	return "GowingPackedEntityBundle( " + getTypeId() + ":" + getTypeName() + ", " + keysToString() + " )";
//	return "PackedEntityBundle<" + getTypeName() + ">( super = " + getSuperBundle() + ", " + size() + " values )";

    }

    @SuppressWarnings("WeakerAccess")
    public String keysToString() {

	FormattingLinkedList<EntityName> keys = new FormattingLinkedList<>( keySet() );

	return keys.toString();

    }

    public void addHolder( GowingPackableThingHolder holder ) {

	put( holder.getName(), holder );

    }

    /**
     Helper method to get an optional entity reference field.
     @param name the name of the optional entity reference field.
     @return the entity reference specified by the field, or null if the field does not exist within this bundle or exists but is null.
     @throws ClassCastException if the specified field exists but is not a GowingEntityReference value.
     */

    public GowingEntityReference getOptionalEntityReference( EntityName name ) {

	GowingPackableThingHolder ref = getNullableField( name );
	if ( ref == null ) {

	    return null;

	} else {

	    return ref.EntityTypeReference();

	}

    }

    /**
     Helper method to get a mandatory entity reference field.
     @param name the name of the mandatory entity reference field.
     @return the entity reference specified by the field (will not be null).
     @throws IllegalArgumentException if the specified field does not exist within this bundle.
     @throws NullPointerException if the specified field exists within this bundle but is null.
     @throws ClassCastException if the specified field exists but is not a GowingEntityReference value.
     */

    @NotNull
    public GowingEntityReference getMandatoryEntityReference( EntityName name ) {

	GowingPackableThingHolder ref = getNotNullField( name );

	return ref.EntityTypeReference();

    }

    /**
     Get a field which must exist and have a non-null value.
     @param name the name of the desired field.
     @return the holder containing the field.
     @throws IllegalArgumentException if the specified field does not exist within this bundle.
     @throws NullPointerException if the field specified exists within this bundle but is null.
     */

    public GowingPackableThingHolder getNotNullField( EntityName name ) {

	GowingPackableThingHolder holder = get( name );
	if ( holder == null ) {

	    throw new IllegalArgumentException( "required field \"" + name + "\" is missing" );

	}

	if ( holder.isNull() ) {

	    throw new NullPointerException( "field \"" + name + "\" is null" );

	}

	return holder;

    }

    /**
     Get a field which must exist but which might have a null value.
     @param name the name of the desired field.
     @return the holder containing the field.
     */

    public GowingPackableThingHolder getNullableField( EntityName name ) {

	GowingPackableThingHolder holder = get( name );
	if ( holder == null ) {

	    throw new IllegalArgumentException( "required field \"" + name + "\" is missing" );

	}

	return holder;

    }

    /**
     Determine if a field exists.
     <p/>Makes it possible to have totally optional fields.
     @param tag the name of the optional field.
     @return {@code true} if a field by that name exists; {@code false} otherwise.
     */

    public boolean doesFieldExist( EntityName tag ) {

        return containsKey( tag );

    }

    public URI recoverURI( EntityName tag ) {

	String s = getNullableField( tag ).StringValue();
	if ( s == null ) {

	    return null;

	} else {

	    try {

		return new URI( s );

	    } catch ( URISyntaxException e ) {

		throw new HowDidWeGetHereError( "syntax error parsing URI " + ObtuseUtil.enquoteForJavaString( s ), e );

	    }

	}

    }

    public URL recoverURL( EntityName tag ) {

	String s = getNullableField( tag ).StringValue();
	if ( s == null ) {

	    return null;

	} else {

	    try {

		return new URL( s );

	    } catch ( MalformedURLException e ) {

		throw new HowDidWeGetHereError( "syntax error parsing URL " + ObtuseUtil.enquoteForJavaString( s ), e );

	    }

	}

    }

    public File recoverFile( EntityName tag ) {

	String s = getNullableField( tag ).StringValue();
	if ( s == null ) {

	    return null;

	} else {

	    return new File( s );

	}

    }

    public String StringValue( EntityName gtag ) {

	return getNullableField( gtag ).StringValue();

    }

    public String[] StringArrayValue( EntityName gtag ) {

        return getNullableField( gtag ).StringArrayValue();

    }

    public byte byteValue( EntityName gtag ) {

	return getNotNullField( gtag ).byteValue();

    }

    public byte[] byteArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).PrimitiveByteArrayValue();

    }

    public Byte ByteValue( EntityName gtag ) {

	return getNullableField( gtag ).ByteValue();

    }

    public Byte[] ByteArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).ContainerByteArrayValue();

    }

    public short shortValue( EntityName gtag ) {

	return getNotNullField( gtag ).shortValue();

    }

    public short[] shortArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).PrimitiveShortArrayValue();

    }

    public Short ShortValue( EntityName gtag ) {

	return getNullableField( gtag ).ShortValue();

    }

    public Short[] ShortArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).ContainerShortArrayValue();

    }

    public int intValue( EntityName gtag ) {

	return getNotNullField( gtag ).intValue();

    }

    public int[] intArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).PrimitiveIntArrayValue();

    }

    public Integer IntegerValue( EntityName gtag ) {

	return getNullableField( gtag ).IntegerValue();

    }

    public Integer[] IntegerArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).ContainerIntegerArrayValue();

    }

    public long longValue( EntityName gtag ) {

	return getNotNullField( gtag ).longValue();

    }

    public long[] longArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).PrimitiveLongArrayValue();

    }

    public Long LongValue( EntityName gtag ) {

	return getNullableField( gtag ).LongValue();

    }

    public Long[] LongArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).ContainerLongArrayValue();

    }

    public float floatValue( EntityName gtag ) {

	return getNotNullField( gtag ).floatValue();

    }

    public float[] floatArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).PrimitiveFloatArrayValue();

    }

    public Float FloatValue( EntityName gtag ) {

	return getNullableField( gtag ).FloatValue();

    }

    public Float[] FloatArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).ContainerFloatArrayValue();

    }

    public double doubleValue( EntityName gtag ) {

	return getNotNullField( gtag ).doubleValue();

    }

    public double[] doubleArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).PrimitiveDoubleArrayValue();

    }

    public Double DoubleValue( EntityName gtag ) {

	return getNullableField( gtag ).DoubleValue();

    }

    public Double[] DoubleArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).ContainerDoubleArrayValue();

    }

    public char charValue( EntityName gtag ) {

	return getNotNullField( gtag ).charValue();

    }

    public char[] charArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).PrimitiveCharArrayValue();

    }

    public Character CharacterValue( EntityName gtag ) {

	return getNullableField( gtag ).CharacterValue();

    }

    public Character[] CharacterArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).ContainerCharacterArrayValue();

    }

    public boolean booleanValue( EntityName gtag ) {

	return getNotNullField( gtag ).booleanValue();

    }

    public boolean[] booleanArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).PrimitiveBooleanArrayValue();

    }

    public Boolean BooleanValue( EntityName gtag ) {

	return getNullableField( gtag ).BooleanValue();

    }

    public Boolean[] BooleanArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).ContainerBooleanArrayValue();

    }

    public Number NumberValue( EntityName gtag ) {

	return getNullableField( gtag ).NumberValue();

    }

    public Number[] NumberArrayValue( EntityName gtag ) {

	return getNullableField( gtag ).NumberArrayValue();

    }

}
