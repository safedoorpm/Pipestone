package com.obtuse.util.gowing;

import com.obtuse.util.FormattingLinkedList;
import com.obtuse.util.gowing.p2a.GowingEntityReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
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
    public Optional<GowingPackedEntityBundle> getSuperBundle() {

	return Optional.ofNullable( _superBundle );

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

	FormattingLinkedList<EntityName> keys = new FormattingLinkedList<EntityName>( keySet() );

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

}
