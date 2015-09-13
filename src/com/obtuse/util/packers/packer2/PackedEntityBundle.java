package com.obtuse.util.packers.packer2;

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

public class PackedEntityBundle extends TreeMap<EntityName2,Packable2ThingHolder2> {

    private final EntityTypeName2 _typeName;

    private final int _version;

    private final PackedEntityBundle _superBundle;

    private final int _typeId;

//    private final PackingId2 _packingId;

    public PackedEntityBundle(
	    @NotNull EntityTypeName2 typeName,
	    int version,
	    @Nullable PackedEntityBundle superBundle,
	    @NotNull PackerContext2 packerContext
    ) {
	super();

	_typeName = typeName;
	_version = version;
	_typeId = packerContext.rememberTypeName( typeName );
	_superBundle = superBundle;

//	_packingId = new PackingId2( typeName, context.getTypeReferenceId( typeName ), entityId );

    }

    public PackedEntityBundle(
	    @NotNull EntityTypeName2 typeName,
	    int typeId,
	    @Nullable PackedEntityBundle superBundle,
	    int version,
	    @NotNull UnPackerContext2 unPackerContext
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
    public PackedEntityBundle getSuperBundle() {

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

	return "PackedEntityBundle( " + getTypeId() + ":" + getTypeName() + ", " + keysToString() + " )";
//	return "PackedEntityBundle<" + getTypeName() + ">( super = " + getSuperBundle() + ", " + size() + " values )";

    }

    @SuppressWarnings("WeakerAccess")
    public String keysToString() {

	FormattingLinkedList<EntityName2> keys = new FormattingLinkedList<EntityName2>( keySet() );

	return keys.toString();

    }

    public void addHolder( Packable2ThingHolder2 holder ) {

	put( holder.getName(), holder );

    }

    /**
     Get a field which must exist and have a non-null value.
     @param name the name of the desired field.
     @return the holder containing the field.
     */

    public Packable2ThingHolder2 getNotNullField( EntityName2 name ) {

	Packable2ThingHolder2 holder = get( name );
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

    public Packable2ThingHolder2 getNullableField( EntityName2 name ) {

	Packable2ThingHolder2 holder = get( name );
	if ( holder == null ) {

	    throw new IllegalArgumentException( "required field \"" + name + "\" is missing" );

	}

	return holder;

    }

}
