package com.obtuse.util.gowing.packer2;

/*
 * Copyright © 2015 Obtuse Systems Corporation
 */

import com.obtuse.util.FormattingLinkedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TreeMap;

/**
 Carry around the packed form of an entity.
 */

public class GowingPackedEntityBundle extends TreeMap<EntityName2,GowingPackable2ThingHolder2> {

    private final EntityTypeName2 _typeName;

    private final int _version;

    private final GowingPackedEntityBundle _superBundle;

    private final int _typeId;

//    private final PackingId2 _packingId;

    public GowingPackedEntityBundle(
	    @NotNull EntityTypeName2 typeName,
	    int version,
	    @Nullable GowingPackedEntityBundle superBundle,
	    @NotNull GowingPackerContext2 packerContext
    ) {
	super();

	_typeName = typeName;
	_version = version;
	_typeId = packerContext.rememberTypeName( typeName );
	_superBundle = superBundle;

//	_packingId = new PackingId2( typeName, context.getTypeReferenceId( typeName ), entityId );

    }

    public GowingPackedEntityBundle(
	    @NotNull EntityTypeName2 typeName,
	    int typeId,
	    @Nullable GowingPackedEntityBundle superBundle,
	    int version,
	    @NotNull GowingUnPackerContext2 unPackerContext
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

    @Nullable
    public GowingPackedEntityBundle getSuperBundle() {

	return _superBundle;

    }

    public int getVersion() {

	return _version;

    }

    @SuppressWarnings("WeakerAccess")
    public EntityTypeName2 getTypeName() {

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

	FormattingLinkedList<EntityName2> keys = new FormattingLinkedList<EntityName2>( keySet() );

	return keys.toString();

    }

    public void addHolder( GowingPackable2ThingHolder2 holder ) {

	put( holder.getName(), holder );

    }

    /**
     Get a field which must exist and have a non-null value.
     @param name the name of the desired field.
     @return the holder containing the field.
     */

    public GowingPackable2ThingHolder2 getNotNullField( EntityName2 name ) {

	GowingPackable2ThingHolder2 holder = get( name );
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

    public GowingPackable2ThingHolder2 getNullableField( EntityName2 name ) {

	GowingPackable2ThingHolder2 holder = get( name );
	if ( holder == null ) {

	    throw new IllegalArgumentException( "required field \"" + name + "\" is missing" );

	}

	return holder;

    }

}
